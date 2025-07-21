package com.example.fu_self_learning_app.activities.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.activities.MainActivity;
import com.example.fu_self_learning_app.activities.admin.AdminHomePageActivity;
import com.example.fu_self_learning_app.models.UserInfo;
import com.example.fu_self_learning_app.models.request.LoginRequest;
import com.example.fu_self_learning_app.models.response.LoginResponse;
import com.example.fu_self_learning_app.network.APIClient;
import com.example.fu_self_learning_app.services.AuthService;
import com.example.fu_self_learning_app.utils.APIErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button buttonLogin, buttonGoRegister;
//    Button buttonTest;
    AuthService authService = APIClient.getClient().create(AuthService.class);

    private void storeLoginDataToSharedPreferences(String accessToken, String refreshToken, UserInfo userInfo) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putString("access_token", accessToken);
        editor.putString("refresh_token", refreshToken);
        editor.putString("email", userInfo.getEmail());
        editor.putString("username", userInfo.getUsername());
        editor.putString("avatarUrl", userInfo.getAvatarUrl());
        editor.putString("role", userInfo.getRole());
        editor.putString("phone_number", userInfo.getPhoneNumber());
        editor.putInt("user_id", userInfo.getId()); // Lưu user_id cho chat
        editor.apply();
        
        Log.d("LoginActivity", "✅ Saved user_id: " + userInfo.getId() + " to SharedPreferences");
    }

    private void loginAsync(LoginRequest req) {
        Log.d("LoginActivity", "🚀 Starting login request for email: " + req.getEmail());
        
        authService.login(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d("LoginActivity", "📥 Login response received - Success: " + response.isSuccessful());
                
                if(response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse == null) {
                        Log.e("LoginActivity", "❌ Login response body is null!");
                        Toast.makeText(getApplicationContext(), "Error: Empty response from server", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    String accessToken = loginResponse.getAccessToken();
                    String refreshToken = loginResponse.getRefreshToken();
                    UserInfo userInfo = loginResponse.getUserInfo();
                    
                    Log.d("LoginActivity", "🔑 Access Token: " + (accessToken != null ? "Present" : "NULL"));
                    Log.d("LoginActivity", "🔄 Refresh Token: " + (refreshToken != null ? "Present" : "NULL"));
                    Log.d("LoginActivity", "👤 UserInfo: " + (userInfo != null ? "Present" : "NULL"));
                    
                    if (userInfo != null) {
                        Log.d("LoginActivity", "📋 UserInfo details - ID: " + userInfo.getId() + ", Username: " + userInfo.getUsername() + ", Email: " + userInfo.getEmail());
                    }
                    
                    storeLoginDataToSharedPreferences(accessToken, refreshToken, userInfo);
                    Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                    String role = userInfo.getRole();
                    Log.d("ROLE", role);
                    if(role.equals("student") || role.equals("instructor")) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if(role.equals("admin")) {
                        Intent intent = new Intent(LoginActivity.this, AdminHomePageActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Log.e("LoginActivity", "❌ Login failed - HTTP " + response.code() + ": " + response.message());
                    APIErrorUtils.handleError(getApplicationContext(), response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LoginActivity", "💥 Login API failure: " + t.getMessage(), t);
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        
        Log.d("LoginActivity", "🔐 Login button clicked - Email: " + email + ", Password length: " + password.length());
        
        if(email.isEmpty() || password.isEmpty()) {
            Log.w("LoginActivity", "⚠️ Empty email or password");
            Toast.makeText(getApplicationContext(), "Email and Password must be provided", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("LoginActivity", "✅ Calling loginAsync with email: " + email);
            loginAsync(new LoginRequest(email, password));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoRegister = findViewById(R.id.buttonGoRegister);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });


        buttonGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

//        buttonTest = findViewById(R.id.buttonTest);
//        buttonTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedPreferences prefs = getSharedPreferences("Auth", MODE_PRIVATE);
//                String accessToken = prefs.getString("access_token", "Not Found");
//                Log.d("ACCESS_TOKEN", accessToken);
//            }
//        });
    }
}
