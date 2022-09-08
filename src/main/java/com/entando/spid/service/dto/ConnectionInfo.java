package com.entando.spid.service.dto;

import org.apache.commons.lang3.StringUtils;

/**
 * This DTO contains everything needed to connect with a Keycloak instance
 */
public class ConnectionInfo {

    private String username;
    private String password;
    private String host;
    private String realm;

    public ConnectionInfo(String host) {
        this.host = host;
    }

    public void setLogin(String username, String password) {
        try {
//      this.password = new String(Base64.getDecoder().decode(password));
//      this.username = new String(Base64.getDecoder().decode(username));
            this.password = password;
            this.username = username;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getCLientId() {
        return username;
    }

    public String getClientSecret() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isValid() {
        return (StringUtils.isNotBlank(host) && StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password));
    }
}
