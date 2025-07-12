package com.example.fu_self_learning_app.models;

public class Post {
    private int id;
    private UserInfo user;      // Thông tin người đăng
    private String content;
    private String imageUrl;
    private String createdAt;
    private int likeCount;
    private int commentCount;
    private boolean isLiked;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

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
                ", content='" + content + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", isLiked=" + isLiked +
                '}';
    }
}
