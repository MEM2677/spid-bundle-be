package com.entando.spid.service;

import com.entando.spid.domain.ServiceStatus;
import com.entando.spid.service.dto.ConnectionClient;

public interface KeycloakService {

    /**
     * Configure Keycloak for SPID. Errors will shut down the service!
     * This will initiate shutdown in case of error
     * @param connection Keycloak instance to modify
     * @return true if the configuration was successful, false otherwise
     */
    Boolean configureOrShutDown(ConnectionClient connection);

    /**
     * Revert the configuration
     * @param connection Keycloak instance to modify
     * @return true if the revert operation went smoothly; false means that errors happened
     * while processing; check the log for details
     */
    boolean revertConfiguration(ConnectionClient connection);

    ServiceStatus getStatus(ConnectionClient connection);

    /**
     * Configure a single Keycloak instance
     * @param connection parameters to connect to Keycloak
     * @return true if everything went well, false otherwise
     */
    boolean configure(ConnectionClient connection);
}
