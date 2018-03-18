package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button classBtn = (Button) findViewById(R.id._class);
        Button studyBtn = (Button) findViewById(R.id.studyMaterial);
        Button teachingDeptBtn = (Button) findViewById(R.id.teachingDepartment);
        Button eventsBtn = (Button) findViewById(R.id.events);
        ImageButton notificationBtn = (ImageButton) findViewById(R.id.notification);
        ImageButton profileBtn = (ImageButton) findViewById(R.id.user);

        classBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classActivity = new Intent("com.example.lenovopc.jagrati.CLASSLIST");
                startActivity(classActivity);
            }
        });

        studyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classActivity = new Intent("com.example.lenovopc.jagrati.STUDYMATERIAL");
                startActivity(classActivity);
            }
        });

        teachingDeptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classActivity = new Intent("com.example.lenovopc.jagrati.TEACHINGDEPT");
                startActivity(classActivity);
            }
        });

        eventsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classActivity = new Intent("com.example.lenovopc.jagrati.EVENTS");
                startActivity(classActivity);
            }
        });

        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classActivity = new Intent("com.example.lenovopc.jagrati.NOTIFICATION");
                startActivity(classActivity);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classActivity = new Intent("com.example.lenovopc.jagrati.PROFILE");
                Bundle bundle = new Bundle();
                bundle.putInt("userId", userId);
                classActivity.putExtras(bundle);
                startActivity(classActivity);
            }
        });
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
