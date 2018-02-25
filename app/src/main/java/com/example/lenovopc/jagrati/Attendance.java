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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Attendance extends BaseActivity {
    private ArrayList<String> selectedStudents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
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
        Log.d("@@@@@", selectedStudents.toString());
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

    private void initializeStudentList(JSONArray students) {
        for (int i=0; i < students.length(); i++) {
            try {
                JSONObject student = students.getJSONObject(i);
                JSONObject studentUser = student.getJSONObject("user");
                final String id = studentUser.getString("id");
                String firstName = studentUser.getString("first_name");
                String lastName = studentUser.getString("last_name");
                String fullName = firstName + " " + lastName;
                String village = student.getString("village").equals("null") ? "Unknown" : student.getString("village");
                String displayPictureURL = student.getString("display_picture").equals("null") ? null: student.getString("display_picture");

                GridLayout studentGrid = (GridLayout) findViewById(R.id.studentGrid);
                View studentBlockView = getLayoutInflater().inflate(R.layout.student_block, null);

                TextView studentNameView = (TextView) studentBlockView.findViewById(R.id.studentName);
                studentNameView.setText(fullName);

                TextView villageView = (TextView) studentBlockView.findViewById(R.id.villageName);
                villageView.setText(village);

                CheckBox attendanceCheckBox = (CheckBox) studentBlockView.findViewById(R.id.attendance);

                try {
                    Class[] parameterTypes = new Class[2];
                    parameterTypes[0] = Bitmap.class;
                    parameterTypes[1] = CheckBox.class;
                    Method method = Attendance.class.getMethod("setCheckBoxDP", parameterTypes);
                    new DownloadImageTask(null, method, this, attendanceCheckBox).execute(displayPictureURL);
                } catch (NoSuchMethodException e) {
                    //
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
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // TODO: Check dstWidth and Height for other phones than Asus Zenfone
    public void setCheckBoxDP(Bitmap bmap, CheckBox cb) {
        Bitmap _bmap = Bitmap.createScaledBitmap(bmap, 150, 150, false);
        bmap.recycle();
        Drawable drawable = new BitmapDrawable(getResources(), _bmap);
        cb.setBackground(drawable);
    }
}
