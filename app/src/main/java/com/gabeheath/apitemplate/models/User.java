package com.gabeheath.apitemplate.models;

/**
 * Created by gabeheath on 2/11/17.
 */

public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String passwordConfirmation;

    public User(String firstName, String lastName, String username, String email, String password, String passwordConfirmation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }
}
