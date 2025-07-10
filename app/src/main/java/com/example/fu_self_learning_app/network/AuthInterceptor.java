package com.example.fu_self_learning_app.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Get token from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("Auth", Context.MODE_PRIVATE);
        String token = prefs.getString("access_token", null);
        System.out.println("token: " + token);
        // If no token available, proceed with original request
        if (token == null || token.isEmpty()) {
            return chain.proceed(originalRequest);
        }

        // Add Authorization header with Bearer token
        Request.Builder requestBuilder = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        Request requestWithAuth = requestBuilder.build();
        return chain.proceed(requestWithAuth);
    }
} 