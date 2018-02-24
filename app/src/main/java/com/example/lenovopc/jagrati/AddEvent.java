package com.example.lenovopc.jagrati;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

public class AddEvent extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        setBackOnClickListener(this);
        setPageTitle("Add Event");

        Button submitEventBtn = (Button) findViewById(R.id.postEvent);
        submitEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEventForm();
            }
        });
    }

    private void submitEventForm() {
        RadioGroup eventTypeRadioGroup = (RadioGroup) findViewById(R.id.eventType);
        int checkedEventId = eventTypeRadioGroup.getCheckedRadioButtonId();
        String eventType = "EVENT";

        if (checkedEventId == R.id.meeting) {
            eventType = "MEETING";
        }

        // TODO: Get other input field values and submit form

    }

    public void hideImageInput(View view) {
        Button imageInputBtn = (Button) findViewById(R.id.imageInputBtn);
        imageInputBtn.setVisibility(View.GONE);
    }

    public void showImageInput(View view) {
        Button imageInputBtn = (Button) findViewById(R.id.imageInputBtn);
        imageInputBtn.setVisibility(View.VISIBLE);
    }
}
