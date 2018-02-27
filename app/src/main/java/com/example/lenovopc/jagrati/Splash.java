package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

public class Splash extends BaseActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                } catch(InterruptedException e){
                    e.printStackTrace();
                } finally {
                    moveToLoginIfNotLoggedIn();
                }
            }
        };
        timer.start();
    }

    private void moveToLoginIfNotLoggedIn() {
        final DatabaseHelper db = new DatabaseHelper(this);
        Cursor userRow = db.getRow();

        if (userRow.getCount() != 0) {
            moveToActivity("MAIN");
        } else {
            moveToActivity("LOGIN");
        }
    }

    private void moveToActivity(String activityName) {
        Intent launchNextActivity = new Intent("com.example.lenovopc.jagrati." + activityName);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(launchNextActivity);
    }
}