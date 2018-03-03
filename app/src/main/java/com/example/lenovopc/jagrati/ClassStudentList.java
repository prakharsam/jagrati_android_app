package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class ClassStudentList extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_student_list);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            setBackOnClickListener();

            String className = bundle.getString("className");
            setPageTitle("Class " + className);

            final String classId = bundle.getString("classId");
            getStudents(classId);

            final String numActiveStudents = bundle.getString("numActiveStudents");

            final Bundle _bundle = new Bundle();
            _bundle.putString("classId", classId);

            Button attendanceBtn = (Button) findViewById(R.id.getAttendance);
            attendanceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent attendanceActivity = new Intent("com.example.lenovopc.jagrati.ATTENDANCE");
                    _bundle.putString("numActiveStudents", numActiveStudents);
                    attendanceActivity.putExtras(_bundle);
                    startActivity(attendanceActivity);
                }
            });

            Button historyBtn = (Button) findViewById(R.id.history);
            historyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent historyActivity = new Intent("com.example.lenovopc.jagrati.HISTORY");
                    historyActivity.putExtras(_bundle);
                    startActivity(historyActivity);
                }
            });

            Button classFeedbackBtn = (Button) findViewById(R.id.classFeedback);
            classFeedbackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent classFeedbackActivity = new Intent("com.example.lenovopc.jagrati.CLASSFEEDBACK");
                    classFeedbackActivity.putExtras(_bundle);
                    startActivity(classFeedbackActivity);
                }
            });
        }
    }

    private void getStudents(String classId) {
        final String teachersURL = apiURL + "/students/?_class=" + classId;

        JsonArrayRequest req = new JsonArrayRequest(
                teachersURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        initializeStudentList(response);
                    }
                },
                VolleySingleton.errorListener
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

    private void initializeStudentList(JSONArray students) {
        for (int i=0; i < students.length(); i++) {
            try {
                JSONObject student = students.getJSONObject(i);
                JSONObject studentUser = student.getJSONObject("user");
                final String id = studentUser.getString("id");
                String firstName = studentUser.getString("first_name");
                String lastName = studentUser.getString("last_name");
                String fullName = firstName + " " + lastName;
                String village = student.getString("village").equals("null") ? "" : student.getString("village");
                String displayPictureURL = student.getString("display_picture");
                boolean isActiveStudent = studentUser.getBoolean("is_active");

                LinearLayout activeStudentsLayout = (LinearLayout) findViewById(R.id.activeStudentsList);
                LinearLayout inactiveStudentsLayout = (LinearLayout) findViewById(R.id.inactiveStudentsList);

                View studentLinkView = getLayoutInflater().inflate(R.layout.profile_subject_button, null);

                Button nameBtn = (Button) studentLinkView.findViewById(R.id.volunteerName);
                nameBtn.setText(fullName);
                nameBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent studentProfileActivity = new Intent("com.example.lenovopc.jagrati.STUDENTPROFILE");
                        Bundle bundle = new Bundle();
                        bundle.putString("studentId", id);
                        studentProfileActivity.putExtras(bundle);
                        startActivity(studentProfileActivity);
                    }
                });

                ImageView dpIView = (ImageView) studentLinkView.findViewById(R.id.displayPicture);

                if (!displayPictureURL.equals("null")) {
                    new DownloadImageTask(dpIView, null, null, null).execute(displayPictureURL);
                }

                TextView villageView = (TextView) studentLinkView.findViewById(R.id.volunteerDiscipline);
                villageView.setText(village);

                if (isActiveStudent) {
                    activeStudentsLayout.addView(studentLinkView);
                } else {
                    inactiveStudentsLayout.addView(studentLinkView);
                }
            } catch(JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

        }
    }
}
