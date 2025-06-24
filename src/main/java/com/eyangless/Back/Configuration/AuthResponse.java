package com.eyangless.Back.Configuration;

public class AuthResponse {
    private String token;

    // Constructor, getter, and setter
    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

