package com.example.fu_self_learning_app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.activities.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {
    TextView textViewWelcomeUser;
    Button buttonLogout;
    Button buttonViewCourses;
    private boolean isLoggedIn;
    private String username;

    private void getLoginData() {
        SharedPreferences prefs = getSharedPreferences("Auth", MODE_PRIVATE);
        isLoggedIn = prefs.getBoolean("is_logged_in", false);
        username = prefs.getString("username", null);
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("Auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewWelcomeUser = findViewById(R.id.textViewWelcomeUser);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonViewCourses = findViewById(R.id.buttonViewCourses);
        getLoginData();
        if(!isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        
        buttonViewCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CoursesActivity.class);
                startActivity(intent);
            }
        });
        if(username != null) {
            textViewWelcomeUser.setText("Welcome " + username);
        }
    }
}