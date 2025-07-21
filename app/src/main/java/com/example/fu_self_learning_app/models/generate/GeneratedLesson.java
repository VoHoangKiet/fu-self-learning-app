package com.example.fu_self_learning_app.models.generate;

public class GeneratedLesson {
    private String title;
    private String description;

    public GeneratedLesson(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "GeneratedLesson{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
