package com.example.fu_self_learning_app.models.request;

public class CommentRequest {
    private int postId;
    private String content;
    private Integer parentId;

    public CommentRequest(int postId, String content) {
        this.postId = postId;
        this.content = content;
        this.parentId = null;
    }

    public CommentRequest(int postId, String content, Integer parentId) {
        this.postId = postId;
        this.content = content;
        this.parentId = parentId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
} 