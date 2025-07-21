package com.example.fu_self_learning_app.activities.instructor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.activities.admin.AdminInstructorRequestActivity;
import com.example.fu_self_learning_app.activities.admin.AdminUserBanActivity;
import com.example.fu_self_learning_app.activities.admin.AdminUserUnbanActivity;
import com.example.fu_self_learning_app.activities.auth.LoginActivity;
import com.google.android.material.navigation.NavigationView;

public class InstructorHomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView textViewWelcomeUser;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructor_activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        textViewWelcomeUser = findViewById(R.id.textViewWelcomeUser);

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

        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderUsername = headerView.findViewById(R.id.nav_header_username);

        SharedPreferences prefs = getSharedPreferences("Auth", MODE_PRIVATE);
        String username = prefs.getString("username", null);

        textViewWelcomeUser.setText("Welcome " + username);
        navHeaderUsername.setText(username);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_create_course) {
            Intent intent = new Intent(this, InstructorCourseCreateActivity.class);
            startActivity(intent);
        }

        if (id == R.id.nav_logout) {
            // Handle logout
            logout();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("Auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
