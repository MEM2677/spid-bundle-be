package com.entando.spid.domain.keycloak;

import com.fasterxml.jackson.annotation.JsonInclude;

/*
 {
        "id": "ff44adc6-035d",
        "requirement": "REQUIRED",
        "displayName": "Review Profile",
        "alias": "review profile config",
        "requirementChoices": [
            "REQUIRED",
            "ALTERNATIVE",
            "DISABLED"
        ],
        "configurable": true,
        "providerId": "idp-review-profile",
        "authenticationConfig": "148c2a93-806a",
        "level": 0,
        "index": 0
    }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Execution {

  private String id;
  private String requirement;
  private String displayName;
  private String description;
  private String alias;
  private String[] requirementChoices;
  private Boolean configurable;
  private Boolean authenticationFlow;
  private String flowId;
  private String providerId;
  private String authenticationConfig;
  private Integer level;
  private Integer index;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRequirement() {
    return requirement;
  }

  public void setRequirement(String requirement) {
    this.requirement = requirement;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String[] getRequirementChoices() {
    return requirementChoices;
  }

  public void setRequirementChoices(String[] requirementChoices) {
    this.requirementChoices = requirementChoices;
  }

  public Boolean getConfigurable() {
    return configurable;
  }

  public void setConfigurable(Boolean configurable) {
    this.configurable = configurable;
  }

  public String getProviderId() {
    return providerId;
  }

  public void setProviderId(String providerId) {
    this.providerId = providerId;
  }

  public String getAuthenticationConfig() {
    return authenticationConfig;
  }

  public void setAuthenticationConfig(String authenticationConfig) {
    this.authenticationConfig = authenticationConfig;
  }

  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  public Integer getIndex() {
    return index;
  }

  public void setIndex(Integer index) {
    this.index = index;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getAuthenticationFlow() {
    return authenticationFlow;
  }

  public void setAuthenticationFlow(Boolean authenticationFlow) {
    this.authenticationFlow = authenticationFlow;
  }

  public String getFlowId() {
    return flowId;
  }

  public void setFlowId(String flowId) {
    this.flowId = flowId;
  }
}
