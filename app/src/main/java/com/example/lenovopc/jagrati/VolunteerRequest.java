package com.example.lenovopc.jagrati;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VolunteerRequest extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_request);
        setBackOnClickListener();
        setPageTitle("Volunteer Requests");

        getVolunteerRequests();
    }

    private void getVolunteerRequests() {
        final String subjectsURL = apiURL + "/join_requests/?status=PENDING";

        JsonArrayRequest req = new JsonArrayRequest(
                subjectsURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            initializeJoinRequests(response);
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

    private void initializeJoinRequests(JSONArray joinRequests) throws JSONException {
        for (int i=0; i < joinRequests.length(); i++) {
            JSONObject joinRequest = joinRequests.getJSONObject(i);
            final int joinRequestId = joinRequest.getInt("id");
            String name = joinRequest.getString("name");
            String email = joinRequest.getString("email");

            LinearLayout joinRequestsList = (LinearLayout) findViewById(R.id.joinRequestsList);
            final View requestCard = getLayoutInflater().inflate(R.layout.request_card, null);

            TextView nameView = (TextView) requestCard.findViewById(R.id.volunteerName);
            nameView.setText(name);

            TextView emailView = (TextView) requestCard.findViewById(R.id.volunteerEmail);
            emailView.setText(email);

            Button acceptBtn = (Button) requestCard.findViewById(R.id.accept);
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    processRequest(joinRequestId, "A", requestCard);
                }
            });

            Button rejectBtn = (Button) requestCard.findViewById(R.id.remove);
            rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    processRequest(joinRequestId, "R", requestCard);
                }
            });

            joinRequestsList.addView(requestCard);
        }
    }

    private void processRequest(int requestId, String requestType, final View requestCard) {
        final String joinRequestProcessURL = apiURL + "/join_requests/" + requestId + "/process/";

        JSONObject formData = new JSONObject();
        try {
            formData.put("type", requestType);
        } catch (JSONException e) {
            return;
        }

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.PUT,
                joinRequestProcessURL,
                formData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(
                                VolunteerRequest.this,
                                "Join Request Processed Successfully",
                                Toast.LENGTH_SHORT
                        ).show();
                        ((LinearLayout) requestCard.getParent()).removeView(requestCard);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", new String(error.networkResponse.data));
                        error.printStackTrace();
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
