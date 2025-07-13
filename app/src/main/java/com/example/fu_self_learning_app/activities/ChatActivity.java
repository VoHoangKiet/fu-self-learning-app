package com.example.fu_self_learning_app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.adapters.ChatAdapter;
import com.example.fu_self_learning_app.models.ChatMessage;
import com.example.fu_self_learning_app.models.request.LoadMessagesRequest;
import com.example.fu_self_learning_app.models.request.SendMessageRequest;
import com.example.fu_self_learning_app.services.ChatSocketService;

import java.util.List;

// Activity hiển thị giao diện chat với Socket.IO
public class ChatActivity extends AppCompatActivity implements ChatSocketService.ChatEventListener {
    private static final String TAG = "ChatActivity";
    
    private RecyclerView recyclerMessages;
    private EditText editMessage;
    private ImageButton btnSend;
    private ImageButton btnBack;
    private TextView textChatTitle;
    private TextView textConnectionStatus;
    
    private ChatAdapter chatAdapter;
    private ChatSocketService chatService;
    
    private int currentUserId;
    private int receiverUserId;
    private String receiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        initViews();
        getIntentData();
        setupRecyclerView();
        setupSocketService();
        setupListeners();
        
        // Load tin nhắn cũ
        loadPreviousMessages();
    }

    private void initViews() {
        recyclerMessages = findViewById(R.id.recycler_messages);
        editMessage = findViewById(R.id.edit_message);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back);
        textChatTitle = findViewById(R.id.text_chat_title);
        textConnectionStatus = findViewById(R.id.text_connection_status);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        receiverUserId = intent.getIntExtra("receiverUserId", -1);
        receiverName = intent.getStringExtra("receiverName");
        
        if (receiverUserId == -1) {
            Toast.makeText(this, "Error: Invalid receiver ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Lấy current user ID từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("Auth", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);
        
        if (currentUserId == -1) {
            Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Set title
        if (!TextUtils.isEmpty(receiverName)) {
            textChatTitle.setText(receiverName);
        } else {
            textChatTitle.setText("User " + receiverUserId);
        }
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(this);
        recyclerMessages.setAdapter(chatAdapter);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Hiển thị tin nhắn mới nhất ở cuối
        recyclerMessages.setLayoutManager(layoutManager);
    }

    private void setupSocketService() {
        chatService = new ChatSocketService(this);
        chatService.setEventListener(this);
        chatService.connect();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Gửi tin nhắn khi nhấn Enter
        editMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String message = editMessage.getText().toString().trim();
        
        if (TextUtils.isEmpty(message)) {
            return;
        }
        
        if (!chatService.isConnected()) {
            Toast.makeText(this, "Not connected to chat server", Toast.LENGTH_SHORT).show();
            return;
        }
        
        SendMessageRequest request = new SendMessageRequest(currentUserId, receiverUserId, message);
        chatService.sendMessage(request);
        
        // Clear input
        editMessage.setText("");
    }

    private void loadPreviousMessages() {
        if (chatService.isConnected()) {
            LoadMessagesRequest request = new LoadMessagesRequest(currentUserId, receiverUserId);
            chatService.loadMessages(request);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatService != null) {
            chatService.disconnect();
        }
    }

    // ChatSocketService.ChatEventListener implementations
    @Override
    public void onNewMessage(ChatMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "New message received: " + message.getMessage());
                chatAdapter.addMessage(message);
                scrollToBottom();
            }
        });
    }

    @Override
    public void onMessagesLoaded(List<ChatMessage> messages) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Messages loaded: " + messages.size());
                chatAdapter.setMessages(messages);
                scrollToBottom();
            }
        });
    }

    @Override
    public void onMessageSent(ChatMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Message sent: " + message.getMessage());
                // Tin nhắn đã được gửi thành công
                // Có thể thêm indicator hoặc xử lý khác ở đây
            }
        });
    }

    @Override
    public void onConnectionError(String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "Connection error: " + error);
                textConnectionStatus.setText("Disconnected");
                Toast.makeText(ChatActivity.this, "Connection error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scrollToBottom() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (chatAdapter.getItemCount() > 0) {
                    recyclerMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }
        }, 100);
    }
    
    private void updateConnectionStatus(boolean connected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textConnectionStatus.setText(connected ? "Connected" : "Connecting...");
            }
        });
    }
}
