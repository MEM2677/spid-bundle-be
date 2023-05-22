package com.entando.spid.service.impl;

import com.entando.spid.config.ApplicationProperties;
import com.entando.spid.domain.Organization;
import com.entando.spid.service.ConfigurationService;
import com.entando.spid.service.KeycloakService;
import com.entando.spid.service.dto.ConnectionClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationServiceImpl implements ConfigurationService {

    private final Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

    private ApplicationProperties config;

    @Autowired
    private KeycloakService keycloakService;

    public ConfigurationServiceImpl(ApplicationProperties config) {
        this.config = config;
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
        return new Organization(config);
    }

    @Override
    public void updateConfiguration(ApplicationProperties config) {
        this.config = config;
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

}
