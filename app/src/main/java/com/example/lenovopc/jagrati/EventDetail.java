package com.example.lenovopc.jagrati;

import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventDetail extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        setBackOnClickListener();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            setBackOnClickListener();

            int eventId = bundle.getInt("eventId");

            if (eventId > 0) {
                getEventDetail(eventId);
            } else {
                String eventTitle = bundle.getString("eventTitle");
                String eventDate = bundle.getString("eventDate");
                String eventCreatedAt = bundle.getString("eventCreatedAt");
                String eventURL = bundle.getString("eventURL");
                String eventDescription = bundle.getString("eventDescription");
                initializeEvent(eventTitle, eventDate, eventCreatedAt, eventURL, eventDescription);
            }
        }

    }

    private void getEventDetail(int eventId) {
        final String eventURL = apiURL + "/events/" + eventId;

        JsonObjectRequest req = new JsonObjectRequest(
                eventURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String title = response.getString("title");
                            String date = response.getString("time");
                            String createdAt = response.getString("created_at");
                            String image = response.getString("image");
                            String description = response.getString("description");
                            initializeEvent(title, date, createdAt, image, description);
                        } catch (JSONException e) {
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

    private void initializeEvent(String eventTitle, String eventDate, String eventCreatedAt,
                             String eventURL, String eventDescription) {
        NetworkImageView eventImageView = (NetworkImageView) findViewById(R.id.eventImage);

        if (!eventURL.equals("null")) {
            eventImageView.setImageUrl(eventURL, imageLoader);
        }

        TextView eventName = (TextView)findViewById(R.id.eventName);
        eventName.setText(eventTitle);

        //TODO: Edit created date format
        TextView eventCreatedAtView = (TextView)findViewById(R.id.dateTimePublish);
        eventCreatedAtView.setText(eventCreatedAt);
        TextView eventDescriptionView = (TextView)findViewById(R.id.eventDescription);
        eventDescriptionView.setText(eventDescription);

        String [] dateTime = eventDate.replace("Z", "").split("T");

        TextView eventDateView = (TextView)findViewById(R.id.dateEvent);
        eventDateView.setText(dateTime[0]);

        TextView eventTimeView = (TextView)findViewById(R.id.timeEvent);
        eventTimeView.setText(dateTime[1]);

    }


}











