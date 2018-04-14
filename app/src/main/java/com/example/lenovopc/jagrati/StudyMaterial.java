package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
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

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

public class StudyMaterial extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teaching_department);
        setBackOnClickListener();
        setPageTitle("Study Material");
        getClasses();

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
        popup.getMenuInflater().inflate(R.menu.menu_options_1, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(StudyMaterial.this,
                        "Clicked popup menu item " + item.getTitle(),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        popup.show();
    }

    private void getClasses() {
        final String classesURL = apiURL + "/classes/";

        JsonArrayRequest req = new JsonArrayRequest(
                classesURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        initializeClasses(response);
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

    private void initializeClasses(JSONArray classes) {
        for (int i=0; i < classes.length(); i++) {
            try {
                JSONObject _class = classes.getJSONObject(i);
                final String id = _class.getString("id");
                final String name = _class.getString("name");
                // TODO: Format this date label
                final String updatedAt = _class.getString("updated_at");

                GridLayout gridLayout = (GridLayout) findViewById(R.id.gridWrap);
                View subjectButtonView = getLayoutInflater().inflate(R.layout.subject_button, null);

                Button subjectNameBtn = (Button) subjectButtonView.findViewById(R.id.buttonTitle);
                subjectNameBtn.setText(name);
                subjectNameBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: Change intent value
                        Intent subjectDeptActivity = new Intent("com.example.lenovopc.jagrati.STUDYMATERIALCLASS");
                        Bundle bundle = new Bundle();
                        bundle.putString("classId", id);
                        bundle.putString("className", name);
                        subjectDeptActivity.putExtras(bundle);
                        startActivity(subjectDeptActivity);
                    }
                });

                TextView numTeachersTextView = (TextView) subjectButtonView.findViewById(R.id.buttonCaption);
                numTeachersTextView.setText(updatedAt);

                gridLayout.addView(subjectButtonView);
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

        }
    }
}
