package com.entando.spid.domain.keycloak;

/*
{
  "id": "552f2bd8-2448-47a6-ad7c-b25bc48733f9",
  "alias": "SPID first broker login",
  "description": "Actions taken after first broker login with identity provider account, which is not yet linked to any Keycloak account",
  "providerId": "basic-flow",
  "topLevel": true,
  "builtIn": false
}
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationFlow {

  private String id;
  private String alias;
  private String Description;
  private String providerId;
  private Boolean topLevel;
  private Boolean builtIn;
  @JsonIgnore
  private Object[] authenticationExecutions;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDescription() {
    return Description;
  }

  public void setDescription(String description) {
    Description = description;
  }

  public String getProviderId() {
    return providerId;
  }

  public void setProviderId(String providerId) {
    this.providerId = providerId;
  }

  public Boolean getTopLevel() {
    return topLevel;
  }

  public void setTopLevel(Boolean topLevel) {
    this.topLevel = topLevel;
  }

  public Boolean getBuiltIn() {
    return builtIn;
  }

  public void setBuiltIn(Boolean builtIn) {
    this.builtIn = builtIn;
  }

  public Object[] getAuthenticationExecutions() {
    return authenticationExecutions;
  }

  public void setAuthenticationExecutions(Object[] authenticationExecutions) {
    this.authenticationExecutions = authenticationExecutions;
  }
}
