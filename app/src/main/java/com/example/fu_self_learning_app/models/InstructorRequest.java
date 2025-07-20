package com.example.fu_self_learning_app.models;

public class InstructorRequest {
    int id;
    UserInfo user;
    String pdfUrl;
    String createdAt;
    String status;

    public InstructorRequest(int id, UserInfo user, String pdfUrl, String createdAt, String status) {
        this.id = id;
        this.user = user;
        this.pdfUrl = pdfUrl;
        this.createdAt = createdAt;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public UserInfo getUser() {
        return user;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "InstructorRequest{" +
                "id=" + id +
                ", user=" + user +
                ", pdfUrl='" + pdfUrl + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
