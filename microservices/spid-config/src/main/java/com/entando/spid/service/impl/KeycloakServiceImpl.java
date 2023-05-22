package com.entando.spid.service.impl;

import com.entando.spid.ConfigUtils;
import com.entando.spid.domain.Idp;
import com.entando.spid.domain.Organization;
import com.entando.spid.domain.ServiceStatus;
import com.entando.spid.domain.keycloak.AuthenticationFlow;
import com.entando.spid.domain.keycloak.Client;
import com.entando.spid.domain.keycloak.Execution;
import com.entando.spid.domain.keycloak.IdentityProvider;
import com.entando.spid.domain.keycloak.Token;
import com.entando.spid.service.ConfigurationService;
import com.entando.spid.service.IdpService;
import com.entando.spid.service.KeycloakService;
import com.entando.spid.service.dto.ConnectionClient;
import com.entando.spid.service.dto.MapperAttribute;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.entando.spid.Constants.*;

@Component
public class KeycloakServiceImpl implements KeycloakService {

    private final Logger logger = LoggerFactory.getLogger(KeycloakServiceImpl.class);

    private final IdpService idpService;

    @Autowired
    private ConfigurationService configurationService;

    private final ApplicationContext appContext;

    public KeycloakServiceImpl(IdpService idpService, ApplicationContext appContext) {
        this.idpService = idpService;
        this.appContext = appContext;
    }

    protected void shutdown(int exitCode) {
        logger.info("Shutting down service for unexpected errors");
        logger.warn("exit code: {}", exitCode);
        SpringApplication.exit(appContext, () -> exitCode);
        System.exit(exitCode);
    }

    
    @Override
    public Boolean configureOrShutDown(ConnectionClient connection) {
        boolean success = false;

        try {
            if (connection.isValid() && (success = configure(connection))) {
                logger.info("Host [{}] configuration complete", connection.getHost());
            } else {
                logger.error("Host [{}] configuration failed", connection.getHost());
                // exit the service
                shutdown(0);
            }
        } catch (Throwable t) {
            logger.error("Unexpected error", t);
            // exit the service
            shutdown(0);
        }
        return success;
    }

    /**
     * Negotiate a service account token for REST operations
     * @return access token (SAT)
     */
    protected Token negotiateToken(ConnectionClient connection) {
        return getServiceAccountToken(connection);
    }

    private Client findClient(Client[] clients, String id) {
        if (clients != null) {
            // find the entando-web
            Optional<Client> client = Arrays.stream(clients)
                .filter(c -> c.getClientId().equals(id))
                .findFirst();
            if (client.isPresent()) {
                return client.get();
            }
        }
        return null;
    }

    @Override
    public boolean revertConfiguration(ConnectionClient connection) {
        boolean flawless = true;
        List<Idp> templates = idpService.getTemplates();

        try {
            final Token token = negotiateToken(connection);
            final String host = connection.getHost();
            final String realm = connection.getRealm();
            // 1 -  delete providers
            if (templates != null && !templates.isEmpty()) {
                for (Idp provider: templates) {
                    if (deleteProvider(host, token, realm, provider.getName())) {
                        logger.info("provider {} deleted  successfully", provider.getName());
                    } else {
                        logger.error("error deleting provider {}", provider.getName());
                    }
                }
            }
            // 2 - delete the authentication flow
            List<AuthenticationFlow> flows = getAuthenticationFlows(host, realm, token);
            if (flows != null && !flows.isEmpty()) {
                Optional<AuthenticationFlow> flow = flows
                    .stream()
                    .filter(f -> f.getAlias().equals(KEYCLOAK_NEW_AUTH_FLOW_NAME))
                    .findFirst();
                if (flow.isPresent()) {
                    String flowId = flow.get().getId();
                    if (deleteAuthenticationFlow(host, token, realm, flowId)) {
                        logger.info("Authentication flow {} successfully deleted", KEYCLOAK_NEW_AUTH_FLOW_NAME);
                    } else {
                        logger.error("Error deleting Authentication flow " +  KEYCLOAK_NEW_AUTH_FLOW_NAME);
                        flawless = false;
                    }
                } else {
                    logger.error("Authentication flow '{}' not found!", KEYCLOAK_NEW_AUTH_FLOW_NAME);
                }
            }
        } catch (Throwable t) {
            logger.error("Error reverting the configuration!", t);
            flawless = false;
        }
        return flawless;
    }

    @Override
    public ServiceStatus getStatus(ConnectionClient connection) {
        Optional<AuthenticationFlow> flow = Optional.ofNullable(null);
        List<IdentityProvider> providers = null;

        try {
            final Token token = negotiateToken(connection);
            final String host = connection.getHost();
            final String realm = connection.getRealm();
            // 1 - check for Authentication flow
            List<AuthenticationFlow> flows = getAuthenticationFlows(host, realm, token);
            if (flows != null && !flows.isEmpty()) {
                flow = flows
                    .stream()
                    .filter(f -> f.getAlias().equals(KEYCLOAK_NEW_AUTH_FLOW_NAME))
                    .findFirst();
            }
            // 2 - list providers
            providers = getIdentityProviders(host, token, realm);
        } catch (Throwable t) {
            logger.error("error in getStatus", t);
        }
        return new ServiceStatus(providers, flow.isPresent());
    }

    @Override
    public boolean configure(ConnectionClient connection) {
        final Organization organization = configurationService.getOrganization();
        Execution[] executions;
        Map<String, String> envVars = System.getenv();

        String doJwtMap = envVars.get("KEYCLOAK_MAP_JWT");
        boolean mapJwt = StringUtils.isNotBlank(doJwtMap) && Boolean.parseBoolean(doJwtMap);

        try {
            Token token = negotiateToken(connection);
            final String host = connection.getHost();
            final String realm = connection.getRealm();
            // 1 - configure authentication flow
            if (!duplicateAuthenticationFlow(host, token, realm)) {
                logger.error("could not create authentication flow, aborting configuration");
                return false;
            }
            logger.info("Keycloak config: authorization flow created with name [{}]", KEYCLOAK_NEW_AUTH_FLOW_NAME);
            // 2 - add executable
            if (!addExecutable(host, token, realm)) {
                logger.error("could not add execution to the authentication flow, aborting configuration");
                return false;
            }
            // 3 - get the id of the newly created execution
            executions = getExecutions(host, token, realm);
            if (executions == null || executions.length == 0
                || !executions[executions.length - 1].getDisplayName().equals(KEYCLOAK_EXECUTION_EXPECTED_DISPLAY_NAME)) {
                logger.error("could not obtain the execution for the flow, aborting configuration");
                return false;
            }
            String id = executions[executions.length - 1].getId();
            logger.debug("Target execution ID is [{}] ", id);
            //  4 - move executable to its position
            for (int i = 0; i < 2; i++) {
                if (!raiseExecutionPriority(host, token, realm, id)) {
                    logger.error("Could not raise the execution level of the target execution " + id);
                    return false;
                }
            }
            // 5 - edit requirements of the given executables
            // 5A - REQUIRED for Automatically "Set Existing User"
            if (!updateExecutionRequirement(host, token, realm, executions, KEYCLOAK_EXECUTION_EXPECTED_DISPLAY_NAME, "REQUIRED")) {
                logger.error("Cannot find target execution [" + KEYCLOAK_EXECUTION_EXPECTED_DISPLAY_NAME + "], aborting setup");
                return false;
            }
            // 5B - DISABLED for "Confirm Link Existing Account"
            if (!updateExecutionRequirement(host, token, realm, executions, KEYCLOAK_EXECUTION_CONFIRM_LINK_DISPLAY_NAME, "DISABLED")) {
                logger.error("Cannot find target execution [" + KEYCLOAK_EXECUTION_CONFIRM_LINK_DISPLAY_NAME + "], aborting setup");
                return false;
            }
            // 5C - DISABLED for "SPID first broker login Account verification options"
            if (!updateExecutionRequirement(host, token, realm, executions, KEYCLOAK_EXECUTION_VERIFICATION_OPTIONS_DISPLAY_NAME, "DISABLED")) {
                logger.error("Cannot find target execution [" + KEYCLOAK_EXECUTION_VERIFICATION_OPTIONS_DISPLAY_NAME + "], aborting setup");
                return false;
            }
            logger.info("authentication flow successfully configured");

            // 6 - configure identity provider
            List<Idp> templates = idpService.getTemplates();
            if (templates == null || templates.isEmpty()) {
                logger.error("No Identity Provider templates found!");
                return false;
            }
            // 7 - create mapping for SPID profile
            int configured = 0;
            for (Idp template: templates) {
                if (configureProvider(host, token, realm, template, organization)) {
                    configured++;
                }
            }
            if (configured == 0) {
                logger.error("No provider configured!");
                    return false;
            }
            // 8 create JWT mappings
            if (mapJwt) {
            Client[] clients = getClients(host, token,  realm,0);
            Client client = findClient(clients, ENTANDO_WEB_CLIENT_ID);
            if (client != null) {
                String clientId = client.getId();

                logger.debug("Creating JWT mappings for client {}", clientId);
                for (MapperAttribute attr: KEYCLOAK_IDP_MAPPING) {
                    if (!createJwtMapping(host, token, realm, clientId, attr)) {
                        logger.error("Could not create JWT mapping for attribute {}", attr.getAttributeName());
                        return false;
                    }
                }
                logger.info("JWT mappings created");
            }
            } else {
                logger.info("Skipping JWT mapping as requested");
            }
            logger.info("{} identity providers successfully configured", configured);
        } catch (Throwable t) {
            logger.error("unexpected error in configureKeycloak", t);
            return false;
        }
        return true;
    }

    /**
     * Configure single identity provider
     * @param host the keycloak address
     * @param token the SAT
     * @param template configuration template
     * @param organization the organization data
     * @return true if setup was successful, false otherwise
     */
    protected boolean configureProvider(String host, Token token, String realm, Idp template, Organization organization) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // add the organization properties to the template merging JSON properties
            JSONObject json = ConfigUtils.configureIdp(organization, template.getConfig());
            // create the Idp Object
            IdentityProvider idp = objectMapper.readValue(json.toString(), IdentityProvider.class);
            // create configuration
            if (!createIdentityProvider(host, token, realm, idp)) {
                logger.error("Cannot configure the service provider [" + idp.getAlias() + "], aborting setup");
                return false;
            }
            // create mapping for SPID profile
            if (!addMapperUsername(host, token, realm, idp.getAlias())) {
                logger.error("Cannot configure the mapper for IdP [" + idp.getAlias() + "], aborting setup");
                return false;
            }
            for (MapperAttribute entry : KEYCLOAK_IDP_MAPPING) {
                logger.debug("configuring mapper {}", entry);
                if (!addMapperGeneric(host, token, realm, idp.getAlias(), entry.getName(), entry.getAttributeName(), entry.getUserAttributeName())) {
                    logger.error("Cannot configure the mapper for IdP [" + idp.getAlias() + "]:" + entry + ", aborting setup");
                    return false;
                }
            }
        } catch (Throwable t) {
            logger.error("Error configuring the identity provider", t);
        }
        logger.info("identity provider [{}] successfully configured", template.getName());
        return true;
    }

    /**
     * Get an access token through client authentication
     * @param connection parameters
     * @return the SAT access token
     */
    protected Token getServiceAccountToken(ConnectionClient connection) {
        Token token = null;
        WebClient client = WebClient.create();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        final String REST_URI = connection.getHost() + "/realms/" + connection.getRealm() + "/protocol/openid-connect/token";

        body.add("client_id", connection.getCLientId());
        body.add("client_secret", connection.getClientSecret());
        body.add("grant_type", "client_credentials");

        try {
            token =
                client.post()
                    .uri(new URI(REST_URI))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromFormData(body))
                    .exchangeToMono(result -> {
                        if (result.statusCode()
                            .equals(HttpStatus.OK)) {
                            return result.bodyToMono(Token.class);
                        } else {
                            logger.error("Unexpected status: {}" , result.statusCode());
                            return Mono.empty();
                        }
                    })
                    .block();
        } catch (Throwable t) {
            logger.error("error getting the admin access token", t);
        }
        return token;
    }


    /**
     * Get the access token authenticating as privileged user
     * @param connection info
     * @return the UAT access token
     */
    protected Token getAdminToken(ConnectionClient connection) {
        Token token = null;
        WebClient client = WebClient.create();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        final String REST_URI = connection.getHost() + "/realms/master/protocol/openid-connect/token";

        body.add("username", connection.getUsername());
        body.add("password", connection.getPassword());
        body.add("grant_type", "password");
        body.add("client_id", "admin-cli");
        body.add("client_secret", "admin-cli");
        body.add("scope", "openid");

        try {
            token =
                client.post()
                    .uri(new URI(REST_URI))
                    //                .header("Authorization", "Bearer MY_SECRET_TOKEN")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromFormData(body))
//                .retrieve()
//                .bodyToMono(Token.class)
//                .block();
                    .exchangeToMono(result -> {
                        if (result.statusCode()
                            .equals(HttpStatus.OK)) {
                            return result.bodyToMono(Token.class);
                        } else {
                            logger.error("Unexpected status: {}" , result.statusCode());
                            return Mono.empty();
                        }
                    })
                    .block();
        } catch (Throwable t) {
            logger.error("error getting the admin access token", t);
        }
        return token;
    }

    /**
     * Duplicate existing authentication flow for customization
     * @param host Keycloak address
     * @param token the SAT or UAT
     * @param realm the target realm
     * @return true if the flow was duplicated
     */
    protected boolean duplicateAuthenticationFlow(String host, Token token, String realm) {
        final String REST_URI = encodePath(host + "/admin/realms/" + realm + "/authentication/flows/" + KEYCLOAK_DEFAULT_AUTH_FLOW + "/copy");
        // for a simple payload there's no need to disturb Jackson
        String payload = "{\"newName\":\"" + KEYCLOAK_NEW_AUTH_FLOW_NAME + "\"}";
        Boolean created = false;
        WebClient client = WebClient.create();

        try {
            created = client.post()
                    .uri(new URI(REST_URI))
                    .header("Authorization", "Bearer " + token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(payload))
                    .exchangeToMono(result -> {
                        if (result.statusCode()
                            .equals(HttpStatus.CREATED)) {
                            return Mono.just(true);
                        } else {
                            logger.error("Unexpected status: {}" , result.statusCode());
                            return Mono.just(false);
                        }
                    })
                    .block();
        } catch (Throwable t) {
            logger.error("error in duplicateAuthenticationFlow", t);
        }
        return created;
    }

    protected List<AuthenticationFlow> getAuthenticationFlows(String host, String realm, Token token) {
        AuthenticationFlow[] flows = null;
        final String REST_URI = encodePath(host + "/admin/realms/" + realm + "/authentication/flows");
        WebClient client = WebClient.create();

        try {
            flows = client
                .get()
                .uri(new URI(REST_URI))
                .header("Authorization", "Bearer " + token.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(result -> {
                    if (result.statusCode()
                        .equals(HttpStatus.OK)) {
                        return result.bodyToMono(AuthenticationFlow[].class);
                    } else {
                        logger.error("Unexpected status: {}" , result.statusCode());
                        return Mono.empty();
                    }
                })
                .block();
        } catch (Throwable t) {
            logger.error("error in getAuthenticationFlows", t);
        }

        return Arrays.asList(flows);
    }

    /**
     * Delete flow
     * @param host  Keycloak address
     * @param token the SAT or UAT
     * @param realm the target realm
     * @param id flow id
     * @return true if the operation was successful
     */
    protected boolean deleteAuthenticationFlow(String host, Token token, String realm, String id) {
        Boolean deleted = false;
        final String REST_URI = encodePath(host + "/admin/realms/" + realm + "/authentication/flows/" + id);
        WebClient client = WebClient.create();

        try {
            deleted =
                client
                    .delete()
                    .uri(new URI(REST_URI))
                    .header("Authorization", "Bearer " + token.getAccessToken())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(result -> {
                        if (result.statusCode()
                            .equals(HttpStatus.NO_CONTENT)) {
                            return Mono.just(true);
                        } else {
                            logger.error("Unexpected status: {}" , result.statusCode());
                            return Mono.just(false);
                        }
                    })
                    .block();
        } catch (Throwable t) {
            logger.error("error in deleteAuthenticationFlow", t);
        }

        return deleted;
    }

    /**
     * Add a new execution to the flow
     * @param host Keycloak address
     * @param token the SAT or UAT
     * @param realm the target realm
     * @return true if the execution was successfully added
     */
    protected boolean addExecutable(String host, Token token, String realm) {
        final String REST_URI = encodePath(host + "/admin/realms/" + realm + "/authentication/flows/" + KEYCLOAK_EXECUTION_HANDLE_EXISTING_ACCOUNT_NAME + "/executions/execution");
        WebClient client = WebClient.create();
        // for a simple payload there's no need to disturb Jackson
        String payload = "{\"provider\":\"idp-auto-link\"}";
        Boolean created = false;

        try {
            created = client.post()
                    .uri(new URI(REST_URI))
                    .header("Authorization", "Bearer " + token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(payload))
                    .exchangeToMono(result -> {
                        if (result.statusCode()
                            .equals(HttpStatus.CREATED)) {
                            return Mono.just(true);
                        } else {
                            logger.error("Unexpected status: {}" , result.statusCode());
                            return Mono.just(false);
                        }
                    })
                    .block();
        } catch (Throwable t) {
            logger.error("error in duplicateAuthFlow", t);
        }
        return created;
    }

    /**
     * Get the executions list of a given flow
     * @param host Keycloak address
     * @param token the SAT or UAT
     * @param realm the target realm
     * @return the array of executions found
     */
    protected Execution[] getExecutions(String host, Token token, String realm) {
        Execution[] executions = null;
        final String REST_URI = encodePath(host + "/admin/realms/" + realm + "/authentication/flows/" + KEYCLOAK_NEW_AUTH_FLOW_NAME + "/executions");
        WebClient client = WebClient.create();

        try {
            executions = client
                    .get()
                    .uri(new URI(REST_URI))
                    .header("Authorization", "Bearer " + token.getAccessToken())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(result -> {
                        if (result.statusCode()
                            .equals(HttpStatus.OK)) {
                            return result.bodyToMono(Execution[].class);
                        } else {
                            logger.error("Unexpected status: {}" , result.statusCode());
                            return Mono.empty();
                        }
                    })
                    .block();
        } catch (Throwable t) {
            logger.error("error in getExecutions", t);
        }
        return executions;
    }

    /**
     * Raise the priority of a give execution by one
     * @param host host Keycloak address
     * @param token the SAT or UAT
     * @param realm the target realm
     * @param id the id of the execution
     * @return true if the priority was changed
     */
    protected boolean raiseExecutionPriority(String host, Token token, String realm, String id) {
        final String REST_URI = encodePath(host + "/admin/realms/" + realm + "/authentication/executions/" + id + "/raise-priority");
        WebClient client = WebClient.create();
        Boolean success = true;

        try {
            success =
                client
                    .post()
                    .uri(new URI(REST_URI))
                    .header("Authorization", "Bearer " + token.getAccessToken())
                    .exchangeToMono(result -> {
                        if (result.statusCode()
                            .equals(HttpStatus.NO_CONTENT)) {
                            return Mono.just(true);
                        } else {
                            logger.error("Unexpected status: {}" , result.statusCode());
                            return Mono.just(false);
                        }
                    })
                    .block();
        } catch (Throwable t) {
            logger.error("error in raiseExecutionPriority", t);
        }
        return success;
    }

    /**
     * Update the requirements of a given execution
     * @param host Keycloak address
     * @param token the SAT or UAT
     * @param realm the target realm
     * @param executions the list of executions
     * @param executionName name of the execution to update
     * @param requirement the desired requirement
     * @return true if the execution was updated
     */
    protected boolean updateExecutionRequirement(String host, Token token, String realm, Execution[] executions, String executionName, String requirement) {
        AuthenticationFlow updated = null;
        Optional<Execution> execOpt = findExecution(executions, executionName);

        if (execOpt.isPresent()) {
            Execution execution = execOpt.get();
            execution.setRequirement(requirement); // TODO constant
            updated = updateExecution(host, token, realm, execution);
        } else {
            logger.error("Cannot find target execution " + executionName + ", aborting setup");
        }
        return updated != null;
    }

    /**
     * Update the requirements of a given execution
     * @param host Keycloak address
     * @param token the SAT or UAT
     * @param realm the target realm
     * @param execution the execution to update
     * @return true if the execution was updated
     */
    protected AuthenticationFlow updateExecution(String host, Token token, String realm, Execution execution) {
        final String REST_URI = encodePath(host + "/admin/realms/" + realm + "/authentication/flows/" + KEYCLOAK_NEW_AUTH_FLOW_NAME + "/executions");
        AuthenticationFlow flow = null;
        WebClient client = WebClient.create();

        try {
            flow =
                client
                    .put()
                    .uri(new URI(REST_URI))
                    .header("Authorization", "Bearer " + token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(execution))
                    .exchangeToMono(result -> {
                        if (result.statusCode()
                            .equals(HttpStatus.ACCEPTED)) {
                            return result.bodyToMono(AuthenticationFlow.class);
                        } else {
                            logger.error("Unexpected status: {}" , result.statusCode());
                            return Mono.empty();
                        }
                    })
                    .block();
        } catch (Throwable t) {
            logger.error("error in updateExecution", t);
        }
        return flow;
    }

    /**
     * Create a new Identity Provider
     * @param host Keycloak address
     * @param token the SAT or UAT
     * @param realm the target realm
     * @param idp the new Identity Provider
     * @return true if the new IdP was created
     */
    protected boolean createIdentityProvider(String host, Token token, String realm, IdentityProvider idp) {
        final String REST_URI = encodePath(host + "/admin/realms/" + realm + "/identity-provider/instances");
        WebClient client = WebClient.create();
        Boolean created = false;
        try {
            created =
                client
                    .post()
                    .uri(new URI(REST_URI))
                    .header("Authorization", "Bearer " + token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(idp))
                    .exchangeToMono(result -> {
                        if (result.statusCode()
                            .equals(HttpStatus.CREATED)) {
                            return Mono.just(true);
                        } else {
                            logger.error("Unexpected status: {}" , result.statusCode());
                            return Mono.just(false);
                        }
                    })
                    .block();
        } catch (Throwable t) {
            logger.error("error in createIdentityProvider", t);
        }
        return created;
    }

    protected List<IdentityProvider> getIdentityProviders(String host, Token token, String realm) {
        final String REST_URI = encodePath(host + "/admin/realms/" + realm + "/identity-provider/instances");
        WebClient client = WebClient.create();
        IdentityProvider[] providers = new IdentityProvider[0];

        try {
            providers = client
                .get()
                .uri(new URI(REST_URI))
                .header("Authorization", "Bearer " + token.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(result -> {
                    if (result.statusCode()
                        .equals(HttpStatus.OK)) {
                        return result.bodyToMono(IdentityProvider[].class);
                    } else {
                        logger.error("Unexpected status: {}" , result.statusCode());
                        return Mono.empty();
                    }
                })
                .block();
        } catch (Throwable t) {
            logger.error("error in createIdentityProvider", t);
        }
        return Arrays.asList(providers);
    }

    /**
     * Add a mapper fpr the username, different from the others
     * @param host Keycloak address
     * @param token the SAT or UAT
     * @param realm the target realm
     * @param idpAlias the name (alias) of the provider
     * @return true if the mapper was created
     */
    protected boolean addMapperUsername(String host, Token token, String realm, String idpAlias) {
        String payload = USERNAME_MAPPER_CFG
            .replace("_IDP_ALIAS_", idpAlias);
        return addMapperElement(host, token, realm, idpAlias, payload);
    }

    protected boolean addMapperGeneric(String host, Token token, String realm, String idpAlias, String name, String attributeName, String userAttributeName) {
        String payload = ATTRIBUTE_MAPPER_CFG
            .replace("_IDP_ALIAS_", idpAlias)
            .replace("_ATTRIBUTE_NAME_", attributeName)
            .replace("_USER_ATTRIBUTE_", userAttributeName)
            .replace("_NAME_", name);
        return addMapperElement(host, token, realm, idpAlias, payload);
    }

    /**
     * Add a generic mapper element (other than username)
     * @param host Keycloak address
     * @param token the SAT or UAT
     * @param realm the target realm
     * @param idpAlias the name (alias) of the provider
     * @param payload the mapper (string containing a JSON)
     * @return true if the mapper was created
     */
    private boolean addMapperElement(String host, Token token, String realm, String idpAlias, String payload) {
        final String REST_URI = encodePath(host + "/admin/realms/" + realm + "/identity-provider/instances/" + idpAlias + "/mappers");
        Boolean created = false;
        WebClient client = WebClient.create();

        try {
            created =
                client
                    .post()
                    .uri(new URI(REST_URI))
                    .header("Authorization", "Bearer " + token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(payload))
                    .exchangeToMono(result -> {
                        if (result.statusCode()
                            .equals(HttpStatus.CREATED)) {
                            return Mono.just(true);
                        } else {
                            logger.error("Unexpected status: {}" , result.statusCode());
                            return Mono.just(false);
                        }
                    })
                    .block();
        } catch (Throwable t) {
            logger.error("error in addMapperElement", t);
        }
        return created;
    }

    /**
     * Delete the desired provider
     * @param host Keycloak address
     * @param token the SAT or UAT
     * @param realm the target realm
     * @param idpAlias the name (alias) of the provider
     * @return
     */
    private boolean deleteProvider(String host, Token token, String realm, String idpAlias) {
        final String REST_URI = encodePath(host + "/admin/realms/" + realm + "/identity-provider/instances/" + idpAlias);
        Boolean deleted = false;
        WebClient client = WebClient.create();

        try {
            deleted =
                client
                    .delete()
                    .uri(new URI(REST_URI))
                    .header("Authorization", "Bearer " + token.getAccessToken())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(result -> {
                        if (result.statusCode()
                            .equals(HttpStatus.NO_CONTENT)) {
                            return Mono.just(true);
                        } else {
                            logger.error("Unexpected status: {}" , result.statusCode());
                            return Mono.just(false);
                        }
                    })
                    .block();
        } catch (Throwable t) {
            logger.error("error in deleteProvider", t);
        }
        return deleted;
    }

    /**
     *
     * @param host Keycloak address
     * @param token the SAT or UAT
     * @param realm the target realm
     * @param start (for pagination) initial element to query for
     * @return the array of clients
     */
    protected Client[] getClients(String host, Token token, String realm, int start) {
        Client[] clients = null;
        final String REST_URI = host + "/admin/realms/" + realm + "/clients?first=" + start + "&max=20";
        WebClient client = WebClient.create();

        try {
            clients = client
                .get()
                .uri(new URI(REST_URI))
                .header("Authorization", "Bearer " + token.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(result -> {
                    if (result.statusCode()
                        .equals(HttpStatus.OK)) {
                        return result.bodyToMono(Client[].class);
                    } else {
                        logger.error("Unexpected status: {}" , result.statusCode());
                        return Mono.empty();
                    }
                })
                .block();
        } catch (Throwable t) {
            logger.error("error in getExecutions", t);
        }
        return clients;
    }

    /**
     * Include the user attributes in the JWT body
     * @param host Keycloak address
     * @param token the SAT or UAT
     * @param clientId the ID of the Keycloak client to modify
     * @param payload the JSON string representing the mapper to add
     * @return true if the JWT body was modified, false otherwise
     */
    protected boolean createJwtMapping(String host, Token token, String realm, String clientId, String payload) {
        final String REST_URI = encodePath(host + "/admin/realms/" + realm + "/clients/" + clientId + "/protocol-mappers/models");
        WebClient client = WebClient.create();
        Boolean created = false;

        try {
            created =
                client.post()
                    .uri(new URI(REST_URI))
                    .header("Authorization", "Bearer " + token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(payload))
                    .exchangeToMono(result -> {
                        if (result.statusCode()
                            .equals(HttpStatus.CREATED)) {
                            return Mono.just(true);
                        } else {
                            logger.error("Unexpected status: {}" , result.statusCode());
                            return Mono.just(false);
                        }
                    })
                    .block();
        } catch (Throwable t) {
            logger.error("error in updateExecution", t);
        }
        return created;
    }

    /**
     * Include the user attributes in the JWT body
     * @param host Keycloak address
     * @param token the SAT or UAT
     * @param realm the target realm
     * @param clientId the ID of the Keycloak client to modify
     * @param attribute the attribute to map to JWT
     * @return true if the JWT body was modified, false otherwise
     */
    protected boolean createJwtMapping(String host, Token token, String realm, String clientId, MapperAttribute attribute) {
        String payload = JWT_MAPPER_ATTRIBUTE
            .replace("_ATTRIBUTE_NAME_", attribute.getAttributeName());
        return  createJwtMapping(host, token, realm, clientId, payload);
    }

    /**
     * Find the execution given its display name
     * @param executions the array of executions
     * @param displayName the name to look for
     * @return the desired execution
     */
    protected Optional<Execution> findExecution(Execution[] executions, String displayName) {
        return Arrays.stream(executions)
            //      .peek(e -> System.out.println(">?> " + e.getDisplayName()))
            .filter(e -> e.getDisplayName().equals(displayName))
            .findFirst();
    }

    private String encodePath(String path) {
        path = UriUtils.encodePath(path, "UTF-8");
        return path;
    }

    // templates for mappers
    private static final String USERNAME_MAPPER_CFG =
        "{\n" +
            "   \"identityProviderAlias\":\"_IDP_ALIAS_\",\n" +
            "   \"config\":{\n" +
            "      \"syncMode\":\"INHERIT\",\n" +
            "      \"template\":\"${ATTRIBUTE.fiscalNumber}\"\n" +
            "   },\n" +
            "   \"name\":\"User Name\",\n" +
            "   \"identityProviderMapper\":\"spid-saml-username-idp-mapper\"\n" +
            "}";

    private static final String ATTRIBUTE_MAPPER_CFG =
        "{\n" +
            "   \"identityProviderAlias\":\"_IDP_ALIAS_\",\n" +
            "   \"config\":{\n" +
            "      \"syncMode\":\"INHERIT\",\n" +
            "      \"attribute.name\":\"_ATTRIBUTE_NAME_\",\n" +
            "      \"user.attribute\":\"_USER_ATTRIBUTE_\"\n" +
            "   },\n" +
            "   \"name\":\"_NAME_\",\n" +
            "   \"identityProviderMapper\":\"spid-user-attribute-idp-mapper\"\n" +
            "}";

    private static final String JWT_MAPPER_ATTRIBUTE = "{\n" +
        "  \"protocol\": \"openid-connect\",\n" +
        "  \"config\": {\n" +
        "    \"id.token.claim\": \"false\",\n" +
        "    \"access.token.claim\": \"true\",\n" +
        "    \"userinfo.token.claim\": \"true\",\n" +
        "    \"multivalued\": \"\",\n" +
        "    \"aggregate.attrs\": \"\",\n" +
        "    \"jsonType.label\": \"String\",\n" +
        "    \"user.attribute\": \"_ATTRIBUTE_NAME_\",\n" +
        "    \"claim.name\": \"spid._ATTRIBUTE_NAME_\"\n" +
        "  },\n" +
        "  \"name\": \"spid._ATTRIBUTE_NAME_\",\n" +
        "  \"protocolMapper\": \"oidc-usermodel-attribute-mapper\"\n" +
        "}";
}
