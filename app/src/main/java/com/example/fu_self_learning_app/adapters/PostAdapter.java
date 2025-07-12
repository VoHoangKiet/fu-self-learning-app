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
        if (post.getImage() != null && !post.getImage().isEmpty()) {
            holder.imagePost.setVisibility(View.VISIBLE);
            Picasso.get().load(post.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imagePost);
        } else {
            holder.imagePost.setVisibility(View.GONE);
        }
        // Like/Comment
        holder.textLikeCount.setText("Like: " + post.getLikeCount());
        holder.textCommentCount.setText("Comment: " + post.getCommentCount());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAvatar, imagePost;
        TextView textUsername, textTitle, textLikeCount, textCommentCount;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
            imagePost = itemView.findViewById(R.id.imagePost);
            textUsername = itemView.findViewById(R.id.textUsername);
            textTitle = itemView.findViewById(R.id.textTitle);
            textLikeCount = itemView.findViewById(R.id.textLikeCount);
            textCommentCount = itemView.findViewById(R.id.textCommentCount);
        }
    }
} 