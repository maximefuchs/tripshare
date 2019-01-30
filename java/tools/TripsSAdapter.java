package com.example.maxime.tripshare.tools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.maxime.tripshare.R;
import com.example.maxime.tripshare.User;
import com.example.maxime.tripshare.homePage.HomeFragment;
import com.example.maxime.tripshare.homePage.HomePageActivity;
import com.example.maxime.tripshare.pageProfile.ProfileFragment;
import com.example.maxime.tripshare.pageSteps.Step;
import com.example.maxime.tripshare.pageSteps.StepsActivity;
import com.example.maxime.tripshare.pageTrips.Trip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.maxime.tripshare.homePage.HomePageActivity.context;

public class TripsSAdapter extends RecyclerView.Adapter<TripsSAdapter.ViewHolder> {

    LayoutInflater mInflater;

    int resources;

    List<Trip> trips;
    boolean socialFeed;

    DataBaseTrip dbTrip;
    SharedPreferences pref;

    public static FragmentManager manager;
    public static ProfileFragment pf;


    // data is passed into the constructor
    public TripsSAdapter(Context context, int resources, List<Trip> trips, boolean socialFeed) {
        this.mInflater = LayoutInflater.from(context);
        this.resources = resources;
        this.trips = trips;
        this.socialFeed = socialFeed;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public TripsSAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(resources, parent, false);
        return new TripsSAdapter.ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull final TripsSAdapter.ViewHolder holder, final int position) {

        dbTrip = HomePageActivity.dbTrip;
        pref = HomePageActivity.pref;
        final List<Integer> idFavedTrips = dbTrip.getIdTripsByUserId(pref.getInt("idUser", -1));

        holder.tvCity.setText(trips.get(position).getCity());
        holder.tvCountry.setText(trips.get(position).getCountry());
        if (trips.get(position).getEndCity() != null) {
            holder.rlEnd.setVisibility(View.VISIBLE);
            holder.tvEndCity.setText(trips.get(position).getEndCity());
            holder.tvEndCountry.setText(trips.get(position).getEndCountry());
        }
        holder.tvDate.setText(trips.get(position).getDate());
        holder.tvEndDate.setText(trips.get(position).getEndDate());
        if (socialFeed) {

            holder.llSocial.setVisibility(View.VISIBLE);
            if (idFavedTrips.contains(trips.get(position).getId())) {
                holder.ivFavourite.setImageResource(R.drawable.faved);
            }
            holder.llCreator.setVisibility(View.VISIBLE);
            holder.creator.setText(trips.get(position).getCreator());

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;

            Bitmap bm = BitmapFactory.decodeFile(trips.get(position).getPicCreator(), options);
            bm.compress(Bitmap.CompressFormat.JPEG, 0, new ByteArrayOutputStream());
            Drawable profilePic = new BitmapDrawable(HomePageActivity.res, bm);

            holder.picCreator.setImageDrawable(profilePic);

            holder.picCreator.setClickable(true);
            holder.picCreator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProfile(trips.get(position).getUserId(), holder.buffer);
                }
            });

        } else {
            holder.ibDelete.setVisibility(View.VISIBLE);
            holder.ibShare.setVisibility(View.VISIBLE);
            List<Integer> idSharedTrips = new ArrayList<>();
            for (int i = 0; i < HomeFragment.sharedtrips.size(); i++) {
                idSharedTrips.add(HomeFragment.sharedtrips.get(i).getId());
            }
            if (idSharedTrips.contains(trips.get(position).getId())) {
                holder.ibShare.setImageResource(R.drawable.shared);
            }
        }


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;

        Bitmap bm = BitmapFactory.decodeFile(trips.get(position).getPic(), options);
        bm.compress(Bitmap.CompressFormat.JPEG, 0, new ByteArrayOutputStream());
        Drawable image = new BitmapDrawable(HomePageActivity.res, bm);


        holder.llTrip.setBackground(image);

        holder.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.buffer.getVisibility() != View.VISIBLE) {
                    holder.buffer.setVisibility(View.VISIBLE);
                    holder.ibShare.setImageResource(R.drawable.shared);
                    shareTrip(trips.get(position).getId(), holder.buffer);
                    HomeFragment.sharedtrips.add(trips.get(position));
                    ProfileFragment.mysharedtrips.add((trips.get(position)));
                }
            }
        });
        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.buffer.getVisibility() != View.VISIBLE) {
                    holder.buffer.setVisibility(View.VISIBLE);
                    removeTrip(position, holder.buffer);
                    int idTrip = trips.get(position).getId();
                    for (int i = 0; i < HomeFragment.sharedtrips.size(); i++) {
                        if(idTrip == HomeFragment.sharedtrips.get(i).getId()){
                            HomeFragment.sharedtrips.remove(i);
                        }
                    }
                    trips.remove(position);
                }
            }
        });

        holder.tvCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(position, holder.buffer);
            }
        });


        holder.ivFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idFavedTrips.contains(trips.get(position).getId())) {
                    holder.ivFavourite.setImageResource(R.drawable.favorite);
                    Toast.makeText(HomePageActivity.context, "Unfaved", Toast.LENGTH_SHORT).show();
                    dbTrip.removeTrip(trips.get(position).getId());
                    //idTrips.remove(position - 1);
                } else {
                    holder.ivFavourite.setImageResource(R.drawable.faved);
                    Toast.makeText(HomePageActivity.context, "Faved", Toast.LENGTH_SHORT).show();
                    dbTrip.addTrip(trips.get(position));
                    idFavedTrips.add(trips.get(position).getId());
                }
            }
        });

    }

    public void click(int position, final ProgressBar tripBuffer) {
        if(tripBuffer.getVisibility() != View.VISIBLE) {

            final int idTrip = trips.get(position).getId();
            final SharedPreferences pref = HomePageActivity.pref;

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isMyTrip", trips.get(position).getUserId() == pref.getInt("idUser", -1));
            editor.commit();

            tripBuffer.setVisibility(View.VISIBLE);

            StringRequest request = new StringRequest(Request.Method.GET,
                    HomePageActivity.wsURL + "traveller-web/rest/step/stepsByTripId/" + idTrip,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("Get steps response : " + response);

                            try {
                                JSONArray jsonSteps = new JSONArray(response);
                                for (int i = 0; i < jsonSteps.length(); i++) {
                                    JSONObject jsonstep = jsonSteps.getJSONObject(i);
                                    Step step = new Step();
                                    step.setId(jsonstep.getInt("id"));
                                    step.setName(jsonstep.getString("name"));
                                    step.setDate(jsonstep.getString("date"));
                                    GeoPoint point = new GeoPoint(jsonstep.getDouble("latitude"),
                                            jsonstep.getDouble("longitude"));
                                    step.setGeo(point);
                                    step.setCoverPic(jsonstep.getString("picFile"));
                                    step.setStory(jsonstep.getString("story"));
                                    step.setIdTrip(idTrip);

                                    StepsActivity.steps.add(step);
                                }
                                Toast.makeText(HomePageActivity.context, "steps found", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(HomePageActivity.context, StepsActivity.class);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putInt("idTrip", idTrip);
                                editor.commit();
                                tripBuffer.setVisibility(View.GONE);
                                HomePageActivity.context.startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("get steps error : " + error);
                    Toast.makeText(HomePageActivity.context, "Error loading steps", Toast.LENGTH_LONG).show();
                }
            });

            RequestQueue queue = Volley.newRequestQueue(HomePageActivity.context);
            queue.add(request);
        }
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return trips.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llTrip;
        TextView tvCity;
        TextView tvCountry;
        TextView tvEndCity;
        TextView tvEndCountry;
        TextView tvDate;
        TextView tvEndDate;
        RelativeLayout rlEnd;
        RelativeLayout rlTemplate;
        LinearLayout llSocial;
        //ImageButton ibLike;
        //ImageButton ibDislike;
        ImageView ivFavourite;
        ImageView ibShare;
        ImageView ibDelete;
        ProgressBar buffer;
        LinearLayout llCreator;
        ImageView picCreator;
        TextView creator;

        ViewHolder(View itemView) {
            super(itemView);

            llTrip = (LinearLayout) itemView.findViewById(R.id.llTrip);
            tvCity = (TextView) itemView.findViewById(R.id.tvCity);
            tvCountry = (TextView) itemView.findViewById(R.id.tvCountry);
            tvEndCity = (TextView) itemView.findViewById(R.id.tvEndCity);
            tvEndCountry = (TextView) itemView.findViewById(R.id.tvEndCountry);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvEndDate = (TextView) itemView.findViewById(R.id.tvEndDate);
            rlEnd = (RelativeLayout) itemView.findViewById(R.id.rlEnd);
            rlTemplate = (RelativeLayout) itemView.findViewById(R.id.rlTemplate);
            llSocial = (LinearLayout) itemView.findViewById(R.id.llSocial);
            //ibLike = (ImageButton) itemView.findViewById(R.id.ibLike);
            //ibDislike = (ImageButton) itemView.findViewById(R.id.ibDislike);
            ivFavourite = (ImageView) itemView.findViewById(R.id.ivFavourite);
            ibShare = (ImageView) itemView.findViewById(R.id.ibShare);
            ibDelete = (ImageView) itemView.findViewById(R.id.ibDelete);
            buffer = (ProgressBar) itemView.findViewById(R.id.tripBuffer);
            llCreator = (LinearLayout) itemView.findViewById(R.id.llCreator);
            picCreator = (ImageView) itemView.findViewById(R.id.profilePic);
            creator = (TextView) itemView.findViewById(R.id.creatorName);
        }
    }

    public void shareTrip(int idTrip, final ProgressBar tripBuffer) {
        StringRequest request = new StringRequest(Request.Method.POST,
                HomePageActivity.wsURL + "traveller-web/rest/trip/shareTrip/" + idTrip,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Share trip response " + response);
                        tripBuffer.setVisibility(View.GONE);
                        Toast.makeText(HomePageActivity.context, "Trip shared", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Share trip error : " + error);
                Toast.makeText(HomePageActivity.context, "Error to share trip", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(HomePageActivity.context);
        queue.add(request);
    }

    public void removeTrip(final int position, final ProgressBar tripBuffer) {
        int idTrip = trips.get(position).getId();
        StringRequest request = new StringRequest(Request.Method.POST,
                HomePageActivity.wsURL + "traveller-web/rest/trip/removeTrip"
                        + "?id=" + idTrip,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("remove trip response : " + response);
                        tripBuffer.setVisibility(View.GONE);
                        Toast.makeText(HomePageActivity.context, "Trip deleted", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error delete trip : " + error);
                Toast.makeText(HomePageActivity.context, "Error to delete trip", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(HomePageActivity.context);
        queue.add(request);
    }

    public void showProfile(int id, final ProgressBar buffer){
        buffer.setVisibility(View.VISIBLE);

        ProfileFragment.mysharedtrips.clear();

        manager = ((AppCompatActivity) context).getSupportFragmentManager();
        pf = new ProfileFragment();
        final boolean[] finish = {false};

        StringRequest request = new StringRequest(Request.Method.GET,
                HomePageActivity.wsURL + "traveller-web/rest/user/userById/" + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonUser = new JSONObject(response);
                            User user = new User();
                            user.setFirstname(jsonUser.getString("firstname"));
                            user.setLastname(jsonUser.getString("lastname"));
                            user.setProfilpic(jsonUser.getString("profilepic"));

                            ProfileFragment.otherUser = user;


                            if(finish[0]) {
                                manager.beginTransaction()
                                        .add(R.id.mainContainer, pf).commit();
                                ProfileFragment.dispCurrent = false;
                                buffer.setVisibility(View.GONE);
                            } else {
                                finish[0] = true;
                            }

                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error find this user : " + error);
                Toast.makeText(HomePageActivity.context, "Error to find this user", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(HomePageActivity.context);
        queue.add(request);


        StringRequest request2 = new StringRequest(Request.Method.GET,
                HomePageActivity.wsURL + "traveller-web/rest/trip/sharedTrips/" + id,
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
                                trip.setUserId(jsontrip.getInt("user"));

                                ProfileFragment.mysharedtrips.add(trip);
                            }
                            if(finish[0]) {
                                manager.beginTransaction()
                                        .add(R.id.mainContainer, pf).commit();
                                ProfileFragment.dispCurrent = false;
                                buffer.setVisibility(View.GONE);
                            } else {
                                finish[0] = true;
                            }

                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error find trips from this user : " + error);
                Toast.makeText(HomePageActivity.context, "Error to find shared trip from user", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(request2);



    }

}
