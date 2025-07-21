package com.example.fu_self_learning_app.activities.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.adapters.UserBanAdapter;
import com.example.fu_self_learning_app.adapters.UserUnbanAdapter;
import com.example.fu_self_learning_app.models.UserInfo;
import com.example.fu_self_learning_app.network.APIClient;
import com.example.fu_self_learning_app.services.UserManagementService;
import com.example.fu_self_learning_app.utils.APIErrorUtils;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserUnbanActivity extends AppCompatActivity {
    RecyclerView recyclerViewUsers;
    UserUnbanAdapter userUnbanAdapter;
    UserManagementService userManagementService;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user_management);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        userManagementService = APIClient.getClient(this).create(UserManagementService.class);
        userManagementService.getAllUsers().enqueue(new Callback<List<UserInfo>>() {
            @Override
            public void onResponse(Call<List<UserInfo>> call, Response<List<UserInfo>> response) {
                List<UserInfo> userList = response.body();
                if(response.isSuccessful()) {
                    if(userList == null) {
                        Log.d("USERS", "NULL");
                    } else {
                        userList = userList.stream().filter(user -> !user.getActive()).collect(Collectors.toList());
                        userUnbanAdapter = new UserUnbanAdapter(AdminUserUnbanActivity.this, userList, userManagementService);
                        recyclerViewUsers.setAdapter(userUnbanAdapter);
                    }
                } else {
                    APIErrorUtils.handleError(getApplicationContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<UserInfo>> call, Throwable t) {
                Log.e("API Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
