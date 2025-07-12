package com.example.fu_self_learning_app.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.adapters.PostAdapter;
import com.example.fu_self_learning_app.models.Post;
import com.example.fu_self_learning_app.models.UserInfo;
import java.util.ArrayList;
import java.util.List;

public class SocialActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        postList = getMockPosts();
        postAdapter = new PostAdapter(postList);
        recyclerViewPosts.setAdapter(postAdapter);
    }

    // Mock data
    private List<Post> getMockPosts() {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Post post = new Post();
            post.setId(i);
            UserInfo user = new UserInfo();
            user.getUsername();
            post.setUser(user);
            post.setContent("Đây là bài post số " + i);
            post.setCreatedAt("2024-07-13");
            post.setLikeCount(i * 3);
            post.setCommentCount(i * 2);
            post.setLiked(i % 2 == 0);
            posts.add(post);
        }
        return posts;
    }
} 