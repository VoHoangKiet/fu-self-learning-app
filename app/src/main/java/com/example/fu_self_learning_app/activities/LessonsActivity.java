package com.example.fu_self_learning_app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.adapters.ExpandableTopicAdapter;
import com.example.fu_self_learning_app.models.Lesson;
import com.example.fu_self_learning_app.models.Topic;
import com.example.fu_self_learning_app.models.response.EnrollmentCheckResponse;
import com.example.fu_self_learning_app.network.APIClient;
import com.example.fu_self_learning_app.services.CourseService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessonsActivity extends AppCompatActivity implements ExpandableTopicAdapter.OnLessonClickListener {

    public static final String EXTRA_COURSE_ID = "course_id";
    public static final String EXTRA_COURSE_TITLE = "course_title";
    private static final String TAG = "LessonsActivity";

    // Views
    private RecyclerView recyclerViewLessons;
    private Button buttonEnroll;

    // Data
    private int courseId;
    private String courseTitle;
    private ExpandableTopicAdapter topicAdapter;
    private List<Topic> topics;
    private List<Lesson> allLessons;
    private CourseService courseService;
    private boolean isEnrolled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        // Get data from intent
        courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
        courseTitle = getIntent().getStringExtra(EXTRA_COURSE_TITLE);

        if (courseId == -1) {
            Toast.makeText(this, "Invalid course ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupServices();
        setupRecyclerView();
        setupListeners();
        checkEnrollmentStatus();
        loadCourseData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-check enrollment status when returning from other activities
        checkEnrollmentStatus();
    }

    private void initViews() {
        recyclerViewLessons = findViewById(R.id.recyclerViewLessons);
        buttonEnroll = findViewById(R.id.buttonEnroll);
    }

    private void setupServices() {
        courseService = APIClient.getClient().create(CourseService.class);
    }

    private void setupRecyclerView() {
        topics = new ArrayList<>();
        allLessons = new ArrayList<>();
        topicAdapter = new ExpandableTopicAdapter(topics);
        topicAdapter.setOnLessonClickListener(this);
        
        recyclerViewLessons.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLessons.setAdapter(topicAdapter);
    }

    private void setupListeners() {
        buttonEnroll.setOnClickListener(v -> {
            Toast.makeText(this, "Please go back to course detail to enroll", Toast.LENGTH_SHORT).show();
            finish(); // Go back to course detail
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
                    Log.d(TAG, "Enrollment status: " + isEnrolled);
                    updateEnrollButton();
                } else {
                    Log.e(TAG, "Failed to check enrollment: " + response.code());
                    // Default to not enrolled, show button
                    isEnrolled = false;
                    updateEnrollButton();
                }
            }

            @Override
            public void onFailure(Call<EnrollmentCheckResponse> call, Throwable t) {
                Log.e(TAG, "Error checking enrollment", t);
                // Default to not enrolled, show button
                isEnrolled = false;
                updateEnrollButton();
            }
        });
    }

    private void updateEnrollButton() {
        if (isEnrolled) {
            // Hide enroll button if already enrolled
            buttonEnroll.setVisibility(View.GONE);
            Log.d(TAG, "User already enrolled - hiding enroll button");
        } else {
            // Show enroll button if not enrolled
            buttonEnroll.setVisibility(View.VISIBLE);
            Log.d(TAG, "User not enrolled - showing enroll button");
        }
    }

    private void loadCourseData() {
        Log.d(TAG, "Loading course topics for courseId: " + courseId);
        
        Call<List<Topic>> call = courseService.getCourseTopics(courseId);
        call.enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Topic> topics = response.body();
                    Log.d(TAG, "Topics loaded successfully: " + topics.size() + " topics");
                    populateUIWithTopics(topics);
                } else {
                    Log.e(TAG, "Failed to load topics: " + response.code() + " " + response.message());
                    Toast.makeText(LessonsActivity.this, "Failed to load course lessons", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {
                Log.e(TAG, "Network error loading topics", t);
                Toast.makeText(LessonsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUIWithTopics(List<Topic> topicsList) {
        if (topicsList == null) {
            Log.e(TAG, "Topics list is null");
            return;
        }

        Log.d(TAG, "Populating UI with " + topicsList.size() + " topics");

        // Process topics and lessons
        topics.clear();
        allLessons.clear();
        
        if (!topicsList.isEmpty()) {
            Log.d(TAG, "Found " + topicsList.size() + " topics");
            
            for (Topic topic : topicsList) {
                if (topic != null) {
                    topics.add(topic);
                    
                    if (topic.getLessons() != null && !topic.getLessons().isEmpty()) {
                        Log.d(TAG, "Topic '" + topic.getTitle() + "' has " + topic.getLessons().size() + " lessons");
                        
                        // Set default values for lessons that may be missing from API
                        for (Lesson lesson : topic.getLessons()) {
                            if (lesson.getLessonType() == null) {
                                lesson.setLessonType("video");
                            }
                            // lesson.setCompleted(false); // Default to not completed
                        }
                        
                        // Sort lessons by ID and add to total list
                        List<Lesson> sortedLessons = sortLessonsById(topic.getLessons());
                        allLessons.addAll(sortedLessons);
                    } else {
                        Log.d(TAG, "Topic '" + topic.getTitle() + "' has no lessons");
                    }
                }
            }
        } else {
            Log.w(TAG, "No topics found in course");
        }

        Log.d(TAG, "Total lessons found: " + allLessons.size());

        // Sort topics by ID before updating RecyclerView
        List<Topic> sortedTopics = sortTopicsById(topics);
        
        // Update RecyclerView
        topicAdapter.updateTopics(sortedTopics);

    }

    private List<Topic> sortTopicsById(List<Topic> topics) {
        List<Topic> sortedList = new ArrayList<>(topics);
        
        Collections.sort(sortedList, new Comparator<Topic>() {
            @Override
            public int compare(Topic topic1, Topic topic2) {
                // Sort by ID ascending (1, 2, 3, ...)
                return Integer.compare(topic1.getId(), topic2.getId());
            }
        });
        Log.d(TAG, "Sorted topics: " + sortedList);
        return sortedList;
    }

    private List<Lesson> sortLessonsById(List<Lesson> lessons) {
        List<Lesson> sortedList = new ArrayList<>(lessons);
        
        Collections.sort(sortedList, new Comparator<Lesson>() {
            @Override
            public int compare(Lesson lesson1, Lesson lesson2) {
                // Sort by ID ascending (1, 2, 3, ...)
                return Integer.compare(lesson1.getId(), lesson2.getId());
            }
        });
        Log.d(TAG, "Sorted lessons: " + sortedList);
        return sortedList;
    }

    @Override
    public void onLessonClick(Lesson lesson, int position) {
        Log.d(TAG, "Lesson clicked: " + lesson.getTitle());
        
        // Play video lesson or open content
        if (lesson.getVideoUrl() != null && !lesson.getVideoUrl().isEmpty()) {
            // Open video player
            Intent intent = VideoPlayerActivity.createIntent(this, lesson.getVideoUrl(), lesson.getTitle());
            startActivity(intent);
            
            // Mark as completed (this should be done after video ends, but for demo we do it immediately)
            lesson.setCompleted(true);
            topicAdapter.notifyDataSetChanged(); // Refresh entire adapter to update UI
        } else {
            Toast.makeText(this, "No video available for this lesson", Toast.LENGTH_SHORT).show();
        }
    }

    public static Intent createIntent(Context context, int courseId, String courseTitle) {
        Intent intent = new Intent(context, LessonsActivity.class);
        intent.putExtra(EXTRA_COURSE_ID, courseId);
        intent.putExtra(EXTRA_COURSE_TITLE, courseTitle);
        return intent;
    }
} 