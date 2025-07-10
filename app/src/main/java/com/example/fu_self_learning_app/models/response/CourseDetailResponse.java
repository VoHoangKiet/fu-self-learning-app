package com.example.fu_self_learning_app.models.response;

import com.example.fu_self_learning_app.models.Category;
import com.example.fu_self_learning_app.models.Instructor;
import com.example.fu_self_learning_app.models.Topic;

import java.util.List;

// Model cho API response của course detail
public class CourseDetailResponse {
    private int id;
    private String title;
    private String description;
    private String imageUrl;
    private String videoIntroUrl;
    private int totalDuration;
    private int totalLessons;
    private String price;
    private String createdAt;
    private Instructor instructor;
    private List<Category> categories;
    private List<Topic> topics;

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoIntroUrl() {
        return videoIntroUrl;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public int getTotalLessons() {
        return totalLessons;
    }

    public String getPrice() {
        return price;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setVideoIntroUrl(String videoIntroUrl) {
        this.videoIntroUrl = videoIntroUrl;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public void setTotalLessons(int totalLessons) {
        this.totalLessons = totalLessons;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "CourseDetailResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", videoIntroUrl='" + videoIntroUrl + '\'' +
                ", totalDuration=" + totalDuration +
                ", totalLessons=" + totalLessons +
                ", price='" + price + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", instructor=" + instructor +
                ", categories=" + categories +
                ", topics=" + topics +
                '}';
    }
} 