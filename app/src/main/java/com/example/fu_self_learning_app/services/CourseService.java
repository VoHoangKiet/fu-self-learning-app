package com.example.fu_self_learning_app.services;

import com.example.fu_self_learning_app.models.Course;
import com.example.fu_self_learning_app.models.Topic;
import com.example.fu_self_learning_app.models.request.EnrollmentRequest;
import com.example.fu_self_learning_app.models.response.CourseDetailResponse;
import com.example.fu_self_learning_app.models.response.EnrollmentCheckResponse;
import com.example.fu_self_learning_app.models.response.EnrollmentResponse;
import com.example.fu_self_learning_app.models.response.OrderResponse;
import com.example.fu_self_learning_app.models.request.OrderRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

// Interface định nghĩa các API endpoints cho khóa học
// Authorization header sẽ được tự động thêm bởi AuthInterceptor
public interface CourseService {
    
    @GET("v1/courses") // API endpoint để lấy danh sách tất cả khóa học
    Call<List<Course>> getAllCourses();
    
    @GET("v1/courses") // API endpoint để lấy danh sách khóa học với phân trang
    Call<List<Course>> getCourses(@Query("page") int page, @Query("limit") int limit);
    
    @GET("v1/courses/{id}") // API endpoint để lấy thông tin chi tiết một khóa học
    Call<CourseDetailResponse> getCourseDetail(@Path("id") int courseId);
    
    @GET("v1/courses/category/{category}") // API endpoint để lấy khóa học theo danh mục
    Call<List<Course>> getCoursesByCategory(@Path("category") String category);
    
    @GET("v1/courses/search") // API endpoint để tìm kiếm khóa học
    Call<List<Course>> searchCourses(@Query("q") String query);
    
    @GET("v1/enrollments/course/{courseId}/check") // API endpoint để check enrollment
    Call<EnrollmentCheckResponse> checkEnrollment(@Path("courseId") int courseId);
    
    @POST("v1/enrollments") // API endpoint để enroll course
    Call<EnrollmentResponse> enrollCourse(@Body EnrollmentRequest request);
    
    @GET("v1/courses/{courseId}/topics") // API endpoint để lấy topics và lessons của course
    Call<List<Topic>> getCourseTopics(@Path("courseId") int courseId);

    @POST("v1/orders/create/{courseId}") // API endpoint để tạo order cho confirm payment
    Call<OrderResponse> createOrder(@Path("courseId") int courseId, @Body OrderRequest request);
} 