package com.example.fu_self_learning_app.models;

import java.util.List;

public class Post {
    private int id;
    private String title;
    private String body;
    private List<String> images; // Thay image thành images
    private String status;
    private UserInfo user;
    private String createdAt;
    private int likeCount;
    private int commentCount;
    private boolean isLiked;

    // Getter/setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public List<String> getImages() {
        return images;
    }
    public void setImages(List<String> images) { this.images = images; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", images=" + images +
                ", createdAt='" + createdAt + '\'' +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", isLiked=" + isLiked +
                '}';
    }
}
