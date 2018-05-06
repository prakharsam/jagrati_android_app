package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class VolunteerManagement extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_management);
        setBackOnClickListener();
        setPageTitle("Volunteer Management");

        Button volunteerListBtn = (Button) findViewById(R.id.volunteerList);

        volunteerListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classActivity = new Intent("com.example.lenovopc.jagrati.VOLUNTEERLIST");
                startActivity(classActivity);
            }
        });

        Button volunteerRequestBtn = (Button) findViewById(R.id.volunteerRequest);

        volunteerRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classActivity = new Intent("com.example.lenovopc.jagrati.VOLUNTEERREQUEST");
                startActivity(classActivity);
            }
        });

        Button volunteerAttendanceBtn = (Button) findViewById(R.id.volunteerAttendance);

        volunteerAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classActivity = new Intent("com.example.lenovopc.jagrati.TEACHINGDEPT");
                Bundle bundle = new Bundle();
                bundle.putBoolean("forAttendance", true);
                classActivity.putExtras(bundle);
                startActivity(classActivity);
            }
        });
    }
}
