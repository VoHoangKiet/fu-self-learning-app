package com.example.fu_self_learning_app.network;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// lớp quản lý kết nối tới server thông qua Retrofit
// cách dùng để tạo một service instance gọi API:
// AuthService authService = APIClient.getClient().create(AuthService.class);
public class APIClient {
    private static final String BASE_URL = "https://fu-self-learning-api-22235821035.asia-southeast1.run.app/api/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        return getClient(null);
    }

    public static Retrofit getClient(Context context) {
        if(retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // Add AuthInterceptor if context is provided
            if (context != null) {
                httpClient.addInterceptor(new AuthInterceptor(context));
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
