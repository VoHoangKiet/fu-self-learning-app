package com.example.fu_self_learning_app.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// lớp quản lý kết nối tới server thông qua Retrofit
// cách dùng để tạo một service instance gọi API:
// AuthService authService = APIClient.getClient().create(AuthService.class);
public class APIClient {
    private static final String BASE_URL = "https://fu-self-learning-api-22235821035.asia-southeast1.run.app/api/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
