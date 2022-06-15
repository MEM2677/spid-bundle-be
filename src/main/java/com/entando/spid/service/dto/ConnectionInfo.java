package com.entando.spid.service.dto;

public class ConnectionInfo {

    private String username;
    private String password;
    private String host;

    public ConnectionInfo(String host) {
        this.host = host;
    }

    public void setLogin(String username, String password) {
        try {
//      this.password = new String(Base64.getDecoder().decode(password));
//      this.username = new String(Base64.getDecoder().decode(username));
            this.password = new String(password);
            this.username = new String(username);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
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
}
