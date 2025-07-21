package com.example.fu_self_learning_app.models;

import com.google.gson.annotations.SerializedName;

public class Lesson {
    private int id;
    private String title;
    private String description;
    private String videoUrl;
    
    @SerializedName("videoDuration")
    private int videoDuration; // API response field name
    
    private int duration; // in minutes - for backward compatibility
    private int topicId;
    private int orderIndex;
    private boolean isCompleted;
    private String lessonType; // "video", "text", "quiz", etc.

    public Lesson() {}

    public Lesson(int id, String title, String description, String videoUrl, int duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getDuration() {
        // Return videoDuration if available, otherwise duration
        return videoDuration > 0 ? videoDuration : duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(int videoDuration) {
        this.videoDuration = videoDuration;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getLessonType() {
        return lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }

    public String getFormattedDuration() {
        int totalMinutes = getDuration(); // This will get videoDuration or duration
        if (totalMinutes <= 0) return "0:00";
        
        // Check if videoDuration is in seconds (API) or minutes (local)
        if (videoDuration > 0) {
            // videoDuration is in minutes from API, treat as minutes
            int minutes = totalMinutes;
            int seconds = 0;
            
            if (minutes >= 60) {
                int hours = minutes / 60;
                minutes = minutes % 60;
                return String.format("%d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format("%d:%02d", minutes, seconds);
            }
        } else {
            // duration is in minutes (legacy)
            int minutes = totalMinutes;
            int seconds = 0;
            
            if (minutes >= 60) {
                int hours = minutes / 60;
                minutes = minutes % 60;
                return String.format("%d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format("%d:%02d", minutes, seconds);
            }
        }
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", videoDuration=" + videoDuration +
                ", duration=" + duration +
                ", topicId=" + topicId +
                ", orderIndex=" + orderIndex +
                ", isCompleted=" + isCompleted +
                ", lessonType='" + lessonType + '\'' +
                '}';
    }
} 