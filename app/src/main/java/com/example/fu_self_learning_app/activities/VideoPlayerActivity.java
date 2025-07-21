package com.example.fu_self_learning_app.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fu_self_learning_app.R;

@SuppressLint("deprecation")
public class VideoPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO_URL = "video_url";
    public static final String EXTRA_VIDEO_TITLE = "video_title";
    private static final String TAG = "VideoPlayerActivity";

    // Views
    private VideoView videoView;
    private FrameLayout videoContainer;
    private ProgressBar progressBarVideo;
    private ProgressBar progressBarLoading;
    private ImageView btnPlayPause;
    private ImageView btnBack;
    private ImageView btnFullscreen;
    private ImageView btnZoomIn;
    private ImageView btnZoomOut;
    private ImageView btnZoomReset;
    private TextView textViewTime;
    private LinearLayout mediaControls;
    private LinearLayout zoomControls;

    // Video & Zoom
    private float scaleFactor = 1.0f;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private Handler progressHandler;
    private boolean isPlaying = false;
    private boolean isFullscreen = false;
    private int videoDuration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        String videoUrl = getIntent().getStringExtra(EXTRA_VIDEO_URL);
        String videoTitle = getIntent().getStringExtra(EXTRA_VIDEO_TITLE);

        if (videoUrl == null || videoUrl.isEmpty()) {
            Toast.makeText(this, "Invalid video URL", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Loading video: " + videoUrl);

        initViews();
        setupGestureDetectors();
        setupListeners();
        loadVideo(videoUrl);

        if (videoTitle != null) {
            setTitle(videoTitle);
        }
    }

    private void initViews() {
        videoView = findViewById(R.id.videoView);
        videoContainer = findViewById(R.id.videoContainer);
        progressBarVideo = findViewById(R.id.progressBarVideo);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnBack = findViewById(R.id.btnBack);
        btnFullscreen = findViewById(R.id.btnFullscreen);
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomOut = findViewById(R.id.btnZoomOut);
        btnZoomReset = findViewById(R.id.btnZoomReset);
        textViewTime = findViewById(R.id.textViewTime);
        mediaControls = findViewById(R.id.mediaControls);
        zoomControls = findViewById(R.id.zoomControls);

        progressHandler = new Handler();
    }

    private void setupGestureDetectors() {
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 4.0f));
                applyZoomToVideo();
                return true;
            }
        });

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (scaleFactor == 1.0f) {
                    scaleFactor = 2.0f;
                } else {
                    scaleFactor = 1.0f;
                }
                applyZoomToVideo();
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                toggleMediaControls();
                return true;
            }
        });
    }

    private void setupListeners() {
        // Touch events for zoom
        videoView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            gestureDetector.onTouchEvent(event);
            return true;
        });

        // Video event listeners
        videoView.setOnPreparedListener(mediaPlayer -> {
            progressBarLoading.setVisibility(View.GONE);
            videoDuration = mediaPlayer.getDuration();
            updateProgressBar();
            Log.d(TAG, "Video prepared, duration: " + videoDuration);
        });

        videoView.setOnCompletionListener(mediaPlayer -> {
            isPlaying = false;
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            Log.d(TAG, "Video completed");
        });

        videoView.setOnErrorListener((mediaPlayer, what, extra) -> {
            Log.e(TAG, "Video error: " + what + ", " + extra);
            progressBarLoading.setVisibility(View.GONE);
            Toast.makeText(VideoPlayerActivity.this, "Error playing video", Toast.LENGTH_SHORT).show();
            return true;
        });

        // Button listeners
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnBack.setOnClickListener(v -> finish());
        btnFullscreen.setOnClickListener(v -> toggleFullscreen());
        btnZoomIn.setOnClickListener(v -> zoomIn());
        btnZoomOut.setOnClickListener(v -> zoomOut());
        btnZoomReset.setOnClickListener(v -> resetZoom());
    }

    private void loadVideo(String videoUrl) {
        progressBarLoading.setVisibility(View.VISIBLE);
        
        try {
            Uri videoUri = Uri.parse(videoUrl);
            videoView.setVideoURI(videoUri);
            videoView.requestFocus();
            Log.d(TAG, "Video URI set: " + videoUri);
        } catch (Exception e) {
            Log.e(TAG, "Error loading video", e);
            Toast.makeText(this, "Error loading video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Zoom methods
    private void applyZoomToVideo() {
        videoView.setScaleX(scaleFactor);
        videoView.setScaleY(scaleFactor);
        Log.d(TAG, "Zoom applied: " + scaleFactor);
    }

    private void zoomIn() {
        scaleFactor = Math.min(4.0f, scaleFactor + 0.25f);
        applyZoomToVideo();
    }

    private void zoomOut() {
        scaleFactor = Math.max(0.5f, scaleFactor - 0.25f);
        applyZoomToVideo();
    }

    private void resetZoom() {
        scaleFactor = 1.0f;
        applyZoomToVideo();
    }

    // Media control methods
    private void togglePlayPause() {
        if (isPlaying) {
            videoView.pause();
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            isPlaying = false;
        } else {
            videoView.start();
            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            isPlaying = true;
        }
    }

    private void toggleFullscreen() {
        if (isFullscreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isFullscreen = false;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isFullscreen = true;
        }
    }

    private void toggleMediaControls() {
        if (mediaControls.getVisibility() == View.VISIBLE) {
            hideControls();
        } else {
            showControls();
        }
    }

    private void showControls() {
        mediaControls.setVisibility(View.VISIBLE);
        zoomControls.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
    }

    private void hideControls() {
        mediaControls.setVisibility(View.GONE);
        zoomControls.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);
    }

    private void updateProgressBar() {
        if (videoView.isPlaying() && videoDuration > 0) {
            int currentPosition = videoView.getCurrentPosition();
            int progress = (int) ((currentPosition * 100.0) / videoDuration);
            progressBarVideo.setProgress(progress);

            // Update time text
            String currentTime = formatTime(currentPosition);
            String totalTime = formatTime(videoDuration);
            textViewTime.setText(currentTime + " / " + totalTime);

            // Continue updating
            progressHandler.postDelayed(this::updateProgressBar, 1000);
        }
    }

    private String formatTime(int timeMs) {
        int seconds = (timeMs / 1000) % 60;
        int minutes = (timeMs / (1000 * 60)) % 60;
        int hours = timeMs / (1000 * 60 * 60);
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @SuppressLint("deprecation")
    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isFullscreen = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            videoView.pause();
            isPlaying = false;
        }
    }

    @Override
    protected void onDestroy() {
        if (progressHandler != null) {
            progressHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    public static Intent createIntent(Context context, String videoUrl, String videoTitle) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(EXTRA_VIDEO_URL, videoUrl);
        intent.putExtra(EXTRA_VIDEO_TITLE, videoTitle);
        return intent;
    }
} 