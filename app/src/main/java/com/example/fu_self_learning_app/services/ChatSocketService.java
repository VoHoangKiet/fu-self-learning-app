package com.example.fu_self_learning_app.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.fu_self_learning_app.models.ChatMessage;
import com.example.fu_self_learning_app.models.request.LoadMessagesRequest;
import com.example.fu_self_learning_app.models.request.SendMessageRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

// Service quản lý WebSocket chat với Socket.IO
public class ChatSocketService {
    private static final String TAG = "ChatSocketService";
    private static final String SOCKET_URL = "https://fu-self-learning-api-22235821035.asia-southeast1.run.app/chat";
    
    private Socket socket;
    private Context context;
    private Gson gson;
    private ChatEventListener eventListener;
    private SimpleDateFormat dateFormat;

    public interface ChatEventListener {
        void onNewMessage(ChatMessage message);
        void onMessagesLoaded(List<ChatMessage> messages);
        void onMessageSent(ChatMessage message);
        void onConnectionError(String error);
        void onSocketConnected(); // Thêm method mới
    }

    public ChatSocketService(Context context) {
        this.context = context;
        this.gson = new Gson();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        initSocket();
    }

    private void initSocket() {
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            
            // Lấy userId từ SharedPreferences
            SharedPreferences prefs = context.getSharedPreferences("Auth", Context.MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);
            
            Log.d(TAG, "🔧 Initializing socket with userId: " + userId);
            
            if (userId != -1) {
                options.query = "userId=" + userId;
                Log.d(TAG, "✅ Added userId to socket query: " + options.query);
            } else {
                Log.w(TAG, "⚠️ No userId found in SharedPreferences");
            }
            
            socket = IO.socket(SOCKET_URL, options);
            setupEventListeners();
            
            Log.d(TAG, "🚀 Socket initialized successfully");
            
        } catch (URISyntaxException e) {
            Log.e(TAG, "❌ Socket initialization error: " + e.getMessage());
        }
    }

    private void setupEventListeners() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Socket connected");
                if (eventListener != null) {
                    eventListener.onSocketConnected();
                }
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Socket disconnected");
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "Socket connection error: " + args[0]);
                if (eventListener != null) {
                    eventListener.onConnectionError("Connection failed");
                }
            }
        });

        socket.on("newMessage", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args.length > 0 && eventListener != null) {
                    try {
                        JsonObject jsonObject = new JsonParser().parse(args[0].toString()).getAsJsonObject();
                        ChatMessage message = parseChatMessage(jsonObject);
                        eventListener.onNewMessage(message);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing new message: " + e.getMessage());
                    }
                }
            }
        });

        socket.on("messagesLoaded", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args.length > 0 && eventListener != null) {
                    try {
                        Log.d(TAG, "📥 messagesLoaded event received, raw data: " + args[0].toString());
                        
                        Type listType = new TypeToken<List<JsonObject>>(){}.getType();
                        List<JsonObject> jsonMessages = gson.fromJson(args[0].toString(), listType);
                        List<ChatMessage> messages = new ArrayList<>();
                        
                        Log.d(TAG, "🔢 Total messages loaded: " + jsonMessages.size());
                        
                        for (int i = 0; i < jsonMessages.size(); i++) {
                            JsonObject jsonObj = jsonMessages.get(i);
                            ChatMessage message = parseChatMessage(jsonObj);
                            messages.add(message);
                            Log.d(TAG, "📨 Message " + i + ": senderId=" + message.getSenderId() + 
                                ", receiverId=" + message.getReceiverId() + 
                                ", message=" + message.getMessage().substring(0, Math.min(20, message.getMessage().length())) + "...");
                        }
                        
                        eventListener.onMessagesLoaded(messages);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing loaded messages: " + e.getMessage());
                    }
                } else {
                    Log.w(TAG, "⚠️ messagesLoaded called but no data received or no listener");
                }
            }
        });

        socket.on("messageSent", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args.length > 0 && eventListener != null) {
                    try {
                        JsonObject jsonObject = new JsonParser().parse(args[0].toString()).getAsJsonObject();
                        ChatMessage message = parseChatMessage(jsonObject);
                        eventListener.onMessageSent(message);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing sent message: " + e.getMessage());
                    }
                }
            }
        });
    }

    private ChatMessage parseChatMessage(JsonObject jsonObject) {
        ChatMessage message = new ChatMessage();
        
        if (jsonObject.has("id")) {
            message.setId(jsonObject.get("id").getAsInt());
        }
        
        if (jsonObject.has("senderUserId")) {
            message.setSenderId(jsonObject.get("senderUserId").getAsInt());
        } else if (jsonObject.has("senderId")) {
            message.setSenderId(jsonObject.get("senderId").getAsInt());
        }
        
        if (jsonObject.has("receiverUserId")) {
            message.setReceiverId(jsonObject.get("receiverUserId").getAsInt());
        } else if (jsonObject.has("receiverId")) {
            message.setReceiverId(jsonObject.get("receiverId").getAsInt());
        }
        
        if (jsonObject.has("message")) {
            message.setMessage(jsonObject.get("message").getAsString());
        }
        
        if (jsonObject.has("createdAt")) {
            try {
                String dateStr = jsonObject.get("createdAt").getAsString();
                Date date = dateFormat.parse(dateStr);
                message.setCreatedAt(date);
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date: " + e.getMessage());
                message.setCreatedAt(new Date());
            }
        } else {
            message.setCreatedAt(new Date());
        }
        
        return message;
    }

    public void connect() {
        if (socket != null && !socket.connected()) {
            socket.connect();
        }
    }

    public void disconnect() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
        }
    }

    public void sendMessage(SendMessageRequest request) {
        if (socket != null && socket.connected()) {
            JsonObject data = new JsonObject();
            data.addProperty("senderUserId", request.getSenderUserId());
            data.addProperty("receiverUserId", request.getReceiverUserId());
            data.addProperty("message", request.getMessage());
            
            socket.emit("sendMessage", data);
        }
    }

    public void loadMessages(LoadMessagesRequest request) {
        if (socket != null && socket.connected()) {
            JsonObject data = new JsonObject();
            data.addProperty("senderUserId", request.getSenderUserId());
            data.addProperty("receiverUserId", request.getReceiverUserId());
            
            Log.d(TAG, "📨 Loading messages - senderUserId: " + request.getSenderUserId() + ", receiverUserId: " + request.getReceiverUserId());
            socket.emit("loadMessages", data);
        } else {
            Log.e(TAG, "❌ Cannot load messages - socket not connected");
        }
    }

    public void setEventListener(ChatEventListener listener) {
        this.eventListener = listener;
    }

    public boolean isConnected() {
        return socket != null && socket.connected();
    }
}
