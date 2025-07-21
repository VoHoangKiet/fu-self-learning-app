package com.example.fu_self_learning_app.models.generate;

import java.util.List;

public class GeneratedTopic {
    private String title;
    private String description;
    private List<GeneratedLesson> lessons;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<GeneratedLesson> getLessons() {
        return lessons;
    }

    @Override
    public String toString() {
        return "GeneratedTopic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", lessons=" + lessons +
                '}';
    }
}
