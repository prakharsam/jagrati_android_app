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

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

public class TeachingDepartment extends BaseActivity {
    boolean forAttendance = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teaching_department);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            forAttendance = bundle.getBoolean("forAttendance");
        }

        getSubjects();
        setBackOnClickListener();
        setPageTitle("Teaching Department");

        final ImageButton optionBtn = (ImageButton) findViewById(R.id.options);
        optionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPopupButtonClick(optionBtn);
            }
        });
    }

    protected void onPopupButtonClick(View button) {
        PopupMenu popup = new PopupMenu(this, button);
        popup.getMenuInflater().inflate(R.menu.menu_options_1, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(TeachingDepartment.this,
                        "Clicked popup menu item " + item.getTitle(),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        popup.show();
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

    private void initializeSubjects(JSONArray subjects) {
        for (int i=0; i < subjects.length(); i++) {
            try {
                final JSONObject subject = subjects.getJSONObject(i);
                final String id = subject.getString("id");
                final String name = subject.getString("name");
                final String numVolunteers = subject.getString("num_volunteers");
                final String numVolunteersLabel = numVolunteers + " Teachers";

                GridLayout gridLayout = (GridLayout) findViewById(R.id.gridWrap);
                View subjectButtonView = getLayoutInflater().inflate(R.layout.subject_button, null);

                Button subjectNameBtn = (Button) subjectButtonView.findViewById(R.id.buttonTitle);
                subjectNameBtn.setText(name);
                subjectNameBtn.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Class cls = SubjectTeachingDepartment.class;
                        if (forAttendance) {
                            cls = VolunteerAttendance.class;
                        }
                        Intent subjectDeptActivity = new Intent(TeachingDepartment.this, cls);
                        Bundle bundle = new Bundle();
                        bundle.putString("subjectId", id);
                        bundle.putString("subjectName", name);
                        subjectDeptActivity.putExtras(bundle);
                        startActivity(subjectDeptActivity);
                    }
                });

                TextView numTeachersTextView = (TextView) subjectButtonView.findViewById(R.id.buttonCaption);
                numTeachersTextView.setText(numVolunteersLabel);

                gridLayout.addView(subjectButtonView);
            } catch (JSONException e) {
                // TODO: Show error here.
            }
        }
    }
}
