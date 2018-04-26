package com.example.lenovopc.jagrati;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentFeedback extends BaseActivity {
    int studentId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_feedback);

        setBackOnClickListener();
        setPageTitle("Student Feedback");

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            studentId = bundle.getInt("userId");
            String userFullName = bundle.getString("fullName");
            TextView userNameView = (TextView) findViewById(R.id.studentName);
            userNameView.setText(userFullName);

            Button formSubmitBtn = (Button) findViewById(R.id.formSubmitBtn);
            formSubmitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitFeedback();
                }
            });
        }
    }

    private void submitFeedback() {
        EditText titleView = (EditText) findViewById(R.id.feedbackTitle);
        String title = String.valueOf(titleView.getText());

        EditText feedbackView = (EditText) findViewById(R.id.feedback);
        String feedback = String.valueOf(feedbackView.getText());

        if (title.length() == 0) {
            titleView.setError("This field is required.");
            titleView.requestFocus();
            return;
        }

        if (feedback.length() == 0) {
            feedbackView.setError("This field is required");
            feedbackView.requestFocus();
            return;
        }

        String studentFeedbackURL = apiURL + "/student_feedback/";

        JSONObject formData = new JSONObject();
        try {
            formData.put("student_id", studentId);
            formData.put("user_id", userId);
            formData.put("title", title);
            formData.put("feedback", feedback);
        } catch (JSONException e) {
            return;
        }

        RelativeLayout studentFeedbackForm = (RelativeLayout) findViewById(R.id.studentFeedbackForm);

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                studentFeedbackURL,
                formData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(
                                StudentFeedback.this,
                                "Feedback saved successfully",
                                Toast.LENGTH_SHORT
                        ).show();
                        finish();
                    }
                },
                getErrorListener(studentFeedbackForm)
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
