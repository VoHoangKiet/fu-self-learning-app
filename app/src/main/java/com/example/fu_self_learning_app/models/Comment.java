package com.example.fu_self_learning_app.models;

import java.util.List;

public class Comment {
    private int id;
    private int postId;
    private Integer parentId;   // ID của comment cha (null nếu là comment gốc)
    private UserInfo user;      // Thông tin người comment
    private String content;
    private String createdAt;
    private List<Comment> replies;  // Danh sách replies

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<Comment> getReplies() { return replies; }
    public void setReplies(List<Comment> replies) { this.replies = replies; }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", postId=" + postId +
                ", parentId=" + parentId +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", content='" + content + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", replies=" + (replies != null ? replies.size() : 0) +
                '}';
    }
} 