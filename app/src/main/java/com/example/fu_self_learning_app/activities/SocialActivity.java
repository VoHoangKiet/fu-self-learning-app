package com.example.fu_self_learning_app.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.adapters.PostAdapter;
import com.example.fu_self_learning_app.models.Post;
import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.fu_self_learning_app.models.UserInfo;
import com.example.fu_self_learning_app.network.APIClient;
import com.example.fu_self_learning_app.services.SocialService;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class SocialActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private SocialService socialService;

    private static final int PICK_IMAGE_REQUEST = 1001;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        recyclerViewPosts.setAdapter(postAdapter);

        // Lấy avatarUrl từ SharedPreferences và load vào header
        SharedPreferences prefs = getSharedPreferences("Auth", MODE_PRIVATE);
        String avatarUrl = prefs.getString("avatarUrl", "");
        ImageView imageAvatar = findViewById(R.id.imageAvatar);
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.placeholder_avatar)
                .error(R.drawable.placeholder_avatar)
                .into(imageAvatar);
        } else {
            imageAvatar.setImageResource(R.drawable.placeholder_avatar);
        }

        findViewById(R.id.fabAddPost).setOnClickListener(v -> showCreatePostDialog());

        // Khởi tạo SocialService với authentication
        socialService = APIClient.getClient(this).create(SocialService.class);

        // Load posts từ API
        loadPostsFromApi();
        
        // Test button để tạo bài viết đơn giản
        findViewById(R.id.editPostContent).setOnLongClickListener(v -> {
            testCreateSimplePost();
            return true;
        });
    }

    private void loadPostsFromApi() {
        socialService.getAllPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    postList.addAll(response.body());
                    postAdapter.notifyDataSetChanged();
                    Log.d("DEBUG_POST", "Loaded " + postList.size() + " posts from API");
                } else {
                    Log.e("DEBUG_POST", "Failed to load posts: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e("DEBUG_POST", "Error loading posts: " + t.getMessage());
            }
        });
    }

    private void showCreatePostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_post, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        ImageView imagePostPreview = dialogView.findViewById(R.id.imagePostPreview);
        Button btnPickImage = dialogView.findViewById(R.id.btnPickImage);
        EditText editPostTitle = dialogView.findViewById(R.id.editPostTitle);
        EditText editPostContent = dialogView.findViewById(R.id.editPostBody);
        Button btnCreatePost = dialogView.findViewById(R.id.btnCreatePost);

        btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
            // Lưu dialog và imagePostPreview để update sau khi chọn ảnh
            imagePostPreview.setTag("dialog:" + dialog.hashCode());
            getWindow().getDecorView().setTag(R.id.imagePostPreview, imagePostPreview);
        });

        btnCreatePost.setOnClickListener(v -> {
            String title = editPostTitle.getText().toString().trim();
            String content = editPostContent.getText().toString().trim();
            Log.d("DEBUG_POST", "Title: " + title + ", Content: " + content);
            
            if (TextUtils.isEmpty(title)) {
                editPostTitle.setError("Vui lòng nhập tiêu đề");
                return;
            }
            
            if (TextUtils.isEmpty(content)) {
                editPostContent.setError("Vui lòng nhập nội dung");
                return;
            }
            
            // Tạo RequestBody cho các trường
            RequestBody titleBody = RequestBody.create(okhttp3.MediaType.parse("text/plain"), title);
            RequestBody bodyBody = RequestBody.create(okhttp3.MediaType.parse("text/plain"), content);
            RequestBody statusBody = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "published");
            
            // Chuẩn bị danh sách ảnh
            List<MultipartBody.Part> imageParts = new ArrayList<>();
            if (selectedImageUri != null) {
                try {
                    // Tạo file từ Uri
                    File imageFile = createTempFileFromUri(selectedImageUri);
                    RequestBody imageRequestBody = RequestBody.create(okhttp3.MediaType.parse("image/*"), imageFile);
                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData("images", imageFile.getName(), imageRequestBody);
                    imageParts.add(imagePart);
                } catch (Exception e) {
                    Log.e("DEBUG_POST", "Error processing image: " + e.getMessage());
                }
            }
            
            // Gọi API tạo post
            Log.d("DEBUG_POST", "Calling API with title: " + title + ", body: " + content + ", images: " + imageParts.size());
            socialService.createPostWithImages(titleBody, bodyBody, statusBody, imageParts)
                .enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        Log.d("DEBUG_POST", "API Response Code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("DEBUG_POST", "Post created successfully via API! Post ID: " + response.body().getId());
                            // Thêm post mới vào đầu danh sách
                            postList.add(0, response.body());
                            postAdapter.notifyItemInserted(0);
                            recyclerViewPosts.scrollToPosition(0);
                            dialog.dismiss();
                            selectedImageUri = null;
                        } else {
                            Log.e("DEBUG_POST", "Failed to create post: " + response.code());
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                Log.e("DEBUG_POST", "Error body: " + errorBody);
                            } catch (Exception e) {
                                Log.e("DEBUG_POST", "Error reading error body: " + e.getMessage());
                            }
                            // Fallback: thêm post vào local list
                            createLocalPost(title, content);
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {
                        Log.e("DEBUG_POST", "Error creating post: " + t.getMessage());
                        // Fallback: thêm post vào local list
                        createLocalPost(title, content);
                        dialog.dismiss();
                    }
                });
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            // Lấy lại dialog và imagePostPreview
            ImageView imagePostPreview = (ImageView) getWindow().getDecorView().getTag(R.id.imagePostPreview);
            if (imagePostPreview != null && selectedImageUri != null) {
                imagePostPreview.setImageURI(selectedImageUri);
            }
        }
    }

    private File createTempFileFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("post_image", ".jpg", getCacheDir());
        OutputStream outputStream = new FileOutputStream(tempFile);
        
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        
        inputStream.close();
        outputStream.close();
        return tempFile;
    }

    private void createLocalPost(String title, String content) {
        // Fallback: tạo post local khi API fail
        SharedPreferences prefs = getSharedPreferences("Auth", MODE_PRIVATE);
        UserInfo user = new UserInfo();
        user.setUsername(prefs.getString("username", ""));
        user.setAvatarUrl(prefs.getString("avatarUrl", ""));
        
        Post newPost = new Post();
        newPost.setUser(user);
        newPost.setTitle(title);
        newPost.setBody(content);
        newPost.setCreatedAt("Bây giờ");
        newPost.setLikeCount(0);
        newPost.setCommentCount(0);
        // Thêm imageList
        List<String> imageList = new ArrayList<>();
        if (selectedImageUri != null) {
            imageList.add(selectedImageUri.toString());
        }
        newPost.setImages(imageList);
        
        postList.add(0, newPost);
        postAdapter.notifyItemInserted(0);
        recyclerViewPosts.scrollToPosition(0);
        selectedImageUri = null;
    }

    private void testCreateSimplePost() {
        Log.d("DEBUG_POST", "Testing simple post creation...");
        
        // Test 1: Multipart API
        RequestBody titleBody = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "Test Post");
        RequestBody bodyBody = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "This is a test post from Android app");
        RequestBody statusBody = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "published");
        List<MultipartBody.Part> imageParts = new ArrayList<>();
        
        socialService.createPostWithImages(titleBody, bodyBody, statusBody, imageParts)
            .enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    Log.d("DEBUG_POST", "Multipart API Response Code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("DEBUG_POST", "Multipart post created successfully! ID: " + response.body().getId());
                        loadPostsFromApi();
                    } else {
                        Log.e("DEBUG_POST", "Multipart post failed: " + response.code());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            Log.e("DEBUG_POST", "Multipart error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e("DEBUG_POST", "Error reading multipart error body: " + e.getMessage());
                        }
                        // Test 2: Simple API
                        testSimpleAPI();
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Log.e("DEBUG_POST", "Multipart post error: " + t.getMessage());
                    // Test 2: Simple API
                    testSimpleAPI();
                }
            });
    }

    private void testSimpleAPI() {
        Log.d("DEBUG_POST", "Testing simple API...");
        
        Post testPost = new Post();
        testPost.setTitle("Simple Test Post");
        testPost.setBody("This is a simple test post");
        testPost.setStatus("published");
        
        socialService.createSimplePost(testPost)
            .enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    Log.d("DEBUG_POST", "Simple API Response Code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("DEBUG_POST", "Simple post created successfully! ID: " + response.body().getId());
                        loadPostsFromApi();
                    } else {
                        Log.e("DEBUG_POST", "Simple post failed: " + response.code());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            Log.e("DEBUG_POST", "Simple error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e("DEBUG_POST", "Error reading simple error body: " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Log.e("DEBUG_POST", "Simple post error: " + t.getMessage());
                }
            });
    }
} 