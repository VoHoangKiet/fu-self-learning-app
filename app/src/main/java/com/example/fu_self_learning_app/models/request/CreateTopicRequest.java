package com.example.fu_self_learning_app.models.request;

public class CreateTopicRequest {
    private String title;

    private String description;

    public CreateTopicRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
