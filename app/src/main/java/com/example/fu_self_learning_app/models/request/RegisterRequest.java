package com.example.fu_self_learning_app.models.request;

// model định nghĩa kiểu data gửi đến server
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    public RegisterRequest(String username, String email, String password, String confirmPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}
