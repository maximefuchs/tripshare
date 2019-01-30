package com.example.maxime.tripshare.pageTrips;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.maxime.tripshare.R;
import com.example.maxime.tripshare.UserAddActivity;
import com.example.maxime.tripshare.homePage.HomePageActivity;
import com.fxn.pix.Pix;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TripAddActivity extends AppCompatActivity {

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;

    EditText City;
    EditText Country;
    ToggleButton roadTripToggle;
    EditText endCity;
    EditText endCountry;
    ImageView calendar;
    TextView startDay;
    LinearLayout rlEndDay;
    ImageView calendarEnd;
    TextView endDay;
    Button add;
    ImageView cameraButton;
    LinearLayout llRoadtrip;
    ProgressBar bufferAddTrip;

    SharedPreferences pref;
    int idUser;

    boolean roadtripCalendar = false;
    public static boolean roadtrip = false;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String pictureFile;
    boolean picAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_add);

        pref = getSharedPreferences("pref", MODE_PRIVATE);

        idUser = pref.getInt("idUser",-1);

        City = (EditText) findViewById(R.id.etCity);
        Country = (EditText) findViewById(R.id.etCountry);
        roadTripToggle = (ToggleButton) findViewById(R.id.roadTripSwitch);
        endCity = (EditText) findViewById(R.id.etEndCity);
        endCountry = (EditText) findViewById(R.id.etEndCountry);
        rlEndDay = (LinearLayout) findViewById(R.id.rlEndDay);
        calendar = (ImageView) findViewById(R.id.ivCalendar);
        startDay = (TextView) findViewById(R.id.tvStartDate);
        calendarEnd = (ImageView) findViewById(R.id.ivCalendarEnd);
        endDay = (TextView) findViewById(R.id.tvEndDate);
        add = (Button) findViewById(R.id.btnAdd);
        cameraButton = (ImageView) findViewById(R.id.cameraBtn);
        llRoadtrip = (LinearLayout) findViewById(R.id.llRoadTrip);
        bufferAddTrip = (ProgressBar) findViewById(R.id.bufferAddTrip);

        picAdded = false;

        roadTripToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roadTripToggle.isChecked()){
                    roadtrip = true;
                    llRoadtrip.setVisibility(View.VISIBLE);

                } else {
                    roadtrip = false;
                    llRoadtrip.setVisibility(View.GONE);
                }
            }
        });

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
                //verifyDates(startDay.getText().toString(),endDay.getText().toString());
            }
        };

        calendar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                roadtripCalendar = false;

                // TODO Auto-generated method stub
                new DatePickerDialog(TripAddActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        calendarEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                roadtripCalendar = true;
                // TODO Auto-generated method stub
                new DatePickerDialog(TripAddActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pix.start(TripAddActivity.this, REQUEST_IMAGE_CAPTURE);
            }
        });



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean notCompleted = TextUtils.isEmpty(City.getText().toString())
                        || TextUtils.isEmpty(Country.getText().toString())
                        || TextUtils.isEmpty(startDay.getText().toString())
                        || (roadTripToggle.isChecked()&& TextUtils.isEmpty(endCity.getText().toString()))
                        || (roadTripToggle.isChecked() && TextUtils.isEmpty(endCountry.getText().toString()));
                if(notCompleted){
                    Toast.makeText(TripAddActivity.this, "Please fill all areas", Toast.LENGTH_SHORT).show();
                } else if(!picAdded){
                    Toast.makeText(TripAddActivity.this, "Please add a picture",Toast.LENGTH_SHORT).show();
                } else if (bufferAddTrip.getVisibility() != View.VISIBLE){

                    bufferAddTrip.setVisibility(View.VISIBLE);
                    final Trip trip;
                    if (roadtrip) {
                        trip = new Trip(City.getText().toString(),
                                Country.getText().toString(),
                                startDay.getText().toString(),
                                endDay.getText().toString(),
                                endCity.getText().toString(),
                                endCountry.getText().toString(),
                                pictureFile, idUser,
                                pref.getString("userFN", "unknown"),
                                pref.getString("userPP", ""));
                    } else {
                        trip = new Trip(City.getText().toString(),
                                Country.getText().toString(),
                                startDay.getText().toString(),
                                endDay.getText().toString(),
                                pictureFile, idUser,
                                pref.getString("userFN", "unknown"),
                                pref.getString("userPP", ""));
                    }


                    final JSONObject jsontrip = new JSONObject();
                    try{
                        jsontrip.put("city", City.getText().toString());
                        jsontrip.put("country", Country.getText().toString());
                        jsontrip.put("startDate", startDay.getText().toString());
                        jsontrip.put("endDate", endDay.getText().toString());
                        if(roadtrip) {
                            jsontrip.put("endCity", endCity.getText().toString());
                            jsontrip.put("endCountry", endCountry.getText().toString());
                        }
                        jsontrip.put("picFile", pictureFile);
                        jsontrip.put("creator", pref.getString("userFN", "unknown"));
                        jsontrip.put("picCreator", pref.getString("userPP", ""));
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    StringRequest request = new StringRequest(Request.Method.POST,
                            getString(R.string.WSurl) + "traveller-web/rest/trip/addTrip"
                            + "?idUser=" + idUser,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(TripAddActivity.this, "New trip added",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(TripAddActivity.this, HomePageActivity.class);
                                    trip.setId(Integer.parseInt(response));
                                    //MyTripsFragment.trips.clear();
                                    MyTripsFragment.trips.add(trip);
                                    finish();
                                    //startActivity(intent);
                                    //getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new MyTripsFragment()).commit();
                                    overridePendingTransition(R.anim.slide_in_bottom,R.anim.slide_out_bottom);
                                    bufferAddTrip.setVisibility(View.GONE);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Error add trip : " + error);
                            Toast.makeText(TripAddActivity.this, "Error to add trip",Toast.LENGTH_LONG).show();
                            bufferAddTrip.setVisibility(View.GONE);
                        }
                    }){
                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            return jsontrip.toString().getBytes();
                        }

                        @Override
                        public String getBodyContentType() {
                            return "application/json";
                        }
                    };

                    RequestQueue queue = Volley.newRequestQueue(TripAddActivity.this);
                    queue.add(request);


//                    MyTripsFragment.trips.add(trip);
                }
            }
        });

    }
    private void updateLabel(){
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        if(roadtripCalendar){
            endDay.setTextSize(20);
            endDay.setText(sdf.format(myCalendar.getTime()));
        } else {
            startDay.setTextSize(20);
            startDay.setText(sdf.format(myCalendar.getTime()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            pictureFile = returnValue.get(0);
            Bitmap bm = BitmapFactory.decodeFile(pictureFile);
            Drawable image = new BitmapDrawable(getResources(), bm);
            cameraButton.setImageDrawable(image);
            picAdded = true;
        }
    }

    @Override
    public void onBackPressed() {
     //   Intent intent = new Intent(TripAddActivity.this, HomePageActivity.class);
        finish();
       // startActivity(intent);
    }
}
