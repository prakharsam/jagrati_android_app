package com.example.lenovopc.jagrati;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
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

        Button submitEventBtn = (Button) findViewById(R.id.formSubmitBtn);
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

        final Calendar calendar = Calendar.getInstance();
        final EditText dateTextView = (EditText) findViewById(R.id.date);
        final EditText timeTextView = (EditText) findViewById(R.id.time);

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String label = getDateLabel(year, monthOfYear, dayOfMonth);
                        dateTextView.setText(label);
                    }
                };
                DatePickerDialog dpd = new DatePickerDialog(
                    AddEvent.this,
                    date,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                );

                dpd.setTitle("Select Date");
                dpd.show();
            }
        });

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        calendar.set(Calendar.HOUR, hour);
                        calendar.set(Calendar.MINUTE, minute);

                        String label = getTimeLabel(hour, minute);
                        timeTextView.setText(label);
                    }
                };
                TimePickerDialog tpd = new TimePickerDialog(
                        AddEvent.this,
                        time,
                        calendar.get(Calendar.HOUR),
                        calendar.get(Calendar.MINUTE),
                        true
                );

                tpd.setTitle("Select Time");
                tpd.show();
            }
        });
    }

    private String getDateLabel(int year, int monthOfYear, int dayOfMonth) {
        String monthStr = Integer.toString(monthOfYear+1);
        String zeroMonthPad = "";
        if (monthStr.length() < 2) {
            zeroMonthPad = "0";
        }

        String dayStr = Integer.toString(dayOfMonth);
        String zeroDayPad = "";
        if (dayStr.length() < 2) {
            zeroDayPad = "0";
        }

        return year + " - " + zeroMonthPad + (monthOfYear + 1) + " - " + zeroDayPad + dayOfMonth;
    }

    private String getTimeLabel(int hour, int minute) {
        String hourStr = Integer.toString(hour);
        String zeroHourPad = "";
        if (hourStr.length() < 2) {
            zeroHourPad = "0";
        }

        String minuteStr = Integer.toString(minute);
        String zeroMinPad = "";
        if (minuteStr.length() < 2) {
            zeroMinPad = "0";
        }

        return zeroHourPad + hour + " : " + zeroMinPad + minute;
    }

    private void submitEventForm() {
        String eventsURL = apiURL + "/events/";

        RadioGroup eventTypeRadioGroup = (RadioGroup) findViewById(R.id.eventType);
        EditText eventTitle = (EditText) findViewById(R.id.eventTitle);
        EditText eventDescription = (EditText) findViewById(R.id.eventDescription);
        EditText eventDate = (EditText) findViewById(R.id.date);
        EditText eventTime = (EditText) findViewById(R.id.time);

        int checkedEventId = eventTypeRadioGroup.getCheckedRadioButtonId();
        String eventType = "EVENT";

        if (checkedEventId == R.id.meeting) {
            eventType = "MEETING";
        }

        final String _type = eventType;
        final String title = String.valueOf(eventTitle.getText());
        final String description = String.valueOf(eventDescription.getText());
        String date = String.valueOf(eventDate.getText()).replaceAll("\\s+", "");
        String time = String.valueOf(eventTime.getText()).replaceAll("\\s+", "");

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

        final String modifiedTime = date + " " + time + ":00";
        final View eventFormView = findViewById(R.id.addEventForm);

        if (isFormValid) {
            showProgress(true, eventFormView);
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                eventsURL,
                new Response.Listener<NetworkResponse>() {
                   @Override
                   public void onResponse(NetworkResponse response) {
                       showProgress(false, eventFormView);
                       Toast.makeText(
                            AddEvent.this,
                            "Event Created Successfully",
                            Toast.LENGTH_SHORT
                       ).show();

                       Intent data = new Intent();
                       data.putExtra("event", new String(response.data));
                       setResult(RESULT_OK, data);
                       finish();
                   }
                },
                getErrorListener(eventFormView)
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
        removeImage(view);
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
                ImageView selectedImgView = (ImageView) findViewById(R.id.selectedImage);
                Button imageInputBtn = (Button) findViewById(R.id.imageInputBtn);
                Button removeImageBtn = (Button) findViewById(R.id.removeSelectedImage);
                selectedImgView.setVisibility(View.VISIBLE);
                selectedImgView.setBackground(new BitmapDrawable(getResources(), bitmap));
                imageInputBtn.setText("Change Image");
                removeImageBtn.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeImage(View v) {
        ImageView selectedImgView = (ImageView) findViewById(R.id.selectedImage);
        Button imageInputBtn = (Button) findViewById(R.id.imageInputBtn);
        Button removeImageBtn = (Button) findViewById(R.id.removeSelectedImage);
        selectedImgView.setBackground(null);
        selectedImgView.setVisibility(View.GONE);
        imageInputBtn.setText("Upload Image");
        removeImageBtn.setVisibility(View.GONE);
        bitmap = null;
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

