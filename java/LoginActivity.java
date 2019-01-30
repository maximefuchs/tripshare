package com.example.maxime.tripshare;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.maxime.tripshare.homePage.HomePageActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {


    EditText username;
    EditText password;
    Button Login;
    Button NewUser;
    ProgressBar loginBuffer;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}
                        , 10);
            }
        }


        username = (EditText) findViewById(R.id.Username);
        password = (EditText) findViewById(R.id.Password);
        Login = (Button) findViewById(R.id.BtnLog);
        NewUser = (Button) findViewById(R.id.BtnNewUser);
        loginBuffer = (ProgressBar) findViewById(R.id.loginBuffer);

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        if(pref.getInt("idUser", -1) != -1){
            Toast.makeText(LoginActivity.this, "You are connected" , Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
        }

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBuffer.setVisibility(View.VISIBLE);
                loginAccess(username.getText().toString(),password.getText().toString());
            }
        });

        NewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, UserAddActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            }
        });

    }

    public void loginAccess(String username, String password){
        StringRequest request = new StringRequest(Request.Method.GET,
                getString(R.string.WSurl) + "traveller-web/rest/user/findUser"
                + "?username='" + username
                + "'&password='" + password +"'",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonUser = new JSONObject(response);

                            final SharedPreferences.Editor editor = pref.edit();
                            editor.putInt("idUser",jsonUser.getInt("id"));
                            editor.putString("userFN",jsonUser.getString("firstname"));
                            editor.putString("userLN",jsonUser.getString("lastname"));
                            editor.putString("userUN",jsonUser.getString("username"));
                            editor.putString("userPW",jsonUser.getString("password"));
                            editor.putString("userPP",jsonUser.getString("profilepic"));
                            editor.commit();

                            Intent intent = new Intent(LoginActivity.this,HomePageActivity.class);
                            finish();
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this,"User not found",Toast.LENGTH_LONG).show();
                        }
                        loginBuffer.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loginBuffer.setVisibility(View.GONE);
                System.out.println("Error login " + error);
                Toast.makeText(LoginActivity.this,"Error to find user",Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(request);
    }

}
