package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // TODO: Change intent values according to buttons
        Button classBtn = (Button) findViewById(R.id._class);
        Button studyBtn = (Button) findViewById(R.id.studyMaterial);
        Button teachingDeptBtn = (Button) findViewById(R.id.teachingDepartment);
        Button eventsBtn = (Button) findViewById(R.id.events);

        classBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classActivity = new Intent("com.example.lenovopc.jagrati.MAIN");
                startActivity(classActivity);
            }
        });

        studyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classActivity = new Intent("com.example.lenovopc.jagrati.MAIN");
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
                Intent classActivity = new Intent("com.example.lenovopc.jagrati.MAIN");
                startActivity(classActivity);
            }
        });
    }
}
