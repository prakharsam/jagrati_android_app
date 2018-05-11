package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VolunteerList extends BaseActivity {

    SearchView editSearch;
    ArrayList<VolunteerLink> volunteerList = new ArrayList<>();
    ListView volunteerListView;
    ListViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_list);
        setBackOnClickListener();
        setPageTitle("Volunteer List");

        getVolunteers();

        editSearch = (SearchView) findViewById(R.id.search);
        editSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editSearch.setIconified(false);
            }
        });

        volunteerListView = (ListView) findViewById(R.id.volunteerList);
    }

    public void getVolunteers() {
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

            volunteerList.add(
                new VolunteerLink(userId, volunteerFullName, discipline, displayPicURL)
            );
        }

        adapter = new ListViewAdapter(this, volunteerList, false);
        final ListViewAdapter _adapter = adapter;
        volunteerListView.setAdapter(_adapter);

        editSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                _adapter.filter(s);
                return false;
            }
        });
    }

    public void selectUser(int userId, String fullName, String discipline, String dpURL) {
        Intent profileActivity = new Intent("com.example.lenovopc.jagrati.PROFILE");
        Bundle bundle = new Bundle();
        bundle.putInt("userId", userId);
        profileActivity.putExtras(bundle);
        startActivity(profileActivity);
    }
}
