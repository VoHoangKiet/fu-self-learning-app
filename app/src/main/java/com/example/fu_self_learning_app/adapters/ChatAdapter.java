package com.example.fu_self_learning_app.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Adapter cho RecyclerView hiển thị danh sách tin nhắn chat
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<ChatMessage> messages;
    private Context context;
    private int currentUserId;
    private SimpleDateFormat timeFormat;

    public ChatAdapter(Context context) {
        this.context = context;
        this.messages = new ArrayList<>();
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        
        // Lấy current user ID từ SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("Auth", Context.MODE_PRIVATE);
        this.currentUserId = prefs.getInt("user_id", -1);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        if (message.getSenderId() == currentUserId) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        
        if (viewType == VIEW_TYPE_SENT) {
            View view = inflater.inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        
        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
        notifyDataSetChanged();
    }
    
    /**
     * Replace optimistic message with confirmed message from server
     * (Giống React khi update message state từ server response)
     */
    public void replaceOptimisticMessage(ChatMessage confirmedMessage) {
        // Find optimistic message by message content and sender
        for (int i = messages.size() - 1; i >= 0; i--) {
            ChatMessage existing = messages.get(i);
            
            // Check if this is the optimistic message (temporary ID = -1)
            if (existing.getId() == -1 && 
                existing.getSenderId() == confirmedMessage.getSenderId() &&
                existing.getReceiverId() == confirmedMessage.getReceiverId() &&
                existing.getMessage().equals(confirmedMessage.getMessage())) {
                
                // Replace with confirmed message
                messages.set(i, confirmedMessage);
                notifyItemChanged(i);
                return;
            }
        }
        
        // If no optimistic message found, just add the confirmed message
        addMessage(confirmedMessage);
    }

    // ViewHolder cho tin nhắn đã gửi
    class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        TextView textTime;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message);
            textTime = itemView.findViewById(R.id.text_time);
        }

        public void bind(ChatMessage message) {
            textMessage.setText(message.getMessage());
            if (message.getCreatedAt() != null) {
                textTime.setText(timeFormat.format(message.getCreatedAt()));
            }
        }
    }

    // ViewHolder cho tin nhắn đã nhận
    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        TextView textTime;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message);
            textTime = itemView.findViewById(R.id.text_time);
        }

        public void bind(ChatMessage message) {
            textMessage.setText(message.getMessage());
            if (message.getCreatedAt() != null) {
                textTime.setText(timeFormat.format(message.getCreatedAt()));
            }
        }
    }
}
