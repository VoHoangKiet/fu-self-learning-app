
package com.example.fu_self_learning_app.services;

import com.example.fu_self_learning_app.models.request.ChangePasswordRequest;
import com.example.fu_self_learning_app.models.request.ForgotPasswordRequest;
import com.example.fu_self_learning_app.models.request.ResetPasswordRequest;
import com.example.fu_self_learning_app.models.request.UserProfileRequest;
import com.example.fu_self_learning_app.models.response.UserProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.POST;
import retrofit2.http.Header;
public interface UserService {

    @GET("v1/users/me")
    Call<UserProfileResponse> getProfile(@Header("Authorization") String token);

    @PUT("v1/users/me")
    Call<UserProfileResponse> updateProfile(
            @Header("Authorization") String token,
            @Body UserProfileRequest request
    );

    @POST("v1/users/change-password")
    Call<UserProfileResponse> changePassword(
            @Header("Authorization") String token,
            @Body ChangePasswordRequest request
    );

    @POST("v1/users/forgot-password")
    Call<Boolean> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("v1/users/update-forgot-password")
    Call<Boolean> resetForgotPassword(@Body ResetPasswordRequest request);
}
