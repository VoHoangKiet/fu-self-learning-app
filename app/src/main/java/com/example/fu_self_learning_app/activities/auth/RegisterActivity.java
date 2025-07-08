package com.example.fu_self_learning_app.activities.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.request.RegisterRequest;
import com.example.fu_self_learning_app.network.APIClient;
import com.example.fu_self_learning_app.services.AuthService;
import com.example.fu_self_learning_app.utils.APIErrorUtils;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextFullName, editTextEmail, editTextPassword, editTextConfirmPassword;
    Button buttonRegister, buttonGoLogin;
    AuthService authService = APIClient.getClient().create(AuthService.class);

    private void callRegister(RegisterRequest req) {
        authService.register(req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // nếu status code là 200,201,...,299
                if(response.isSuccessful()) {
                    try {
                        // đọc plain text từ response
                        String message = response.body().string();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    APIErrorUtils.handleError(getApplicationContext(), response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void register() {
        String fullname = editTextFullName.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        if(fullname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Provide All Input Fields", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("DEBUG", "Call Register");
            callRegister(new RegisterRequest(fullname, email, password, confirmPassword));
//            Toast.makeText(getApplicationContext(), "Register successfully", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_register);

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        buttonRegister = findViewById(R.id.buttonRegister);
        buttonGoLogin = findViewById(R.id.buttonGoLogin);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        buttonGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
