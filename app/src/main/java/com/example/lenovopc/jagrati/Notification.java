package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Notification extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setBackOnClickListener();
        setPageTitle("Notifications");

        getUserNotifications();
    }

    private void getUserNotifications() {
        final String notificationsURL = apiURL + "/user_notifications/";

        JsonArrayRequest req = new JsonArrayRequest(
                notificationsURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            initializeUserNotifications(response);
                        } catch (JSONException e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                errorListener
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

    private void initializeUserNotifications(JSONArray userNotifications) throws JSONException {
        for (int i=0; i < userNotifications.length(); i++) {
            JSONObject userNotification = userNotifications.getJSONObject(i);
            JSONObject notification = userNotification.getJSONObject("notification");
            final String type = notification.getString("_type");
            String firstLetter = String.valueOf(type.toUpperCase().charAt(0));
            String title = notification.getString("content");
            String displayDate = notification.getString("display_date");
            final int instanceId = notification.getInt("instance_id");

            Class cls = MainActivity.class;
            final Bundle bundle = new Bundle();

            if (type.equals("event")) {
                title = "New Event - " + title;
                cls = EventDetail.class;
                bundle.putInt("eventId", instanceId);
            } else if (type.equals("class_feedback")) {
                title = title + " History Updated";
                cls = History.class;
                bundle.putString("classId", String.valueOf(instanceId));
            } else if (type.equals("join_request")) {
                title = "Join Request - " + title;
                cls = VolunteerRequest.class;
                bundle.putInt("joinRequestId", instanceId);
            }

            LinearLayout notificationList = (LinearLayout) findViewById(R.id.notificationList);
            View notificationCard = getLayoutInflater().inflate(R.layout.notification_card, null);

            TextView firstLetterView = (TextView) notificationCard.findViewById(R.id.firstLetter);
            firstLetterView.setText(firstLetter);

            TextView titleView = (TextView) notificationCard.findViewById(R.id.title);
            titleView.setText(title);

            TextView timeView = (TextView) notificationCard.findViewById(R.id.time);
            timeView.setText(displayDate);

            final Class finalCls = cls;
            notificationCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent action = new Intent(Notification.this, finalCls);
                    action.putExtras(bundle);
                    startActivity(action);
                }
            });

            notificationList.addView(notificationCard);
        }
    }
}
