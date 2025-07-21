package com.example.fu_self_learning_app.models.response;

import com.google.gson.annotations.SerializedName;

public class OrderResponse {
    private boolean success;
    private String message;
    private String orderId;

    @SerializedName("payUrl")
    private String paymentUrl;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }
} 