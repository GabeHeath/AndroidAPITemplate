package com.gabeheath.apitemplate.models;

/**
 * Created by gabeheath on 2/9/17.
 */

public class Login {
    private String email;
    private String password;
    private String auth_token;

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getAuthToken() {
        return auth_token;
    }
}
