package com.example.maxime.tripshare.pageProfile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.maxime.tripshare.R;
import com.example.maxime.tripshare.homePage.HomePageActivity;
import com.example.maxime.tripshare.pageTrips.Trip;
import com.example.maxime.tripshare.tools.TripsSAdapter;

import java.util.ArrayList;
import java.util.List;

public class DispTripFragment extends Fragment {

    RecyclerView recyclerView;

    public static List<Trip> dispTrip = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.fragment_disp_trip, container, false);

        recyclerView= (RecyclerView) convertView.findViewById(R.id.recyclerView);

        LinearLayoutManager horizontalLayoutManagaer =
                new LinearLayoutManager(HomePageActivity.context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);

        TripsSAdapter adapter = new TripsSAdapter(HomePageActivity.context, R.layout.trips_template, dispTrip, true);
        recyclerView.setAdapter(adapter);



        return convertView;
    }


}
