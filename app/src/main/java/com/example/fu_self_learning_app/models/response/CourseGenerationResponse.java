package com.example.fu_self_learning_app.models.response;

import com.example.fu_self_learning_app.models.generate.GeneratedCourse;
import com.example.fu_self_learning_app.models.generate.GeneratedTopic;

import java.util.List;

public class CourseGenerationResponse {
    private GeneratedCourse course;
    private List<GeneratedTopic> topics;

    public GeneratedCourse getCourse() {
        return course;
    }

    public List<GeneratedTopic> getTopics() {
        return topics;
    }
}
