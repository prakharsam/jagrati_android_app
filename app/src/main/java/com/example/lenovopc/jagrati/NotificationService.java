package com.example.lenovopc.jagrati;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NotificationService extends Service {
    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    public String jwtVal = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            jwtVal = bundle.getString("jwtVal");
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                getUserNotifications();

                handler.postDelayed(runnable, 300000);
            }
        };

        handler.postDelayed(runnable, 300000);
    }

    public void getUserNotifications() {
        final String notificationURL = getString(R.string.api_url) + "/user_notifications/?is_seen=False";
        RequestQueue queue = VolleySingleton.getInstance(
                getApplicationContext()
        ).getRequestQueue();

        JsonArrayRequest req = new JsonArrayRequest(
                notificationURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        createNotifications(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.e("Error:", error.networkResponse.toString());
                            error.printStackTrace();
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
    }

    public void createNotifications(JSONArray notifications) {
        for (int i=0; i < notifications.length(); i++) {
            try {
                JSONObject userNotification = notifications.getJSONObject(i);
                JSONObject notification = userNotification.getJSONObject("notification");
                String _type = notification.getString("_type");
                String content = notification.getString("content");
                String displayDate = notification.getString("display_date");
                int instanceId = notification.getInt("instance_id");

                String title;
                Class cls;
                Bundle bundle = new Bundle();

                if (_type.equals("event")) {
                    title = "New Event - " + content;
                    cls = EventDetail.class;
                    bundle.putInt("eventId", instanceId);
                } else if (_type.equals("join_request")) {
                    title = "Join Request - " + content;
                    cls = VolunteerRequest.class;
                    bundle.putInt("joinRequestId", instanceId);
                } else if (_type.equals("class_feedback")) {
                    title = content + " History Updated";
                    cls = History.class;
                    bundle.putString("classId", String.valueOf(instanceId));
                } else {
                    continue;
                }

                createNotification(title, displayDate, cls, bundle);
            } catch (JSONException e) {
                Log.e("Error: ", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void createNotification(String title, String contentText, Class cls, Bundle bundle) {
        Intent resultIntent = new Intent(this, cls);
        resultIntent.putExtras(bundle);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_jagrati_logo)
                        .setContentTitle(title)
                        .setContentText(contentText)
                        .setContentIntent(resultPendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

        NotificationManagerCompat mNotificationManager =
                NotificationManagerCompat.from(this);

        mNotificationManager.notify(new Random().nextInt(100), builder.build());
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }
}
