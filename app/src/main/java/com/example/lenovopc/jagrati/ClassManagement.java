package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

public class ClassManagement extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_management);
        setBackOnClickListener();
        setPageTitle("Class Management");
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

}
