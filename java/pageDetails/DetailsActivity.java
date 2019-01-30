package com.example.maxime.tripshare.pageDetails;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.wifi.hotspot2.ConfigParser;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.maxime.tripshare.R;
import com.example.maxime.tripshare.homePage.HomePageActivity;
import com.example.maxime.tripshare.tools.GalleryAdapter;
import com.fxn.pix.Pix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    public static List<String> Pics = new ArrayList<>();
    public static String story = "Story";
    public static int currentStepId;
    boolean addIsClicked;

    TextView stepNumber;
    TextView stepName;
    public static TextView tvStory;
    GridView gvDetails;
    FrameLayout container;
    FloatingActionButton btnAdd;
    LinearLayout LL;
    RelativeLayout RL;
    ProgressBar detailsBuffer;

    SharedPreferences pref;

    public static Resources resources;
    public static Context context;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    Fragment currentFragment = new Fragment();
    FragmentManager manager;
    boolean display = false;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        resources = getResources();
        context = this;

        addIsClicked = false;

        stepNumber = (TextView) findViewById(R.id.stepNumber);
        stepName = (TextView) findViewById(R.id.stepName);
        tvStory = (TextView) findViewById(R.id.tvStory);
        gvDetails = (GridView) findViewById(R.id.gvDetails);
        container = (FrameLayout) findViewById(R.id.container);
        btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        LL = (LinearLayout) findViewById(R.id.LL);
        RL = (RelativeLayout) findViewById(R.id.RL);
        detailsBuffer = (ProgressBar) findViewById(R.id.detailsBuffer);

        pref = getSharedPreferences("pref",MODE_PRIVATE);
        manager = getSupportFragmentManager();

        tvStory.setText(story);

        stepNumber.setText(pref.getInt("number",0)+"");
        String name = pref.getString("name","");
        name = name.toUpperCase();
        String nameVert = "";
        int l = name.length();
        for(int i = 0 ; i < l ; i++){
            nameVert += name.charAt(i) + "\n";
        }
        stepName.setText(nameVert);
        currentStepId = pref.getInt("idStep", -1);

        GalleryAdapter adapter = new GalleryAdapter(this, R.layout.pics_template, Pics);
        gvDetails.setAdapter(adapter);


        if(!pref.getBoolean("isMyTrip",false)) {
            btnAdd.setVisibility(View.GONE);
        } else {
            tvStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFragment = new DisplayFragment();
                    Bundle b = new Bundle();
                    b.putBoolean("pic", false);
                    b.putString("story", tvStory.getText().toString());
                    b.putInt("position", pref.getInt("number",-1) - 1);
                    currentFragment.setArguments(b);
                    container.setVisibility(View.VISIBLE);
                    manager.beginTransaction().add(R.id.container, currentFragment).commit();
                    display = true;
                }
            });
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pix.start(DetailsActivity.this, REQUEST_IMAGE_CAPTURE,5);
            }
        });


        gvDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentFragment = new DisplayFragment();
                Bundle b = new Bundle();
                b.putBoolean("pic", true);
                b.putString("element",Pics.get(position));
                currentFragment.setArguments(b);
                container.setVisibility(View.VISIBLE);
                manager.beginTransaction().add(R.id.container, currentFragment).commit();
                display = true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        detailsBuffer.setVisibility(View.VISIBLE);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            final ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            int length = returnValue.size();
            if(length == 0){
                detailsBuffer.setVisibility(View.GONE);
            }
            String jsonArray = "[";
            int c = 1;
            for(String pic : returnValue){
                jsonArray += " { \"picture\" : \"" + pic + "\" }";
                if(c!=length){
                    jsonArray += ",";
                }
                c++;
            }
            jsonArray += "]";

            JSONArray jsonPics = new JSONArray();
            try{
                 jsonPics = new JSONArray(jsonArray);
            } catch(JSONException e){
                e.printStackTrace();
            }
            final JSONArray jsonArrayPics = jsonPics;

            StringRequest request = new StringRequest(Request.Method.POST,
                    getString(R.string.WSurl) + "traveller-web/rest/step/addPictures/" + currentStepId,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("Response add pics : " + response);
                            Toast.makeText(DetailsActivity.this, "Pics added", Toast.LENGTH_SHORT).show();
                            for (String pic : returnValue) {
                                Pics.add(pic);
                            }
                            detailsBuffer.setVisibility(View.GONE);
                            finish();
                            startActivity(new Intent(DetailsActivity.this,DetailsActivity.class));
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error add pics : " + error);
                    Toast.makeText(DetailsActivity.this, "Error to add pics", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public byte[] getBody() throws AuthFailureError {
                    System.out.println("////////////jsonArrayPics///////////////\n" + jsonArrayPics);
                    return jsonArrayPics.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            } ;


            RequestQueue queue = Volley.newRequestQueue(HomePageActivity.context);
            queue.add(request);



        }
    }

    @Override
    public void onBackPressed() {
        if(display) {
            manager.beginTransaction().remove(currentFragment).commit();
            container.setVisibility(View.GONE);
            display = false;
        } else {
            Pics.clear();
            story = "Story";
            finish();
        }
    }
}
