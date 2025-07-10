package com.example.fu_self_learning_app.services;

import com.example.fu_self_learning_app.models.Course;
import com.example.fu_self_learning_app.models.response.CourseDetailResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
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
} 