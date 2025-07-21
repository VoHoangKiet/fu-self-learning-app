package com.example.fu_self_learning_app.models.generate;

import java.util.List;

public class GeneratedCourse {
    private String title;
    private String description;
    private List<Integer> categoryIds;
    private List<GeneratedTopic> topics;

    public GeneratedCourse(String title, String description, List<Integer> categoryIds, List<GeneratedTopic> topics) {
        this.title = title;
        this.description = description;
        this.categoryIds = categoryIds;
        this.topics = topics;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public List<GeneratedTopic> getTopics() {
        return topics;
    }

    @Override
    public String toString() {
        return "GeneratedCourse{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", categoryIds=" + categoryIds +
                ", topics=" + topics +
                '}';
    }
}
