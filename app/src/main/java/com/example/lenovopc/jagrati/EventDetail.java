package com.example.lenovopc.jagrati;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDetail extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        setBackOnClickListener();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            setBackOnClickListener();

            String eventTitle = bundle.getString("eventTitle");
            String eventDate = bundle.getString("eventDate");
            String eventCreatedAt = bundle.getString("eventCreatedAt");
            String eventURL = bundle.getString("eventURL");
            String eventDescription = bundle.getString("eventDescription");



            getTeachers(eventTitle,eventDate,eventCreatedAt,eventURL,eventDescription);
        }

    }

    private void getTeachers(String eventTitle,String eventDate,String eventCreatedAt,String eventURL,String eventDescription) {




        ImageView eventImageView = (ImageView)findViewById(R.id.eventImage);

        if (!eventURL.equals("null")) {
            new DownloadImageTask(eventImageView, null, null, null).execute(eventURL);
        }

        TextView eventName = (TextView)findViewById(R.id.eventName);
        eventName.setText(eventTitle);

        //TODO: Edit created date format
        TextView eventCreatedAtView = (TextView)findViewById(R.id.dateTimePublish);
        eventCreatedAtView.setText(eventCreatedAt);
        TextView eventDescriptionView = (TextView)findViewById(R.id.eventDescription);
        eventDescriptionView.setText(eventDescription);


        String [] dateTime = eventDate.split("T");

        TextView eventDateView = (TextView)findViewById(R.id.dateEvent);
        eventDateView.setText(dateTime[0]);

        TextView eventTimeView = (TextView)findViewById(R.id.timeEvent);
        eventTimeView.setText(dateTime[1]);

    }


}











