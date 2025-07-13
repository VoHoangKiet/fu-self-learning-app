package com.example.fu_self_learning_app.models.request;


public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    public ChangePasswordRequest(String currentPassword, String newPassword, String confirmPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
