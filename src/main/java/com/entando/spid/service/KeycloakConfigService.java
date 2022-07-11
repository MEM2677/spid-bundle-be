package com.entando.spid.service;

import com.entando.spid.domain.keycloak.AuthenticationFlow;
import com.entando.spid.domain.keycloak.Execution;
import com.entando.spid.domain.keycloak.IdentityProvider;
import com.entando.spid.domain.keycloak.Token;
import com.entando.spid.service.dto.ConnectionInfo;
import com.entando.spid.service.dto.MapperAttribute;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static com.entando.spid.Constants.*;

@Component
public class KeycloakConfigService {

    private final Logger logger = LoggerFactory.getLogger(KeycloakConfigService.class);

    @Scheduled(fixedRate = Long.MAX_VALUE, initialDelay = 2000)
    public void configure() {
        Map<String, String> envVars = System.getenv();

        Boolean enabled = Boolean.parseBoolean(envVars.get("SPID_CONFIG_ACTIVE"));
        String host = envVars.get("DEFAULT_SSO_IN_NAMESPACE_SERVICE_PORT_8080_TCP_ADDR");
        String port = envVars.get("DEFAULT_SSO_IN_NAMESPACE_SERVICE_SERVICE_PORT");
        String proto = envVars.get("KEYCLOAK_PROTO");
        String username = envVars.get("KEYCLOAK_USERNAME");
        String password = envVars.get("KEYCLOAK_PASSWORD");

        if (!enabled) {
            logger.warn("Aborting Keycloak configuration as requested");
            return;
        }
        try {
            if (StringUtils.isBlank(proto)) {
                proto = DEFAULT_PROTO;
            }
            host = proto + "://" + host;
            if (StringUtils.isNotBlank(port)) {
                host = host + ":" + port;
            }
            ConnectionInfo connection = new ConnectionInfo(host);
            connection.setLogin(username, password);

            Token token = getAdminToken(connection);

            if (configureKeycloak(connection, token)) {
                logger.info("Host [{}] configuration complete", host);
            } else {
                logger.error("Host [{}] configuration failed", host);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    protected boolean configureKeycloak(ConnectionInfo info, Token token) {
        Execution[] executions;
        try {
            String host = info.getHost();
            // 1 - configure authentication flow
            if (!duplicateAuthFlow(host, token)) {
                logger.error("could not create authentication flow, aborting configuration");
                return false;
            }
            logger.info("Keycloak config: authorization flow created with name [{}]", KEYCLOAK_NEW_AUTH_FLOW_NAME);
            // 2 - add executable
            if (!addExecutable(host, token)) {
                logger.error("could not add execution to the authentication flow, aborting configuration");
                return false;
            }
            // 3 - get the id of the newly created execution
            executions = getExecutions(host, token);
            if (executions == null || executions.length == 0
                || !executions[executions.length - 1].getDisplayName().equals(KEYCLOAK_EXECUTION_EXPECTED_DISPLAY_NAME)) {
                logger.error("could not obtain the execution for the flow, aborting configuration");
                return false;
            }
            String id = executions[executions.length - 1].getId();
            logger.debug("Target execution ID is [{}] ");
            //  4 - move executable to its position
            for (int i = 0; i < 2; i++) {
                if (!raiseExecutionPriority(host, token, id)) {
                    logger.error("Could not raise the execution level of the target execution " + id);
                    return false;
                }
            }
            // 5 - edit requirements of the given executables
            // 5A - REQUIRED for Automatically "Set Existing User"
            if (!updateExecutionRequirement(host, token, executions, KEYCLOAK_EXECUTION_EXPECTED_DISPLAY_NAME, "REQUIRED")) {
                logger.error("Cannot find target execution [" + KEYCLOAK_EXECUTION_EXPECTED_DISPLAY_NAME + "], aborting setup");
                return false;
            }
            // 5B - DISABLED for "Confirm Link Existing Account"
            if (!updateExecutionRequirement(host, token, executions, KEYCLOAK_EXECUTION_CONFIRM_LINK_DISPLAY_NAME, "DISABLED")) {
                logger.error("Cannot find target execution [" + KEYCLOAK_EXECUTION_CONFIRM_LINK_DISPLAY_NAME + "], aborting setup");
                return false;
            }
            // 5C - DISABLED for "SPID first broker login Account verification options"
            if (!updateExecutionRequirement(host, token, executions, KEYCLOAK_EXECUTION_VERIFICATION_OPTIONS_DISPLAY_NAME, "DISABLED")) {
                logger.error("Cannot find target execution [" + KEYCLOAK_EXECUTION_VERIFICATION_OPTIONS_DISPLAY_NAME + "], aborting setup");
                return false;
            }
            logger.info("authentication flow successfully configured");

            // 6 - configure identity provider
            ObjectMapper objectMapper = new ObjectMapper();
            IdentityProvider idp = objectMapper.readValue(PUBLIC_TEST_IdP, IdentityProvider.class);
            if (!createIdentityProvider(host, token, idp)) {
                logger.error("Cannot configure the service provider [" + KEYCLOAK_IDP_DISPLAY_NAME + "], aborting setup");
                return false;
            }
            // 7 - create mapping for SPID profile
            if (!addMapperUsername(host, token)) {
                logger.error("Cannot configure the mapper for IdP [" + KEYCLOAK_IDP_ALIAS + "], aborting setup");
                return false;
            }
            for (MapperAttribute entry : KEYCLOAK_IDP_MAPPING) {
                logger.debug("configuring mapper {}", entry);
                if (!addMapperGeneric(host, token, entry.getName(), entry.getAttributeName(), entry.getUserAttributeName())) {
                    logger.error("Cannot configure the mapper for IdP [" + KEYCLOAK_IDP_ALIAS + "]:" + entry + ", aborting setup");
                    return false;
                }
            }
            logger.info("identity provider [{}] successfully configured", KEYCLOAK_IDP_DISPLAY_NAME);
        } catch (Throwable t) {
            logger.error("unexpected error in configureKeycloak", t);
        }
        return true;
    }

    /**
     *
     * @param connection
     * @return
     */
    protected Token getAdminToken(ConnectionInfo connection) {
        Token token = null;
        WebClient client = WebClient.create();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        final String REST_URI = connection.getHost() + "/auth/realms/master/protocol/openid-connect/token";

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
     *
     * @param host
     * @param token
     * @return
     */
    protected boolean duplicateAuthFlow(String host, Token token) {
        final String REST_URI = encodePath(
            host + "/auth/admin/realms/" + KEYCLOAK_DEFAULT_REALM + "/authentication/flows/" + KEYCLOAK_DEFAULT_AUTH_FLOW + "/copy"
        );
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
            logger.error("error in duplicateAuthFlow", t);
        }
        return created;
    }

    /**
     *
     * @param host
     * @param token
     * @return
     */
    protected boolean addExecutable(String host, Token token) {
        final String REST_URI = encodePath(host + "/auth/admin/realms/" + KEYCLOAK_DEFAULT_REALM + "/authentication/flows/" + KEYCLOAK_EXECUTION_HANDLE_EXISTING_ACCOUNT_NAME + "/executions/execution");
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
     *
     * @param host
     * @param token
     * @return
     */
    protected Execution[] getExecutions(String host, Token token) {
        Execution[] executions = null;
        final String REST_URI = encodePath(
            host + "/auth/admin/realms/" + KEYCLOAK_DEFAULT_REALM + "/authentication/flows/" + KEYCLOAK_NEW_AUTH_FLOW_NAME + "/executions"
        );
        WebClient client = WebClient.create();

        try {
            executions = (Execution[]) client
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
     *
     * @param host
     * @param token
     * @param id
     * @return
     */
    protected boolean raiseExecutionPriority(String host, Token token, String id) {
        final String REST_URI = encodePath(
            host + "/auth/admin/realms/" + KEYCLOAK_DEFAULT_REALM + "/authentication/executions/" + id + "/raise-priority"
        );
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
     *
     * @param host
     * @param token
     * @param executions
     * @param executionName
     * @param requirement
     * @return
     */
    protected boolean updateExecutionRequirement(String host, Token token, Execution[] executions, String executionName, String requirement) {
        AuthenticationFlow updated = null;
        Optional<Execution> execOpt = findExecution(executions, executionName);

        if (execOpt.isPresent()) {
            Execution execution = execOpt.get();
            execution.setRequirement(requirement); // TODO constant
            updated = updateExecution(host, token, execution);
        } else {
            logger.error("Cannot find target execution " + executionName + ", aborting setup");
        }
        return updated != null;
    }

    /**
     *
     * @param host
     * @param token
     * @param execution
     * @return
     */
    protected AuthenticationFlow updateExecution(String host, Token token, Execution execution) {
        final String REST_URI = encodePath(
            host + "/auth/admin/realms/" + KEYCLOAK_DEFAULT_REALM + "/authentication/flows/" + KEYCLOAK_NEW_AUTH_FLOW_NAME + "/executions"
        );
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
     *
     * @param host
     * @param token
     * @param idp
     * @return
     */
    protected boolean createIdentityProvider(String host, Token token, IdentityProvider idp) {
        final String REST_URI = encodePath(host + "/auth/admin/realms/" + KEYCLOAK_DEFAULT_REALM + "/identity-provider/instances");
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

    /**
     *
     * @param host
     * @param token
     * @return
     */
    protected boolean addMapperUsername(String host, Token token) {
        return addMapperElement(host, token, USERNAME_MAPPER_CFG);
    }

    protected boolean addMapperGeneric(String host, Token token, String name, String attributeName, String userAttributeName) {
        String payload = ATTRIBUTE_MAPPER_CFG
            .replace("_ATTRIBUTE_NAME_", attributeName)
            .replace("_USER_ATTRIBUTE_", userAttributeName)
            .replace("_NAME_", name);
        return addMapperElement(host, token, payload);
    }

    /**
     *
     * @param host
     * @param token
     * @param payload
     * @return
     */
    private boolean addMapperElement(String host, Token token, String payload) {
        final String REST_URI = encodePath(
            host + "/auth/admin/realms/" + KEYCLOAK_DEFAULT_REALM + "/identity-provider/instances/" + KEYCLOAK_IDP_ALIAS + "/mappers"
        );
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
     *
     * @param executions
     * @param displayName
     * @return
     */
    protected Optional<Execution> findExecution(Execution[] executions, String displayName) {
        return Arrays
            .asList(executions)
            .stream()
            //      .peek(e -> System.out.println(">?> " + e.getDisplayName()))
            .filter(e -> e.getDisplayName().equals(displayName))
            .findFirst();
    }

    private String encodePath(String path) {
        path = UriUtils.encodePath(path, "UTF-8");
        return path;
    }

    // templates for mappers
    private static String USERNAME_MAPPER_CFG =
        "{\n" +
            "   \"identityProviderAlias\":\"" +
            KEYCLOAK_IDP_ALIAS +
            "\",\n" +
            "   \"config\":{\n" +
            "      \"syncMode\":\"INHERIT\",\n" +
            "      \"template\":\"${ATTRIBUTE.fiscalNumber}\"\n" +
            "   },\n" +
            "   \"name\":\"User Name\",\n" +
            "   \"identityProviderMapper\":\"spid-saml-username-idp-mapper\"\n" +
            "}";

    private static String ATTRIBUTE_MAPPER_CFG =
        "{\n" +
            "   \"identityProviderAlias\":\"" +
            KEYCLOAK_IDP_ALIAS +
            "\",\n" +
            "   \"config\":{\n" +
            "      \"syncMode\":\"INHERIT\",\n" +
            "      \"attribute.name\":\"_ATTRIBUTE_NAME_\",\n" +
            "      \"user.attribute\":\"_USER_ATTRIBUTE_\"\n" +
            "   },\n" +
            "   \"name\":\"_NAME_\",\n" +
            "   \"identityProviderMapper\":\"spid-user-attribute-idp-mapper\"\n" +
            "}";
}
