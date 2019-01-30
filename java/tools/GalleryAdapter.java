package com.example.maxime.tripshare.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;


import com.example.maxime.tripshare.R;
import com.example.maxime.tripshare.pageDetails.DetailsActivity;

import java.util.List;

public class GalleryAdapter extends ArrayAdapter<String> {

    Context context;
    List<String> txtsPics;
    int resources;

    ImageView ivImage;

    public GalleryAdapter(Context context, int resources, List<String> txtsPics) {
        super(context, resources, txtsPics);
        this.context = context;
        this.txtsPics = txtsPics;
        this.resources = resources;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resources, null);

        ivImage = (ImageView) convertView.findViewById(R.id.image);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;

        Bitmap bm = BitmapFactory.decodeFile(txtsPics.get(position),options);
        Drawable image = new BitmapDrawable(DetailsActivity.resources, bm);
        ivImage.setImageDrawable(image);

        return convertView;
    }
}
