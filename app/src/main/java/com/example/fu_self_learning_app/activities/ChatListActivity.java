package com.example.fu_self_learning_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fu_self_learning_app.R;

import java.util.ArrayList;
import java.util.List;

// Activity hiển thị danh sách users để chat (demo)
public class ChatListActivity extends AppCompatActivity {
    
    private ListView listViewUsers;
    private ImageButton btnBack;
    private TextView textTitle;
    
    private List<UserItem> userItems;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        
        initViews();
        setupUsersList();
        setupListeners();
    }

    private void initViews() {
        listViewUsers = findViewById(R.id.list_users);
        btnBack = findViewById(R.id.btn_back);
        textTitle = findViewById(R.id.text_title);
        
        textTitle.setText("Choose User to Chat");
    }

    private void setupUsersList() {
        // Demo data - trong thực tế sẽ load từ API
        userItems = new ArrayList<>();
        userItems.add(new UserItem(1, "Alice", "alice@example.com"));
        userItems.add(new UserItem(2, "Bob", "bob@example.com"));
        userItems.add(new UserItem(3, "Charlie", "charlie@example.com"));
        userItems.add(new UserItem(4, "Diana", "diana@example.com"));
        userItems.add(new UserItem(5, "Eve", "eve@example.com"));
        
        List<String> userNames = new ArrayList<>();
        for (UserItem user : userItems) {
            userNames.add(user.getName() + " (" + user.getEmail() + ")");
        }
        
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNames);
        listViewUsers.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserItem selectedUser = userItems.get(position);
                openChatActivity(selectedUser);
            }
        });
    }

    private void openChatActivity(UserItem user) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("receiverUserId", user.getId());
        intent.putExtra("receiverName", user.getName());
        startActivity(intent);
    }
    
    // Lớp model đơn giản cho user
    private static class UserItem {
        private int id;
        private String name;
        private String email;
        
        public UserItem(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
        
        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }
}
