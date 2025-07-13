package com.example.fu_self_learning_app.services;

import com.example.fu_self_learning_app.models.FollowerUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

// Interface cho Follow API
public interface FollowService {
    @GET("v1/follow/followers")
    Call<List<FollowerUser>> getFollowers(@Header("Authorization") String authorization);
}
