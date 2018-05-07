package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

public class TransferVolunteerAttendance extends VolunteerList {
    String selectedUserId = "";
    String selectedFullName = "";
    String selectedDiscipline = "";
    String selectedDpURL = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_list);
        setPageTitle("Transfer Attendance");
        setBackOnClickListener();

        adapter = new ListViewAdapter(this, volunteerList, true);

        editSearch = (SearchView) findViewById(R.id.search);
        editSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editSearch.setIconified(false);
            }
        });

        volunteerListView = (ListView) findViewById(R.id.volunteerList);

        LinearLayout transferButtons = (LinearLayout) findViewById(R.id.transferFormButtons);
        transferButtons.setVisibility(View.VISIBLE);

        Button cancelTransferBtn = (Button) findViewById(R.id.cancelTransfer);
        cancelTransferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button transferAttendanceBtn = (Button) findViewById(R.id.transfer);
        transferAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transferAttendance();
            }
        });
    }

    @Override
    public void selectUser(int userId, String fullName, String discipline, String dpURL) {
        selectedUserId = String.valueOf(userId);
        selectedFullName = fullName;
        selectedDiscipline = discipline;
        selectedDpURL = dpURL;
    }

    private void transferAttendance() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Intent data = new Intent();

            bundle.putString("id", selectedUserId);
            bundle.putString("tFullName", selectedFullName);
            bundle.putString("tDiscipline", selectedDiscipline);
            bundle.putString("tDpURL", selectedDpURL);

            data.putExtras(bundle);

            setResult(RESULT_OK, data);
            finish();
        }
    }
}
