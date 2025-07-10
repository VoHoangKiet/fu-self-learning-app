package com.example.fu_self_learning_app.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fu_self_learning_app.R;

@SuppressLint("deprecation")
public class VideoPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO_URL = "video_url";
    public static final String EXTRA_VIDEO_TITLE = "video_title";

    private WebView webView;
    private ProgressBar progressBar;
    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View customView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        String videoUrl = getIntent().getStringExtra(EXTRA_VIDEO_URL);
        String videoTitle = getIntent().getStringExtra(EXTRA_VIDEO_TITLE);

        if (videoUrl == null || videoUrl.isEmpty()) {
            finish();
            return;
        }

        initViews();
        setupWebView();
        loadVideo(videoUrl);

        if (videoTitle != null) {
            setTitle(videoTitle);
        }
    }

    private void initViews() {
        webView = findViewById(R.id.webViewVideo);
        progressBar = findViewById(R.id.progressBarVideo);
        customViewContainer = findViewById(R.id.customViewContainer);
    }

    @SuppressLint({"SetJavaScriptEnabled", "deprecation"})
    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    onHideCustomView();
                    return;
                }

                customView = view;
                customViewCallback = callback;
                customViewContainer.addView(customView);
                customViewContainer.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
            }

            @Override
            public void onHideCustomView() {
                VideoPlayerActivity.this.onHideCustomView();
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });
    }

    private void loadVideo(String videoUrl) {
        progressBar.setVisibility(View.VISIBLE);
        
        String videoHtml = createVideoHtml(videoUrl);
        webView.loadDataWithBaseURL(null, videoHtml, "text/html", "UTF-8", null);
    }

    private String createVideoHtml(String videoUrl) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0, user-scalable=no'>" +
                "<style>" +
                "* { margin: 0; padding: 0; box-sizing: border-box; }" +
                "body { background: #000; overflow: hidden; }" +
                "video { " +
                "  width: 100vw; " +
                "  height: 100vh; " +
                "  object-fit: contain; " +
                "  outline: none; " +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<video " +
                "  controls " +
                "  autoplay " +
                "  playsinline " +
                "  webkit-playsinline " +
                "  controlslist='nodownload'>" +
                "<source src='" + videoUrl + "' type='video/mp4'>" +
                "<source src='" + videoUrl + "' type='video/webm'>" +
                "<source src='" + videoUrl + "' type='video/ogg'>" +
                "Your browser does not support the video tag." +
                "</video>" +
                "<script>" +
                "document.addEventListener('DOMContentLoaded', function() {" +
                "  var video = document.querySelector('video');" +
                "  video.addEventListener('loadstart', function() { console.log('Video loading started'); });" +
                "  video.addEventListener('error', function(e) { console.error('Video error:', e); });" +
                "});" +
                "</script>" +
                "</body>" +
                "</html>";
    }

    @SuppressLint("deprecation")
    @Override
    public void onBackPressed() {
        if (customView != null) {
            // Check API level before using getWebChromeClient
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                webView.getWebChromeClient().onHideCustomView();
            } else {
                // Fallback for older API levels
                onHideCustomView();
            }
        } else if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void onHideCustomView() {
        if (customView == null) {
            return;
        }

        customView.setVisibility(View.GONE);
        customViewContainer.removeView(customView);
        customViewContainer.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);

        if (customViewCallback != null) {
            customViewCallback.onCustomViewHidden();
        }

        customView = null;
        customViewCallback = null;
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.removeAllViews();
            webView.destroy();
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