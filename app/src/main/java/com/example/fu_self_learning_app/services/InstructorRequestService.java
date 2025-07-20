package com.example.fu_self_learning_app.services;

import com.example.fu_self_learning_app.models.InstructorRequest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface InstructorRequestService {
    @Multipart
    @POST("v1/instructor-requests")
    Call<ResponseBody> uploadPdf(@Part MultipartBody.Part pdfFile);

    @GET("v1/instructor-requests")
    Call<List<InstructorRequest>> viewAll();

    @GET("v1/instructor-requests/{id}/pdf")
    Call<ResponseBody> streamPdf(@Path("id") int id);

    @PATCH("v1/instructor-requests/{id}/approve")
    Call<ResponseBody> approveRequest(@Path("id") int id);

    @PATCH("v1/instructor-requests/{id}/reject")
    Call<ResponseBody> rejectRequest(@Path("id") int id);

}
