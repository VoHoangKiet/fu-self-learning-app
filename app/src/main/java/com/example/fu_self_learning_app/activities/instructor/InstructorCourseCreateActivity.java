package com.example.fu_self_learning_app.activities.instructor;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.adapters.generate.TopicAdapter;
import com.example.fu_self_learning_app.models.Course;
import com.example.fu_self_learning_app.models.generate.GeneratedCourse;
import com.example.fu_self_learning_app.models.generate.GeneratedTopic;
import com.example.fu_self_learning_app.models.request.CreateTopicRequest;
import com.example.fu_self_learning_app.models.response.CourseDetailResponse;
import com.example.fu_self_learning_app.models.response.CourseGenerationResponse;
import com.example.fu_self_learning_app.network.APIClient;
import com.example.fu_self_learning_app.services.CourseGenerationService;
import com.example.fu_self_learning_app.utils.APIErrorUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstructorCourseCreateActivity extends AppCompatActivity {
    private static final int PICK_IMPORT_PDF_REQUEST = 1;
    private static final int PICK_DOCUMENT_PDF_REQUEST = 2;
    private static final int PICK_COURSE_IMAGE_REQUEST = 3;
    private static final int PICK_COURSE_VIDEO_REQUEST = 4;
    Button buttonImport;
    private Uri pdfUri;
    CourseGenerationService courseGenerationService;
    RecyclerView recyclerViewTopics;
    TopicAdapter topicAdapter;
    TextView textHeader;
    TextView textCourseTitle, textCourseDescription;
    Button buttonCreate, buttonAddDocument, buttonAddImage;
    GeneratedCourse course;
    List<GeneratedTopic> topics;
    MultipartBody.Part documentPart, imagePart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructor_activity_course_create);
        courseGenerationService = APIClient.getClient(this).create(CourseGenerationService.class);

        recyclerViewTopics = findViewById(R.id.recyclerViewTopics);
        recyclerViewTopics.setLayoutManager(new LinearLayoutManager(this));

        textHeader = findViewById(R.id.textHeader);
        textCourseTitle = findViewById(R.id.textCourseTitle);
        textCourseDescription = findViewById(R.id.textCourseDescription);

        buttonCreate = findViewById(R.id.buttonCreate);
        buttonCreate.setOnClickListener(view -> createCourse());

        buttonAddDocument = findViewById(R.id.buttonAddDocument);
        buttonAddDocument.setOnClickListener(view -> openAddDocumentChooser());

        buttonAddImage = findViewById(R.id.buttonAddImage);
        buttonAddImage.setOnClickListener(view -> openAddImageChooser());

        buttonImport = findViewById(R.id.buttonImport);
        buttonImport.setOnClickListener(view -> openImportPDFChooser());
    }

    private void createCourse() {
        RequestBody titlePart = createPartFromString(course.getTitle());
        RequestBody descriptionPart = createPartFromString(course.getDescription());
        List<Integer> categoryIds = course.getCategoryIds();

        File imageFile = null, videoFile = null, documentFile = null;
        try {
            imageFile = copyResourceToFile(getApplicationContext(), R.drawable.course_image, "course_image.png");
            videoFile = copyResourceToFile(getApplicationContext(), R.raw.course_intro, "course_intro.mp4");
            documentFile = copyResourceToFile(getApplicationContext(), R.drawable.course_image, "course_document.png");
        } catch(IOException e) {
            Log.e("COPY RESOURCE ERROR", e.getMessage());
        }

        if(imageFile == null || videoFile == null || documentFile == null) {
            return;
        }

//        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/png"), imageFile);
        RequestBody videoRequestBody = RequestBody.create(MediaType.parse("video/mp4"), videoFile);

//        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), imageRequestBody);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("video", videoFile.getName(), videoRequestBody);

        if(imagePart == null || videoPart == null || documentPart == null) {
            Toast.makeText(getApplicationContext(), "Image, Video, and Document are required", Toast.LENGTH_SHORT).show();
            return;
        }

        courseGenerationService.createCourse(titlePart, descriptionPart, categoryIds, imagePart, videoPart, documentPart).enqueue(new Callback<CourseDetailResponse>() {
            @Override
            public void onResponse(Call<CourseDetailResponse> call, Response<CourseDetailResponse> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Create Course successfully", Toast.LENGTH_SHORT).show();

                    CourseDetailResponse course1 = response.body();
                    if(course1 == null) {
                        Log.d("DEBUG", "course1 NULL");
                        return;
                    }

                    Log.d("DEBUG", course1.toString());
                    int courseId = course1.getId();
                    for(int i = 0; i < topics.size(); ++i) {
                        GeneratedTopic topic = topics.get(i);
                        courseGenerationService.createTopicInstructor(courseId, new CreateTopicRequest(topic.getTitle(), topic.getDescription())).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(!response.isSuccessful()) {
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
                } else {
                    APIErrorUtils.handleError(getApplicationContext(), response);
                }
            }

            @Override
            public void onFailure(Call<CourseDetailResponse> call, Throwable t) {
                Log.e("API Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    private RequestBody createPartFromString(String text) {
        return RequestBody.create(MultipartBody.FORM, text);
    }

    public File copyResourceToFile(Context context, int resourceId, String filename) throws IOException {
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        File outFile = new File(context.getCacheDir(), filename);
        FileOutputStream outputStream = new FileOutputStream(outFile);

        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();

        return outFile;
    }

    private void openAddDocumentChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Choose PDF"), PICK_DOCUMENT_PDF_REQUEST);
    }

    // mở File Picker để chọn File PDF
    private void openImportPDFChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // lấy dữ liệu từ user, từ các ứng dụng như File Manager, GG Drive
        intent.setType("application/pdf"); // only PDF allowed
        intent.addCategory(Intent.CATEGORY_OPENABLE); // chỉ các tệp có thể mở
        startActivityForResult(Intent.createChooser(intent, "Choose PDF"), PICK_IMPORT_PDF_REQUEST); // gọi activty, chờ kết quả File được trả về
    }

    private void openAddImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // lấy dữ liệu từ user, từ các ứng dụng như File Manager, GG Drive
        intent.setType("image/png"); // only PDF allowed
        intent.addCategory(Intent.CATEGORY_OPENABLE); // chỉ các tệp có thể mở
        startActivityForResult(Intent.createChooser(intent, "Choose Video"), PICK_COURSE_IMAGE_REQUEST); // gọi activty, chờ kết quả File được trả về
    }

    private void openAddVideoChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // lấy dữ liệu từ user, từ các ứng dụng như File Manager, GG Drive
        intent.setType("video/*"); // only PDF allowed
        intent.addCategory(Intent.CATEGORY_OPENABLE); // chỉ các tệp có thể mở
        startActivityForResult(Intent.createChooser(intent, "Choose Image"), PICK_COURSE_VIDEO_REQUEST); // gọi activty, chờ kết quả File được trả về
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMPORT_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData(); // lấy Content URI của file (do OS quản lý), dùng ContentResolver & đọc qua stream
            String fileName = getFileName(pdfUri);
            uploadPdf(pdfUri, fileName);
        } else if(requestCode == PICK_DOCUMENT_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            String fileName = getFileName(pdfUri);
            try {
                File file = createTempFileFromUri(pdfUri, fileName);
                RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
                documentPart = MultipartBody.Part.createFormData("document", file.getName(), requestFile);
                Toast.makeText(getApplicationContext(), "Add Document successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("API Failure", e.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == PICK_COURSE_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            String fileName = getFileName(pdfUri);
            try {
                File file = createTempFileFromUri(pdfUri, fileName);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), file);
                imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                Toast.makeText(getApplicationContext(), "Add Image successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("API Failure", e.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // lấy tên file từ Content URI
    private String getFileName(Uri uri) {
        String name = "file.pdf";
        // lấy thông tin metadata từ uri, cursor chứa các cột thông tin của File
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            // lấy cột DISPLAY_NAME, nếu ko có trả về lõi (getColumnIndexOrThrow)
            name = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
            cursor.close();
        }
        return name;
    }

    private void uploadPdf(Uri uri, String fileName) {
        try {
            File file = createTempFileFromUri(uri, fileName);
            RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file); // tạo RequestBody chứa dữ liệu file
            MultipartBody.Part part = MultipartBody.Part.createFormData("pdf", file.getName(), requestFile); // Bọc thành MultipartBody.Part với field name là "file"

            Call<CourseGenerationResponse> call = courseGenerationService.generateCourseFromPdf(part);
            call.enqueue(new Callback<CourseGenerationResponse>() {
                @Override
                public void onResponse(Call<CourseGenerationResponse> call, Response<CourseGenerationResponse> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(InstructorCourseCreateActivity.this, "Upload successfully, wait for response..", Toast.LENGTH_SHORT).show();
                        if(response.body() != null) {
                            course = response.body().getCourse();
                            topics = response.body().getTopics();
//                            Log.d("DEBUG", "course: " + course.toString());
//                            Log.d("DEBUG", "topics: " + topics.toString());

                            textHeader.setVisibility(TextView.VISIBLE);
                            textCourseTitle.setVisibility(TextView.VISIBLE);
                            textCourseDescription.setVisibility(TextView.VISIBLE);
                            textCourseTitle.setText(course.getTitle());
                            textCourseDescription.setText(course.getDescription());
                            buttonImport.setVisibility(View.INVISIBLE);
                            buttonCreate.setVisibility(View.VISIBLE);
                            buttonAddDocument.setVisibility(View.VISIBLE);
                            buttonAddImage.setVisibility(View.VISIBLE);
                            topicAdapter = new TopicAdapter(InstructorCourseCreateActivity.this, topics);
                            recyclerViewTopics.setAdapter(topicAdapter);
                        }

                    } else {
                        APIErrorUtils.handleError(getApplicationContext(), response);
                    }
                }

                @Override
                public void onFailure(Call<CourseGenerationResponse> call, Throwable t) {
                    Log.e("Upload PDF (API) error: ", t.getMessage());
                    Toast.makeText(InstructorCourseCreateActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e("ERROR", "Upload pdf error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Cannot read file, error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // tạo file tạm thời từ Uri, dùng stream để ghi và đọc
    private File createTempFileFromUri(Uri uri, String fileName) throws Exception {
        File tempFile = new File(getCacheDir(), fileName); // tạo một file trong thư mục cache của app
        InputStream inputStream = getContentResolver().openInputStream(uri); // object đọc dữ liệu từ File
        OutputStream outputStream = new FileOutputStream(tempFile); // object ghi dữ liệu vào tempFile
        byte[] buffer = new byte[4096]; // buffer tối đa chứa 4096 bytes
        int read;
        while ((read = inputStream.read(buffer)) != -1) { // đọc buffer, read == -1 nghĩa là đã đọc hết File
            outputStream.write(buffer, 0, read);
        }
        inputStream.close();
        outputStream.close();
        return tempFile;
    }
}
