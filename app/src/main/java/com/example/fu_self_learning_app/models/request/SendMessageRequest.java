package com.example.fu_self_learning_app.models.request;

// Request model để gửi tin nhắn
public class SendMessageRequest {
    private int senderUserId;
    private int receiverUserId;
    private String message;

    public SendMessageRequest() {}

    public SendMessageRequest(int senderUserId, int receiverUserId, String message) {
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.message = message;
    }

    // Getters and setters
    public int getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(int senderUserId) {
        this.senderUserId = senderUserId;
    }

    public int getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(int receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
