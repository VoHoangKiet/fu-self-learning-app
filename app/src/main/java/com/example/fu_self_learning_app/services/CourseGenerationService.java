package com.example.fu_self_learning_app.services;

import com.example.fu_self_learning_app.models.Course;
import com.example.fu_self_learning_app.models.request.CreateTopicRequest;
import com.example.fu_self_learning_app.models.response.CourseDetailResponse;
import com.example.fu_self_learning_app.models.response.CourseGenerationResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface CourseGenerationService {
    @Multipart
    @POST("v1/courses/generate-from-pdf")
    Call<CourseGenerationResponse> generateCourseFromPdf(@Part MultipartBody.Part pdfFile);

//    @Multipart
//    @POST("v1/courses/create-with-structure")
//    Call<ResponseBody> createCourseWithStructure(
//            @Part("course") RequestBody course,
//            @Part("topics") RequestBody topics,
//            @Part MultipartBody.Part thumbnail, // Pass null if the file is optional
//            @Part MultipartBody.Part videoIntro   // Pass null if the file is optional
//    );

    @Multipart
    @POST("v1/courses")
    Call<CourseDetailResponse> createCourse(
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("categoryIds") List<Integer> categoryIds,
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part video,
            @Part MultipartBody.Part document
    );

    @POST("v1/courses/{courseId}/topics")
    Call<ResponseBody> createTopicInstructor(
            @Path("courseId") int courseId,
            @Body CreateTopicRequest createTopic
    );
}
