package com.entando.spid.service.impl;

import com.entando.spid.config.ApplicationProperties;
import com.entando.spid.service.ConfigurationService;
import com.entando.spid.service.dto.ConnectionInfo;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationServiceImpl implements ConfigurationService {

    private final ApplicationProperties config;

    public ConfigurationServiceImpl(ApplicationProperties config) {
        this.config = config;
    }

    @Override
    public ConnectionInfo getConnection() {
        String authUrl = config.getKeycloakAuthUrl();

        // get rid of trailing slashes from URL
        if (authUrl.endsWith("/")) {
            authUrl = authUrl.substring(0, authUrl.length()- 1);
            config.setKeycloakAuthUrl(authUrl);
        }

        ConnectionInfo connection = new ConnectionInfo(authUrl);
        connection.setLogin(config.getKeycloakClientId(), config.getKeycloakClientSecret());
        connection.setRealm(config.getKeycloakRealm());
        return connection;
    }

    @Override
    public ApplicationProperties getConfiguration() {
        return config;
    }

    @Override
    public String getKeycloakAuthUrl() {
        return config.getKeycloakAuthUrl();
    }

}
