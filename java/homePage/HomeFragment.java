package com.example.maxime.tripshare.homePage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.maxime.tripshare.R;
import com.example.maxime.tripshare.pageTrips.Trip;
import com.example.maxime.tripshare.tools.DataBaseTrip;
import com.example.maxime.tripshare.tools.TripsSAdapter;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    RecyclerView lst;

    public static List<Trip> sharedtrips = new ArrayList<>();
    DataBaseTrip dbTrips;


    SharedPreferences pref;

    boolean longClicked;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_home, container, false);

        pref = HomePageActivity.pref;

        lst = (RecyclerView) convertView.findViewById(R.id.lvHomeTrips);

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(HomePageActivity.context, LinearLayoutManager.VERTICAL, false);
        lst.setLayoutManager(horizontalLayoutManagaer);
        TripsSAdapter adapter = new TripsSAdapter(getContext(), R.layout.trips_template, sharedtrips, true);
        lst.setAdapter(adapter);

        longClicked = false;

        dbTrips = new DataBaseTrip(HomePageActivity.context);

        HomePageActivity.homePageBuffer.setVisibility(View.GONE);

        return  convertView;
    }
}
