package com.example.lenovopc.jagrati;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PopupSetDays extends BaseActivity {
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_set_days);
        setBackOnClickListener();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.7));

        getMaxAbsents();
    }

    private void getMaxAbsents() {
        String configURL = apiURL + "/config/";

        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET,
                configURL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            initializeMaxAbsents(response);
                        } catch(JSONException e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                errorListener
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "JWT " + jwtVal);
                return headers;
            }
        };

        queue.add(req);
    }

    private void initializeMaxAbsents(JSONArray configs) throws JSONException {
        final JSONObject config = configs.getJSONObject(0);
        final int id = config.getInt("id");
        int maxAbsents = config.getInt("num_inactive_student_days");

        EditText maxAbsentView = (EditText) findViewById(R.id.maxAbsents);
        maxAbsentView.setText(String.valueOf(maxAbsents));

        Button saveBtn = (Button) findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMaxAbsents(id);
            }
        });
    }

    private void updateMaxAbsents(int id) {
        EditText maxAbsentView = (EditText) findViewById(R.id.maxAbsents);
        String updatedMaxAbsents = String.valueOf(maxAbsentView.getText());
        String configURL = apiURL + "/config/" + id + "/";

        JSONObject formData = new JSONObject();
        try {
            formData.put("num_inactive_student_days", updatedMaxAbsents);
        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.PUT,
                configURL,
                formData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(PopupSetDays.this, "Absent Limit updated successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                errorListener
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "JWT " + jwtVal);
                return headers;
            }
        };

        queue.add(req);
    }
}
