package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AddStudent extends BaseActivity {
    Bitmap displayPic = null;
    boolean isEdit = false;
    int studentId = -1;
    String nullValuesLabel = "";

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

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            isEdit = true;
            studentId = bundle.getInt("userId");
            getStudent(studentId);
        }
    }

    private void getStudent(int userId) {
        final String teachersURL = apiURL + "/students/" + userId;

        JsonObjectRequest req = new JsonObjectRequest(
                teachersURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            initializeStudentForm(response);
                        } catch (JSONException e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }
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

    public void setStudentDP(Bitmap bitmap, ImageButton dpIView) {
        Bitmap _bmap = Bitmap.createScaledBitmap(bitmap, 150, 150, false);
        displayPic = _bmap;
        Drawable drawable = new BitmapDrawable(getResources(), _bmap);
        dpIView.setImageDrawable(drawable);
    }

    private void initializeStudentForm(JSONObject student) throws JSONException {
        JSONObject studentUser = student.getJSONObject("user");
        JSONObject classData = student.getJSONObject("_class");

        String firstName = studentUser.optString("first_name").equals("null") ? nullValuesLabel : studentUser.optString("first_name");
        String lastName = studentUser.optString("last_name").equals("null") ? nullValuesLabel : studentUser.optString("last_name");
        String village = student.optString("village").equals("null") ? nullValuesLabel : student.optString("village");
        String displayPictureURL = student.optString("display_picture").equals("null") ? nullValuesLabel : student.optString("display_picture");
        String classLabel = classData.optString("name").equals("null") ? nullValuesLabel : classData.optString("name");
        String sex = student.optString("sex").equals("null") ? nullValuesLabel : student.optString("sex");
        String dob = student.optString("dob").equals("null") ? nullValuesLabel : student.optString("dob");
        String mother = student.optString("mother").equals("null") ? nullValuesLabel : student.optString("mother");
        String father = student.optString("father").equals("null") ? nullValuesLabel : student.optString("father");
        String contact = student.optString("contact").equals("null") ? nullValuesLabel : student.optString("contact");
        String emergencyContact = student.optString("emergency_contact").equals("null") ? nullValuesLabel : student.optString("emergency_contact");
        String address = student.optString("address").equals("null") ? nullValuesLabel : student.optString("address");

        ImageButton dpIView = (ImageButton) findViewById(R.id.displayPicture);
        if (!displayPictureURL.equals("null")) {
            Class[] parameterTypes = new Class[2];
            parameterTypes[0] = Bitmap.class;
            parameterTypes[1] = ImageButton.class;
            try {
                Method method = AddStudent.class.getMethod("setStudentDP", parameterTypes);
                new DownloadImageTask(method, this, null, dpIView).execute(displayPictureURL);
            } catch (NoSuchMethodException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }

        EditText firstNameView = (EditText) findViewById(R.id.firstName);
        firstNameView.setText(firstName);

        EditText lastNameView = (EditText) findViewById(R.id.lastName);
        lastNameView.setText(lastName);

        EditText villageView = (EditText) findViewById(R.id.village);
        villageView.setText(village);

        EditText classView = (EditText) findViewById(R.id.classNum);
        classView.setText(classLabel);

        EditText sexView = (EditText) findViewById(R.id.sex);
        sexView.setText(sex);

        EditText motherNameView = (EditText) findViewById(R.id.motherName);
        motherNameView.setText(mother);

        EditText fatherNameView = (EditText) findViewById(R.id.fatherName);
        fatherNameView.setText(father);

        EditText contactView = (EditText) findViewById(R.id.contactNumber);
        contactView.setText(contact);

        EditText emergencyContactView = (EditText) findViewById(R.id.emergencyNumber);
        emergencyContactView.setText(emergencyContact);

        EditText addressView = (EditText) findViewById(R.id.address);
        addressView.setText(address);

        EditText dobView = (EditText) findViewById(R.id.dob);
        dobView.setText(dob);
    }

    private boolean validateForm() {
        EditText firstNameView = (EditText) findViewById(R.id.firstName);
        if (firstNameView.getText().length() == 0) {
            firstNameView.setError("This field is required.");
            firstNameView.requestFocus();
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
        int requestType = Request.Method.POST;

        if (isEdit) {
            studentsURL += String.valueOf(studentId) + "/";
            requestType = Request.Method.PUT;
        }

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
                requestType,
                studentsURL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        showProgress(false, studentFormView);
                        String successToastLabel = "Student Added Successfully";

                        if (isEdit) {
                            successToastLabel = "Student Edited Successfully";
                        }

                        Toast.makeText(
                                AddStudent.this,
                                successToastLabel,
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
                selectedImgView.setImageDrawable(new BitmapDrawable(getResources(), displayPic));

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
        selectedImgView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_home_user));
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
