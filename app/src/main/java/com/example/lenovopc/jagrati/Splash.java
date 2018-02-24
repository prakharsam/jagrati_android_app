package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Splash extends BaseActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            final String userInfoURL = apiURL + "/user_details/?jwt=" + jwtVal;
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
                        if (error instanceof NoConnectionError) {
                            // TODO: Show no internet connection message here.
                        } else {
                            moveToActivity("LOGIN");
                        }
                    }
                }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "JWT " + jwtVal);
                    return headers;
                }
            };

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