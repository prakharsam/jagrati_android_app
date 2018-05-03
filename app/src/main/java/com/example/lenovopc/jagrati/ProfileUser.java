package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

public class ProfileUser extends BaseActivity {
    String nullValuesLabel = "We don't know";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        setBackOnClickListener();
        setPageTitle("Profile");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int _userId = bundle.getInt("userId");
            getUserProfile(_userId);

            final ImageButton optionBtn = (ImageButton) findViewById(R.id.options);
            Button logoutBtn = (Button) findViewById(R.id.logout);
            if (userId != _userId) {
                optionBtn.setVisibility(View.GONE);
                logoutBtn.setVisibility(View.GONE);
            } else {
                optionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    onPopupButtonClick(optionBtn);
                    }
                });
            }
        }
    }

    protected void onPopupButtonClick(View button) {
        PopupMenu popup = new PopupMenu(this, button);
        popup.getMenuInflater().inflate(R.menu.menu_options_1, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("Edit")) {
                    // TODO: Change intent to EDITPROFILE
                    Intent editUserIntent = new Intent("com.example.lenovopc.jagrati.MAIN");
                    startActivityForResult(editUserIntent, 100);
                }
                return true;
            }
        });

        popup.show();
    }

    private void getUserProfile(int _userId) {
        final String getProfileURL = apiURL + "/volunteers/" + _userId;

        JsonObjectRequest req = new JsonObjectRequest(
                getProfileURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject user) {
                        try {
                            initializeUserProfile(user);
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

    private void initializeUserInterests(JSONArray interests, int gridId) throws JSONException {
        for (int i=0; i < interests.length(); i++) {
            JSONObject interest = interests.getJSONObject(i);
            String name = interest.getString("name");

            GridLayout interestGrid = (GridLayout) findViewById(gridId);
            View interestBlock = getLayoutInflater().inflate(R.layout.hobby_skill_block, null);
            TextView interestName = (TextView) interestBlock.findViewById(R.id.name);
            interestName.setText(name);
            interestGrid.addView(interestBlock);
        }
    }

    private void initializeUserProfile(JSONObject user) throws JSONException {
        JSONObject userInfo = user.getJSONObject("user");
        JSONObject attendanceData = user.getJSONObject("attendance");
        JSONArray hobbies = user.getJSONArray("hobbies");
        JSONArray skills = user.getJSONArray("skills");

        initializeUserInterests(hobbies, R.id.hobbyGrid);
        initializeUserInterests(skills, R.id.skillGrid);

        String firstName = userInfo.getString("first_name");
        String lastName = userInfo.getString("last_name");
        String fullName = firstName + " " + lastName;
        String email = userInfo.getString("email").equals("") ? nullValuesLabel : userInfo.getString("email");
        String status = user.getString("status").equals("null") ? nullValuesLabel : user.getString("status");
        String programme = user.getString("programme").equals("null") ? nullValuesLabel : user.getString("programme") ;
        String discipline = user.getString("discipline").equals("null") ? nullValuesLabel : user.getString("discipline");
        String displayPictureURL = user.getString("display_picture");
        String dob = user.getString("dob").equals("null") ? nullValuesLabel : user.getString("dob");
        String batch = user.getString("batch").equals("null") ? nullValuesLabel : user.getString("batch");
        String contact = user.getString("contact").equals("null") ? nullValuesLabel : user.getString("contact");
        String address = user.getString("address").equals("null") ? nullValuesLabel : user.getString("address");
        boolean isContactHidden = user.getBoolean("is_contact_hidden");
        int attendanceCount = attendanceData.getInt("attendance");
        int totalClasses = attendanceData.getInt("total_classes");

        if (!displayPictureURL.equals("null")) {
            NetworkImageView dpIView = (NetworkImageView) findViewById(R.id.displayPicture);
            dpIView.setImageUrl(displayPictureURL, imageLoader);
            dpIView.setBackground(null);
        }

        TextView nameView = (TextView) findViewById(R.id.name);
        nameView.setText(fullName);

        TextView attendanceView = (TextView) findViewById(R.id.attendance);
        attendanceView.setText(attendanceCount + "/" + totalClasses);

        TextView bioView = (TextView) findViewById(R.id.bio);
        bioView.setText(status);

        TextView mailView = (TextView) findViewById(R.id.mailId);
        mailView.setText(email);

        TextView disciplineView = (TextView) findViewById(R.id.discipline);
        disciplineView.setText(discipline);

        TextView batchView = (TextView) findViewById(R.id.batch);
        batchView.setText(getBatchLabel(batch, programme));

        TextView dobView = (TextView) findViewById(R.id.birthDate);
        dobView.setText(dob);

        TextView addressView = (TextView) findViewById(R.id.address);
        addressView.setText(address);
    }

    private String getBatchLabel(String batch, String programme) {
        int _batch = Integer.parseInt(batch);
        int endYear = 0;

        if (programme.equals("B.Tech.") || programme.equals("B.Des.")) {
            endYear = _batch + 4;
        } else if (programme.equals("M.Tech.") || programme.equals("M.Des.")) {
            endYear = _batch + 2;
        } else if (programme.equals("PhD")) {
            endYear = _batch + 5;
        }

        if (endYear != 0) {
            return _batch + "-" + endYear + " Batch";
        } else {
            return _batch + " Batch";
        }
    }
}