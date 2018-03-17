package com.example.lenovopc.jagrati;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class History extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            setBackOnClickListener();
            setPageTitle("History");

            String classId = bundle.getString("classId");
            getClassHistory(classId);
        }
    }

    private void getClassHistory(String classId) {
        final String historyURL = apiURL + "/class_feedback/?_class=" + classId;

        JsonArrayRequest req = new JsonArrayRequest(
                historyURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        initializeClassHistory(response);
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

    private void initializeClassHistory(JSONArray history) {
        LinearLayout historyListLayout = (LinearLayout) findViewById(R.id.historyList);

        for (int i=0; i < history.length(); i++) {
            try {
                JSONObject historyObj = history.getJSONObject(i);
                JSONObject subject = historyObj.getJSONObject("subject");
                String subjectName = subject.getString("name");
                String feedback = historyObj.getString("feedback");
                // TODO: Format the date value
                String createdAt = historyObj.getString("created_at");

                View historyBlockView = getLayoutInflater().inflate(R.layout.history_block, null);

                TextView subjectView = (TextView) historyBlockView.findViewById(R.id.subject);
                subjectView.setText(subjectName);

                TextView feedbackView = (TextView) historyBlockView.findViewById(R.id.feedback);
                feedbackView.setText(feedback);

                TextView createdAtView = (TextView) historyBlockView.findViewById(R.id.createdAt);
                createdAtView.setText(createdAt);

                historyListLayout.addView(historyBlockView);
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
