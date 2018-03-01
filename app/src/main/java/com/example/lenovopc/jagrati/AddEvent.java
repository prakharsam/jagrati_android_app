package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddEvent extends BaseActivity {
    Bitmap bitmap = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        setBackOnClickListener();
        setPageTitle("Add Event");

        Button submitEventBtn = (Button) findViewById(R.id.postEvent);
        Button uploadImage = (Button)findViewById(R.id.imageInputBtn) ;
        submitEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEventForm();
            }
        });

        // open gallery
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });
    }


    private void submitEventForm() {
        String eventsURL = apiURL + "/events/";

        RadioGroup eventTypeRadioGroup = (RadioGroup) findViewById(R.id.eventType);
        EditText eventTitle = (EditText) findViewById(R.id.eventTitle);
        EditText eventDescription = (EditText) findViewById(R.id.eventDescription);
        EditText eventDate = (EditText) findViewById(R.id.getDate);
        EditText eventTime = (EditText) findViewById(R.id.editText);

        int checkedEventId = eventTypeRadioGroup.getCheckedRadioButtonId();
        String eventType = "EVENT";

        if (checkedEventId == R.id.meeting) {
            eventType = "MEETING";
        }

        final String _type = eventType;
        final String title = String.valueOf(eventTitle.getText());
        final String description = String.valueOf(eventDescription.getText());
        String date = String.valueOf(eventDate.getText());
        String time = String.valueOf(eventTime.getText());

        Pattern date_pattern = Pattern.compile("^[0-9]{4}-(((0[13578]|(10|12))-(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)-(0[1-9]|[1-2][0-9]|30)))$");
        Matcher date_matcher = date_pattern.matcher(date);

        Pattern time_pattern = Pattern.compile("^(20|21|22|23|[01]\\d|\\d)((:[0-5]\\d){1,2})$");//24 hour format
        Matcher time_matcher = time_pattern.matcher(time);

        View focusView = null;
        boolean isFormValid = true;

        if (title.isEmpty()) {
            eventTitle.setError("This field is required.");
            focusView = eventTitle;
            isFormValid = false;
        }

        if (description.isEmpty()) {
            eventDescription.setError("This field is required.");
            focusView = eventDescription;
            isFormValid = false;
        }

        if (!date_matcher.matches()) {
            eventDate.setError("Invalid Date Format.");
            focusView = eventDate;
            isFormValid = false;
        }

        if (!time_matcher.matches()) {
            eventTime.setError("Invalid Time Format.");
            focusView = eventTime;
            isFormValid = false;
        }

        final String modifiedTime = date+ " " + time + ":00";

        if (isFormValid) {
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, eventsURL,
               new Response.Listener<NetworkResponse>() {
                   @Override
                   public void onResponse(NetworkResponse response) {
                       Intent data = new Intent();
                       data.putExtra("event", new String(response.data));
                       setResult(RESULT_OK, data);
                       finish();
                   }
               },
               VolleySingleton.errorListener
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    params.put("_type", _type);
                    params.put("title", title);
                    params.put("description", description);
                    params.put("time", modifiedTime);

                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                   Map<String, DataPart> params = new HashMap<>();
                   long imageName = System.currentTimeMillis();
                   byte[] fileData = getFileDataFromDrawable(bitmap);

                   if (fileData != null) {
                       params.put("image", new DataPart(imageName + ".png", fileData));
                   }

                   return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                   Map<String, String> headers = new HashMap<>();
                   headers.put("Authorization", "JWT " + jwtVal);
                   return headers;
                }
            };

           queue.add(volleyMultipartRequest);
        } else {
            focusView.requestFocus();
        }
    }

    public void hideImageInput(View view) {
        Button imageInputBtn = (Button) findViewById(R.id.imageInputBtn);
        imageInputBtn.setVisibility(View.GONE);
    }

    public void showImageInput(View view) {
        Button imageInputBtn = (Button) findViewById(R.id.imageInputBtn);
        imageInputBtn.setVisibility(View.VISIBLE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                 bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}

