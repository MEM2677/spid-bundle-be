package com.entando.spid.domain.keycloak;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;

/*
{
    "access_token": "eyJfg",
    "expires_in": 60,
    "refresh_expires_in": 1800,
    "refresh_token": "eyX0",
    "token_type": "Bearer",
    "id_token": "ey2sBaA",
    "not-before-policy": 0,
    "session_state": "6b95b04c-2669-4427-9e1f-db4475de8d13",
    "scope": "openid email profile"
}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Token {

  private String accessToken;
  private Long expiresIn;
  private Long refreshExpiresIn;
  private String refreshToken;
  private String tokenType;
  private String idToken;
  private Long notBeforePolicy; // not-before-policy;
  private String sessionState;
  private String scope;

  public String getAccessToken() {
    return accessToken;
  }

  @JsonSetter("access_token")
  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public Long getExpiresIn() {
    return expiresIn;
  }

  @JsonSetter("expires_in")
  public void setExpiresIn(Long expiresIn) {
    this.expiresIn = expiresIn;
  }

  public Long getRefreshExpiresIn() {
    return refreshExpiresIn;
  }

  @JsonSetter("refresh_expires_in")
  public void setRefreshExpiresIn(Long refreshExpiresIn) {
    this.refreshExpiresIn = refreshExpiresIn;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  @JsonSetter("refresh_token")
  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  @JsonSetter("token_type")
  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public String getIdToken() {
    return idToken;
  }

  @JsonSetter("id_token")
  public void setIdToken(String idToken) {
    this.idToken = idToken;
  }

  public Long getNotBeforePolicy() {
    return notBeforePolicy;
  }

  @JsonSetter("not-before-policy")
  public void setNotBeforePolicy(Long notBeforePolicy) {
    this.notBeforePolicy = notBeforePolicy;
  }

  public String getSessionState() {
    return sessionState;
  }

  @JsonSetter("session_state")
  public void setSessionState(String sessionState) {
    this.sessionState = sessionState;
  }

  public String getScope() {
    return scope;
  }

  @JsonSetter("scope")
  public void setScope(String scope) {
    this.scope = scope;
  }
}
