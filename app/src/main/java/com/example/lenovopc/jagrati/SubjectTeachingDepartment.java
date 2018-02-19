package com.example.lenovopc.jagrati;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SubjectTeachingDepartment extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_teaching_department);
        Bundle bundle = getIntent().getExtras();
        String subjectId = bundle.getString("subjectId");
        getTeachers(subjectId);
    }

    private void getTeachers(String subjectId) {
        final String teachersURL = apiURL + "/department/?subject=" + subjectId;

        JsonArrayRequest req = new JsonArrayRequest(
                teachersURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        initializeSubjectDepartment(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Show error here.
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

    private void initializeSubjectDepartment(JSONArray department) {
        for (int i=0; i < department.length(); i++) {
            try {
                JSONObject teacher = department.getJSONObject(i);

            } catch (JSONException e) {
                // TODO: Show error here.
            }
        }
    }
}
