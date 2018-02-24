package com.example.lenovopc.jagrati;

import android.os.Bundle;

public class EventDetail extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        setBackOnClickListener(this);
    }
}
