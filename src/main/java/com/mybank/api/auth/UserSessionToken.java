package com.mybank.api.auth;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class UserSessionToken {

    @Id
    private String username;
    private String token;

    public UserSessionToken(String username, String token) {
        this.username = username;
        this.token = token;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
