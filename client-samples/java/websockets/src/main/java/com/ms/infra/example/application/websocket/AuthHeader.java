package com.ms.infra.example.application.websocket;

public class AuthHeader {
    public final String token;
    public AuthHeader (String token) {
        this.token = token;
    }

    public String getHeaderValue() {
        return "Bearer " + this.token;
    }
}
