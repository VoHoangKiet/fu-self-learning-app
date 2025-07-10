package com.example.fu_self_learning_app.models;

import java.util.Date;
import java.util.List;

// Model đại diện cho thông tin khóa học
public class Course {
    private int id;
    private String title;
    private String description;
    private String imageUrl; // API field name
    private Instructor instructor; // nested object
    private List<Category> categories; // array of categories
    private String createdAt; // API returns string date
    
    // Fields for UI display (mock values for missing API fields)
    private double price;
    private int duration; // thời lượng tính bằng phút
    private String level; // beginner, intermediate, advanced
    private int enrolledCount;
    private double rating;

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

    public Instructor getInstructor() {
        return instructor;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Helper methods for UI
    public String getThumbnailUrl() {
        return imageUrl; // alias for backward compatibility
    }

    public String getCategory() {
        if (categories != null && !categories.isEmpty()) {
            return categories.get(0).getName();
        }
        return "General";
    }

    public String getInstructorName() {
        if (instructor != null) {
            return instructor.getUsername();
        }
        return "Unknown Instructor";
    }

    public double getPrice() {
        return price;
    }

    public int getDuration() {
        return duration;
    }

    public String getLevel() {
        return level;
    }

    public int getEnrolledCount() {
        return enrolledCount;
    }

    public double getRating() {
        return rating;
    }

    // Remove old methods that don't exist in API

    // Setters for testing and data creation
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

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Legacy setters for backward compatibility
    public void setThumbnailUrl(String thumbnailUrl) {
        this.imageUrl = thumbnailUrl;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setEnrolledCount(int enrolledCount) {
        this.enrolledCount = enrolledCount;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setInstructorName(String instructorName) {
        if (this.instructor == null) {
            this.instructor = new Instructor();
        }
        this.instructor.setUsername(instructorName);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", instructor=" + instructor +
                ", categories=" + categories +
                ", createdAt='" + createdAt + '\'' +
                ", price=" + price +
                ", duration=" + duration +
                ", level='" + level + '\'' +
                ", enrolledCount=" + enrolledCount +
                ", rating=" + rating +
                '}';
    }
} 