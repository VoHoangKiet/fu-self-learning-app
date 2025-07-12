package com.example.fu_self_learning_app.services;

import com.example.fu_self_learning_app.models.Post;
import com.example.fu_self_learning_app.models.Comment;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface SocialService {
    @GET("v1/posts")
    Call<List<Post>> getAllPosts();

    @GET("v1/posts/{id}")
    Call<Post> getPostDetail(@Path("id") int postId);

    @POST("v1/posts")
    Call<Post> createPost(@Body Post post);

    @Multipart
    @POST("v1/posts")
    Call<Post> createPostWithImages(
        @Part("title") RequestBody title,
        @Part("body") RequestBody body,
        @Part("status") RequestBody status,
        @Part List<MultipartBody.Part> images
    );

    // API đơn giản để test
    @POST("v1/posts")
    Call<Post> createSimplePost(@Body Post post);

    @POST("v1/posts/{id}/comments")
    Call<Comment> createComment(@Path("id") int postId, @Body Comment comment);

    @POST("v1/posts/{id}/like")
    Call<Void> likePost(@Path("id") int postId);
} 