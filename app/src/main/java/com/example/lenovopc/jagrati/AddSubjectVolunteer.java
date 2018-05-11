package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddSubjectVolunteer extends BaseActivity {
    String selectedVolunteerId = "-1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_subject_volunteer);
        setBackOnClickListener();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            final String id = bundle.getString("id");
            String name = bundle.getString("name");

            setPageTitle("Add Volunteer To " + name);

            Button addVolunteerBtn = (Button) findViewById(R.id.formSubmitBtn);
            addVolunteerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitForm(id);
                }
            });

            getVolunteers();
        }
    }

    private void getVolunteers() {
        final String volunteersURL = apiURL + "/volunteers/";

        JsonArrayRequest req = new JsonArrayRequest(
                volunteersURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            initializeVolunteersDropdown(response);
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

    private void initializeVolunteersDropdown(final JSONArray volunteers) throws JSONException {
        ArrayList<String> volunteerNames = new ArrayList<>();
        Spinner volunteersView = (Spinner) findViewById(R.id.volunteerDropdown);

        volunteerNames.add("Select Volunteer");
        for (int i=0; i < volunteers.length(); i++) {
            JSONObject volunteer = volunteers.getJSONObject(i);
            JSONObject volunteerUser = volunteer.getJSONObject("user");
            String firstName = volunteerUser.getString("first_name");
            String lastName = volunteerUser.getString("last_name");
            String fullName = firstName + " " + lastName;

            volunteerNames.add(fullName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                volunteerNames
        );
        volunteersView.setAdapter(adapter);
        volunteersView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    JSONObject selectedVolunteer = volunteers.getJSONObject(position-1);
                    JSONObject volunteerUser = selectedVolunteer.getJSONObject("user");
                    selectedVolunteerId = volunteerUser.getString("id");
                } catch (JSONException e) {
                    selectedVolunteerId = "-1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void submitForm(String subjectId) {
        if (selectedVolunteerId.equals("-1")) {
            Spinner volunteersView = (Spinner) findViewById(R.id.volunteerDropdown);
            TextView errorText = (TextView) volunteersView.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            volunteersView.requestFocus();
            Toast.makeText(this, "Please select a volunteer", Toast.LENGTH_SHORT).show();
        } else {
            String volunteerSubjectURL = apiURL + "/department/";

            JSONObject formData = new JSONObject();
            try {
                formData.put("subject_id", subjectId);
                formData.put("volunteer_id", selectedVolunteerId);
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            final RelativeLayout formView = (RelativeLayout) findViewById(R.id.addSubjectVolunteerForm);
            showProgress(true, formView);

            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.POST,
                    volunteerSubjectURL,
                    formData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            showProgress(false, formView);
                            Toast.makeText(AddSubjectVolunteer.this, "Added Volunteer Successfully",
                                    Toast.LENGTH_SHORT).show();

                            Intent data = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("volunteer", response.toString());
                            data.putExtras(bundle);
                            setResult(RESULT_OK, data);
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showProgress(false, formView);
                            if (error.networkResponse == null) {
                                if (error.getClass().equals(NoConnectionError.class) || error.getClass().equals(TimeoutError.class)) {
                                    Toast.makeText(
                                            mCtx,
                                            "Seems like there is no internet connection.",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            } else if (error.networkResponse.statusCode == 400) {
                                String errorStr = new String(error.networkResponse.data);
                                try {
                                    JSONObject errorJSON = new JSONObject(errorStr);
                                    JSONArray nonFieldError = errorJSON.getJSONArray("non_field_errors");
                                    String errorText = nonFieldError.getString( 0);
                                    if (errorText.equals("The fields volunteer, subject must make a unique set.")) {
                                        Toast.makeText(AddSubjectVolunteer.this, "This volunteer already exists in the subject",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(AddSubjectVolunteer.this, "There was some internal problem. Please try again",
                                            Toast.LENGTH_SHORT).show();
                                    Log.e("Error", e.getMessage());
                                    e.printStackTrace();
                                }
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
    }
}
