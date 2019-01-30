package com.example.maxime.tripshare.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.maxime.tripshare.R;
import com.example.maxime.tripshare.homePage.HomePageActivity;
import com.example.maxime.tripshare.pageTrips.MyTripsFragment;
import com.example.maxime.tripshare.pageTrips.Trip;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static java.lang.System.out;

public class TripsAdapter extends ArrayAdapter<Trip> {

    Context context;
    List<Trip> trips;
    int ressources;
    boolean socialFeed;


    public TripsAdapter(@NonNull Context context, int res, @NonNull List<Trip> objects, boolean socialFeed) {
        super(context, res, objects);
        this.context = context;
        this.ressources = res;
        this.trips = objects;
        this.socialFeed = socialFeed;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder vHolder;

        if (convertView == null) {
            convertView = inflater.inflate(ressources, null);

            vHolder = new ViewHolder();
            vHolder.llTrip = (LinearLayout) convertView.findViewById(R.id.llTrip);
            vHolder.tvCity = (TextView) convertView.findViewById(R.id.tvCity);
            vHolder.tvCountry = (TextView) convertView.findViewById(R.id.tvCountry);
            vHolder.tvEndCity = (TextView) convertView.findViewById(R.id.tvEndCity);
            vHolder.tvEndCountry = (TextView) convertView.findViewById(R.id.tvEndCountry);
            vHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            vHolder.tvEndDate = (TextView) convertView.findViewById(R.id.tvEndDate);
            vHolder.rlEnd = (RelativeLayout) convertView.findViewById(R.id.rlEnd);
            vHolder.rlTemplate = (RelativeLayout) convertView.findViewById(R.id.rlTemplate);
            vHolder.llSocial = (LinearLayout) convertView.findViewById(R.id.llSocial);
            //vHolder.ibLike = (ImageButton) convertView.findViewById(R.id.ibLike);
            //vHolder.ibDislike = (ImageButton) convertView.findViewById(R.id.ibDislike);
            vHolder.ivFavourite = (ImageView) convertView.findViewById(R.id.ivFavourite);
            vHolder.ibShare = (ImageButton) convertView.findViewById(R.id.ibShare);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        vHolder.tvCity.setText(trips.get(position).getCity());
        vHolder.tvCountry.setText(trips.get(position).getCountry());
        if (trips.get(position).getEndCity() != null) {
            vHolder.rlEnd.setVisibility(View.VISIBLE);
            vHolder.tvEndCity.setText(trips.get(position).getEndCity());
            vHolder.tvEndCountry.setText(trips.get(position).getEndCountry());
        }
        vHolder.tvDate.setText(trips.get(position).getDate());
        vHolder.tvEndDate.setText(trips.get(position).getEndDate());
        if(socialFeed){
            vHolder.llSocial.setVisibility(View.VISIBLE);
        }


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;

        Bitmap bm = BitmapFactory.decodeFile(trips.get(position).getPic(), options);
        bm.compress(Bitmap.CompressFormat.JPEG,0,new ByteArrayOutputStream());
        Drawable image = new BitmapDrawable(HomePageActivity.res, bm);


        vHolder.llTrip.setBackground(image);

        return convertView;

                /*
        Bitmap mbitmap = ((BitmapDrawable) image).getBitmap();
        Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint mpaint = new Paint();
        mpaint.setColor(Color.BLACK);
        mpaint.setAntiAlias(true);
        mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 100, 100, mpaint);
        // Round Image Corner 100 100 100 100
        */

        //Drawable i = new BitmapDrawable(TripsActivity.resources, imageRounded);


    }

    class ViewHolder {
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
        ImageButton ibShare;
    }


}
