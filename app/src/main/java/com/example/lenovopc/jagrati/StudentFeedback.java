package com.example.lenovopc.jagrati;

import android.os.Bundle;

public class StudentFeedback extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_feedback);

        setBackOnClickListener();
        setPageTitle("Student Feedback");
    }
}
