package com.example.fu_self_learning_app.models.request;

public class OrderRequest {
    private double amount;

    public OrderRequest(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
} 