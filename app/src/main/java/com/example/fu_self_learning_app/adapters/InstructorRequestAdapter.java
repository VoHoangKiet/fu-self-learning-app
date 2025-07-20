package com.example.fu_self_learning_app.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.fu_self_learning_app.activities.admin.AdminViewInstructorRequestActivity;
import com.example.fu_self_learning_app.models.InstructorRequest;
import com.example.fu_self_learning_app.services.InstructorRequestService;
import com.example.fu_self_learning_app.utils.APIErrorUtils;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstructorRequestAdapter extends RecyclerView.Adapter<InstructorRequestAdapter.InstructorRequestViewHolder> {
    Context context;
    private List<InstructorRequest> instructorRequestList;
    private InstructorRequestService instructorRequestService;

    public InstructorRequestAdapter(Context context, List<InstructorRequest> instructorRequestList, InstructorRequestService instructorRequestService) {
        this.context = context;
        this.instructorRequestList = instructorRequestList;
        this.instructorRequestService = instructorRequestService;
    }

    public static class InstructorRequestViewHolder extends RecyclerView.ViewHolder {
        TextView textUsername, textCreatedAt;
        ImageView imageAvatar;
        Button buttonViewPdf, buttonApprove, buttonReject;

        public InstructorRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.textUsername);
            textCreatedAt = itemView.findViewById(R.id.textCreatedAt);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
            buttonViewPdf = itemView.findViewById(R.id.buttonViewPdf);
            buttonApprove = itemView.findViewById(R.id.buttonApprove);
            buttonReject = itemView.findViewById(R.id.buttonReject);
        }
    }

    @NonNull
    @Override
    public InstructorRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_instructor_request_item, parent, false);
        return new InstructorRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorRequestViewHolder holder, int position) {
        InstructorRequest instructorRequest = instructorRequestList.get(position);
        holder.textUsername.setText(instructorRequest.getUser().getUsername());
        holder.textCreatedAt.setText(instructorRequest.getCreatedAt().substring(0, 10));
        holder.buttonViewPdf.setOnClickListener(view -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            InstructorRequest instructorRequest1 = instructorRequestList.get(pos);

            Intent intent = new Intent(context, AdminViewInstructorRequestActivity.class);
            intent.putExtra("instructorRequestId", instructorRequest1.getId());
            context.startActivity(intent);
        });

        holder.buttonApprove.setOnClickListener(view -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            InstructorRequest instructorRequest1 = instructorRequestList.get(pos);

            instructorRequestService.approveRequest(instructorRequest1.getId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(context, "Approve Successfully", Toast.LENGTH_SHORT).show();
                        instructorRequestList.remove(pos);
                        notifyItemRemoved(pos);
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

        holder.buttonReject.setOnClickListener(view -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            InstructorRequest instructorRequest1 = instructorRequestList.get(pos);

            instructorRequestService.rejectRequest(instructorRequest1.getId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(context, "Reject Successfully", Toast.LENGTH_SHORT).show();
                        instructorRequestList.remove(pos);
                        notifyItemRemoved(pos);
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
        return instructorRequestList.size();
    }
}
