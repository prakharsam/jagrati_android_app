package com.example.lenovopc.jagrati;

import android.content.Intent;
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
import android.widget.ImageButton;
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

public class VolunteerAttendance extends BaseActivity {
    String subjectId;
    private ArrayList<String> selectedVolunteers = new ArrayList<>();
    private ArrayList<String> extraVolunteers = new ArrayList<>();
    GridLayout volunteerGrid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_attendance);
        setBackOnClickListener();
        setPageTitle("Volunteer Attendance");

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            subjectId = bundle.getString("subjectId");
            getSubjectVolunteers();
        }

        volunteerGrid = (GridLayout) findViewById(R.id.volunteerGrid);

        Button submitAttendanceBtn = (Button) findViewById(R.id.submitAttendance);
        submitAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAttendanceForm();
            }
        });
    }

    private void getSubjectVolunteers() {
        final String volunteerURL = apiURL + "/department/?subject=" + subjectId;

        JsonArrayRequest req = new JsonArrayRequest(
                volunteerURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            initializeVolunteers(response);
                        } catch (JSONException e){
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

    private void initializeVolunteers(JSONArray volunteers) throws JSONException {
        for (int i=0; i < volunteers.length(); i++) {
            JSONObject subjectVolunteer = volunteers.getJSONObject(i);
            JSONObject volunteer = subjectVolunteer.getJSONObject("volunteer");
            final String id = volunteer.getString("id");
            final String firstName = volunteer.getString("first_name");
            final String lastName = volunteer.getString("last_name");
            final String fullName = firstName + " " + lastName;
            final String discipline = subjectVolunteer.getString("discipline");
            final String displayPictureURL = subjectVolunteer.getString("display_picture");

            View volunteerBlock = getLayoutInflater().inflate(R.layout.attendance_volunteer_block, null);

            TextView studentNameView = (TextView) volunteerBlock.findViewById(R.id.studentName);
            studentNameView.setText(fullName);

            TextView villageView = (TextView) volunteerBlock.findViewById(R.id.villageName);
            villageView.setText(discipline);

            CheckBox attendanceCheckBox = (CheckBox) volunteerBlock.findViewById(R.id.attendance);

            if (!displayPictureURL.equals("")) {
                setUserDP(attendanceCheckBox, displayPictureURL);
            }

            attendanceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        int index = selectedVolunteers.indexOf(id);

                        if (index != -1) {
                            selectedVolunteers.remove(index);
                        }
                    } else {
                        selectedVolunteers.add(id);
                    }
                }
            });

            final int idx = i;
            ImageButton transferBtn = (ImageButton) volunteerBlock.findViewById(R.id.transferAttendance);
            transferBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int index = selectedVolunteers.indexOf(id);
                    if (index != -1) {
                        selectedVolunteers.remove(index);
                    }

                    Intent transferActivity = new Intent(VolunteerAttendance.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("firstName", firstName);
                    bundle.putInt("childIndex", idx);
                    transferActivity.putExtras(bundle);
                    startActivityForResult(transferActivity, 100);
                }
            });

            volunteerGrid.addView(volunteerBlock);
        }
    }

    private void submitAttendanceForm() {
        final String attendancePostURL = apiURL + "/attendance/";
        JSONObject formData = new JSONObject();

        try {
            formData.put("user_ids", new JSONArray(selectedVolunteers));
            formData.put("extra_user_ids", new JSONArray(extraVolunteers));
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
                                VolunteerAttendance.this,
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

    public void setCheckBoxDP(Bitmap bmap, CheckBox cb) {
        Bitmap _bmap = Bitmap.createScaledBitmap(bmap, 150, 150, false);
        Drawable drawable = new BitmapDrawable(getResources(), _bmap);
        cb.setBackground(drawable);
    }

    private void removeTransferredVolunteer(String userId, View childView, String fullName, String dpURL, String discipline) {
        int index = extraVolunteers.indexOf(userId);
        if (index != -1) {
            extraVolunteers.remove(index);
        }

        CheckBox attendanceCheckBox = (CheckBox) childView.findViewById(R.id.attendance);
        attendanceCheckBox.setChecked(false);

        TextView nameView = (TextView) childView.findViewById(R.id.studentName);
        nameView.setText(fullName);

        TextView disciplineView = (TextView) childView.findViewById(R.id.villageName);
        disciplineView.setText(discipline);

        if (!dpURL.equals("")) {
            setUserDP(attendanceCheckBox, dpURL);
        }

        TextView transferredFrom = (TextView) childView.findViewById(R.id.transferredFrom);
        transferredFrom.setVisibility(View.GONE);
    }

    private void setUserDP(CheckBox attendanceCheckBox, String dpURL) {
        try {
            Class[] parameterTypes = new Class[2];
            parameterTypes[0] = Bitmap.class;
            parameterTypes[1] = CheckBox.class;
            Method method = StudentAttendance.class.getMethod("setCheckBoxDP", parameterTypes);
            new DownloadImageTask(method, this, attendanceCheckBox, null).execute(dpURL);
        } catch (NoSuchMethodException e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String userId = data.getExtras().getString("id");
            String firstName = data.getExtras().getString("firstName");
            String tFullName = data.getExtras().getString("tFullName");
            String tDiscipline = data.getExtras().getString("tDiscipline");
            String tDpURL = data.getExtras().getString("tDpURL");

            int childIndex = data.getExtras().getInt("childIndex");
            View childView = volunteerGrid.getChildAt(childIndex);

            TextView transferredFrom = (TextView) childView.findViewById(R.id.transferredFrom);
            transferredFrom.setText(firstName + "'s Attendance");
            transferredFrom.setVisibility(View.VISIBLE);

            CheckBox attendanceCheckBox = (CheckBox) childView.findViewById(R.id.attendance);
            if (!tDpURL.equals("")) {
                setUserDP(attendanceCheckBox, tDpURL);
            }

            TextView nameView = (TextView) childView.findViewById(R.id.studentName);
            nameView.setText(tFullName);

            TextView villageView = (TextView) childView.findViewById(R.id.villageName);
            villageView.setText(tDiscipline);

            attendanceCheckBox.setChecked(true);

            extraVolunteers.add(userId);
        }
    }
}
