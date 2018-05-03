package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class VolunteerList extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_list);
        setBackOnClickListener();
        setPageTitle("Volunteer List");

        getVolunteers();
    }

    private void getVolunteers() {
        final String volunteersURL = apiURL + "/volunteers/";

        JsonArrayRequest req = new JsonArrayRequest(
                volunteersURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            initializeVolunteers(response);
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

    private void initializeVolunteers(JSONArray volunteers) throws JSONException {
        for (int i=0; i < volunteers.length(); i++) {
            JSONObject volunteer = volunteers.getJSONObject(i);
            JSONObject volunteerUser = volunteer.getJSONObject("user");
            final int userId = volunteerUser.getInt("id");
            String volunteerFirstName = volunteerUser.getString("first_name");
            String volunteerLastName = volunteerUser.getString("last_name");
            String volunteerFullName = volunteerFirstName + " " + volunteerLastName;
            String discipline = volunteer.getString("discipline");
            String displayPicURL = volunteer.getString("display_picture");

            LinearLayout volunteerListLayout = (LinearLayout) findViewById(R.id.volunteerList);
            View profileLinkView = getLayoutInflater().inflate(R.layout.profile_subject_button, null);

            Button volunteerNameBtn = (Button) profileLinkView.findViewById(R.id.volunteerName);
            volunteerNameBtn.setText(volunteerFullName);
            volunteerNameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileActivity = new Intent("com.example.lenovopc.jagrati.PROFILE");
                    Bundle bundle = new Bundle();
                    bundle.putInt("userId", userId);
                    profileActivity.putExtras(bundle);
                    startActivity(profileActivity);
                }
            });

            TextView disciplineView = (TextView) profileLinkView.findViewById(R.id.volunteerDiscipline);
            disciplineView.setText(discipline);

            NetworkImageView dpIView = (NetworkImageView) profileLinkView.findViewById(R.id.displayPicture);
            if (!displayPicURL.equals("null")) {
                dpIView.setImageUrl(displayPicURL, imageLoader);
                dpIView.setBackground(null);
            }

            volunteerListLayout.addView(profileLinkView);
        }
    }
}
