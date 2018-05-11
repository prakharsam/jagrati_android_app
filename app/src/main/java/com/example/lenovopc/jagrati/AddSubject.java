package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddSubject extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_subject);
        setBackOnClickListener();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String header = bundle.getString("header");
            final String urlSlug = bundle.getString("urlSlug");
            setPageTitle(header);

            Button formSubmitBtn = (Button) findViewById(R.id.formSubmitBtn);
            formSubmitBtn.setText(header);
            formSubmitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitForm(urlSlug);
                }
            });
        }
    }

    private void submitForm(String urlSlug) {
        EditText nameView = (EditText) findViewById(R.id.name);
        final String name = String.valueOf(nameView.getText());

        if (!name.equals("")) {
            String addSubjectURL = apiURL + urlSlug;

            JSONObject formData = new JSONObject();
            try {
                formData.put("name", name);
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            final RelativeLayout formWrap = (RelativeLayout) findViewById(R.id.addSubjectForm);

            showProgress(true, formWrap);
            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.POST,
                    addSubjectURL,
                    formData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            showProgress(false, formWrap);
                            Toast.makeText(AddSubject.this, "Added successfully",
                                    Toast.LENGTH_SHORT).show();
                            try {
                                Intent data = new Intent();
                                data.putExtra("name", name);
                                data.putExtra("id", response.getString("id"));
                                setResult(RESULT_OK, data);
                                finish();
                            } catch (JSONException e) {
                                Log.e("Error", e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    },
                    getErrorListener(formWrap)
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "JWT " + jwtVal);
                    return headers;
                }
            };

            queue.add(req);
        } else {
            nameView.setError("This field is required.");
            nameView.requestFocus();
        }
    }
}
