package com.example.fu_self_learning_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.Post;
import com.squareup.picasso.Picasso;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Post post, int position);
    }
    private OnDeleteClickListener deleteClickListener;
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }
    private OnPostClickListener postClickListener;
    public void setOnPostClickListener(OnPostClickListener listener) {
        this.postClickListener = listener;
    }

    public interface OnLikeClickListener {
        void onLikeClick(Post post, int position);
    }
    private OnLikeClickListener likeClickListener;
    public void setOnLikeClickListener(OnLikeClickListener listener) {
        this.likeClickListener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        // Avatar
        if (post.getUser() != null && post.getUser().getAvatarUrl() != null && !post.getUser().getAvatarUrl().isEmpty()) {
            Picasso.get().load(post.getUser().getAvatarUrl())
                    .placeholder(R.drawable.placeholder_avatar)
                    .error(R.drawable.placeholder_avatar)
                    .into(holder.imageAvatar);
        } else {
            holder.imageAvatar.setImageResource(R.drawable.placeholder_avatar);
        }
        // Tên user
        holder.textUsername.setText(post.getUser() != null ? post.getUser().getUsername() : "");
        // Title và Body
        if (post.getTitle() != null && !post.getTitle().isEmpty()) {
            holder.textTitle.setText(post.getTitle());
        } else {
            holder.textTitle.setText(post.getBody());
        }
        // Ảnh post
        List<String> images = post.getImages();
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            holder.imagePost.setVisibility(View.VISIBLE);
            String imageUrl = images.get(0);
            if (!imageUrl.startsWith("http")) {
                imageUrl = "https://fu-self-learning-api-22235821035.asia-southeast1.run.app" + imageUrl;
            }
            android.util.Log.d("DEBUG_IMAGE_URL", "imageUrl = " + imageUrl);
            Picasso.get().load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imagePost);
        } else {
            holder.imagePost.setVisibility(View.GONE);
        }
        // Like/Comment với toggle functionality
        holder.textLikeCount.setText("Like: " + post.getLikeCount());
        holder.textCommentCount.setText("Comment: " + post.getCommentCount());
        
        // Set màu cho like text dựa trên trạng thái liked
        if (post.isLiked()) {
            holder.textLikeCount.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark));
        } else {
            holder.textLikeCount.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
        }
        
        // Click listener cho like
        holder.textLikeCount.setOnClickListener(v -> {
            if (likeClickListener != null) {
                likeClickListener.onLikeClick(post, position);
            }
        });
        holder.btnDelete.setOnClickListener(v -> {
            android.util.Log.d("DEBUG_DELETE_CLICK", "Clicked delete for postId: " + post.getId());
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(post, position);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (postClickListener != null) {
                postClickListener.onPostClick(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAvatar, imagePost, btnDelete;
        TextView textUsername, textTitle, textLikeCount, textCommentCount;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
            imagePost = itemView.findViewById(R.id.imagePost);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            textUsername = itemView.findViewById(R.id.textUsername);
            textTitle = itemView.findViewById(R.id.textTitle);
            textLikeCount = itemView.findViewById(R.id.textLikeCount);
            textCommentCount = itemView.findViewById(R.id.textCommentCount);
        }
    }
} 