package com.example.fu_self_learning_app.services;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface InstructorRequestService {
    @Multipart
    @POST("v1/instructor-requests")
    Call<ResponseBody> uploadPdf(@Part MultipartBody.Part pdfFile);
}
