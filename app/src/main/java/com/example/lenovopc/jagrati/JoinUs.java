package com.example.lenovopc.jagrati;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class JoinUs extends Activity {
    EditText emailView = null;
    EditText nameView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setJoinUsTextOnLayout();
    }

    private void setJoinUsTextOnLayout() {
        TextView title = (TextView) findViewById(R.id.loginTitle);
        title.setText("Join Us");

        emailView = (EditText) findViewById(R.id.email);

        nameView = (EditText) findViewById(R.id.password);
        nameView.setInputType(InputType.TYPE_CLASS_TEXT);
        nameView.setHint("Name");

        Button forgotBtn = (Button) findViewById(R.id.forgotPassword);
        forgotBtn.setVisibility(View.GONE);

        Button sendRequestBtn = (Button) findViewById(R.id.logInButton);
        sendRequestBtn.setText("SEND REQUEST");
        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid = validateInputs();
                if (isValid) {
                    sendRequest();
                }
            }
        });
    }

    private boolean validateInputs() {
        String email = emailView.getText().toString();
        String name = nameView.getText().toString();
        View focusView = null;

        emailView.setError(null);
        nameView.setError(null);

        if (email.isEmpty() || !email.contains("@")) {
            emailView.setError("Invalid email");
            focusView = emailView;
        }

        if (name.isEmpty()) {
            nameView.setError("This field is required.");
            focusView = nameView;
        }

        if (focusView != null) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void sendRequest() {
        String email = emailView.getText().toString();
        String name = emailView.getText().toString();

        final String apiURL = getString(R.string.api_url);

        RequestQueue queue = VolleySingleton.getInstance(
                getApplicationContext()
        ).getRequestQueue();

        JSONObject formData = new JSONObject();
        try {
            formData.put("email", email);
            formData.put("name", name);
        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        String joinUsURL = apiURL + "/join_requests/";
        final TextView infoText = (TextView) findViewById(R.id.infoText);

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                joinUsURL,
                formData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        emailView.setText("");
                        nameView.setText("");
                        infoText.setText("Success. You will receive an email" +
                                " with your password when the admin approves your request.");
                        infoText.setTextColor(Color.parseColor("#00bb00"));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", new String(error.networkResponse.data));
                        error.printStackTrace();
                        if (error.networkResponse == null) {
                            if (error.getClass().equals(NoConnectionError.class) ||
                                error.getClass().equals(TimeoutError.class)) {
                                infoText.setText("Seems like there is no internet" +
                                        "connection.");
                            }
                        } else {
                            infoText.setText("Email Id already exists.");
                        }
                    }
                }
        );

        queue.add(req);
    }
}
