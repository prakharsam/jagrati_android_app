package com.example.lenovopc.jagrati;

import android.os.Bundle;

public class VolunteerRequest extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_request);
        setBackOnClickListener();
        setPageTitle("Volunteer Requests");}
}
