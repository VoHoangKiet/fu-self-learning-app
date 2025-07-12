package com.example.fu_self_learning_app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.activities.auth.LoginActivity;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView textViewWelcomeUser;
    Button buttonLogout;
    Button buttonViewCourses;
    private boolean isLoggedIn;
    private String username;

    // Navigation components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    private void getLoginData() {
        SharedPreferences prefs = getSharedPreferences("Auth", MODE_PRIVATE);
        isLoggedIn = prefs.getBoolean("is_logged_in", false);
        username = prefs.getString("username", null);
        Log.d("DEBUG_TOKEN", username); // Ghi log token để kiểm tra

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

        // Initialize views
        textViewWelcomeUser = findViewById(R.id.textViewWelcomeUser);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonViewCourses = findViewById(R.id.buttonViewCourses);

        // Initialize navigation components
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Set up toolbar
        setSupportActionBar(toolbar);

        // Set up navigation drawer
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set navigation item selected listener
        navigationView.setNavigationItemSelectedListener(this);

        // Update navigation header with username
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderUsername = headerView.findViewById(R.id.nav_header_username);

        getLoginData();
        if(!isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if(username != null) {
            textViewWelcomeUser.setText("Welcome " + username);
            navHeaderUsername.setText(username);
        }

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonViewCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CoursesActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // 👉 Mở UserInformationActivity khi chọn "Profile"
            Intent intent = new Intent(MainActivity.this, UserInformationActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_courses) {
            // Navigate to courses
            Intent intent = new Intent(MainActivity.this, CoursesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            // Handle settings navigation
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.nav_social) {
            Intent intent = new Intent(MainActivity.this, SocialActivity.class);
            startActivity(intent); 
        }else if (id == R.id.nav_logout) {
            // Handle logout
            logout();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}