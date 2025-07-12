package com.example.fu_self_learning_app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.request.UserProfileRequest;
import com.example.fu_self_learning_app.models.response.UserProfileResponse;
import com.example.fu_self_learning_app.network.APIClient;
import com.example.fu_self_learning_app.services.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInformationActivity extends AppCompatActivity {

    private EditText edtUsername, edtPhoneNumber, edtAvatarUrl;
    private EditText edtEmail;

    private ImageView imgAvatar;
    private Button btnUpdate;

    private UserService userService;
    private String token;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        // Ánh xạ view
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtAvatarUrl = findViewById(R.id.edtAvatarUrl);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnUpdate = findViewById(R.id.btnUpdate);
        Button btnPickAvatar = findViewById(R.id.btnPickAvatar);
        Button btnChangePassword = findViewById(R.id.btnChangePassword);

        // Lấy token từ SharedPreferences (giống trong MainActivity)
        SharedPreferences prefs = getSharedPreferences("Auth", MODE_PRIVATE);
        token = "Bearer " + prefs.getString("access_token", ""); // 👈 Sửa key
        Log.d("DEBUG_TOKEN", token); // Ghi log token để kiểm tra

        // Khởi tạo retrofit service
        userService = APIClient.getClient(this).create(UserService.class);

        // Gọi API để lấy thông tin người dùng
        getUserProfile();

        // Cập nhật thông tin người dùng khi click nút Update
        btnUpdate.setOnClickListener(view -> updateUserProfile());

        // Mở gallery chọn avatar mới
        btnPickAvatar.setOnClickListener(view -> openGallery());

        // Chuyển sang ChangePasswordActivity
        btnChangePassword.setOnClickListener(view -> {
            Intent intent = new Intent(UserInformationActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
    }

    private void getUserProfile() {
        userService.getProfile(token).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
               if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse user = response.body();
                    Log.d("DEBUG_PROFILE", "Username: " + user.getUsername());
                    edtUsername.setText(user.getUsername());
                    edtEmail.setText(user.getEmail());
                    edtPhoneNumber.setText(user.getPhoneNumber());
                    edtAvatarUrl.setText(user.getAvatarUrl());


                    Glide.with(UserInformationActivity.this)
                            .load(user.getAvatarUrl())
                            .placeholder(R.drawable.placeholder_avatar)
                            .into(imgAvatar);
               } else {
                    Toast.makeText(UserInformationActivity.this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(UserInformationActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile() {
        String username = edtUsername.getText().toString();
        String phone = edtPhoneNumber.getText().toString();
        String avatarUrl = edtAvatarUrl.getText().toString();
        String email = edtEmail.getText().toString();
        UserProfileRequest request = new UserProfileRequest(username, phone, avatarUrl, email);

        userService.updateProfile(token, request).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserInformationActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    getUserProfile(); // Tải lại thông tin mới
                } else {
                    Toast.makeText(UserInformationActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(UserInformationActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            edtAvatarUrl.setText(imageUri.toString());
            Glide.with(this).load(imageUri).into(imgAvatar);
        }
    }
}
