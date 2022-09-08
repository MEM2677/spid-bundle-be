package com.entando.spid.service;

import com.entando.spid.service.dto.ConnectionInfo;
import org.springframework.scheduling.annotation.Scheduled;

public interface KeycloakService {

    /**
     * Configure Keycloak for SPID. Errors will shut down the service!
     */
    void configure();

    /**
     * Revert the configuration
     * @param connection Keycloak instance to modify
     * @return true if the revert operation went smoothly; false means that errors happened
     * while processing; check the log for details
     */
    boolean revertConfiguration(ConnectionInfo connection);
}
