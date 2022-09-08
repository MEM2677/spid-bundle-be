package com.entando.spid.service;

import com.entando.spid.config.ApplicationProperties;
import com.entando.spid.service.dto.ConnectionInfo;

public interface ConfigurationService {

    /**
     * Get connection info from the configuration
     * @return
     */
    ConnectionInfo getConnection();

    /**
     * Export the configuration
     * @return
     */
    ApplicationProperties getConfiguration();

    String getKeycloakAuthUrl();

}
