package com.example.lenovopc.jagrati;

import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileUser extends BaseActivity {
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
        }
    }

    private void getUserProfile(int _userId) {
        final String getProfileURL = apiURL + "/students/" + _userId;

        JsonObjectRequest req = new JsonObjectRequest(
                getProfileURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject student) {
                        initializeStudentProfile(student);
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

    private void initializeStudentProfile(JSONObject student) {
        // TODO: Update layout code
    }
}

//import android.app.Activity;
//package com.javatpoint.popupmenu;
//        import android.os.Bundle;
//        import android.app.Activity;
//        import android.view.Menu;
//        import android.view.MenuItem;
//        import android.view.View;
//        import android.view.View.OnClickListener;
//        import android.widget.Button;
//        import android.widget.PopupMenu;
//        import android.widget.Toast;
//public class MainActivity extends Activity {
//    Button options;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile_user);
//        options = (Button) findViewById(R.id.options);
//        options.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                //Creating the instance of PopupMenu
//                PopupMenu popup = new PopupMenu(MainActivity.this, options);
//                //Inflating the Popup using xml file
//                popup.getMenuInflater().inflate(R.menu.menu_options, popup.getMenu());
//
//                //registering popup with OnMenuItemClickListener
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//                        Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
//                        return true;
//                    }
//                });
//
//                popup.show();//showing popup menu
//            }
//        });//closing the setOnClickListener method
//    }
//}