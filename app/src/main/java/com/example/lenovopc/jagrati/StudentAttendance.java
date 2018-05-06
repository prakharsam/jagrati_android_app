package com.example.lenovopc.jagrati;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.TextView;
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentAttendance extends BaseActivity {
    private ArrayList<String> selectedStudents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            setBackOnClickListener();
            setPageTitle("Attendance");

            String numActiveStudents = bundle.getString("numActiveStudents");
            String classId = bundle.getString("classId");

            TextView numActiveStudentsView = (TextView) findViewById(R.id.numActiveStudents);
            numActiveStudentsView.setText(numActiveStudents);

            final Button submitAttendanceBtn = (Button) findViewById(R.id.submitAttendance);
            submitAttendanceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitAttendanceForm();
                }
            });

            selectedStudents = new ArrayList<>();
            getStudents(classId);
        }
    }

    private void submitAttendanceForm() {
        final String attendancePostURL = apiURL + "/attendance/";
        JSONObject formData = new JSONObject();

        try {
            formData.put("user_ids", new JSONArray(selectedStudents));
            formData.put("extra_user_ids", new JSONArray());
        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                attendancePostURL,
                formData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(
                            StudentAttendance.this,
                            "Attendance saved successfully",
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

    private void getStudents(String classId) {
        final String studentsURL = apiURL + "/students/?_class=" + classId;

        JsonArrayRequest req = new JsonArrayRequest(
                studentsURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        initializeStudentList(response);
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

    private void initializeStudentList(JSONArray students) {
        for (int i=0; i < students.length(); i++) {
            try {
                JSONObject student = students.getJSONObject(i);
                JSONObject studentUser = student.getJSONObject("user");
                final String id = studentUser.getString("id");
                String firstName = studentUser.getString("first_name");
                String lastName = studentUser.getString("last_name");
                String fullName = firstName + " " + lastName;
                boolean isActiveStudent = studentUser.getBoolean("is_active");
                String village = student.getString("village").equals("null") ? "Unknown" : student.getString("village");
                String displayPictureURL = student.getString("display_picture").equals("null") ? null : student.getString("display_picture");

                if (isActiveStudent) {
                    GridLayout studentGrid = (GridLayout) findViewById(R.id.studentGrid);
                    View studentBlockView = getLayoutInflater().inflate(R.layout.attendance_student_block, null);

                    TextView studentNameView = (TextView) studentBlockView.findViewById(R.id.studentName);
                    studentNameView.setText(fullName);

                    TextView villageView = (TextView) studentBlockView.findViewById(R.id.villageName);
                    villageView.setText(village);

                    CheckBox attendanceCheckBox = (CheckBox) studentBlockView.findViewById(R.id.attendance);

                    try {
                        Class[] parameterTypes = new Class[2];
                        parameterTypes[0] = Bitmap.class;
                        parameterTypes[1] = CheckBox.class;
                        Method method = StudentAttendance.class.getMethod("setCheckBoxDP", parameterTypes);
                        new DownloadImageTask(method, this, attendanceCheckBox, null).execute(displayPictureURL);
                    } catch (NoSuchMethodException e) {
                        Log.e("Error", e.getMessage());
                        e.printStackTrace();
                    }

                    attendanceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (!isChecked) {
                                int index = selectedStudents.indexOf(id);

                                if (index != -1) {
                                    selectedStudents.remove(index);
                                }
                            } else {
                                selectedStudents.add(id);
                            }

                            TextView numPresentStudentsView = (TextView) findViewById(R.id.numPresentStudents);
                            numPresentStudentsView.setText(String.valueOf(selectedStudents.size()));
                        }
                    });

                    studentGrid.addView(studentBlockView);
                }
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void setCheckBoxDP(Bitmap bmap, CheckBox cb) {
        Bitmap _bmap = Bitmap.createScaledBitmap(bmap, 150, 150, false);
        Drawable drawable = new BitmapDrawable(getResources(), _bmap);
        cb.setBackground(drawable);
    }
}
