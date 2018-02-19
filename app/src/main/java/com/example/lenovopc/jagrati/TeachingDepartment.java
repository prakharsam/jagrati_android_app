package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TeachingDepartment extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teaching_department);
        getSubjects();
    }

    private void getSubjects() {
        final String subjectsURL = apiURL + "/subjects/";

        JsonArrayRequest req = new JsonArrayRequest(
                subjectsURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        initializeSubjects(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Show error message here.
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

    private void initializeSubjects(JSONArray subjects) {
        for (int i=0; i < subjects.length(); i++) {
            try {
                final JSONObject subject = subjects.getJSONObject(i);
                final String id = subject.get("id").toString();
                final String name = subject.get("name").toString();
                final String numVolunteers = subject.get("num_volunteers").toString();
                final String numVolunteersLabel = numVolunteers + " Teachers";

                GridLayout gridLayout = (GridLayout) findViewById(R.id.subjectGrid);
                View subjectButtonView = getLayoutInflater().inflate(R.layout.subject_button, null);

                Button subjectNameBtn = (Button) subjectButtonView.findViewById(R.id.subjectNameButtonLabel);
                subjectNameBtn.setText(name);
                subjectNameBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent subjectDeptActivity = new Intent("com.example.lenovopc.jagrati.SUBJECTDEPT");
                        Bundle bundle = new Bundle();
                        bundle.putString("subjectId", id);
                        subjectDeptActivity.putExtras(bundle);
                        startActivity(subjectDeptActivity);
                    }
                });

                TextView numTeachersTextView = (TextView) subjectButtonView.findViewById(R.id.numTeachersLabel);
                numTeachersTextView.setText(numVolunteersLabel);

                gridLayout.addView(subjectButtonView);
            } catch (JSONException e) {
                // TODO: Show error here.
            }
        }
    }
}
