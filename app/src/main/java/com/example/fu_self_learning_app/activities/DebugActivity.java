package com.example.fu_self_learning_app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fu_self_learning_app.R;

public class DebugActivity extends AppCompatActivity {
    private TextView textDebugInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Tạo TextView đơn giản
        textDebugInfo = new TextView(this);
        textDebugInfo.setPadding(50, 50, 50, 50);
        textDebugInfo.setTextSize(14);
        setContentView(textDebugInfo);
        
        displaySharedPreferencesInfo();
    }
    
    private void displaySharedPreferencesInfo() {
        SharedPreferences prefs = getSharedPreferences("Auth", MODE_PRIVATE);
        
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
        String username = prefs.getString("username", "NOT_FOUND");
        String email = prefs.getString("email", "NOT_FOUND");
        int userId = prefs.getInt("user_id", -1);
        String accessToken = prefs.getString("access_token", "NOT_FOUND");
        
        String debugInfo = "=== SharedPreferences Debug Info ===\n\n" +
                "is_logged_in: " + isLoggedIn + "\n" +
                "username: " + username + "\n" +
                "email: " + email + "\n" +
                "user_id: " + userId + "\n" +
                "access_token: " + (accessToken.length() > 50 ? accessToken.substring(0, 50) + "..." : accessToken) + "\n\n" +
                "=== All Keys ===\n";
        
        // Liệt kê tất cả keys
        for (String key : prefs.getAll().keySet()) {
            Object value = prefs.getAll().get(key);
            debugInfo += key + " = " + value + "\n";
        }
        
        textDebugInfo.setText(debugInfo);
        
        // Log to console
        Log.d("DebugActivity", "📋 SharedPreferences Debug:");
        Log.d("DebugActivity", "is_logged_in: " + isLoggedIn);
        Log.d("DebugActivity", "username: " + username);
        Log.d("DebugActivity", "email: " + email);
        Log.d("DebugActivity", "user_id: " + userId);
        Log.d("DebugActivity", "access_token present: " + (!accessToken.equals("NOT_FOUND")));
    }
}
