package com.example.fu_self_learning_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.Comment;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private int indentLevel;
    private OnReplyClickListener replyClickListener;
    public interface OnReplyClickListener {
        void onReplyClick(Comment comment);
    }
    public void setOnReplyClickListener(OnReplyClickListener listener) {
        this.replyClickListener = listener;
    }

    public CommentAdapter(List<Comment> commentList) {
        this(commentList, 0);
    }

    private CommentAdapter(List<Comment> commentList, int indentLevel) {
        this.commentList = commentList;
        this.indentLevel = indentLevel;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.textUsername.setText(comment.getUser() != null ? comment.getUser().getUsername() : "Unknown");
        holder.textContent.setText(comment.getContent());
        String avatarUrl = comment.getUser() != null ? comment.getUser().getAvatarUrl() : null;
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(holder.imageAvatar.getContext())
                .load(avatarUrl)
                .placeholder(R.drawable.placeholder_avatar)
                .error(R.drawable.placeholder_avatar)
                .into(holder.imageAvatar);
        } else {
            holder.imageAvatar.setImageResource(R.drawable.placeholder_avatar);
        }
        int margin = indentLevel * 40;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (params != null) {
            params.setMarginStart(margin);
            holder.itemView.setLayoutParams(params);
        }
        // Reply button click
        holder.textReply.setOnClickListener(v -> {
            if (replyClickListener != null) {
                replyClickListener.onReplyClick(comment);
            }
        });
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            holder.recyclerViewReplies.setVisibility(View.VISIBLE);
            holder.recyclerViewReplies.setLayoutManager(new LinearLayoutManager(holder.recyclerViewReplies.getContext()));
            CommentAdapter replyAdapter = new CommentAdapter(comment.getReplies(), indentLevel + 1);
            replyAdapter.setOnReplyClickListener(replyClickListener);
            holder.recyclerViewReplies.setAdapter(replyAdapter);
        } else {
            holder.recyclerViewReplies.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAvatar;
        TextView textUsername, textContent, textReply;
        RecyclerView recyclerViewReplies;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
            textUsername = itemView.findViewById(R.id.textUsername);
            textContent = itemView.findViewById(R.id.textContent);
            textReply = itemView.findViewById(R.id.textReply);
            recyclerViewReplies = itemView.findViewById(R.id.recyclerViewReplies);
        }
    }
} 