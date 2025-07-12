package com.example.fu_self_learning_app.models.request;

public class UserProfileRequest {
    private String username;
    private String phoneNumber;
    private String avatarUrl;
    private String email; // 👈 Thêm trường email

    public UserProfileRequest(String username, String phoneNumber, String avatarUrl, String email) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
        this.email = email; // 👈 Gán giá trị email
    }

    public String getUsername() { return username; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getEmail() { return email; } // 👈 Getter email
}
