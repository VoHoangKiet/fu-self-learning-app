package com.example.fu_self_learning_app.models;

import java.util.Date;

// Model đại diện cho response từ API /follow/followers
public class FollowerUser {
    private int id;
    private UserDetails followingUser;
    private Date createdAt;
    private Date updatedAt;

    // Nested class cho user details
    public static class UserDetails {
        private int id;
        private String username;
        private String email;
        private String phoneNumber;
        private Date dob;
        private String password;
        private boolean isActive;
        private String avatarUrl;
        private String role;
        private Date createdAt;
        private Date updatedAt;

        // Getters for UserDetails
        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public Date getDob() {
            return dob;
        }

        public String getPassword() {
            return password;
        }

        public boolean isActive() {
            return isActive;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public String getRole() {
            return role;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public Date getUpdatedAt() {
            return updatedAt;
        }
    }

    // Getters for FollowerUser
    public int getId() {
        return id;
    }

    public UserDetails getFollowingUser() {
        return followingUser;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    // Convenience methods để lấy thông tin user
    public String getUsername() {
        return followingUser != null ? followingUser.getUsername() : null;
    }

    public String getEmail() {
        return followingUser != null ? followingUser.getEmail() : null;
    }

    public String getAvatarUrl() {
        return followingUser != null ? followingUser.getAvatarUrl() : null;
    }

    public String getRole() {
        return followingUser != null ? followingUser.getRole() : null;
    }

    public int getUserId() {
        return followingUser != null ? followingUser.getId() : 0;
    }
}
