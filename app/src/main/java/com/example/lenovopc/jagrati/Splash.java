package com.example.lenovopc.jagrati;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class Splash extends Activity{
    @Override
    protected void onCreate(Bundle Lenovo) {
        super.onCreate(Lenovo);
        setContentView(R.layout.splash);

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(3000);
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
            userRow.moveToFirst();
            final String apiURL = getString(R.string.api_url);
            final String jwtVal = userRow.getString(2);
            final String userInfoURL = apiURL + "/user_details/?jwt=" + jwtVal;

            RequestQueue queue = VolleySingleton.getInstance(
                getApplicationContext()
            ).getRequestQueue();

            JsonObjectRequest req = new JsonObjectRequest(
                userInfoURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String id = response.get("user_id").toString();
                            String isAdmin = response.get("is_admin").toString();

                            boolean result = db.updateData(id, isAdmin, jwtVal);
                            if (result) {
                                moveToActivity("MAIN");
                            } else {
                                moveToActivity("LOGIN");
                            }
                        } catch (JSONException e) {
                            moveToActivity("LOGIN");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        moveToActivity("LOGIN");
                    }
                }
            );

            queue.add(req);
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