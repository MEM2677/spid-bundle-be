package com.entando.spid.service.impl;

import com.entando.spid.ConfigUtils;
import com.entando.spid.config.ApplicationProperties;
import com.entando.spid.domain.Organization;
import com.entando.spid.service.ConfigurationService;
import com.entando.spid.service.KeycloakService;
import com.entando.spid.service.dto.ConnectionClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ConfigurationServiceImpl implements ConfigurationService {

    private final Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

    // this contains both organization data and infrastructure data
    private ApplicationProperties config;

    private Organization organization;

    @Autowired
    private KeycloakService keycloakService;

    public ConfigurationServiceImpl(ApplicationProperties config) {
        ObjectMapper objectMapper = new ObjectMapper();

        this.config = config;
        try {
            Path path = ConfigUtils.getOrganizationFilePath();
            // create the organization from the configuration
            this.organization = new Organization(config);
            // if this is the first run we create the local file that mirrors
            // the configuration from environment variables, otherwise we load
            // the organization data from the file
            if (Files.exists(path)) {
                logger.info("loading organization data from file {}", path);
                String json = ConfigUtils.readFile(path);
                this.organization = objectMapper.readValue(json, Organization.class);
            } else {
                saveOrganization(path, this.organization);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ConnectionClient getConnection() {
        String authUrl = config.getKeycloakAuthUrl();

        // get rid of trailing slashes from URL
        if (authUrl.endsWith("/")) {
            authUrl = authUrl.substring(0, authUrl.length()- 1);
            config.setKeycloakAuthUrl(authUrl);
        }

        ConnectionClient connection = new ConnectionClient(authUrl);
        connection.setLogin(config.getKeycloakClientId(), config.getKeycloakClientSecret());
        connection.setRealm(config.getKeycloakRealm());
        return connection;
    }

    @Override
    public ApplicationProperties getConfiguration() {
        return config;
    }

    @Override
    public Organization getOrganization() {
        return this.organization;
    }

    @Override
    public boolean updateConfiguration(Organization organization) {
        try {
            this.organization = organization;
            Path path = ConfigUtils.getOrganizationFilePath();
            saveOrganization(path, organization);
            return true;
        } catch (Throwable t) {
            logger.error("error updating organization properties", t);
        }
        return false;
    }

    @Scheduled(fixedRate = Long.MAX_VALUE, initialDelay = 2000)
    public void trampoline() {
        if (config.getSpidConfigActive()) {
            logger.debug("Launching automatic configuration");
            ConnectionClient connection = getConnection();
            keycloakService.configureOrShutDown(connection);
        } else {
            logger.warn("Skipping Keycloak configuration as requested");
        }
    }

    protected void saveOrganization(Path path, Organization organization) throws Throwable {
        ObjectMapper objectMapper = new ObjectMapper();

        if (path != null && organization != null) {
            String json = objectMapper.writeValueAsString(organization);
            ConfigUtils.writeFile(path, json);
        } else {
            logger.error("cannot write to file " + path);
        }
    }

}
