package com.example.fu_self_learning_app.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class PayOSHelper {
    private static final String TAG = "PayOSHelper";

    /**
     * Mở PayOS payment URL trong browser
     */
    public static void openPaymentUrl(Context context, String paymentUrl) {
        Log.d(TAG, "Attempting to open payment URL: " + paymentUrl);
        
        if (paymentUrl == null || paymentUrl.isEmpty()) {
            Log.e(TAG, "Payment URL is null or empty");
            Toast.makeText(context, "Invalid payment URL", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Validate URL format
            Uri uri = Uri.parse(paymentUrl);
            if (uri.getScheme() == null) {
                Log.e(TAG, "Invalid URL scheme: " + paymentUrl);
                Toast.makeText(context, "Invalid payment URL format", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            // Add category for better browser selection
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            
            Log.d(TAG, "Resolving intent for URL: " + paymentUrl);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                Log.d(TAG, "Starting browser activity");
                context.startActivity(intent);
                
                // Show user guidance with fallback instruction
                Toast.makeText(context, "Opening payment page...\nIf page doesn't load, please check your internet connection.", Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "No app can handle this URL: " + paymentUrl);
                
                // Try with explicit browser selection
                Intent chooser = Intent.createChooser(intent, "Choose browser for payment");
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                
                try {
                    context.startActivity(chooser);
                    Toast.makeText(context, "Opening payment page...", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to open chooser", ex);
                    Toast.makeText(context, "No browser app found. Please install a browser.", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening payment URL: " + paymentUrl, e);
            Toast.makeText(context, "Error opening payment: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Check xem có phải PayOS callback URL không
     */
    public static boolean isPayOSCallback(String url) {
        if (url == null || url.isEmpty()) return false;
        
        // PayOS callback URLs thường chứa các parameters này
        return url.contains("code=") || url.contains("cancel=") || 
               url.contains("success=") || url.contains("paymentLinkId=");
    }

    /**
     * Parse PayOS callback result từ URL
     */
    public static PaymentResult parsePaymentResult(String callbackUrl) {
        if (callbackUrl == null || callbackUrl.isEmpty()) {
            return new PaymentResult(false, "Invalid callback URL", null);
        }

        Uri uri = Uri.parse(callbackUrl);
        
        // Check success parameter
        String success = uri.getQueryParameter("success");
        String cancel = uri.getQueryParameter("cancel");
        String code = uri.getQueryParameter("code");
        String paymentLinkId = uri.getQueryParameter("paymentLinkId");

        if ("true".equals(success)) {
            return new PaymentResult(true, "Payment successful", paymentLinkId);
        } else if ("true".equals(cancel)) {
            return new PaymentResult(false, "Payment cancelled by user", paymentLinkId);
        } else if (code != null) {
            return new PaymentResult(false, "Payment failed with code: " + code, paymentLinkId);
        } else {
            return new PaymentResult(false, "Unknown payment status", paymentLinkId);
        }
    }

    /**
     * Class để lưu kết quả payment
     */
    public static class PaymentResult {
        private boolean success;
        private String message;
        private String paymentId;

        public PaymentResult(boolean success, String message, String paymentId) {
            this.success = success;
            this.message = message;
            this.paymentId = paymentId;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getPaymentId() {
            return paymentId;
        }
    }
} 