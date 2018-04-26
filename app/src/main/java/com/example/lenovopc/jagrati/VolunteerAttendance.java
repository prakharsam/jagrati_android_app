package com.example.lenovopc.jagrati;

import android.os.Bundle;

public class VolunteerAttendance extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_attendance);
        setBackOnClickListener();
        setPageTitle("Volunteer Attendance");
    }
}
