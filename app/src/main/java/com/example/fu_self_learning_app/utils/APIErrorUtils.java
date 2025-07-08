package com.example.fu_self_learning_app.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class APIErrorUtils {
    // dùng để handle các error từ API (status code không phải đầu 2) như BadRequest, Forbidden, 404 Not Found
    public static void handleError(Context context, Response<?> response) {
        Log.d("API Error", "handleError() called");
        try {
            ResponseBody errorBody = response.errorBody();
            if(errorBody == null) {
                Toast.makeText(context, "Undefined Error", Toast.LENGTH_SHORT).show();
                return;
            }
            String errorJson = errorBody.string();
            JSONObject errorObj = new JSONObject(errorJson);
            
            String errorMessage = errorObj.optString("errorMessage");
            JSONArray data = errorObj.optJSONArray("data");

            String firstError = "";
            if(data != null && data.length() > 0) {
                firstError = data.getString(0);
            }

            Log.e("API Error", "errorMessage: " + errorMessage);
            Log.e("API Error", "firstError: " + firstError);
            String displayMessage = firstError.isEmpty() ? errorMessage : firstError;
            Toast.makeText(context, displayMessage, Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
