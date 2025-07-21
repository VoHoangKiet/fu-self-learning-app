package com.example.fu_self_learning_app.models.request;

import com.example.fu_self_learning_app.models.generate.GeneratedCourse;
import com.example.fu_self_learning_app.models.generate.GeneratedTopic;

import java.util.List;

public class CreateCourseWithStructureRequest {
    private GeneratedCourse course;
    private List<GeneratedTopic> topics;

    public CreateCourseWithStructureRequest(GeneratedCourse course, List<GeneratedTopic> topics) {
        this.course = course;
        this.topics = topics;
    }
}
