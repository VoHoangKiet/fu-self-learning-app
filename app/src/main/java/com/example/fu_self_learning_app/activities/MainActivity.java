package com.example.fu_self_learning_app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.activities.admin.AdminHomePageActivity;
import com.example.fu_self_learning_app.activities.auth.LoginActivity;
import com.example.fu_self_learning_app.activities.instructor.InstructorHomePageActivity;
import com.example.fu_self_learning_app.network.APIClient;
import com.example.fu_self_learning_app.services.InstructorRequestService;
import com.example.fu_self_learning_app.utils.APIErrorUtils;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView textViewWelcomeUser;
    Button buttonLogout;
    Button buttonViewCourses;
    private boolean isLoggedIn;
    private String username, role;

    // Navigation components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    Button buttonInstructorRequest;
    private static final int PICK_PDF_REQUEST = 1;
    private TextView textFileName;
    private Uri pdfUri;
    private InstructorRequestService instructorRequestService;

    private void getLoginData() {
        SharedPreferences prefs = getSharedPreferences("Auth", MODE_PRIVATE);
        isLoggedIn = prefs.getBoolean("is_logged_in", false);
        username = prefs.getString("username", null);
        role = prefs.getString("role", null);
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
        buttonInstructorRequest = findViewById(R.id.buttonInstructorRequest);

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
        } else {
            if(role.equals("admin")) {
                Intent intent = new Intent(MainActivity.this, AdminHomePageActivity.class);
                startActivity(intent);
                finish();
                return;
            } else if(role.equals("instructor")) {
                Intent intent = new Intent(MainActivity.this, InstructorHomePageActivity.class);
                startActivity(intent);
                finish();
                return;
            }
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

        instructorRequestService = APIClient.getClient(getApplicationContext()).create(InstructorRequestService.class);
        buttonInstructorRequest.setOnClickListener(v -> openFileChooser());
    }

    // mở File Picker để chọn File PDF
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // lấy dữ liệu từ user, từ các ứng dụng như File Manager, GG Drive
        intent.setType("application/pdf"); // only PDF allowed
        intent.addCategory(Intent.CATEGORY_OPENABLE); // chỉ các tệp có thể mở
        startActivityForResult(Intent.createChooser(intent, "Choose PDF"), PICK_PDF_REQUEST); // gọi activty, chờ kết quả File được trả về
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData(); // lấy Content URI của file (do OS quản lý), dùng ContentResolver & đọc qua stream
            String fileName = getFileName(pdfUri);
//            Log.d("DEBUG", String.valueOf(pdfUri));
//            Log.d("DEBUG", fileName);
            uploadPdf(pdfUri, fileName);
//            textFileName.setText("Đã chọn: " + fileName);
//            uploadPdf(pdfUri, fileName);
        }
    }

    // lấy tên file từ Content URI
    private String getFileName(Uri uri) {
        String name = "file.pdf";
        // lấy thông tin metadata từ uri, cursor chứa các cột thông tin của File
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            // lấy cột DISPLAY_NAME, nếu ko có trả về lõi (getColumnIndexOrThrow)
            name = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
            cursor.close();
        }
        return name;
    }

    private void uploadPdf(Uri uri, String fileName) {
        try {
            File file = createTempFileFromUri(uri, fileName);
            RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file); // tạo RequestBody chứa dữ liệu file
            MultipartBody.Part part = MultipartBody.Part.createFormData("pdf", file.getName(), requestFile); // Bọc thành MultipartBody.Part với field name là "file"

            Call<ResponseBody> call = instructorRequestService.uploadPdf(part);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Upload successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        APIErrorUtils.handleError(getApplicationContext(), response);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Upload PDF (API) error: ", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e("ERROR", "Upload pdf error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Cannot read file, error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // tạo file tạm thời từ Uri, dùng stream để ghi và đọc
    private File createTempFileFromUri(Uri uri, String fileName) throws Exception {
        File tempFile = new File(getCacheDir(), fileName); // tạo một file trong thư mục cache của app
        InputStream inputStream = getContentResolver().openInputStream(uri); // object đọc dữ liệu từ File
        OutputStream outputStream = new FileOutputStream(tempFile); // object ghi dữ liệu vào tempFile
        byte[] buffer = new byte[4096]; // buffer tối đa chứa 4096 bytes
        int read;
        while ((read = inputStream.read(buffer)) != -1) { // đọc buffer, read == -1 nghĩa là đã đọc hết File
            outputStream.write(buffer, 0, read);
        }
        inputStream.close();
        outputStream.close();
        return tempFile;
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