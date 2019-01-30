package com.example.maxime.tripshare.homePage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.example.maxime.tripshare.pageSteps.Step;
import com.example.maxime.tripshare.pageSteps.StepsActivity;
import com.example.maxime.tripshare.pageTrips.Trip;
import com.example.maxime.tripshare.tools.DataBaseTrip;
import com.example.maxime.tripshare.tools.TripsAdapter;
import com.example.maxime.tripshare.tools.TripsSAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    RecyclerView lst;
    List<Trip> favedtrips = new ArrayList<>();
    DataBaseTrip dbTrip;
    SharedPreferences pref;

    ProgressBar tripBuffer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_favorites, container, false);

        pref = HomePageActivity.pref;
        lst = (RecyclerView) convertView.findViewById(R.id.lvTrips);

        dbTrip = HomePageActivity.dbTrip;
        int idUser = pref.getInt("idUser",-1);
        System.out.println("//////////////////////////////////////// current id User : " + idUser);
        favedtrips = dbTrip.getTripsByUserId(idUser);

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(HomePageActivity.context, LinearLayoutManager.VERTICAL, false);
        lst.setLayoutManager(horizontalLayoutManagaer);
        TripsSAdapter adapter = new TripsSAdapter(HomePageActivity.context,R.layout.trips_template, favedtrips, true);
        lst.setAdapter(adapter);

        HomePageActivity.homePageBuffer.setVisibility(View.GONE);
        return convertView;
    }
}
