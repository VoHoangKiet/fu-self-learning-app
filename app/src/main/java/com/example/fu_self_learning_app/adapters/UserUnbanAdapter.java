package com.example.fu_self_learning_app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.UserInfo;
import com.example.fu_self_learning_app.services.UserManagementService;
import com.example.fu_self_learning_app.utils.APIErrorUtils;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserUnbanAdapter extends RecyclerView.Adapter<UserUnbanAdapter.UserUnbanViewHolder> {
    Context context;
    List<UserInfo> userList;
    UserManagementService userManagementService;

    public UserUnbanAdapter(Context context, List<UserInfo> userList, UserManagementService userManagementService) {
        this.context = context;
        this.userList = userList;
        this.userManagementService = userManagementService;
    }

    public static class UserUnbanViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAvatar;
        TextView textUsername, textEmail, textPhone, textRole;
        Button buttonUnban;
        public UserUnbanViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.textUsername);
            textEmail = itemView.findViewById(R.id.textEmail);
            textPhone = itemView.findViewById(R.id.textPhone);
            textRole = itemView.findViewById(R.id.textRole);
            buttonUnban = itemView.findViewById(R.id.buttonUnban);
        }
    }

    @NonNull
    @Override
    public UserUnbanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_user_unban_item, parent, false);
        return new UserUnbanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserUnbanViewHolder holder, int position) {
        UserInfo user = userList.get(position);
        holder.textUsername.setText(user.getUsername());
        holder.textEmail.setText(user.getEmail());
        holder.textPhone.setText(user.getPhoneNumber());
        holder.textRole.setText(user.getRole());
        holder.buttonUnban.setOnClickListener(view -> {
            int currentPos = holder.getAdapterPosition();
            if(currentPos == RecyclerView.NO_POSITION) return;

            UserInfo currentUser = userList.get(currentPos);
            int userId = currentUser.getId();

            userManagementService.unbanUser(userId).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(context, "Unban Successfully", Toast.LENGTH_SHORT).show();
                        userList.remove(currentPos);
                        notifyItemRemoved(currentPos);
                    } else {
                        APIErrorUtils.handleError(context, response);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("API Failure", t.getMessage());
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
