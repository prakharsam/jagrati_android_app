package com.example.lenovopc.jagrati;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    ImageLoader imageLoader;
    public String jwtVal;
    public boolean isAdmin;
    public int userId;
    public String apiURL;
    public RequestQueue queue;
    public static Context mCtx;
    public static ProgressBar mProgressView;
    public View formView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor userRow = dbHelper.getRow();

        queue = VolleySingleton.getInstance(
                getApplicationContext()
        ).getRequestQueue();

        imageLoader = VolleySingleton.getInstance(
            getApplicationContext()
        ).getImageLoader();

        apiURL = getString(R.string.api_url);

        if (userRow.getCount() != 0) {
            userRow.moveToFirst();
            jwtVal = userRow.getString(2);
            isAdmin = userRow.getInt(1) == 1;
            userId = userRow.getInt(0);
        } else {
            jwtVal = "";
        }

        mCtx = this;
    }

    public void setPageTitle(String title) {
        TextView pageTitleView = (TextView) findViewById(R.id.pageTitle);
        pageTitleView.setText(title);
    }

    public void setBackOnClickListener() {
        ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void logout(View v) {
        DatabaseHelper db = new DatabaseHelper(this);
        db.deleteAllRows();

        Intent loginActivity = new Intent("com.example.lenovopc.jagrati.LOGIN");
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(loginActivity);
    }

    public Response.ErrorListener errorListener = new Response.ErrorListener() {
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
            }
        }
    };

    public Response.ErrorListener getErrorListener(View view) {
        formView = view;
        return errorListener;
    }

    public void showProgress(boolean show, View view) {
        if (view == null) {
            return;
        }

        ColorDrawable blackOverlayColorDrawable= new ColorDrawable(getResources().getColor(R.color.black_overlay));
        Button formSubmitBtn = (Button) view.findViewById(R.id.formSubmitBtn);

        if (show) {
            view.setBackground(blackOverlayColorDrawable);
            formSubmitBtn.setClickable(false);
            formSubmitBtn.setBackgroundColor(Color.parseColor("#cccccc"));
        } else {
            view.setBackground(null);
            formSubmitBtn.setClickable(true);
            formSubmitBtn.setBackgroundColor(Color.parseColor("#f17e01"));
        }
        mProgressView = (ProgressBar) findViewById(R.id.progressBar);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
