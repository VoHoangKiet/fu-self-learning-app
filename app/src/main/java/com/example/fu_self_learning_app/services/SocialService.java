package com.example.fu_self_learning_app.services;

import com.example.fu_self_learning_app.models.Post;
import com.example.fu_self_learning_app.models.Comment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SocialService {
    @GET("v1/posts")
    Call<List<Post>> getAllPosts();

    @GET("v1/posts/{id}")
    Call<Post> getPostDetail(@Path("id") int postId);

    @POST("v1/posts")
    Call<Post> createPost(@Body Post post);

    @POST("v1/posts/{id}/comments")
    Call<Comment> createComment(@Path("id") int postId, @Body Comment comment);

    @POST("v1/posts/{id}/like")
    Call<Void> likePost(@Path("id") int postId);
} 