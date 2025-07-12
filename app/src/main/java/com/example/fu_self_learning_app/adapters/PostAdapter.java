package com.example.fu_self_learning_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.Post;
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
        holder.textUsername.setText(post.getUser() != null ? post.getUser().getUsername() : "");
        holder.textContent.setText(post.getContent());
        holder.textLikeCount.setText("Like: " + post.getLikeCount());
        holder.textCommentCount.setText("Comment: " + post.getCommentCount());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textUsername, textContent, textLikeCount, textCommentCount;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.textUsername);
            textContent = itemView.findViewById(R.id.textContent);
            textLikeCount = itemView.findViewById(R.id.textLikeCount);
            textCommentCount = itemView.findViewById(R.id.textCommentCount);
        }
    }
} 