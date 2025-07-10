package com.example.fu_self_learning_app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.adapters.CourseAdapter;
import com.example.fu_self_learning_app.models.Course;
import com.example.fu_self_learning_app.network.APIClient;
import com.example.fu_self_learning_app.services.CourseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoursesActivity extends AppCompatActivity implements CourseAdapter.OnCourseClickListener {

    private RecyclerView recyclerViewCourses;
    private CourseAdapter courseAdapter;
    private ProgressBar progressBarCourses;
    private TextView textViewResultsCount;
    private EditText editTextSearch;
    private ImageView iconBack;
    private ImageView iconFilter;
    
    private CourseService courseService;
    private List<Course> allCourses;
    private List<Course> filteredCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Ensure status bar doesn't overlap content
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.udemy_purple));
        
        setContentView(R.layout.activity_courses);

        initViews();
        setupRecyclerView();
        setupSearchView();
        initAPI();
        loadCourses();
    }

    private void initViews() {
        recyclerViewCourses = findViewById(R.id.recyclerViewCourses);
        progressBarCourses = findViewById(R.id.progressBarCourses);
        textViewResultsCount = findViewById(R.id.textViewResultsCount);
        editTextSearch = findViewById(R.id.editTextSearch);
        iconBack = findViewById(R.id.iconBack);
        iconFilter = findViewById(R.id.iconFilter);
    }

    private void setupRecyclerView() {
        courseAdapter = new CourseAdapter(this);
        courseAdapter.setOnCourseClickListener(this);
        recyclerViewCourses.setAdapter(courseAdapter);
        recyclerViewCourses.setLayoutManager(new LinearLayoutManager(this));
        
        allCourses = new ArrayList<>();
        filteredCourses = new ArrayList<>();
    }

    private void setupSearchView() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCourses(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        iconBack.setOnClickListener(v -> finish());
        
        iconFilter.setOnClickListener(v -> {
            // TODO: Implement filter functionality
            Toast.makeText(this, "Filter options coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void initAPI() {
        // Use APIClient with context to enable AuthInterceptor
        courseService = APIClient.getClient(this).create(CourseService.class);
        
        // For testing - add a mock token to SharedPreferences
        // TODO: Remove this in production - token should come from login
        addMockTokenForTesting();
    }

    // Mock token for testing API - remove this in production
    private void addMockTokenForTesting() {
        SharedPreferences prefs = getSharedPreferences("Auth", Context.MODE_PRIVATE);
        String existingToken = prefs.getString("access_token", null);
        
        if (existingToken == null || existingToken.isEmpty()) {
            // IMPORTANT: Replace "your-actual-bearer-token-here" with the real token from the image
            // Token should look like: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("access_token", "your-actual-bearer-token-here");
            editor.apply();
            
            // Show warning that token needs to be updated
            Toast.makeText(this, "⚠️ Please update Bearer token in CoursesActivity.addMockTokenForTesting()", Toast.LENGTH_LONG).show();
        }
    }

    private void loadCourses() {
        showLoading();
        
        Call<List<Course>> call = courseService.getAllCourses();
        call.enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                hideLoading();
                
                if (response.isSuccessful() && response.body() != null) {
                    allCourses.clear();
                    List<Course> apiCourses = response.body();
                    
                    // Add mock UI data to API courses
                    for (Course course : apiCourses) {
                        addMockUIData(course);
                    }
                    
                    allCourses.addAll(apiCourses);
                    filteredCourses.clear();
                    filteredCourses.addAll(allCourses);
                    
                    courseAdapter.setCourseList(filteredCourses);
                    updateResultsCount();
                    
                    if (filteredCourses.isEmpty()) {
                        showEmptyState();
                    } else {
                        hideEmptyState();
                    }
                } else {
                    Log.e("CoursesActivity", "API Error: " + response.code() + " - " + response.message());
                    showError("API Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {
                hideLoading();
                Log.e("CoursesActivity", "Network error loading courses", t);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void filterCourses(String query) {
        filteredCourses.clear();
        
        if (query == null || query.trim().isEmpty()) {
            filteredCourses.addAll(allCourses);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Course course : allCourses) {
                if (course.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                    course.getCategory().toLowerCase().contains(lowerCaseQuery) ||
                    course.getInstructorName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredCourses.add(course);
                }
            }
        }
        
        courseAdapter.setCourseList(filteredCourses);
        updateResultsCount();
        
        if (filteredCourses.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
        }
    }

    private void showLoading() {
        findViewById(R.id.frameLayoutLoading).setVisibility(View.VISIBLE);
        recyclerViewCourses.setVisibility(View.GONE);
        findViewById(R.id.layoutEmptyState).setVisibility(View.GONE);
    }

    private void hideLoading() {
        findViewById(R.id.frameLayoutLoading).setVisibility(View.GONE);
        recyclerViewCourses.setVisibility(View.VISIBLE);
    }

    private void showEmptyState() {
        findViewById(R.id.layoutEmptyState).setVisibility(View.VISIBLE);
        recyclerViewCourses.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        findViewById(R.id.layoutEmptyState).setVisibility(View.GONE);
        recyclerViewCourses.setVisibility(View.VISIBLE);
    }

    private void updateResultsCount() {
        String countText = String.format(Locale.getDefault(), "%,d results", filteredCourses.size());
        textViewResultsCount.setText(countText);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCourseClick(Course course) {
        // Navigate to course detail activity
        Intent intent = CourseDetailActivity.createIntent(this, course.getId());
        startActivity(intent);
    }

    // Add mock UI data for fields not provided by API
    private void addMockUIData(Course course) {
        // Generate realistic mock data based on course ID
        int id = course.getId();
        
        // Mock price (some free, some paid)
        double[] prices = {0, 299000, 499000, 699000, 799000, 999000, 1299000};
        course.setPrice(prices[id % prices.length]);
        
        // Mock duration (1-5 hours)
        int[] durations = {120, 180, 240, 300, 360}; // minutes
        course.setDuration(durations[id % durations.length]);
        
        // Mock level
        String[] levels = {"beginner", "intermediate", "advanced"};
        course.setLevel(levels[id % levels.length]);
        
        // Mock rating (4.0 - 5.0)
        double[] ratings = {4.0, 4.2, 4.3, 4.5, 4.6, 4.7, 4.8, 4.9};
        course.setRating(ratings[id % ratings.length]);
        
        // Mock enrolled count
        int[] enrolled = {1200, 3500, 8900, 15600, 23400, 45000, 67800, 89000};
        course.setEnrolledCount(enrolled[id % enrolled.length]);
        
        // Add mock avatar URL if instructor doesn't have one (for testing)
        if (course.getInstructor() != null && 
            (course.getInstructor().getAvatarUrl() == null || course.getInstructor().getAvatarUrl().isEmpty())) {
            
            // Mock avatar URLs for testing
            String[] avatarUrls = {
                "https://i.pravatar.cc/150?img=1",
                "https://i.pravatar.cc/150?img=2", 
                "https://i.pravatar.cc/150?img=3",
                "https://i.pravatar.cc/150?img=4",
                "https://i.pravatar.cc/150?img=5"
            };
            course.getInstructor().setAvatarUrl(avatarUrls[id % avatarUrls.length]);
        }
    }
} 