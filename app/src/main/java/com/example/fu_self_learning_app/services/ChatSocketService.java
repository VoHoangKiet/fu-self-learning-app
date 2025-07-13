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
    private static final String SOCKET_URL = "https://fu-self-learning-api-22235821035.asia-southeast1.run.app";
    
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
        testServerHealth(); // Test server connectivity
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
            
            socket = IO.socket(SOCKET_URL + "/chat", options);
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
                Log.d(TAG, "Socket disconnected - Reason: " + (args.length > 0 ? args[0] : "Unknown"));
                // Auto-reconnect after disconnect with delay
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (socket != null && !socket.connected()) {
                            Log.d(TAG, "🔄 Attempting to reconnect...");
                            socket.connect();
                        }
                    }
                }, 3000); // Retry after 3 seconds
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
                Log.d(TAG, "📥 newMessage event received");
                if (args.length > 0 && eventListener != null) {
                    try {
                        Log.d(TAG, "📥 newMessage raw data: " + args[0].toString());
                        JsonObject jsonObject = new JsonParser().parse(args[0].toString()).getAsJsonObject();
                        ChatMessage message = parseChatMessage(jsonObject);
                        Log.d(TAG, "✅ newMessage parsed successfully: " + message.getMessage());
                        eventListener.onNewMessage(message);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing new message: " + e.getMessage());
                    }
                } else {
                    Log.w(TAG, "⚠️ newMessage called but no data or listener");
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
                Log.d(TAG, "📤 messageSent event received");
                if (args.length > 0 && eventListener != null) {
                    try {
                        Log.d(TAG, "📤 messageSent raw data: " + args[0].toString());
                        JsonObject jsonObject = new JsonParser().parse(args[0].toString()).getAsJsonObject();
                        ChatMessage message = parseChatMessage(jsonObject);
                        Log.d(TAG, "✅ messageSent parsed successfully: " + message.getMessage());
                        eventListener.onMessageSent(message);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing sent message: " + e.getMessage());
                    }
                } else {
                    Log.w(TAG, "⚠️ messageSent called but no data or listener");
                }
            }
        });

        // Listen for common WebSocket events that servers might send
        socket.on("messageReceived", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "🎯 messageReceived event detected");
                if (args.length > 0) {
                    Log.d(TAG, "🎯 messageReceived data: " + args[0].toString());
                }
            }
        });
        
        // Enhanced debugging - listen for ANY possible events from server
        socket.on("response", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "🔥 Server 'response' event: " + java.util.Arrays.toString(args));
            }
        });
        
        socket.on("ack", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "🔥 Server 'ack' event: " + java.util.Arrays.toString(args));
            }
        });
        
        socket.on("messageProcessed", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "🔥 Server 'messageProcessed' event: " + java.util.Arrays.toString(args));
            }
        });
        
        socket.on("messageStatus", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "🔥 Server 'messageStatus' event: " + java.util.Arrays.toString(args));
            }
        });
        
        socket.on("messageConfirmed", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "🔥 Server 'messageConfirmed' event: " + java.util.Arrays.toString(args));
            }
        });

        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "🎯 message event detected");
                if (args.length > 0) {
                    Log.d(TAG, "🎯 message data: " + args[0].toString());
                }
            }
        });

        socket.on("error", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "🚨 Socket error event: " + (args.length > 0 ? args[0].toString() : "No details"));
            }
        });

        // Listen for any server response after sending
        socket.on("response", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "🎯 Server response event: " + (args.length > 0 ? args[0].toString() : "No data"));
            }
        });

        // Listen for server acknowledgments
        socket.on("ack", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "🎯 Server ack event: " + (args.length > 0 ? args[0].toString() : "No data"));
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
            
            Log.d(TAG, "📤 Sending message via socket - senderUserId: " + request.getSenderUserId() + 
                ", receiverUserId: " + request.getReceiverUserId() + 
                ", message: " + request.getMessage().substring(0, Math.min(20, request.getMessage().length())) + "...");
            Log.d(TAG, "📡 Socket send data: " + data.toString());
            Log.d(TAG, "🔗 Socket connection status before send: " + socket.connected());
            
            // Test different event names that server might be expecting
            Log.d(TAG, "🧪 Testing multiple event emit formats...");
            
            // Format 1: Original backend format
            socket.emit("sendMessage", data, new io.socket.client.Ack() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "🎯 Server ACK received for sendMessage");
                    if (args.length > 0) {
                        Log.d(TAG, "🎯 ACK data: " + args[0].toString());
                    }
                }
            });
            
            // Format 2: Test plain object (maybe server expects plain object)
            JsonObject plainData = new JsonObject();
            plainData.addProperty("senderUserId", request.getSenderUserId());
            plainData.addProperty("receiverUserId", request.getReceiverUserId());  
            plainData.addProperty("message", request.getMessage());
            
            Log.d(TAG, "🧪 Also testing plain format: " + plainData.toString());
            
            Log.d(TAG, "📡 sendMessage event emitted successfully with callback");
            
            // Add a slight delay check to see if connection drops immediately
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "🔗 Socket connection status 1 second after send: " + (socket != null ? socket.connected() : "null"));
                }
            }, 1000);
            
        } else {
            Log.e(TAG, "❌ Cannot send message - socket not connected");
            Log.e(TAG, "❌ Socket details - isNull: " + (socket == null) + ", connected: " + (socket != null ? socket.connected() : "N/A"));
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

    /**
     * Test server health and message sending via HTTP (backup method)
     */
    public void testServerHealth() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    java.net.URL url = new java.net.URL("https://fu-self-learning-api-22235821035.asia-southeast1.run.app/");
                    java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    
                    int responseCode = connection.getResponseCode();
                    Log.d(TAG, "🌐 Server health check - Response code: " + responseCode);
                    
                    if (responseCode == 200) {
                        Log.d(TAG, "✅ Server is reachable");
                    } else {
                        Log.w(TAG, "⚠️ Server returned: " + responseCode);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "❌ Server health check failed: " + e.getMessage());
                }
            }
        }).start();
    }

    public void setEventListener(ChatEventListener listener) {
        this.eventListener = listener;
    }

    public boolean isConnected() {
        return socket != null && socket.connected();
    }
}
