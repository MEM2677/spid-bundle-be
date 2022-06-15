package com.entando.spid.domain.keycloak;

/*
{
        "alias": "spid-l1-test",
        "displayName": "SPID L1 TEST",
        "providerId": "spid",
        "enabled": true,
        "updateProfileFirstLoginMode": "on",
        "trustEmail": true,
        "storeToken": false,
        "addReadTokenRoleOnCreate": false,
        "authenticateByDefault": false,
        "linkOnly": false,
        "firstBrokerLoginFlowAlias": "{{new_flow}}",
        "config": {

        }
    }
 */

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdentityProvider {

  private String alias;
  private String displayName;
  private String providerId;
  private String enabled;
  private String updateProfileFirstLoginMode;
  private String trustEmail;
  private String storeToken;
  private String addReadTokenRoleOnCreate;
  private String authenticateByDefault;
  private String linkOnly;
  private String firstBrokerLoginFlowAlias;
  private IdentityProviderConfig config;

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getProviderId() {
    return providerId;
  }

  public void setProviderId(String providerId) {
    this.providerId = providerId;
  }

  public String getEnabled() {
    return enabled;
  }

  public void setEnabled(String enabled) {
    this.enabled = enabled;
  }

  public String getUpdateProfileFirstLoginMode() {
    return updateProfileFirstLoginMode;
  }

  public void setUpdateProfileFirstLoginMode(String updateProfileFirstLoginMode) {
    this.updateProfileFirstLoginMode = updateProfileFirstLoginMode;
  }

  public String getTrustEmail() {
    return trustEmail;
  }

  public void setTrustEmail(String trustEmail) {
    this.trustEmail = trustEmail;
  }

  public String getStoreToken() {
    return storeToken;
  }

  public void setStoreToken(String storeToken) {
    this.storeToken = storeToken;
  }

  public String getAddReadTokenRoleOnCreate() {
    return addReadTokenRoleOnCreate;
  }

  public void setAddReadTokenRoleOnCreate(String addReadTokenRoleOnCreate) {
    this.addReadTokenRoleOnCreate = addReadTokenRoleOnCreate;
  }

  public String getAuthenticateByDefault() {
    return authenticateByDefault;
  }

  public void setAuthenticateByDefault(String authenticateByDefault) {
    this.authenticateByDefault = authenticateByDefault;
  }

  public String getLinkOnly() {
    return linkOnly;
  }

  public void setLinkOnly(String linkOnly) {
    this.linkOnly = linkOnly;
  }

  public String getFirstBrokerLoginFlowAlias() {
    return firstBrokerLoginFlowAlias;
  }

  public void setFirstBrokerLoginFlowAlias(String firstBrokerLoginFlowAlias) {
    this.firstBrokerLoginFlowAlias = firstBrokerLoginFlowAlias;
  }

  public IdentityProviderConfig getConfig() {
    return config;
  }

  public void setConfig(IdentityProviderConfig config) {
    this.config = config;
  }

}
