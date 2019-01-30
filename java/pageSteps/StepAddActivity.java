package com.example.maxime.tripshare.pageSteps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.maxime.tripshare.R;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class StepAddActivity extends AppCompatActivity {

    Context context;

    EditText etName;
    TextView tvDate;
    ImageView calendar;
    ImageView btnCamera;
    Button Add;
    ProgressBar bufferAddStep;

    int idTrip;

    //Date
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;

    String pictureTaken;

    //Map
    private MapView mapView;
    private IMapController mapController;

    //Location
    private LocationManager locationManager;
    private LocationListener listener;
    Location requestLocation;
    boolean locationFound = false;

    ProgressBar mapBuffer;
    ImageButton ibAskPos;
    GeoPoint usedLocation;

    //Photo
    static final int REQUEST_IMAGE_CAPTURE = 1;
    boolean picAdded;

    SharedPreferences pref;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_add);

        context = this;

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        pref = getSharedPreferences("pref", MODE_PRIVATE);

        idTrip  = pref.getInt("idTrip", -1);

        etName = (EditText) findViewById(R.id.etName);
        tvDate = (TextView) findViewById(R.id.tvDate);
        calendar = (ImageView) findViewById(R.id.ivCalendar);
        btnCamera = (ImageView) findViewById(R.id.cameraBtn);
        Add = (Button) findViewById(R.id.btnAdd);
        mapBuffer = (ProgressBar) findViewById(R.id.mapBuffer);
        ibAskPos = (ImageButton) findViewById(R.id.ibAskPos);
        bufferAddStep = (ProgressBar) findViewById(R.id.bufferAddStep);

        picAdded = false;


        mapView = (MapView) this.findViewById(R.id.mapviewAdd);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        mapController.setZoom(3.0);

        GeoPoint centerPoint = new GeoPoint(48.859718, 2.351181); //Paris
        mapController.setCenter(centerPoint);

        //Date
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        calendar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                new DatePickerDialog(StepAddActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        //Outils de localisation
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                requestLocation = location;
                locationFound = true;

                mapBuffer.setVisibility(View.GONE);
                ibAskPos.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();


        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if(mapView.getOverlays().size() == 2){
                    mapView.getOverlays().remove(1);
                }
                usedLocation = p;
                mapController.animateTo(usedLocation);
                Marker marker = new Marker(mapView);
                marker.setPosition(usedLocation);
                marker.setIcon(resize(getDrawable(R.drawable.marker),80,80));
                mapView.getOverlays().add(marker);
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };


        MapEventsOverlay OverlayEvents = new MapEventsOverlay(getBaseContext(), mReceive);
        mapView.getOverlays().add(OverlayEvents);



        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pix.start(StepAddActivity.this, REQUEST_IMAGE_CAPTURE);
            }
        });


        //Ajout d'une étape dans le voyage

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean notCompleted = TextUtils.isEmpty(etName.getText().toString())
                        || TextUtils.isEmpty(tvDate.getText().toString())
                        || usedLocation == null;
                if(notCompleted){
                    Toast.makeText(StepAddActivity.this,"Please fill all areas", Toast.LENGTH_SHORT).show();
                } else if(!picAdded){
                    Toast.makeText(StepAddActivity.this,"Please add a picture",Toast.LENGTH_SHORT).show();
                } else if(bufferAddStep.getVisibility() != View.VISIBLE) {

                    bufferAddStep.setVisibility(View.VISIBLE);
                    final Step step = new Step(etName.getText().toString(),
                            tvDate.getText().toString(),
                            usedLocation,
                            pictureTaken, "Write your story",  idTrip);


                    final JSONObject jsonStep = new JSONObject();
                    try {
                        jsonStep.put("name",etName.getText().toString());
                        jsonStep.put("date",tvDate.getText().toString());
                        jsonStep.put("latitude",usedLocation.getLatitude());
                        jsonStep.put("longitude",usedLocation.getLongitude());
                        jsonStep.put("picFile",pictureTaken);
                        jsonStep.put("story", "Write your Story");
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    StringRequest request = new StringRequest(Request.Method.POST,
                            getString(R.string.WSurl) + "traveller-web/rest/step/addStep" +
                                    "?idTrip=" + idTrip,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    System.out.println("response step add : " + response);
                                    Toast.makeText(StepAddActivity.this, "New step added",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(StepAddActivity.this, StepsActivity.class);

                                    step.setId(Integer.parseInt(response));
                                    StepsActivity.steps.add(step);
                                    StepsActivity.areClicked.add(false);
                                    locationFound = false;
                                    bufferAddStep.setVisibility(View.GONE);

                                    finish();
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_bottom,R.anim.slide_out_bottom);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Error add step : " + error);
                            Toast.makeText(StepAddActivity.this, "Error to add step", Toast.LENGTH_SHORT).show();
                            bufferAddStep.setVisibility(View.GONE);
                        }
                    }){
                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            return jsonStep.toString().getBytes();
                        }

                        @Override
                        public String getBodyContentType() {
                            return "application/json";
                        }
                    };

                    RequestQueue queue = Volley.newRequestQueue(StepAddActivity.this);
                    queue.add(request);

                }
            }
        });

    }


    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        locationManager.requestLocationUpdates("gps", 2000, 0, listener);

        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        ibAskPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if(locationFound){
                    if(mapView.getOverlays().size() == 2){
                        mapView.getOverlays().remove(1);
                    }
                    usedLocation = new GeoPoint(requestLocation.getLatitude(), requestLocation.getLongitude());
                    mapController.animateTo(usedLocation);
                    mapController.setZoom(10.0);
                    Marker marker = new Marker(mapView);
                    marker.setPosition(usedLocation);
                    marker.setIcon(resize(getDrawable(R.drawable.marker),80,80));
                    mapView.getOverlays().add(marker);
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
            btnCamera.setImageDrawable(image);
            picAdded = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(StepAddActivity.this, REQUEST_IMAGE_CAPTURE);
                } else {
                    Toast.makeText(StepAddActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void updateLabel(){
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tvDate.setTextSize(20);
        tvDate.setText(sdf.format(myCalendar.getTime()));
    }

    private Drawable resize(Drawable image, int width, int height){
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width, height, false);
        return new BitmapDrawable(getResources(),bitmapResized);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(StepAddActivity.this,StepsActivity.class);
        locationFound = false;
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_bottom,R.anim.slide_out_bottom);
    }

}
