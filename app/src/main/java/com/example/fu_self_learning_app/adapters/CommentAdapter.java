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
    private OnReplyClickListener replyClickListener;
    private OnDeleteCommentClickListener deleteCommentClickListener;

    public interface OnReplyClickListener {
        void onReplyClick(Comment comment);
    }

    public interface OnDeleteCommentClickListener {
        void onDeleteCommentClick(Comment comment, int position);
    }

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public void setOnReplyClickListener(OnReplyClickListener listener) {
        this.replyClickListener = listener;
    }

    public void setOnDeleteCommentClickListener(OnDeleteCommentClickListener listener) {
        this.deleteCommentClickListener = listener;
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
        
        // Hiển thị thông tin comment
        holder.textUsername.setText(comment.getUser() != null ? comment.getUser().getUsername() : "Unknown");
        holder.textContent.setText(comment.getContent());
        holder.textTime.setText(comment.getCreatedAt());

        // Load avatar
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

        // Xử lý click Reply
        holder.textReply.setOnClickListener(v -> {
            if (replyClickListener != null) {
                replyClickListener.onReplyClick(comment);
            }
        });

        // Xử lý click Delete comment
        holder.btnDeleteComment.setOnClickListener(v -> {
            if (deleteCommentClickListener != null) {
                deleteCommentClickListener.onDeleteCommentClick(comment, holder.getAdapterPosition());
            }
        });

        // Hiển thị replies nếu có
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            holder.repliesContainer.setVisibility(View.VISIBLE);
            holder.recyclerViewReplies.setVisibility(View.VISIBLE);
            holder.textRepliesCount.setVisibility(View.GONE);
            
            // Tạo adapter cho replies
            ReplyAdapter replyAdapter = new ReplyAdapter(comment.getReplies());
            holder.recyclerViewReplies.setLayoutManager(new LinearLayoutManager(holder.recyclerViewReplies.getContext()));
            holder.recyclerViewReplies.setAdapter(replyAdapter);
            
            // Set reply click listener cho replies
            replyAdapter.setOnReplyClickListener(replyClickListener);
        } else {
            holder.repliesContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAvatar, btnDeleteComment;
        TextView textUsername, textContent, textTime, textReply, textRepliesCount;
        View repliesContainer;
        RecyclerView recyclerViewReplies;
        
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
            btnDeleteComment = itemView.findViewById(R.id.btnDeleteComment);
            textUsername = itemView.findViewById(R.id.textUsername);
            textContent = itemView.findViewById(R.id.textContent);
            textTime = itemView.findViewById(R.id.textTime);
            textReply = itemView.findViewById(R.id.textReply);
            textRepliesCount = itemView.findViewById(R.id.textRepliesCount);
            repliesContainer = itemView.findViewById(R.id.repliesContainer);
            recyclerViewReplies = itemView.findViewById(R.id.recyclerViewReplies);
        }
    }
    
    // Adapter riêng cho replies
    private static class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {
        private List<Comment> replies;
        private OnReplyClickListener replyClickListener;

        public ReplyAdapter(List<Comment> replies) {
            this.replies = replies;
        }

        public void setOnReplyClickListener(OnReplyClickListener listener) {
            this.replyClickListener = listener;
        }

        @NonNull
        @Override
        public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply, parent, false);
            return new ReplyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
            Comment reply = replies.get(position);
            
            holder.textUsername.setText(reply.getUser() != null ? reply.getUser().getUsername() : "Unknown");
            holder.textContent.setText(reply.getContent());
            holder.textTime.setText(reply.getCreatedAt());

            // Load avatar
            String avatarUrl = reply.getUser() != null ? reply.getUser().getAvatarUrl() : null;
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(holder.imageAvatar.getContext())
                    .load(avatarUrl)
                    .placeholder(R.drawable.placeholder_avatar)
                    .error(R.drawable.placeholder_avatar)
                    .into(holder.imageAvatar);
            } else {
                holder.imageAvatar.setImageResource(R.drawable.placeholder_avatar);
            }

            // Xử lý click Reply cho reply
            holder.textReply.setOnClickListener(v -> {
                if (replyClickListener != null) {
                    replyClickListener.onReplyClick(reply);
                }
            });
        }

        @Override
        public int getItemCount() {
            return replies.size();
        }

        static class ReplyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageAvatar;
            TextView textUsername, textContent, textTime, textReply;
            
            public ReplyViewHolder(@NonNull View itemView) {
                super(itemView);
                imageAvatar = itemView.findViewById(R.id.imageAvatar);
                textUsername = itemView.findViewById(R.id.textUsername);
                textContent = itemView.findViewById(R.id.textContent);
                textTime = itemView.findViewById(R.id.textTime);
                textReply = itemView.findViewById(R.id.textReply);
            }
        }
    }
} 