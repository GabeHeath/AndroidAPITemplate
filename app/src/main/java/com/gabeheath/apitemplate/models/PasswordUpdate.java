package com.gabeheath.apitemplate.models;

/**
 * Created by gabeheath on 2/26/17.
 */

public class PasswordUpdate {
    private String currentPassword;
    private String password;
    private String passwordConfirmation;

    public PasswordUpdate(String currentPassword, String password, String passwordConfirmation) {
        this.currentPassword = currentPassword;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }
}
