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
import io.socket.client.Ack;
import io.socket.emitter.Emitter;

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
        void onSocketConnected();
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
            
            SharedPreferences prefs = context.getSharedPreferences("Auth", Context.MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);
            
            Log.d(TAG, "🔐 Initializing socket with userId: " + userId);
            
            if (userId != -1) {
                options.query = "userId=" + userId;
                Log.d(TAG, "🔐 Query string: userId=" + userId);
            } else {
                Log.w(TAG, "⚠️ No userId found in SharedPreferences!");
            }
            
            socket = IO.socket(SOCKET_URL, options);
            setupEventListeners();
            
        } catch (URISyntaxException e) {
            Log.e(TAG, "❌ Error creating socket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupEventListeners() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Socket connected successfully");
                if (eventListener != null) {
                    eventListener.onSocketConnected();
                }
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Socket disconnected, attempting reconnect in 3 seconds");
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (socket != null && !socket.connected()) {
                            Log.d(TAG, "Attempting to reconnect socket");
                            socket.connect();
                        }
                    }
                }, 3000);
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "Socket connection error: " + (args.length > 0 ? args[0].toString() : "Unknown error"));
                if (eventListener != null) {
                    eventListener.onConnectionError("Connection failed: " + (args.length > 0 ? args[0].toString() : "Unknown error"));
                }
            }
        });

        socket.on("newMessage", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Received newMessage event");
                if (args.length > 0 && eventListener != null) {
                    try {
                        Log.d(TAG, "newMessage data: " + args[0].toString());
                        JsonObject jsonObject = new JsonParser().parse(args[0].toString()).getAsJsonObject();
                        ChatMessage message = parseChatMessage(jsonObject);
                        Log.d(TAG, "Parsed message ID: " + message.getId() + ", content: " + message.getMessage());
                        eventListener.onNewMessage(message);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing newMessage: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });

        socket.on("messagesLoaded", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Received messagesLoaded event");
                if (args.length > 0 && eventListener != null) {
                    try {
                        Log.d(TAG, "messagesLoaded data: " + args[0].toString());
                        Type listType = new TypeToken<List<JsonObject>>(){}.getType();
                        List<JsonObject> jsonMessages = gson.fromJson(args[0].toString(), listType);
                        List<ChatMessage> messages = new ArrayList<>();
                        
                        for (JsonObject jsonObj : jsonMessages) {
                            ChatMessage message = parseChatMessage(jsonObj);
                            messages.add(message);
                        }
                        
                        Log.d(TAG, "Loaded " + messages.size() + " messages");
                        eventListener.onMessagesLoaded(messages);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing messagesLoaded: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });

        socket.on("messageSent", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Received messageSent event");
                if (args.length > 0 && eventListener != null) {
                    try {
                        Log.d(TAG, "messageSent data: " + args[0].toString());
                        JsonObject jsonObject = new JsonParser().parse(args[0].toString()).getAsJsonObject();
                        ChatMessage message = parseChatMessage(jsonObject);
                        Log.d(TAG, "Message sent successfully - ID: " + message.getId());
                        eventListener.onMessageSent(message);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing messageSent: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });

        // Listen for server errors
        socket.on("error", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "Received server error event");
                if (args.length > 0) {
                    Log.e(TAG, "Server error: " + args[0].toString());
                    if (eventListener != null) {
                        eventListener.onConnectionError("Server error: " + args[0].toString());
                    }
                }
            }
        });

        // Listen for sendMessage errors specifically
        socket.on("sendMessageError", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "Received sendMessageError event");
                if (args.length > 0) {
                    Log.e(TAG, "Send message error: " + args[0].toString());
                    if (eventListener != null) {
                        eventListener.onConnectionError("Send message failed: " + args[0].toString());
                    }
                }
            }
        });

        // Debug: Listen to common events for debugging
        socket.on("connect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "🔔 Debug - connect event received");
            }
        });

        socket.on("disconnect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "🔔 Debug - disconnect event received");
            }
        });

        // Listen for any potential response/acknowledgment events
        socket.on("messageReceived", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "🔔 Debug - messageReceived event");
                if (args.length > 0) {
                    Log.d(TAG, "🔔 messageReceived data: " + args[0].toString());
                }
            }
        });

        socket.on("ack", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "🔔 Debug - ack event");
                if (args.length > 0) {
                    Log.d(TAG, "🔔 ack data: " + args[0].toString());
                }
            }
        });

        // Listen for authentication/authorization events
        socket.on("unauthorized", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "🚫 Unauthorized event received");
                if (args.length > 0) {
                    Log.e(TAG, "🚫 Unauthorized data: " + args[0].toString());
                }
                if (eventListener != null) {
                    eventListener.onConnectionError("Unauthorized access");
                }
            }
        });

        socket.on("forbidden", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "🚫 Forbidden event received");
                if (args.length > 0) {
                    Log.e(TAG, "🚫 Forbidden data: " + args[0].toString());
                }
                if (eventListener != null) {
                    eventListener.onConnectionError("Access forbidden");
                }
            }
        });

        // Listen for validation errors
        socket.on("validationError", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "⚠️ Validation error received");
                if (args.length > 0) {
                    Log.e(TAG, "⚠️ Validation error data: " + args[0].toString());
                }
                if (eventListener != null) {
                    eventListener.onConnectionError("Validation error: " + (args.length > 0 ? args[0].toString() : "Unknown"));
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
                message.setCreatedAt(new Date());
            }
        } else {
            message.setCreatedAt(new Date());
        }
        
        return message;
    }

    public void connect() {
        Log.d(TAG, "Attempting to connect socket");
        if (socket != null) {
            if (!socket.connected()) {
                Log.d(TAG, "Socket not connected, connecting...");
                socket.connect();
            } else {
                Log.d(TAG, "Socket already connected");
            }
        } else {
            Log.e(TAG, "Socket is null, reinitializing...");
            initSocket();
            if (socket != null) {
                socket.connect();
            }
        }
    }

    public void disconnect() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
        }
    }

    public void sendMessage(SendMessageRequest request) {
        Log.d(TAG, "Attempting to send message - Connected: " + (socket != null && socket.connected()));
        
        // Debug: Check current userId in socket connection
        SharedPreferences prefs = context.getSharedPreferences("Auth", Context.MODE_PRIVATE);
        int currentUserId = prefs.getInt("user_id", -1);
        Log.d(TAG, "🔐 Current userId from SharedPreferences: " + currentUserId);
        
        // Validate request data first (matching server DTO validation)
        if (request.getSenderUserId() <= 0) {
            Log.e(TAG, "❌ Invalid senderUserId: " + request.getSenderUserId());
            if (eventListener != null) {
                eventListener.onConnectionError("Invalid sender ID");
            }
            return;
        }
        
        if (request.getReceiverUserId() <= 0) {
            Log.e(TAG, "❌ Invalid receiverUserId: " + request.getReceiverUserId());
            if (eventListener != null) {
                eventListener.onConnectionError("Invalid receiver ID");
            }
            return;
        }
        
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            Log.e(TAG, "❌ Invalid message: empty or null");
            if (eventListener != null) {
                eventListener.onConnectionError("Message cannot be empty");
            }
            return;
        }
        
        // Validate that senderUserId matches current user
        if (request.getSenderUserId() != currentUserId) {
            Log.w(TAG, "⚠️ SenderUserId (" + request.getSenderUserId() + ") does not match current userId (" + currentUserId + ")");
        }
        
        if (socket != null && socket.connected()) {
            // Create JSON string manually for better compatibility
            String jsonData = String.format(
                "{\"senderUserId\":%d,\"receiverUserId\":%d,\"message\":\"%s\"}",
                request.getSenderUserId(),
                request.getReceiverUserId(),
                request.getMessage().trim().replace("\"", "\\\"") // Escape quotes
            );
            
            Log.d(TAG, "✅ Validation passed - Sending message data: " + jsonData);
            Log.d(TAG, "📊 Request details - SenderUserId: " + request.getSenderUserId() + " (type: int)");
            Log.d(TAG, "📊 Request details - ReceiverUserId: " + request.getReceiverUserId() + " (type: int)");
            Log.d(TAG, "📊 Request details - Message: '" + request.getMessage() + "' (type: String)");
            Log.d(TAG, "🔗 Socket connection query was: userId=" + currentUserId);
            
            // Track acknowledgment timeout
            final boolean[] ackReceived = {false};
            android.os.Handler timeoutHandler = new android.os.Handler(android.os.Looper.getMainLooper());
            
            // Send with acknowledgment callback
            socket.emit("sendMessage", jsonData, new Ack() {
                @Override
                public void call(Object... args) {
                    ackReceived[0] = true;
                    Log.d(TAG, "🔔 SendMessage acknowledgment received");
                    if (args.length > 0) {
                        Log.d(TAG, "🔔 Ack data: " + args[0].toString());
                    } else {
                        Log.d(TAG, "🔔 Ack received but no data");
                    }
                }
            });
            
            // Set timeout to check if ack was received
            timeoutHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!ackReceived[0]) {
                        Log.w(TAG, "⚠️ No acknowledgment received after 5 seconds - server may not be responding");
                        Log.w(TAG, "⚠️ This suggests server received request but failed to process or respond");
                        if (eventListener != null) {
                            eventListener.onConnectionError("Server not responding to message");
                        }
                    }
                }
            }, 5000); // 5 second timeout
            
            Log.d(TAG, "Message emit completed");
        } else {
            Log.e(TAG, "Cannot send message - Socket not connected");
            if (eventListener != null) {
                eventListener.onConnectionError("Socket not connected");
            }
        }
    }

    public void loadMessages(LoadMessagesRequest request) {
        Log.d(TAG, "Attempting to load messages - Connected: " + (socket != null && socket.connected()));
        
        if (socket != null && socket.connected()) {
            // Create JSON string for consistency
            String jsonData = String.format(
                "{\"senderUserId\":%d,\"receiverUserId\":%d}",
                request.getSenderUserId(),
                request.getReceiverUserId()
            );
            
            Log.d(TAG, "Loading messages with data: " + jsonData);
            socket.emit("loadMessages", jsonData);
            Log.d(TAG, "Load messages emit completed");
        } else {
            Log.e(TAG, "Cannot load messages - Socket not connected");
            if (eventListener != null) {
                eventListener.onConnectionError("Socket not connected");
            }
        }
    }

    public void setEventListener(ChatEventListener listener) {
        this.eventListener = listener;
    }

    public boolean isConnected() {
        return socket != null && socket.connected();
    }
}
