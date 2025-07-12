package com.example.fu_self_learning_app.models.request;

public class ResetPasswordRequest {
    private String email;
    private String newPassword;
    private String confirmPassword;

    public ResetPasswordRequest(String email, String newPassword, String confirmPassword) {
        this.email = email;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
