package com.example.fu_self_learning_app.models;

public class Post {
    private int id;
    private String title;
    private String body;
    private String image; // URL ảnh sau khi upload
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

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

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
                ", content='" + body + '\'' + // Assuming content is body for now
                ", imageUrl='" + image + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", isLiked=" + isLiked +
                '}';
    }
}
