package com.entando.spid.domain;

import com.entando.spid.domain.keycloak.IdentityProvider;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceStatus {

    // true if one provider and the authentication flow is present
    private boolean installed;
    private boolean authenticationFlowPresent;
    private List<String> providers = new ArrayList<>();

    public ServiceStatus(List<IdentityProvider> providers, Boolean flow) {
        this.authenticationFlowPresent = flow;

        if (providers != null && !providers.isEmpty()) {
            this.providers = providers.stream()
                .map(IdentityProvider::getAlias)
                .collect(Collectors.toList());
        }
        installed = (flow && providers != null && providers.size() > 0);
    }


    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public List<String> getProviders() {
        return providers;
    }

    public void setProviders(List<String> providers) {
        this.providers = providers;
    }

    public boolean isAuthenticationFlowPresent() {
        return authenticationFlowPresent;
    }

    public void setAuthenticationFlowPresent(boolean authenticationFlowPresent) {
        this.authenticationFlowPresent = authenticationFlowPresent;
    }
}
