package com.example.lenovopc.jagrati;

import android.os.Bundle;

public class Notification extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setBackOnClickListener();
        setPageTitle("Notification");
    }
}
