package com.example.fu_self_learning_app.activities.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.network.APIClient;
import com.example.fu_self_learning_app.services.InstructorRequestService;
import com.example.fu_self_learning_app.utils.APIErrorUtils;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminViewInstructorRequestActivity extends AppCompatActivity {
    private PDFView pdfView;
    private InstructorRequestService instructorRequestService;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_view_instructor_request);
        instructorRequestService = APIClient.getClient(this).create(InstructorRequestService.class);
        Intent intent = getIntent();
        int instructorRequestId = intent.getIntExtra("instructorRequestId", -1);
        Log.d("DEBUG", "instructorRequestId: " + instructorRequestId);
        pdfView = findViewById(R.id.pdfView);
        instructorRequestService.streamPdf(instructorRequestId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    try {
                        InputStream inputStream = response.body().byteStream();
                        pdfView.fromStream(inputStream)
                                .enableSwipe(true)
                                .swipeHorizontal(false)
                                .load();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    APIErrorUtils.handleError(getApplicationContext(), response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
