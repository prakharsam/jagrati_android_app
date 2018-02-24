package com.example.lenovopc.jagrati;

import android.os.Bundle;

public class Attendance extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        setBackOnClickListener();
        setPageTitle("Attendance");
    }
}
