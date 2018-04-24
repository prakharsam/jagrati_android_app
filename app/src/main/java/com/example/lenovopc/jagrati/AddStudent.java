package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddStudent extends BaseActivity {
    Bitmap displayPic = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        setPageTitle("Add Student");
        setBackOnClickListener();

        Button saveStudentBtn = (Button) findViewById(R.id.formSubmitBtn);
        saveStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        ImageButton displayPicBtn = (ImageButton) findViewById(R.id.displayPicture);
        displayPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });
    }

    private boolean validateForm() {
        EditText firstNameView = (EditText) findViewById(R.id.firstName);
        if (firstNameView.getText().length() == 0) {
            firstNameView.setError("This field is required.");
            firstNameView.requestFocus();
            return false;
        }

        EditText lastNameView = (EditText) findViewById(R.id.lastName);
        if (lastNameView.getText().length() == 0) {
            lastNameView.setError("This field is required.");
            lastNameView.requestFocus();
            return false;
        }

        EditText classView = (EditText) findViewById(R.id.classNum);
        if (classView.getText().length() == 0) {
            classView.setError("This field is required.");
        }

        return true;
    }

    public void submitForm() {
        if (!validateForm()) {
            return;
        }

        String studentsURL = apiURL + "/students/";
        final View studentFormView = findViewById(R.id.addStudentForm);

        final String firstName = String.valueOf(((EditText) findViewById(R.id.firstName)).getText());
        final String lastName = String.valueOf(((EditText) findViewById(R.id.lastName)).getText());
        final String village = String.valueOf(((EditText) findViewById(R.id.village)).getText());
        final String classNum = String.valueOf(((EditText) findViewById(R.id.classNum)).getText());
        final String sex = String.valueOf(((EditText) findViewById(R.id.sex)).getText());
        final String emergencyContact = String.valueOf(((EditText) findViewById(R.id.emergencyNumber)).getText());
        final String contact = String.valueOf(((EditText) findViewById(R.id.contactNumber)).getText());
        final String motherName = String.valueOf(((EditText) findViewById(R.id.motherName)).getText());
        final String fatherName = String.valueOf(((EditText) findViewById(R.id.fatherName)).getText());
        final String address = String.valueOf(((EditText) findViewById(R.id.address)).getText());
        final String dob = String.valueOf(((EditText) findViewById(R.id.dob)).getText());

        showProgress(true, studentFormView);
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                studentsURL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        showProgress(false, studentFormView);
                        Toast.makeText(
                                AddStudent.this,
                                "Student Added Successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent data = new Intent();
                        data.putExtra("student", new String(response.data));
                        setResult(RESULT_OK, data);
                        finish();
                    }
                },
                getErrorListener(studentFormView)
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("first_name", firstName);
                params.put("last_name", lastName);
                params.put("class_num", classNum);
                params.put("sex", sex);
                params.put("village", village);
                params.put("contact", contact);
                params.put("emergency_contact", emergencyContact);
                params.put("mother", motherName);
                params.put("father", fatherName);
                params.put("address", address);
                if (dob.length() != 0) {
                    params.put("dob", dob);
                }

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imageName = System.currentTimeMillis();
                byte[] fileData = getFileDataFromDrawable(displayPic);

                if (fileData != null) {
                    params.put(
                        "display_picture",
                        new DataPart(imageName + ".png", fileData)
                    );
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "JWT " + jwtVal);
                return headers;
            }
        };

        queue.add(volleyMultipartRequest);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                displayPic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                ImageView selectedImgView = (ImageView) findViewById(R.id.displayPicture);
                selectedImgView.setBackground(new BitmapDrawable(getResources(), displayPic));

//                Button removeImageBtn = (Button) findViewById(R.id.removeSelectedImage);
//                removeImageBtn.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeImage(View v) {
        ImageView selectedImgView = (ImageView) findViewById(R.id.displayPicture);
        Button removeImageBtn = (Button) findViewById(R.id.removeSelectedImage);
        // selectedImgView.setBackground(R.drawable.ic_home_user);
        removeImageBtn.setVisibility(View.GONE);
        displayPic = null;
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
