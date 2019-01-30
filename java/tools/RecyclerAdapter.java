package com.example.maxime.tripshare.tools;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;

import com.example.maxime.tripshare.R;
import com.example.maxime.tripshare.homePage.HomePageActivity;
import com.example.maxime.tripshare.pageProfile.DispTripFragment;
import com.example.maxime.tripshare.pageTrips.Trip;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.example.maxime.tripshare.homePage.HomePageActivity.context;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    LayoutInflater mInflater;

    List<Trip> mysharedtrips;
    int resources;

    public static boolean tripDisplayed = false;
    public static FragmentManager manager;
    public static DispTripFragment dtf;

    // data is passed into the constructor
    public RecyclerAdapter(Context context, int resources, List<Trip> mysharedtrips) {
        this.mInflater = LayoutInflater.from(context);
        this.mysharedtrips = mysharedtrips;
        this.resources = resources;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(resources, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.tvMainCity.setText(mysharedtrips.get(position).getCity());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;

        Bitmap bm = BitmapFactory.decodeFile(mysharedtrips.get(position).getPic(), options);
        bm.compress(Bitmap.CompressFormat.JPEG,0,new ByteArrayOutputStream());
        Drawable image = new BitmapDrawable(HomePageActivity.res, bm);

        holder.ivImage.setImageDrawable(image);
        holder.ivImage.setClickable(true);

        holder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tripDisplayed) {
                    DispTripFragment.dispTrip.clear();
                    DispTripFragment.dispTrip.add(mysharedtrips.get(position));
                    manager = ((AppCompatActivity) context).getSupportFragmentManager();
                    dtf = new DispTripFragment();
                    manager.beginTransaction().add(R.id.mainContainer, dtf).commit();
                    tripDisplayed = true;
                }
            }
        });

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mysharedtrips.size();
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvMainCity;

        ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.image);
            tvMainCity = itemView.findViewById(R.id.MainCity);
        }
    }
}