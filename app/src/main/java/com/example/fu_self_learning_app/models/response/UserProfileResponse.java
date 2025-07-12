package com.example.fu_self_learning_app.models.response;

import java.util.Date;

public class UserProfileResponse {
    private int id;
    private String email;
    private String username;
    private Date dob;
    private String avatarUrl;
    private String role;
    private String phoneNumber;
    private Date createdAt;
    private Date updatedAt;

    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public Date getDob() { return dob; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getRole() { return role; }
    public String getPhoneNumber() { return phoneNumber; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
}
