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
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ClassManagement extends BaseActivity {
    LinearLayout studentsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_management);
        setBackOnClickListener();
        setPageTitle("Class Management");

        studentsList = (LinearLayout) findViewById(R.id.studentList);
        getInactiveStudents();

        final ImageButton optionBtn = (ImageButton) findViewById(R.id.options);
        optionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPopupButtonClick(optionBtn);
            }
        });
    }

    protected void onPopupButtonClick(View button) {
        PopupMenu popup = new PopupMenu(this, button);
        popup.getMenuInflater().inflate(R.menu.menu_options_setdays, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Intent classActivity = new Intent("com.example.lenovopc.jagrati.POPUPSETDAYS");
                startActivity(classActivity);
                return true;
            }
        });

        popup.show();
    }

    private void getInactiveStudents() {
        final String teachersURL = apiURL + "/students/?user__is_active=false";

        JsonArrayRequest req = new JsonArrayRequest(
                teachersURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            initializeStudentList(response);
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

    private void initializeStudentList(JSONArray students) throws JSONException {
        for (int i=0; i < students.length(); i++) {
            JSONObject student = students.getJSONObject(i);
            JSONObject studentUser = student.getJSONObject("user");
            final int id = studentUser.getInt("id");
            final String username = studentUser.getString("username");
            String firstName = studentUser.getString("first_name");
            String lastName = studentUser.getString("last_name");
            String fullName = firstName + " " + lastName;
            String displayPicURL = student.getString("display_picture");
            String village = student.getString("village").equals("null") ? "We don't know" : student.getString("village");

            View studentBlock = getLayoutInflater().inflate(R.layout.profile_subject_button, null);

            Button nameView = (Button) studentBlock.findViewById(R.id.volunteerName);
            nameView.setText(fullName);
            nameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileActivity = new Intent(ClassManagement.this, ProfileStudent.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("userId", id);
                    profileActivity.putExtras(bundle);
                    startActivity(profileActivity);
                }
            });

            TextView villageView = (TextView) studentBlock.findViewById(R.id.volunteerDiscipline);
            villageView.setText(village);

            if (!displayPicURL.equals("null")) {
                NetworkImageView dpIView = (NetworkImageView) studentBlock.findViewById(R.id.displayPicture);
                dpIView.setImageUrl(displayPicURL, imageLoader);
                dpIView.setBackground(null);
            }

            final ImageButton optionsBtn = (ImageButton) studentBlock.findViewById(R.id.options);
            final View _studentBlock = studentBlock;
            optionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openStudentMenu(optionsBtn, id, username, _studentBlock);
                }
            });

            studentsList.addView(studentBlock);
        }
    }

    private void openStudentMenu(View button, final int id, final String username, final View studentBlock) {
        PopupMenu popup = new PopupMenu(this, button);
        popup.getMenuInflater().inflate(R.menu.menu_options_3, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("Set Active")) {
                    setStudentActive(id, username, studentBlock);
                } else if (item.getTitle().equals("Remove")) {
                    removeUser(id, studentBlock);
                }
                return true;
            }
        });

        popup.show();
    }

    private void setStudentActive(int id, String username, final View studentBlock) {
        String setActiveStudentURL = apiURL + "/users/" + id + "/";

        JSONObject formData = new JSONObject();
        try {
            formData.put("is_active", true);
            formData.put("username", username);
        } catch (JSONException e) {
            return;
        }

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.PUT,
                setActiveStudentURL,
                formData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(
                                ClassManagement.this,
                                "User has been set active successfully",
                                Toast.LENGTH_SHORT
                        ).show();
                        studentsList.removeView(studentBlock);
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

    private void removeUser(int id, final View studentBlock) {
        String removeStudentURL = apiURL + "/users/" + id + "/";

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.DELETE,
                removeStudentURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(
                                ClassManagement.this,
                                "User removed successfully",
                                Toast.LENGTH_SHORT
                        ).show();
                        studentsList.removeView(studentBlock);
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

            // handling 204 no content response as success
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                    if (jsonString.length() == 0) {
                        return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                    }
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        queue.add(req);
    }
}
