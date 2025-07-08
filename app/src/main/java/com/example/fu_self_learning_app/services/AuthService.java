package com.example.fu_self_learning_app.services;

import com.example.fu_self_learning_app.models.request.LoginRequest;
import com.example.fu_self_learning_app.models.request.RegisterRequest;
import com.example.fu_self_learning_app.models.response.LoginResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

// interface định nghĩa các API cần gọi
public interface AuthService {
    @POST("v1/auth/register") // API endpoint
    Call<ResponseBody> register(@Body RegisterRequest request);
    // ResponseBody là kiểu dữ liệu trả về từ API, RegisterRequest là kiểu dữ liệu gửi lên server
    // dùng ResponseBody nếu không thể dùng GSON để convert kết quả response sang JSON
    // (hiện bên API đang trả về plain text)

    @POST("v1/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    // dùng interface định nghĩa trong models
    // GSON tự chuyển các Java Class thành JSON tương ứng
}
