package com.example.lenovopc.jagrati;

import android.os.Bundle;

public class ProfileUser extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        setBackOnClickListener();
        setPageTitle("Profile");
    }
}
