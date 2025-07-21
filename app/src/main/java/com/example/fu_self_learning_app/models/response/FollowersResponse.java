package com.example.fu_self_learning_app.models.response;

import com.example.fu_self_learning_app.models.FollowerUser;
import java.util.List;

// Response cho API /follow/followers
public class FollowersResponse {
    private List<FollowerUser> followers;
    private String message;
    private boolean success;

    // Getters
    public List<FollowerUser> getFollowers() {
        return followers;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
