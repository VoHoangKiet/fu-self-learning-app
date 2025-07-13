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

import java.util.ArrayList;
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
    private boolean shouldLoadMessages = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        initViews();
        getIntentData();
        setupRecyclerView();
        setupSocketService();
        setupListeners();
        
        // Đánh dấu rằng cần load tin nhắn khi socket connected
        shouldLoadMessages = true;
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
        
        Log.d(TAG, "🎯 Intent data - receiverUserId: " + receiverUserId + ", receiverName: " + receiverName);
        
        if (receiverUserId == -1) {
            Log.e(TAG, "❌ Invalid receiver ID");
            Toast.makeText(this, "Error: Invalid receiver ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Lấy current user ID từ SharedPreferences với debug logging
        SharedPreferences prefs = getSharedPreferences("Auth", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);
        
        Log.d(TAG, "👤 Current user ID from SharedPreferences: " + currentUserId);
        Log.d(TAG, "🔍 DEBUG: All Auth SharedPreferences values:");
        Log.d(TAG, "  - user_id: " + prefs.getInt("user_id", -999));
        Log.d(TAG, "  - email: " + prefs.getString("email", "NULL"));
        Log.d(TAG, "  - username: " + prefs.getString("username", "NULL"));
        Log.d(TAG, "  - is_logged_in: " + prefs.getBoolean("is_logged_in", false));
        
        if (currentUserId == -1) {
            Log.e(TAG, "❌ Not logged in - user_id not found in SharedPreferences");
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
        Log.d(TAG, "🔄 loadPreviousMessages called - Socket connected: " + chatService.isConnected());
        if (chatService.isConnected()) {
            LoadMessagesRequest request = new LoadMessagesRequest(currentUserId, receiverUserId);
            Log.d(TAG, "📨 Requesting to load messages - currentUserId: " + currentUserId + ", receiverUserId: " + receiverUserId);
            chatService.loadMessages(request);
            shouldLoadMessages = false; // Reset flag
        } else {
            Log.w(TAG, "⚠️ Socket not connected, cannot load messages yet");
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
        // Filter messages - chỉ hiển thị messages của current chat (giống React code)
        boolean isFromCurrentChat = isMessageFromCurrentChat(message);
        
        if (!isFromCurrentChat) {
            Log.d(TAG, "📝 Ignoring message not from current chat - SenderId: " + message.getSenderId() + ", ReceiverId: " + message.getReceiverId());
            return;
        }
        
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "✅ New message received for current chat: " + message.getMessage());
                chatAdapter.addMessage(message);
                scrollToBottom();
            }
        });
    }

    @Override
    public void onMessagesLoaded(List<ChatMessage> messages) {
        Log.d(TAG, "📥 onMessagesLoaded called with " + messages.size() + " messages");
        Log.d(TAG, "🔍 Current chat context - currentUserId: " + currentUserId + ", receiverUserId: " + receiverUserId);
        
        // Filter loaded messages cho current chat
        List<ChatMessage> filteredMessages = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            ChatMessage message = messages.get(i);
            boolean isFromCurrentChat = isMessageFromCurrentChat(message);
            
            Log.d(TAG, "📨 Message " + i + ": senderId=" + message.getSenderId() + 
                ", receiverId=" + message.getReceiverId() + 
                ", isFromCurrentChat=" + isFromCurrentChat +
                ", message=" + message.getMessage().substring(0, Math.min(20, message.getMessage().length())) + "...");
                
            if (isFromCurrentChat) {
                filteredMessages.add(message);
            }
        }
        
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "✅ Messages loaded for current chat: " + filteredMessages.size() + "/" + messages.size());
                chatAdapter.setMessages(filteredMessages);
                scrollToBottom();
            }
        });
    }

    @Override
    public void onMessageSent(ChatMessage message) {
        // Cũng cần filter cho sent messages
        boolean isFromCurrentChat = isMessageFromCurrentChat(message);
        
        if (!isFromCurrentChat) {
            Log.d(TAG, "📝 Ignoring sent message not from current chat");
            return;
        }
        
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "✅ Message sent successfully: " + message.getMessage());
                chatAdapter.addMessage(message);
                scrollToBottom();
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
    
    @Override
    public void onSocketConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "✅ Socket connected successfully");
                textConnectionStatus.setText("Connected");
                textConnectionStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                
                // Load messages khi socket đã connected
                if (shouldLoadMessages) {
                    Log.d(TAG, "🔄 Socket connected, loading previous messages...");
                    loadPreviousMessages();
                }
            }
        });
    }
    
    /**
     * Kiểm tra message có thuộc current chat không (giống logic React)
     * Logic: (sender === currentUserId && receiver === receiverUserId) || 
     *        (sender === receiverUserId && receiver === currentUserId)
     */
    private boolean isMessageFromCurrentChat(ChatMessage message) {
        int senderId = message.getSenderId();
        int receiverId = message.getReceiverId();
        
        boolean isOutgoing = (senderId == currentUserId && receiverId == receiverUserId);
        boolean isIncoming = (senderId == receiverUserId && receiverId == currentUserId);
        
        Log.d(TAG, "🔍 Message filter - SenderId: " + senderId + ", ReceiverId: " + receiverId + 
              ", CurrentUserId: " + currentUserId + ", ReceiverUserId: " + receiverUserId + 
              ", IsOutgoing: " + isOutgoing + ", IsIncoming: " + isIncoming);
        
        return isOutgoing || isIncoming;
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
