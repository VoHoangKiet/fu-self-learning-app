package com.example.fu_self_learning_app.models.request;

public class EnrollmentRequest {
    private int courseId;
    private String paymentMethod; // "payos" hoặc "free"
    private String returnUrl;     // URL để redirect sau khi payment thành công
    private String cancelUrl;     // URL để redirect khi payment bị cancel

    public EnrollmentRequest() {}

    public EnrollmentRequest(int courseId, String paymentMethod) {
        this.courseId = courseId;
        this.paymentMethod = paymentMethod;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }
} 