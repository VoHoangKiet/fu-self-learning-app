package com.example.fu_self_learning_app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.adapters.TopicAdapter;
import com.example.fu_self_learning_app.models.Category;
import com.example.fu_self_learning_app.models.Topic;
import com.example.fu_self_learning_app.models.request.EnrollmentRequest;
import com.example.fu_self_learning_app.models.response.CourseDetailResponse;
import com.example.fu_self_learning_app.models.response.EnrollmentCheckResponse;
import com.example.fu_self_learning_app.models.response.EnrollmentResponse;
import com.example.fu_self_learning_app.network.APIClient;
import com.example.fu_self_learning_app.services.CourseService;
import com.example.fu_self_learning_app.utils.PayOSHelper;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseDetailActivity extends AppCompatActivity implements TopicAdapter.OnTopicClickListener {

    public static final String EXTRA_COURSE_ID = "course_id";
    private static final String TAG = "CourseDetailActivity";

    // Views
    private ImageView imageViewCourseImage;
    private ImageView imageViewBack;
    private ImageView imageViewShare;
    private CardView cardViewPlayButton;
    private TextView textViewCourseTitle;
    private TextView textViewRating;
    private TextView textViewDuration;
    private TextView textViewLessons;
    private LinearLayout layoutCategories;
    private ImageView imageViewInstructorAvatar;
    private TextView textViewInstructorName;
    private TextView textViewInstructorEmail;
    private TextView textViewCourseDescription;
    private RecyclerView recyclerViewTopics;
    private TextView textViewPrice;
    private TextView textViewCreatedDate;
    private Button buttonEnrollNow;

    // Data
    private int courseId;
    private CourseDetailResponse courseDetail;
    private TopicAdapter topicAdapter;
    private CourseService courseService;
    private boolean isEnrolled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        // Get course ID from intent
        courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
        if (courseId == -1) {
            Toast.makeText(this, "Invalid course ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupServices();
        setupRecyclerView();
        setupListeners();
        loadCourseDetail();
        checkEnrollmentStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "CourseDetailActivity onResume - rechecking enrollment status");
        // Re-check enrollment status when returning from payment
        checkEnrollmentStatus();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "CourseDetailActivity onNewIntent");
        
        // Handle potential PayOS callback
        if (intent != null && intent.getData() != null) {
            String data = intent.getData().toString();
            Log.d(TAG, "Received intent data: " + data);
            
            if (PayOSHelper.isPayOSCallback(data)) {
                PayOSHelper.PaymentResult result = PayOSHelper.parsePaymentResult(data);
                handlePaymentResult(result);
            }
        }
    }

    private void handlePaymentResult(PayOSHelper.PaymentResult result) {
        Log.d(TAG, "Handling payment result: success=" + result.isSuccess() + ", message=" + result.getMessage());
        
        if (result.isSuccess()) {
            Toast.makeText(this, "Payment successful! You are now enrolled.", Toast.LENGTH_LONG).show();
            isEnrolled = true;
            updateEnrollmentButton();
        } else {
            Toast.makeText(this, "Payment failed: " + result.getMessage(), Toast.LENGTH_LONG).show();
        }
        
        // Re-check enrollment status from server
        checkEnrollmentStatus();
    }

    private void initViews() {
        imageViewCourseImage = findViewById(R.id.imageViewCourseImage);
        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewShare = findViewById(R.id.imageViewShare);
        cardViewPlayButton = findViewById(R.id.cardViewPlayButton);
        textViewCourseTitle = findViewById(R.id.textViewCourseTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewDuration = findViewById(R.id.textViewDuration);
        textViewLessons = findViewById(R.id.textViewLessons);
        layoutCategories = findViewById(R.id.layoutCategories);
        imageViewInstructorAvatar = findViewById(R.id.imageViewInstructorAvatar);
        textViewInstructorName = findViewById(R.id.textViewInstructorName);
        textViewInstructorEmail = findViewById(R.id.textViewInstructorEmail);
        textViewCourseDescription = findViewById(R.id.textViewCourseDescription);
        recyclerViewTopics = findViewById(R.id.recyclerViewTopics);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewCreatedDate = findViewById(R.id.textViewCreatedDate);
        buttonEnrollNow = findViewById(R.id.buttonEnrollNow);
    }

    private void setupServices() {
        courseService = APIClient.getClient().create(CourseService.class);
    }

    private void setupRecyclerView() {
        recyclerViewTopics.setLayoutManager(new LinearLayoutManager(this));
        topicAdapter = new TopicAdapter(new ArrayList<>());
        topicAdapter.setOnTopicClickListener(this);
        recyclerViewTopics.setAdapter(topicAdapter);
    }

    private void setupListeners() {
        imageViewBack.setOnClickListener(v -> finish());

        imageViewShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, courseDetail != null ? courseDetail.getTitle() : "Amazing Course");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this course: " + 
                (courseDetail != null ? courseDetail.getTitle() : "Amazing Course"));
            startActivity(Intent.createChooser(shareIntent, "Share Course"));
        });

        cardViewPlayButton.setOnClickListener(v -> {
            if (courseDetail != null && courseDetail.getVideoIntroUrl() != null && !courseDetail.getVideoIntroUrl().isEmpty()) {
                // Direct play với video player
                openVideoInPlayer(courseDetail.getVideoIntroUrl());
            } else {
                Toast.makeText(this, "No intro video available", Toast.LENGTH_SHORT).show();
            }
        });

        // Long click để hiển thị options
        cardViewPlayButton.setOnLongClickListener(v -> {
            if (courseDetail != null && courseDetail.getVideoIntroUrl() != null && !courseDetail.getVideoIntroUrl().isEmpty()) {
                openIntroVideo(courseDetail.getVideoIntroUrl());
                return true;
            }
            return false;
        });

        buttonEnrollNow.setOnClickListener(v -> {
            if (courseDetail != null) {
                handleEnrollmentAction();
            }
        });
    }

    private void loadCourseDetail() {
        Call<CourseDetailResponse> call = courseService.getCourseDetail(courseId);
        call.enqueue(new Callback<CourseDetailResponse>() {
            @Override
            public void onResponse(Call<CourseDetailResponse> call, Response<CourseDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    courseDetail = response.body();
                    populateUI();
                } else {
                    Log.e(TAG, "Failed to load course detail: " + response.code() + " " + response.message());
                    Toast.makeText(CourseDetailActivity.this, "Failed to load course details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CourseDetailResponse> call, Throwable t) {
                Log.e(TAG, "Error loading course detail", t);
                Toast.makeText(CourseDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkEnrollmentStatus() {
        Call<EnrollmentCheckResponse> call = courseService.checkEnrollment(courseId);
        call.enqueue(new Callback<EnrollmentCheckResponse>() {
            @Override
            public void onResponse(Call<EnrollmentCheckResponse> call, Response<EnrollmentCheckResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EnrollmentCheckResponse enrollmentCheck = response.body();
                    isEnrolled = enrollmentCheck.isEnrolled();
                    updateEnrollmentButton();
                } else {
                    Log.e(TAG, "Failed to check enrollment: " + response.code());
                    // Default to not enrolled
                    isEnrolled = false;
                    updateEnrollmentButton();
                }
            }

            @Override
            public void onFailure(Call<EnrollmentCheckResponse> call, Throwable t) {
                Log.e(TAG, "Error checking enrollment", t);
                // Default to not enrolled
                isEnrolled = false;
                updateEnrollmentButton();
            }
        });
    }

    private void updateEnrollmentButton() {
        if (isEnrolled) {
            buttonEnrollNow.setText("Continue Learning");
            buttonEnrollNow.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            buttonEnrollNow.setText("Enroll Now");
            buttonEnrollNow.setBackgroundColor(getResources().getColor(R.color.udemy_purple));
        }
    }

    private void handleEnrollmentAction() {
        if (isEnrolled) {
            // Navigate to lessons
            Intent intent = LessonsActivity.createIntent(this, courseId, courseDetail.getTitle());
            startActivity(intent);
        } else {
            // Check if course is free or paid
            if (courseDetail != null) {
                try {
                    double price = Double.parseDouble(courseDetail.getPrice());
                    if (price <= 0) {
                        // Free course - direct enrollment
                        enrollInCourse("free");
                    } else {
                        // Paid course - redirect to payment confirmation
                        Intent intent = PaymentConfirmActivity.createIntent(
                            this, 
                            courseId, 
                            courseDetail.getTitle(), 
                            courseDetail.getPrice(), 
                            courseDetail.getImageUrl()
                        );
                        startActivity(intent);
                    }
                } catch (NumberFormatException e) {
                    // Default to free if price parsing fails
                    enrollInCourse("free");
                }
            }
        }
    }

    private void showPaymentDialog() {
        if (courseDetail == null) return;
        
        String price = formatPrice(courseDetail.getPrice());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Course Enrollment")
                .setMessage("This course costs " + price + ". Would you like to proceed with PayOS payment?\n\n" +
                           "Note: You will be redirected to a secure payment page.")
                .setPositiveButton("Pay with PayOS", (dialog, which) -> {
                    Log.d(TAG, "User confirmed PayOS payment");
                    enrollInCourse("payos");
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Log.d(TAG, "User cancelled payment");
                })
                .setCancelable(true)
                .show();
    }

    private void enrollInCourse(String paymentMethod) {
        Log.d(TAG, "Starting enrollment with payment method: " + paymentMethod);
        
        EnrollmentRequest request = new EnrollmentRequest(courseId, paymentMethod);
        
        Call<EnrollmentResponse> call = courseService.enrollCourse(request);
        call.enqueue(new Callback<EnrollmentResponse>() {
            @Override
            public void onResponse(Call<EnrollmentResponse> call, Response<EnrollmentResponse> response) {
                Log.d(TAG, "Enrollment response received. Success: " + response.isSuccessful() + ", Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    EnrollmentResponse enrollmentResponse = response.body();
                    Log.d(TAG, "Enrollment response body: success=" + enrollmentResponse.isSuccess() + 
                          ", message=" + enrollmentResponse.getMessage() + 
                          ", paymentUrl=" + enrollmentResponse.getPaymentUrl());
                    
                    if (enrollmentResponse.isSuccess()) {
                        if ("payos".equals(paymentMethod)) {
                            String paymentUrl = enrollmentResponse.getPaymentUrl();
                            Log.d(TAG, "PayOS payment URL received: " + paymentUrl);
                            
                            if (paymentUrl != null && !paymentUrl.isEmpty()) {
                                // Open PayOS payment URL
                                Log.d(TAG, "Opening PayOS payment URL");
                                PayOSHelper.openPaymentUrl(CourseDetailActivity.this, paymentUrl);
                                
                                Toast.makeText(CourseDetailActivity.this, 
                                    "Redirecting to payment page...\nReturn to app after payment completion", 
                                    Toast.LENGTH_LONG).show();
                            } else {
                                Log.e(TAG, "PayOS payment URL is null or empty");
                                Toast.makeText(CourseDetailActivity.this, 
                                    "Payment URL not received. Please try again.", 
                                    Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Free enrollment successful
                            Log.d(TAG, "Free enrollment successful");
                            isEnrolled = true;
                            updateEnrollmentButton();
                            Toast.makeText(CourseDetailActivity.this, "Successfully enrolled!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Enrollment failed: " + enrollmentResponse.getMessage());
                        Toast.makeText(CourseDetailActivity.this, 
                            "Enrollment failed: " + enrollmentResponse.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "Enrollment API call failed. Response code: " + response.code());
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    
                    Toast.makeText(CourseDetailActivity.this, 
                        "Enrollment failed. Please check your connection and try again.", 
                        Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<EnrollmentResponse> call, Throwable t) {
                Log.e(TAG, "Network error during enrollment", t);
                Toast.makeText(CourseDetailActivity.this, 
                    "Network error: " + t.getMessage() + "\nPlease check your connection and try again.", 
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateUI() {
        if (courseDetail == null) return;

        // Course image
        if (courseDetail.getImageUrl() != null && !courseDetail.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(courseDetail.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imageViewCourseImage);
        }

        // Course title
        textViewCourseTitle.setText(courseDetail.getTitle());

        // Mock rating (không có trong API)
        textViewRating.setText("4.6");

        // Duration và lessons
        textViewDuration.setText(formatDuration(courseDetail.getTotalDuration()));
        textViewLessons.setText(courseDetail.getTotalLessons() + " lessons");

        // Categories
        populateCategories(courseDetail.getCategories());

        // Instructor
        if (courseDetail.getInstructor() != null) {
            textViewInstructorName.setText(courseDetail.getInstructor().getUsername());
            textViewInstructorEmail.setText(courseDetail.getInstructor().getEmail());
            
            if (courseDetail.getInstructor().getAvatarUrl() != null && !courseDetail.getInstructor().getAvatarUrl().isEmpty()) {
                Picasso.get()
                        .load(courseDetail.getInstructor().getAvatarUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(imageViewInstructorAvatar);
            }
        }

        // Description
        textViewCourseDescription.setText(courseDetail.getDescription());

        // Topics
        if (courseDetail.getTopics() != null) {
            topicAdapter.updateTopics(courseDetail.getTopics());
        }

        // Price
        textViewPrice.setText(formatPrice(courseDetail.getPrice()));

        // Created date
        textViewCreatedDate.setText(formatDate(courseDetail.getCreatedAt()));
    }

    private void populateCategories(List<Category> categories) {
        layoutCategories.removeAllViews();
        
        if (categories == null || categories.isEmpty()) {
            return;
        }

        for (Category category : categories) {
            TextView categoryChip = new TextView(this);
            categoryChip.setText(category.getName());
            categoryChip.setTextSize(12f);
            categoryChip.setTextColor(ContextCompat.getColor(this, R.color.udemy_purple));
            categoryChip.setBackgroundResource(R.drawable.chip_unselected_bg);
            categoryChip.setPadding(24, 12, 24, 12);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 16, 8);
            categoryChip.setLayoutParams(params);
            
            layoutCategories.addView(categoryChip);
        }
    }

    private String formatDuration(int totalMinutes) {
        if (totalMinutes <= 0) return "0h";
        
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        
        if (hours > 0 && minutes > 0) {
            return hours + "h " + minutes + "m";
        } else if (hours > 0) {
            return hours + "h";
        } else {
            return minutes + "m";
        }
    }

    private String formatPrice(String price) {
        if (price == null || price.isEmpty()) return "Free";
        
        try {
            double priceValue = Double.parseDouble(price);
            if (priceValue <= 0) {
                return "Free";
            } else {
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                return formatter.format(priceValue);
            }
        } catch (NumberFormatException e) {
            return "Free";
        }
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return "";
        
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + dateString, e);
            return dateString;
        }
    }

    @Override
    public void onTopicClick(Topic topic, int position) {
        // TODO: Navigate to topic detail or lesson
        Toast.makeText(this, "Topic: " + topic.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void openIntroVideo(String videoUrl) {
        // Show dialog để user chọn cách mở video
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Open Intro Video")
                .setMessage("How would you like to watch the intro video?")
                .setPositiveButton("Video Player", (dialog, which) -> {
                    openVideoInPlayer(videoUrl);
                })
                .setNegativeButton("External Player", (dialog, which) -> {
                    openVideoWithExternalPlayer(videoUrl);
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void openVideoWithExternalPlayer(String videoUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(videoUrl), "video/*");
            
            // Thêm flags để tránh lỗi
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Fallback: mở trong browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                startActivity(browserIntent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening video with external player", e);
            Toast.makeText(this, "Error opening video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openVideoInPlayer(String videoUrl) {
        String videoTitle = courseDetail != null ? courseDetail.getTitle() + " - Intro" : "Course Intro";
        Intent intent = VideoPlayerActivity.createIntent(this, videoUrl, videoTitle);
        startActivity(intent);
    }

    public static Intent createIntent(Context context, int courseId) {
        Intent intent = new Intent(context, CourseDetailActivity.class);
        intent.putExtra(EXTRA_COURSE_ID, courseId);
        return intent;
    }
} 