package com.example.fu_self_learning_app.models.request;

// Request model để load tin nhắn giữa 2 user
public class LoadMessagesRequest {
    private int senderUserId;
    private int receiverUserId;

    public LoadMessagesRequest() {}

    public LoadMessagesRequest(int senderUserId, int receiverUserId) {
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
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
}
