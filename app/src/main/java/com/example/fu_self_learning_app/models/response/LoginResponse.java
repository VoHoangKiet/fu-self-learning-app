package com.example.fu_self_learning_app.models.response;

import com.example.fu_self_learning_app.models.UserInfo;

// model định nghĩa kiểu data được server gửi về
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private UserInfo userInfo;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
