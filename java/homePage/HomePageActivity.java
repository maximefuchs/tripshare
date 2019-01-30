package com.example.maxime.tripshare.homePage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.maxime.tripshare.R;
import com.example.maxime.tripshare.WelcomeFragment;
import com.example.maxime.tripshare.pageProfile.DispTripFragment;
import com.example.maxime.tripshare.pageProfile.ProfileFragment;
import com.example.maxime.tripshare.pageTrips.MyTripsFragment;
import com.example.maxime.tripshare.pageTrips.Trip;
import com.example.maxime.tripshare.tools.DataBaseTrip;
import com.example.maxime.tripshare.tools.RecyclerAdapter;
import com.example.maxime.tripshare.tools.TripsSAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomePageActivity extends AppCompatActivity{

    public static Context context;
    public static Resources res;
    public static int currentIdUser;
    BottomNavigationView bottomMenu;
    public static ProgressBar homePageBuffer;

    public static String wsURL;

    public static SharedPreferences pref;

    public static DataBaseTrip dbTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        context = this;
        res = getResources();
        wsURL = getString(R.string.WSurl);
        bottomMenu = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        homePageBuffer = (ProgressBar) findViewById(R.id.homePageBuffer);

        dbTrip = new DataBaseTrip(context);

        pref = getSharedPreferences("pref",MODE_PRIVATE);

        currentIdUser = pref.getInt("idUser",-1);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainContainer,new WelcomeFragment()).commit();
        homePageBuffer.setVisibility(View.VISIBLE);
        if(HomeFragment.sharedtrips.size() == 0) {
            startHome();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainContainer,new HomeFragment()).commit();
        }

        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                RecyclerAdapter.tripDisplayed = false;
                ProfileFragment.dispCurrent = true;
                homePageBuffer.setVisibility(View.VISIBLE);
                if (id == R.id.home) {

                    if(HomeFragment.sharedtrips.size() != 0){
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainContainer,new HomeFragment()).commit();
                    } else {
                        startHome();
                    }

                } else if (id == R.id.favorites) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mainContainer,new FavouritesFragment()).commit();

                } else if (id == R.id.mytrips) {

                    if(MyTripsFragment.trips.size() != 0) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainContainer,new MyTripsFragment()).commit();
                    } else {
                        startMyTrips();
                    }

                } else if (id == R.id.profile) {

                    if(ProfileFragment.mysharedtrips.size() != 0 && !ProfileFragment.dispCurrent) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainContainer,new ProfileFragment()).commit();
                    } else {
                        ProfileFragment.dispCurrent = true;
                        startProfile();
                    }

                }
                return true;
            }
        });

    }

    public void startMyTrips(){
        StringRequest request = new StringRequest(Request.Method.GET,
                getString(R.string.WSurl) + "traveller-web/rest/trip/tripsByUserId/" + currentIdUser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonTrips = new JSONArray(response);
                            for(int i = 0 ; i<jsonTrips.length() ; i++){
                                JSONObject jsontrip = jsonTrips.getJSONObject(i);
                                Trip trip = new Trip();
                                trip.setId(jsontrip.getInt("id"));
                                trip.setCity(jsontrip.getString("city"));
                                trip.setCountry(jsontrip.getString("country"));
                                if(!jsontrip.getString("endCity").equals("null")){
                                    trip.setEndCity(jsontrip.getString("endCity"));
                                    trip.setEndCountry(jsontrip.getString("endCountry"));
                                }
                                trip.setDate(jsontrip.getString("startDate"));
                                trip.setEndDate(jsontrip.getString("endDate"));
                                trip.setPic(jsontrip.getString("picFile"));
                                trip.setCreator(jsontrip.getString("creator"));
                                trip.setPicCreator(jsontrip.getString("picCreator"));
                                trip.setUserId(currentIdUser);

                                MyTripsFragment.trips.add(trip);
                            }
                            System.out.println("Response find trips : " + response);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.mainContainer,new MyTripsFragment()).commit();

                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error find trip : " + error);
                Toast.makeText(HomePageActivity.this, "Error to find trips", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(HomePageActivity.this);
        queue.add(request);
    }

    public void startHome(){
        StringRequest request = new StringRequest(Request.Method.GET,
                getString(R.string.WSurl) + "traveller-web/rest/trip/sharedTrips",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonTrips = new JSONArray(response);
                            for(int i = 0 ; i<jsonTrips.length() ; i++){
                                JSONObject jsontrip = jsonTrips.getJSONObject(i);
                                Trip trip = new Trip();
                                trip.setId(jsontrip.getInt("id"));
                                trip.setCity(jsontrip.getString("city"));
                                trip.setCountry(jsontrip.getString("country"));
                                if(!jsontrip.getString("endCity").equals("null")){
                                    trip.setEndCity(jsontrip.getString("endCity"));
                                    trip.setEndCountry(jsontrip.getString("endCountry"));
                                }
                                trip.setDate(jsontrip.getString("startDate"));
                                trip.setEndDate(jsontrip.getString("endDate"));
                                trip.setPic(jsontrip.getString("picFile"));
                                trip.setUserId(jsontrip.getInt("user"));
                                trip.setCreator(jsontrip.getString("creator"));
                                trip.setPicCreator(jsontrip.getString("picCreator"));

                                HomeFragment.sharedtrips.add(trip);
                            }
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.mainContainer,new HomeFragment()).commit();

                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error find trip : " + error);
                Toast.makeText(HomePageActivity.this, "Error to find trips", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(HomePageActivity.this);
        queue.add(request);
    }

    public void startProfile(){
        ProfileFragment.mysharedtrips.clear();
        StringRequest request = new StringRequest(Request.Method.GET,
                getString(R.string.WSurl) + "traveller-web/rest/trip/sharedTrips/" + currentIdUser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonTrips = new JSONArray(response);
                            for(int i = 0 ; i<jsonTrips.length() ; i++){
                                JSONObject jsontrip = jsonTrips.getJSONObject(i);
                                Trip trip = new Trip();
                                trip.setId(jsontrip.getInt("id"));
                                trip.setCity(jsontrip.getString("city"));
                                trip.setCountry(jsontrip.getString("country"));
                                if(!jsontrip.getString("endCity").equals("null")){
                                    trip.setEndCity(jsontrip.getString("endCity"));
                                    trip.setEndCountry(jsontrip.getString("endCountry"));
                                }
                                trip.setDate(jsontrip.getString("startDate"));
                                trip.setEndDate(jsontrip.getString("endDate"));
                                trip.setPic(jsontrip.getString("picFile"));
                                trip.setCreator(jsontrip.getString("creator"));
                                trip.setPicCreator(jsontrip.getString("picCreator"));
                                trip.setUserId(currentIdUser);

                                ProfileFragment.mysharedtrips.add(trip);
                            }
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.mainContainer,new ProfileFragment()).commit();

                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error find trip pics : " + error);
                Toast.makeText(HomePageActivity.this, "Error to find trip pics", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(HomePageActivity.this);
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        if(RecyclerAdapter.tripDisplayed){
            RecyclerAdapter.manager.beginTransaction().remove(RecyclerAdapter.dtf).commit();
            RecyclerAdapter.tripDisplayed = false;
        } else if(!ProfileFragment.dispCurrent) {
            TripsSAdapter.manager.beginTransaction().remove(TripsSAdapter.pf).commit();
            ProfileFragment.mysharedtrips.clear();
            ProfileFragment.dispCurrent = true;
        } else {
            finish();
        }
    }
}
