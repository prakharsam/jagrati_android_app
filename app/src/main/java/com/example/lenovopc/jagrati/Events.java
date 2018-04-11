package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Events extends BaseActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    String latestEventDateTime = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run(){
                        swipeRefreshLayout.setRefreshing(false);
                        getEvents(true);
                    }
                },2000);
            }
        });

        getEvents(false);
        setBackOnClickListener();
        setPageTitle("Events");

        if (isAdmin) {
            showAddEventButton();
//            hideEventEditButtons();
        }
        final ImageButton optionBtn = (ImageButton) findViewById(R.id.options);
        optionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPopupButtonClick(optionBtn);
            }
        });
    }

    protected void onPopupButtonClick(View button) {
        PopupMenu popup = new PopupMenu(this, button);
        popup.getMenuInflater().inflate(R.menu.menu_options_2, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(Events.this,
                        "Clicked popup menu item " + item.getTitle(),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        popup.show();
    }

    private void showAddEventButton() {
        ImageButton addEventBtn = (ImageButton) findViewById(R.id.addEvent);
        addEventBtn.setVisibility(View.VISIBLE);
        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewEvent();
            }
        });
    }

    private void getEvents(final boolean refresh) {
        final String classesURL = apiURL + "/events/?created_at__gt=" + latestEventDateTime;

        JsonArrayRequest req = new JsonArrayRequest(
                classesURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            try {
                                JSONObject latestEvent = response.getJSONObject(0);
                                latestEventDateTime = latestEvent.getString("created_at");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        initializeEvents(response, refresh);
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

            NetworkImageView eventImageView = (NetworkImageView) eventCard.findViewById(R.id.eventImage);
            if (!eventImageURL.equals("null")) {
                eventImageView.setImageUrl(eventImageURL, imageLoader);
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

    private void initializeEvents(JSONArray events, boolean refresh) {
        for (int i=0; i < events.length(); i++) {
            try {
                JSONObject event = events.getJSONObject(i);
                if (!refresh) {
                    initializeEvent(event, -1);
                } else {
                    initializeEvent(event, 0);
                }
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void addNewEvent() {
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