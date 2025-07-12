package com.example.fu_self_learning_app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.request.ChangePasswordRequest;
import com.example.fu_self_learning_app.models.response.UserProfileResponse;
import com.example.fu_self_learning_app.network.APIClient;
import com.example.fu_self_learning_app.services.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private Button btnChange;
    private UserService userService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edtOldPassword = findViewById(R.id.edtOldPassword);
        System.out.println(edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnChange = findViewById(R.id.btnChange);

        SharedPreferences prefs = getSharedPreferences("Auth", MODE_PRIVATE);
        token = "Bearer " + prefs.getString("access_token", "");

        // Dùng APIClient thay vì RetrofitClient
        userService = APIClient.getClient(getApplicationContext()).create(UserService.class);

        btnChange.setOnClickListener(v -> {
            String oldPass = edtOldPassword.getText().toString();
            Log.d("DEBUG_OLD_PASSWORD", "Old password: " + oldPass);
            String newPass = edtNewPassword.getText().toString();
            String confirmPass = edtConfirmPassword.getText().toString();

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Xác nhận mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            ChangePasswordRequest request = new ChangePasswordRequest(oldPass, newPass,confirmPass);
            userService.changePassword(token, request).enqueue(new Callback<UserProfileResponse>() {
                @Override
                public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Sai mật khẩu cũ!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                    Toast.makeText(ChangePasswordActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
        Button btnBackToInfo = findViewById(R.id.btnBackToInfo);
        btnBackToInfo.setOnClickListener(view -> {
            finish(); // Đóng ChangePasswordActivity, trở lại màn trước đó (UserInformationActivity)
        });

    }
}
