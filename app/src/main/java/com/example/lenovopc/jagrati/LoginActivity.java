package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.logInButton);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.emailLoginForm);
        mProgressView = findViewById(R.id.progressBar);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            submitLoginForm(email, password);
        }
    }

    private void submitLoginForm(String email, String password) {
        final String apiURL = getString(R.string.api_url);

        RequestQueue queue = VolleySingleton.getInstance(
                getApplicationContext()
        ).getRequestQueue();

        JSONObject formData = new JSONObject();
        try {
            formData.put("username", email);
            formData.put("password", password);
        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        String loginURL = apiURL + "/login/";

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                loginURL,
                formData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token;
                            String isAdmin;
                            String userId;
                            token = response.get("token").toString();
                            isAdmin = response.get("is_admin").toString();
                            userId = response.get("user_id").toString();

                            dbHelper.deleteAllRows();
                            boolean result = dbHelper.insertRow(userId, isAdmin, token);

                            if (result) {
                                Intent mainActivity = new Intent("com.example.lenovopc.jagrati.MAIN");
                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(mainActivity);
                            }
                        } catch (JSONException e) {
                            Log.e("Error: ", e.getMessage());
                            e.printStackTrace();
                        } finally {
                            showError(getString(R.string.try_again_msg));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse == null) {
                            if (error.getClass().equals(NoConnectionError.class) || error.getClass().equals(TimeoutError.class)) {
                                showError("Seems like there is no internet connection.");
                            }
                        } else {
                            showError(getString(R.string.invalid_credentials));
                        }
                    }
                }
        );

        queue.add(req);
    }

    private void showError(String message) {
        showProgress(false);

        mPasswordView.setText("");

        TextView infoTextView = (TextView) findViewById(R.id.infoText);
        infoTextView.setText(message);
    }

    private void toggleLoginForm(boolean show) {
        ImageView headerImgView = (ImageView) findViewById(R.id.headerImage);
        FrameLayout gradient = (FrameLayout) findViewById(R.id.gradient);
        TextView loginTitle = (TextView) findViewById(R.id.loginTitle);
        int visibility = show ? View.VISIBLE : View.GONE;
        float alphaVal = (float) (show ? 0.25 : 0.0);

        headerImgView.setAlpha(alphaVal);
        gradient.setAlpha(alphaVal);
        loginTitle.setAlpha(alphaVal);
        mProgressView.setVisibility(visibility);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        toggleLoginForm(show);
    }
}

