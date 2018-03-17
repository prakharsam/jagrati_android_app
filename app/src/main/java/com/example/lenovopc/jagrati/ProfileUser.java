package com.example.lenovopc.jagrati;

import android.os.Bundle;

public class ProfileUser extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        setBackOnClickListener();
        setPageTitle("Profile");
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