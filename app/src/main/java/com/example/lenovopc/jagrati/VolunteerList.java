package com.example.lenovopc.jagrati;

import android.os.Bundle;

public class VolunteerList extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_list);
        setBackOnClickListener();
        setPageTitle("Volunteer List");
    }
}
