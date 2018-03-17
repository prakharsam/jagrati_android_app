package com.example.lenovopc.jagrati;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassFeedback extends BaseActivity {
    private String classId;
    private JSONArray subjects;
    private ArrayList<String> subjectNames;
    private String selectedSubjectId;
    private Spinner subjectView;
    private EditText feedbackView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_feedback);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            setBackOnClickListener();
            setPageTitle("Class Feedback");

            classId = bundle.getString("classId");
            getSubjects();

            subjectView = (Spinner) findViewById(R.id.subject);
            feedbackView = (EditText) findViewById(R.id.feedback);
            subjectNames = new ArrayList<>();

            final Button submitFeedbackBtn = (Button) findViewById(R.id.submitStudentFeedback);
            submitFeedbackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitFeedbackForm();
                }
            });
        }
    }

    private void getSubjects() {
        final String teachersURL = apiURL + "/syllabus/?_class=" + classId;

        JsonArrayRequest req = new JsonArrayRequest(
                teachersURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        subjects = response;
                        initializeSubjectNamesList();
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

    public void submitFeedbackForm() {
        final String classFeedbackURL = apiURL + "/class_feedback/";

        JSONObject formData = new JSONObject();
        try {
            JSONObject classJSONObject = new JSONObject();
            JSONObject subjectJSONObject = new JSONObject();
            classJSONObject.put("id", classId);
            classJSONObject.put("name", "ANYTHING");
            subjectJSONObject.put("id", selectedSubjectId);
            subjectJSONObject.put("name", "ANYTHING");

            formData.put("_class", classJSONObject);
            formData.put("subject", subjectJSONObject);
            formData.put("feedback", feedbackView.getText());
        } catch (JSONException e) {
            return;
        }

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                classFeedbackURL,
                formData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(
                            ClassFeedback.this,
                            "Feedback saved successfully",
                            Toast.LENGTH_SHORT
                        ).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", new String(error.networkResponse.data));
                        error.printStackTrace();
                    }
                }
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

    private void initializeSubjectNamesList() {
        try {
            JSONObject firstSubject = subjects.getJSONObject(0);
            JSONObject _firstSubject = firstSubject.getJSONObject("subject");
            selectedSubjectId = _firstSubject.getString("id");
        } catch (JSONException e) {
            Log.e("ERROR", e.getMessage());
            e.printStackTrace();
        }

        for (int i=0; i < subjects.length(); i++) {
            try {
                JSONObject subject = subjects.getJSONObject(i);
                JSONObject _subject = subject.getJSONObject("subject");
                subjectNames.add(_subject.getString("name"));

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjectNames);
                subjectView.setAdapter(adapter);
                subjectView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try{
                            JSONObject selectedSubject = subjects.getJSONObject(position);
                            JSONObject _selectedSubject = selectedSubject.getJSONObject("subject");
                            selectedSubjectId = _selectedSubject.getString("id");
                        } catch (JSONException e) {
                            //
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } catch (JSONException e) {
                Log.e("ERROR: ", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
