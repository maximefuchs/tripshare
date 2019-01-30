package com.example.maxime.tripshare.pageTrips;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.maxime.tripshare.R;
import com.example.maxime.tripshare.UserAddActivity;
import com.example.maxime.tripshare.homePage.HomeFragment;
import com.example.maxime.tripshare.pageSteps.Step;
import com.example.maxime.tripshare.pageSteps.StepsActivity;
import com.example.maxime.tripshare.homePage.HomePageActivity;
import com.example.maxime.tripshare.tools.TripsAdapter;
import com.example.maxime.tripshare.tools.TripsSAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class MyTripsFragment extends Fragment {

    RecyclerView lst;
    public static List<Trip> trips = new ArrayList<>();
    public static Resources resources;

    public static FloatingActionButton floatingBtn;

    SharedPreferences pref;

    public static String wsURL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_my_trips, container, false);

        pref = HomePageActivity.pref;
        wsURL = getString(R.string.WSurl);

        resources = getResources();

        lst = (RecyclerView) convertView.findViewById(R.id.lvTrips);
        floatingBtn = (FloatingActionButton) convertView.findViewById(R.id.floatBtn);

//   /storage/emulated/0/DCIM/Camera/20181007_103308.jpg    pic Kelibia

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(HomePageActivity.context, LinearLayoutManager.VERTICAL, false);
        lst.setLayoutManager(horizontalLayoutManagaer);
        TripsSAdapter adapter = new TripsSAdapter(getContext(), R.layout.trips_template, trips, false);
        lst.setAdapter(adapter);

        floatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.context, TripAddActivity.class);
          //      trips.clear();
          //      getActivity().finish();
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_top,R.anim.slide_out_top);
            }
        });


        HomePageActivity.homePageBuffer.setVisibility(View.GONE);
        return convertView;

    }



}
