package com.example.lenovopc.jagrati;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LogoutActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHelper db = new DatabaseHelper(this);
        db.deleteAllRows();

        Intent loginActivity = new Intent("com.example.lenovopc.jagrati.LOGIN");
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(loginActivity);
    }
}
