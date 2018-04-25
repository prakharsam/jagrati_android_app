package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileStudent extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_student);
        setBackOnClickListener();
        setPageTitle("Student Profile");

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            int userId = bundle.getInt("userId");
            getStudent(userId);
        }

        Button studentFeedbackBtn = (Button) findViewById(R.id.studentFeedback);

        studentFeedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classActivity = new Intent("com.example.lenovopc.jagrati.STUDENTFEEDBACK");
                startActivity(classActivity);
            }
        });

        Button timelineBtn = (Button) findViewById(R.id.timeline);

        timelineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classActivity = new Intent("com.example.lenovopc.jagrati.TIMELINE");
                startActivity(classActivity);
            }
        });

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
                Toast.makeText(ProfileStudent.this,
                        "Clicked popup menu item " + item.getTitle(),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        popup.show();
    }

    private void getStudent(int userId) {
        final String teachersURL = apiURL + "/students/" + userId;

        JsonObjectRequest req = new JsonObjectRequest(
                teachersURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            initializeStudent(response);
                        } catch (JSONException e) {
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

    private void initializeStudent(JSONObject student) throws JSONException {
        JSONObject studentUser = student.getJSONObject("user");
        JSONObject attendanceData = student.getJSONObject("attendance");
        JSONObject classData = student.getJSONObject("_class");

        String firstName = studentUser.optString("first_name", "");
        String lastName = studentUser.optString("last_name", "");
        String fullName = firstName + " " + lastName;
        String village = student.optString("village", "");
        String displayPictureURL = student.optString("display_picture", "");
        int attendanceCount = attendanceData.optInt("attendance", 0);
        int totalAttendance = attendanceData.optInt("total_classes", 0);
        String className = classData.optString("name", "");
        String classLabel = getClassLabel(className);
        String sex = student.optString("sex", "");
        String dob = student.optString("dob", "");
        String mother = student.optString("mother", "");
        String father = student.optString("father", "");
        String contact = student.optString("contact", "");
        String emergencyContact = student.optString("emergency_contact", "");
        String address = student.optString("address", "");

        TextView nameView = (TextView) findViewById(R.id.studentName);
        nameView.setText(fullName);

        NetworkImageView dpIView = (NetworkImageView) findViewById(R.id.displayPicture);
        if (!displayPictureURL.equals("null")) {
            dpIView.setImageUrl(displayPictureURL, imageLoader);
        }

        TextView villageView = (TextView) findViewById(R.id.villageName);
        villageView.setText(village);

        TextView totalAttendanceView = (TextView) findViewById(R.id.attendanceData);
        totalAttendanceView.setText(attendanceCount + "/" + totalAttendance);

        TextView classNameView = (TextView) findViewById(R.id.classDetail);
        classNameView.setText(classLabel);

        TextView sexView = (TextView) findViewById(R.id.sex);
        sexView.setText(sex);

        TextView dobView = (TextView) findViewById(R.id.dob);
        dobView.setText(dob);

        TextView motherView = (TextView) findViewById(R.id.motherName);
        motherView.setText(mother);

        TextView fatherView = (TextView) findViewById(R.id.fatherName);
        fatherView.setText(father);

        TextView contactView = (TextView) findViewById(R.id.contactNumber);
        contactView.setText(contact);

        TextView emergencyContactView = (TextView) findViewById(R.id.emergencyNumber);
        emergencyContactView.setText(emergencyContact);

        TextView addressView = (TextView) findViewById(R.id.address);
        addressView.setText(address);
    }

    private String getClassLabel(String className) {
        if (className.equals("1")) {
            return className + "st";
        }

        if (className.equals("2")) {
            return className + "nd";
        }

        if (className.equals("3")) {
            return className + "rd";
        }

        return className + "th";
    }
}
