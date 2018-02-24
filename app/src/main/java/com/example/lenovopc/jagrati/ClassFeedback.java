package com.example.lenovopc.jagrati;

import android.os.Bundle;

public class ClassFeedback extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_feedback);

        setBackOnClickListener();
        setPageTitle("Class Feedback");
    }
}
