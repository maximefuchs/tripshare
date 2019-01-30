package com.example.maxime.tripshare.pageProfile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maxime.tripshare.LoginActivity;
import com.example.maxime.tripshare.R;
import com.example.maxime.tripshare.User;
import com.example.maxime.tripshare.homePage.HomeFragment;
import com.example.maxime.tripshare.homePage.HomePageActivity;
import com.example.maxime.tripshare.pageTrips.MyTripsFragment;
import com.example.maxime.tripshare.pageTrips.Trip;
import com.example.maxime.tripshare.tools.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    RecyclerView recyclerView;
    public static List<Trip> mysharedtrips = new ArrayList<>();

    public static boolean dispCurrent = true;

    public static User otherUser;

    User currentUser;

    TextView tvNom;
    TextView tvPrenom;
    ImageView profilPic;
    Button btnLogOut;

    SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_profile, container, false);

        recyclerView = (RecyclerView) convertView.findViewById(R.id.recyclerView);
        tvNom = (TextView) convertView.findViewById(R.id.tvNom);
        tvPrenom = (TextView) convertView.findViewById(R.id.tvPrenom);
        profilPic = (ImageView) convertView.findViewById(R.id.profilePic);
        btnLogOut = (Button) convertView.findViewById(R.id.btnLogOut);

        pref = HomePageActivity.pref;

        currentUser = new User();
        currentUser.setFirstname(pref.getString("userFN",""));
        currentUser.setLastname(pref.getString("userLN",""));
        currentUser.setUsername(pref.getString("userUN",""));
        currentUser.setPassword(pref.getString("userPW",""));
        currentUser.setProfilpic(pref.getString("userPP",""));

        Bitmap bm;
        if(dispCurrent) {
            tvNom.setText(currentUser.getLastname());
            tvPrenom.setText(currentUser.getFirstname());
            bm = BitmapFactory.decodeFile(currentUser.getProfilpic());
        } else {
            tvNom.setText(otherUser.getLastname());
            tvPrenom.setText(otherUser.getFirstname());
            bm = BitmapFactory.decodeFile(otherUser.getProfilpic());
        }

        Drawable image = new BitmapDrawable(getResources(), bm);
        profilPic.setImageDrawable(image);

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(HomePageActivity.context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);

        RecyclerAdapter adapter = new RecyclerAdapter(HomePageActivity.context, R.layout.recycler_template, mysharedtrips);
        recyclerView.setAdapter(adapter);

        //recyclerView.setClickable(true);

        if(!dispCurrent){
            btnLogOut.setVisibility(View.GONE);
        }

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTripsFragment.trips.clear();
                HomeFragment.sharedtrips.clear();
                ProfileFragment.mysharedtrips.clear();
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(HomePageActivity.context, LoginActivity.class);
                getActivity().finish();
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.bigzoom_in,R.anim.bigzoom_out);
            }
        });

        HomePageActivity.homePageBuffer.setVisibility(View.GONE);
        return convertView;
    }
}