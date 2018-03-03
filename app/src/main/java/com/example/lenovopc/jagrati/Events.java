package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.logging.Handler;

public class Events extends BaseActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run(){
                        swipeRefreshLayout.setRefreshing(false);
                        getEvents();
                    }
                },2000);
            }
        });

        getEvents();

        setBackOnClickListener();
        setPageTitle("Events");

    }

    private void getEvents() {
        final String classesURL = apiURL + "/events/";

        JsonArrayRequest req = new JsonArrayRequest(
                classesURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        initializeEvents(response);
                    }
                },
                VolleySingleton.errorListener
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

    private void initializeEvent(JSONObject event, int index) throws JSONException {
        String eventType = event.getString("_type");

        final String eventTitle = event.getString("title");
        final String createdAt = event.getString("created_at");
        final String eventDate = event.getString("time");
        final String eventImageURL = event.getString("image");
        final String eventDescription = event.getString("description");
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.eventslist);

        if (eventType.equals("EVENT")) {
            View eventCard = getLayoutInflater().inflate(R.layout.event_card, null);

            Button eventBtn = (Button) eventCard.findViewById(R.id.eventPanel);
            eventBtn.setText(eventTitle);
            TextView date = (TextView) eventCard.findViewById(R.id.dateTimeEvent);
            date.setText(createdAt);

            ImageView eventImageView = (ImageView) eventCard.findViewById(R.id.eventImage);

            if (!eventImageURL.equals("null")) {
                new DownloadImageTask(eventImageView, null, null, null).execute(eventImageURL);
            }

            eventBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent eventDetailActivity = new Intent("com.example.lenovopc.jagrati.EVENTDETAIL");
                    Bundle bundle = new Bundle();

                    bundle.putString("eventTitle", eventTitle);
                    bundle.putString("eventDate", eventDate);
                    bundle.putString("eventCreatedAt", createdAt);
                    bundle.putString("eventURL", eventImageURL);
                    bundle.putString("eventDescription", eventDescription);

                    eventDetailActivity.putExtras(bundle);
                    startActivity(eventDetailActivity);
                }
            });

            if (index != -1) {
                linearLayout.addView(eventCard, index);
            } else {
                linearLayout.addView(eventCard);
            }
        } else if (eventType.equals("MEETING")) {
            View announcementCard = getLayoutInflater().inflate(R.layout.announcement_card, null);

            Button announcementButton = (Button) announcementCard.findViewById(R.id.announcementButton);
            announcementButton.setText(eventTitle);

            TextView dateAnnouncement = (TextView) announcementCard.findViewById(R.id.dateTimeAnnouncement);
            dateAnnouncement.setText(createdAt);
            if (index != -1) {
                linearLayout.addView(announcementCard, index);
            } else {
                linearLayout.addView(announcementCard);
            }
        }
    }

    private void initializeEvents(JSONArray events) {
        for (int i=0; i < events.length(); i++) {
            try {
                JSONObject event = events.getJSONObject(i);
                initializeEvent(event, -1);
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void addNewEvent(View view) {
        Intent classActivity = new Intent("com.example.lenovopc.jagrati.ADDEVENT");
        startActivityForResult(classActivity, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            String event = data.getExtras().getString("event");
            try {
                JSONObject eventJSONObject = new JSONObject(event);
                initializeEvent(eventJSONObject, 0);
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
    }





}