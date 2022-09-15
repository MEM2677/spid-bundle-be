package com.entando.spid.service;

import com.entando.spid.config.ApplicationProperties;
import com.entando.spid.domain.Organization;
import com.entando.spid.service.dto.ConnectionClient;

public interface ConfigurationService {

    /**
     * Get connection info from the configuration
     * @return
     */
    ConnectionClient getConnection();

    /**
     * Export the configuration
     * @return
     */
    ApplicationProperties getConfiguration();

    /**
     * Return the organization properties from the configuration in a DTO
     * @return
     */
    Organization getOrganization();

    void updateConfiguration(ApplicationProperties config);
}
