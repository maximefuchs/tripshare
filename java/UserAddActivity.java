package com.example.maxime.tripshare;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.maxime.tripshare.homePage.HomePageActivity;
import com.example.maxime.tripshare.pageSteps.StepAddActivity;
import com.fxn.pix.Pix;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class UserAddActivity extends AppCompatActivity {

    Calendar myCalendar = Calendar.getInstance();

    Button addUser;
    EditText username;
    EditText password;
    EditText firstname;
    EditText lastname;
    ImageView cameraBtn;
    ProgressBar bufferAddUser;

    DatePickerDialog.OnDateSetListener date;

    SharedPreferences pref;

    //Photo
    static final int REQUEST_IMAGE_CAPTURE = 1;
    boolean picAdded;
    String pictureTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);

        addUser = (Button) findViewById(R.id.btnAdd);
        username = (EditText) findViewById(R.id.etUsername);
        password = (EditText) findViewById(R.id.etPassword);
        firstname = (EditText) findViewById(R.id.etFirstname);
        lastname = (EditText) findViewById(R.id.etLastname);
        bufferAddUser = (ProgressBar) findViewById(R.id.bufferAddUser);
        cameraBtn = (ImageView) findViewById(R.id.cameraBtn);

        picAdded = false;

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();


        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pix.start(UserAddActivity.this, REQUEST_IMAGE_CAPTURE);
            }
        });


        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean notCompleted = TextUtils.isEmpty(firstname.getText().toString())
                        || TextUtils.isEmpty(lastname.getText().toString())
                        || TextUtils.isEmpty(username.getText().toString())
                        || TextUtils.isEmpty(password.getText().toString());
                if(notCompleted){
                    Toast.makeText(UserAddActivity.this,"Please fill all areas", Toast.LENGTH_SHORT).show();
                } else if (!picAdded) {
                    Toast.makeText(UserAddActivity.this, "Please add a profil picture", Toast.LENGTH_SHORT).show();
                }
                else if (bufferAddUser.getVisibility() != View.VISIBLE){

                    bufferAddUser.setVisibility(View.VISIBLE);

                    final JSONObject user = new JSONObject();
                    try{
                        user.put("firstname", firstname.getText().toString());
                        user.put("lastname", lastname.getText().toString());
                        user.put("password", password.getText().toString());
                        user.put("username", username.getText().toString());
                        user.put("profilepic", pictureTaken);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }


                    StringRequest request = new StringRequest(Request.Method.POST,
                            getString(R.string.WSurl)+"traveller-web/rest/user/addUser",
                            new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("response add user : " + response);

                            int id = Integer.parseInt(response);
                            if(id == -1) {
                                Toast.makeText(UserAddActivity.this, "Username already used",Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(UserAddActivity.this, "New user added",Toast.LENGTH_LONG).show();
                                finish();
                            }
                            bufferAddUser.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Error add user : " + error);;
                            Toast.makeText(UserAddActivity.this, "Error to add user",Toast.LENGTH_LONG).show();
                            bufferAddUser.setVisibility(View.GONE);
                        }
                    }){
                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            return user.toString().getBytes();
                        }

                        @Override
                        public String getBodyContentType() {
                            return "application/json";
                        }
                    };

                    RequestQueue queue = Volley.newRequestQueue(UserAddActivity.this);
                    queue.add(request);


                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            pictureTaken = returnValue.get(0);
            Bitmap bm = BitmapFactory.decodeFile(pictureTaken);
            Drawable image = new BitmapDrawable(getResources(), bm);
            cameraBtn.setImageDrawable(image);
            picAdded = true;
        }
    }

}
