package com.example.fu_self_learning_app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.FollowerUser;
import com.example.fu_self_learning_app.network.APIClient;
import com.example.fu_self_learning_app.services.FollowService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Activity hiển thị danh sách users để chat từ API /follow/followers
public class ChatListActivity extends AppCompatActivity {
    private static final String TAG = "ChatListActivity";
    
    private ListView listViewUsers;
    private ImageButton btnBack;
    private TextView textTitle;
    
    private List<FollowerUser> followerUsers;
    private ArrayAdapter<String> adapter;
    private FollowService followService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        
        initViews();
        initService();
        setupListeners();
        loadFollowers();
    }

    private void initViews() {
        listViewUsers = findViewById(R.id.list_users);
        btnBack = findViewById(R.id.btn_back);
        textTitle = findViewById(R.id.text_title);
        
        textTitle.setText("Choose User to Chat");
    }
    
    private void initService() {
        followService = APIClient.getClient().create(FollowService.class);
    }

    private void loadFollowers() {
        // Lấy access token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("Auth", MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", null);
        
        if (accessToken == null) {
            Log.e(TAG, "❌ No access token found");
            Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        Log.d(TAG, "🔑 Loading followers with token: " + accessToken.substring(0, Math.min(20, accessToken.length())) + "...");
        
        // Gọi API /follow/followers
        followService.getFollowers("Bearer " + accessToken).enqueue(new Callback<List<FollowerUser>>() {
            @Override
            public void onResponse(Call<List<FollowerUser>> call, Response<List<FollowerUser>> response) {
                Log.d(TAG, "📥 Followers API response - Success: " + response.isSuccessful() + ", Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    followerUsers = response.body();
                    Log.d(TAG, "✅ Loaded " + followerUsers.size() + " followers");
                    setupUsersList();
                } else {
                    Log.e(TAG, "❌ API call failed - HTTP " + response.code());
                    setupDemoUsers(); // Fallback to demo data
                }
            }

            @Override
            public void onFailure(Call<List<FollowerUser>> call, Throwable t) {
                Log.e(TAG, "💥 API call failure: " + t.getMessage(), t);
                Toast.makeText(ChatListActivity.this, "Network error, using demo data", Toast.LENGTH_SHORT).show();
                setupDemoUsers(); // Fallback to demo data
            }
        });
    }

    private void setupUsersList() {
        if (followerUsers == null || followerUsers.isEmpty()) {
            Log.w(TAG, "⚠️ No followers to display");
            Toast.makeText(this, "No followers found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Tạo adapter với tên users - handle null values safely
        List<String> userNames = new ArrayList<>();
        for (int i = 0; i < followerUsers.size(); i++) {
            FollowerUser follower = followerUsers.get(i);
            String displayName;
            
            if (follower == null || follower.getFollowingUser() == null) {
                displayName = "Unknown User " + (i + 1);
                Log.w(TAG, "⚠️ Null follower or followingUser at index " + i);
            } else {
                FollowerUser.UserDetails user = follower.getFollowingUser();
                if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
                    displayName = user.getUsername().trim();
                } else if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                    displayName = user.getEmail().trim();
                } else {
                    displayName = "User " + user.getId();
                    Log.w(TAG, "⚠️ User " + user.getId() + " has null username and email");
                }
            }
            
            userNames.add(displayName);
            Log.d(TAG, "👤 Follower " + i + ": " + displayName + " (Following ID: " + 
                (follower != null && follower.getFollowingUser() != null ? follower.getFollowingUser().getId() : "null") + ")");
        }
        
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNames);
        listViewUsers.setAdapter(adapter);
        
        // Xử lý click item
        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FollowerUser selectedFollower = followerUsers.get(position);
                openChat(selectedFollower);
            }
        });
    }
    
    private void setupDemoUsers() {
        Log.d(TAG, "🎭 Setting up demo users as fallback");
        
        List<String> demoNames = new ArrayList<>();
        demoNames.add("Alice (Demo)");
        demoNames.add("Bob (Demo)");
        demoNames.add("Charlie (Demo)");
        demoNames.add("Diana (Demo)");
        demoNames.add("Eve (Demo)");
        
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, demoNames);
        listViewUsers.setAdapter(adapter);
        
        // Demo click handler
        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openDemoChat(position + 1, demoNames.get(position).replace(" (Demo)", ""));
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private void openChat(FollowerUser follower) {
        if (follower == null || follower.getFollowingUser() == null) {
            Log.e(TAG, "❌ Cannot open chat - follower or followingUser is null");
            Toast.makeText(this, "Error: Invalid user", Toast.LENGTH_SHORT).show();
            return;
        }
        
        FollowerUser.UserDetails user = follower.getFollowingUser();
        String userName = user.getUsername() != null && !user.getUsername().trim().isEmpty() 
            ? user.getUsername().trim()
            : (user.getEmail() != null && !user.getEmail().trim().isEmpty() 
                ? user.getEmail().trim() 
                : "User " + user.getId());
                
        Log.d(TAG, "🚀 Opening chat with user: " + userName + " (ID: " + user.getId() + ")");
        
        Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
        intent.putExtra("receiverUserId", user.getId());
        intent.putExtra("receiverName", userName);
        startActivity(intent);
    }
    
    private void openDemoChat(int userId, String userName) {
        Log.d(TAG, "🎭 Opening demo chat with: " + userName + " (ID: " + userId + ")");
        
        Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
        intent.putExtra("receiverUserId", userId);
        intent.putExtra("receiverName", userName);
        startActivity(intent);
    }
}
