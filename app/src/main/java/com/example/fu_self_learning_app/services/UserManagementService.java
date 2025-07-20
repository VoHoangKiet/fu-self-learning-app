package com.example.fu_self_learning_app.services;

import com.example.fu_self_learning_app.models.UserInfo;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface UserManagementService {
    @GET("v1/admin/users")
    Call<List<UserInfo>> getAllUsers();

    @PATCH("v1/admin/ban/{userId}")
    Call<ResponseBody> banUser(@Path("userId") int userId);

    @PATCH("v1/admin/unban/{userId}")
    Call<ResponseBody> unbanUser(@Path("userId") int userId);
}
