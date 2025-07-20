package com.example.fu_self_learning_app.activities.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.adapters.InstructorRequestAdapter;
import com.example.fu_self_learning_app.models.InstructorRequest;
import com.example.fu_self_learning_app.network.APIClient;
import com.example.fu_self_learning_app.services.InstructorRequestService;
import com.example.fu_self_learning_app.utils.APIErrorUtils;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminInstructorRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InstructorRequestService instructorRequestService;
    private InstructorRequestAdapter instructorRequestAdapter;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_instructor_requests);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        instructorRequestService = APIClient.getClient(this).create(InstructorRequestService.class);
        instructorRequestService.viewAll().enqueue(new Callback<List<InstructorRequest>>() {
            @Override
            public void onResponse(Call<List<InstructorRequest>> call, Response<List<InstructorRequest>> response) {
                if(response.isSuccessful()) {
                    List<InstructorRequest> instructorRequestList = response.body();
                    if(instructorRequestList == null) {
                        Log.d("INSTRUCTOR REQUESTS", "NULL");
                    } else {
                        instructorRequestList = instructorRequestList.stream().filter(request -> request.getStatus().equals("pending")).collect(Collectors.toList());
                        instructorRequestAdapter = new InstructorRequestAdapter(AdminInstructorRequestActivity.this, instructorRequestList, instructorRequestService);
                        recyclerView.setAdapter(instructorRequestAdapter);
                    }
                } else {
                    APIErrorUtils.handleError(getApplicationContext(), response);
                }
            }

            @Override
            public void onFailure(Call<List<InstructorRequest>> call, Throwable t) {
                Log.e("API Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
