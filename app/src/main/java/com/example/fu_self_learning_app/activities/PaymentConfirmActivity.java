package com.example.fu_self_learning_app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fu_self_learning_app.R;
import com.example.fu_self_learning_app.models.request.EnrollmentRequest;
import com.example.fu_self_learning_app.models.request.OrderRequest;
import com.example.fu_self_learning_app.models.response.CourseDetailResponse;
import com.example.fu_self_learning_app.models.response.EnrollmentResponse;
import com.example.fu_self_learning_app.models.response.OrderResponse;
import com.example.fu_self_learning_app.network.APIClient;
import com.example.fu_self_learning_app.services.CourseService;
import com.example.fu_self_learning_app.utils.PayOSHelper;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentConfirmActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "course_id";
    public static final String EXTRA_COURSE_TITLE = "course_title";
    public static final String EXTRA_COURSE_PRICE = "course_price";
    public static final String EXTRA_COURSE_IMAGE = "course_image";
    private static final String TAG = "PaymentConfirmActivity";

    // Views
    private ImageView btnBack;
    private ImageView imageViewCourseImage;
    private TextView textViewCourseTitle;
    private TextView textViewCoursePrice;
    private TextView textViewSubtotal;
    private TextView textViewTotal;
    private LinearLayout layoutPayOSOption;
    private RadioButton radioPayOS;
    private Button btnConfirmPayment;

    // Data
    private int courseId;
    private String courseTitle;
    private String coursePrice;
    private String courseImageUrl;
    private CourseService courseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirm);

        // Get data from intent
        courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
        courseTitle = getIntent().getStringExtra(EXTRA_COURSE_TITLE);
        coursePrice = getIntent().getStringExtra(EXTRA_COURSE_PRICE);
        courseImageUrl = getIntent().getStringExtra(EXTRA_COURSE_IMAGE);

        if (courseId == -1) {
            Toast.makeText(this, "Invalid course information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "PaymentConfirmActivity started for course: " + courseId + ", price: " + coursePrice);

        initViews();
        setupServices();
        setupListeners();
        populateUI();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imageViewCourseImage = findViewById(R.id.imageViewCourseImage);
        textViewCourseTitle = findViewById(R.id.textViewCourseTitle);
        textViewCoursePrice = findViewById(R.id.textViewCoursePrice);
        textViewSubtotal = findViewById(R.id.textViewSubtotal);
        textViewTotal = findViewById(R.id.textViewTotal);
        layoutPayOSOption = findViewById(R.id.layoutPayOSOption);
        radioPayOS = findViewById(R.id.radioPayOS);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
    }

    private void setupServices() {
        courseService = APIClient.getClient().create(CourseService.class);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        layoutPayOSOption.setOnClickListener(v -> {
            radioPayOS.setChecked(true);
        });

        btnConfirmPayment.setOnClickListener(v -> {
            if (radioPayOS.isChecked()) {
                Log.d(TAG, "User confirmed PayOS payment");
                proceedWithPayOSPayment();
            } else {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUI() {
        // Course title
        if (courseTitle != null) {
            textViewCourseTitle.setText(courseTitle);
        }

        // Course image
        if (courseImageUrl != null && !courseImageUrl.isEmpty()) {
            Picasso.get()
                    .load(courseImageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imageViewCourseImage);
        }

        // Price formatting
        String formattedPrice = formatPrice(coursePrice);
        textViewCoursePrice.setText(formattedPrice);
        textViewSubtotal.setText(formattedPrice);
        textViewTotal.setText(formattedPrice);
    }

    private String formatPrice(String price) {
        if (price == null || price.isEmpty()) return "Free";
        
        try {
            double priceValue = Double.parseDouble(price);
            if (priceValue <= 0) {
                return "Free";
            } else {
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                return formatter.format(priceValue);
            }
        } catch (NumberFormatException e) {
            return "Free";
        }
    }

    private void proceedWithPayOSPayment() {
        Log.d(TAG, "Starting PayOS payment process for course: " + courseId);
        
        // Show loading state
        btnConfirmPayment.setEnabled(false);
        btnConfirmPayment.setText("Processing...");

        double amount = 0;
        try {
            amount = Double.parseDouble(coursePrice);
        } catch (Exception e) {
            Log.e(TAG, "Invalid course price: " + coursePrice, e);
            Toast.makeText(this, "Invalid course price", Toast.LENGTH_SHORT).show();
            btnConfirmPayment.setEnabled(true);
            btnConfirmPayment.setText("Confirm Payment");
            return;
        }

        OrderRequest orderRequest = new OrderRequest(amount);
        
        Call<OrderResponse> call = courseService.createOrder(courseId, orderRequest);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                // Reset button state
                btnConfirmPayment.setEnabled(true);
                btnConfirmPayment.setText("Confirm Payment");

                Log.d(TAG, "Order response received. Success: " + response.isSuccessful() + ", Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    OrderResponse orderResponse = response.body();
                    Log.d(TAG, "Order response: success=" + orderResponse.isSuccess() + 
                          ", paymentUrl=" + orderResponse.getPaymentUrl());
                    
                    if (orderResponse.isSuccess()) {
                        String paymentUrl = orderResponse.getPaymentUrl();
                        
                        if (paymentUrl != null && !paymentUrl.isEmpty()) {
                            Log.d(TAG, "Opening PayOS payment URL: " + paymentUrl);
                            
                            // Open PayOS payment URL
                            PayOSHelper.openPaymentUrl(PaymentConfirmActivity.this, paymentUrl);
                            
                            // Show success message and finish
                            Toast.makeText(PaymentConfirmActivity.this, 
                                "Redirecting to PayOS...\nReturn to the app after completing payment", 
                                Toast.LENGTH_LONG).show();
                            
                            // Optional: finish this activity after opening payment
                            finish();
                            
                        } else {
                            Log.e(TAG, "PayOS payment URL is null or empty");
                            Toast.makeText(PaymentConfirmActivity.this, 
                                "Payment URL not received. Please try again.", 
                                Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Order failed: " + orderResponse.getMessage());
                        Toast.makeText(PaymentConfirmActivity.this, 
                            "Payment failed: " + orderResponse.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "Order API call failed. Response code: " + response.code());
                    Toast.makeText(PaymentConfirmActivity.this, 
                        "Failed to process payment. Please check your connection and try again.", 
                        Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                // Reset button state
                btnConfirmPayment.setEnabled(true);
                btnConfirmPayment.setText("Confirm Payment");

                Log.e(TAG, "Network error during payment", t);
                Toast.makeText(PaymentConfirmActivity.this, 
                    "Network error: " + t.getMessage() + "\nPlease check your connection and try again.", 
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    public static Intent createIntent(Context context, int courseId, String courseTitle, String coursePrice, String courseImageUrl) {
        Intent intent = new Intent(context, PaymentConfirmActivity.class);
        intent.putExtra(EXTRA_COURSE_ID, courseId);
        intent.putExtra(EXTRA_COURSE_TITLE, courseTitle);
        intent.putExtra(EXTRA_COURSE_PRICE, coursePrice);
        intent.putExtra(EXTRA_COURSE_IMAGE, courseImageUrl);
        return intent;
    }
} 