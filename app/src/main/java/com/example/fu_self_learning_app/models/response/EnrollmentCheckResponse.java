package com.example.fu_self_learning_app.models.response;

public class EnrollmentCheckResponse {
    private boolean isEnrolled;
    private String enrollmentDate;
    private String message;

    public EnrollmentCheckResponse() {}

    public EnrollmentCheckResponse(boolean isEnrolled, String enrollmentDate, String message) {
        this.isEnrolled = isEnrolled;
        this.enrollmentDate = enrollmentDate;
        this.message = message;
    }

    public boolean isEnrolled() {
        return isEnrolled;
    }

    public void setEnrolled(boolean enrolled) {
        isEnrolled = enrolled;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 