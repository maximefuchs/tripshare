package com.example.maxime.tripshare.pageSteps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.maxime.tripshare.homePage.HomePageActivity;
import com.example.maxime.tripshare.pageDetails.DetailsActivity;
import com.example.maxime.tripshare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class StepsActivity extends AppCompatActivity {

    //Elements à récuperer dans la bdd
    public static List<Step> steps = new ArrayList<>();
    //Liste associée à steps
    public static List<Boolean> areClicked = new ArrayList<>();

    //Map
    private MapView mapView;
    private IMapController mapController;

    //Button add Step
    FloatingActionButton addButton;

    ProgressBar stepBuffer;

    int idTrip;

    SharedPreferences pref;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        // Mise en place de l'affichage de la map

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        stepBuffer = (ProgressBar) findViewById(R.id.stepBuffer);

        idTrip = getIntent().getIntExtra("idTrip",-1);

        pref = getSharedPreferences("pref",MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        mapView = (MapView) this.findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        mapController.setZoom(7.0);

        //Création de la ligne reliant les points
        //Ligne avant les marker pour que la ligne soit affichées sous les markers

        Polyline line = new Polyline();
        line.setWidth(3);
        line.setColor(Color.rgb(255, 0, 0));

        int length = steps.size();
        for(int i = 0 ; i < length ; i++){
            areClicked.add(false); //aucun marker n'est séléctionné
            line.addPoint(steps.get(i).getGeo());
        }
        mapView.getOverlays().add(line);


        //Lecture des étapes et création des markers


        GeoPoint centerPoint = new GeoPoint(48.859718, 2.351181); //Paris
        for(int i = 0 ; i < length ; i++){
            Marker marker = new Marker(mapView);
            marker.setPosition(steps.get(i).getGeo());
            marker.setIcon(resize(getDrawable(R.drawable.marker), 100, 100));

            final int finalI = i;
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    if(areClicked.get(finalI)){

                        stepBuffer.setVisibility(View.VISIBLE);

                        StringRequest request = new StringRequest(Request.Method.GET,
                                getString(R.string.WSurl) + "traveller-web/rest/step/picsByStepId/" + steps.get(finalI).getId(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try{
                                            JSONArray jsonPics = new JSONArray(response);
                                            System.out.println("Response find details : " + response);
                                            for(int j = 0 ; j<jsonPics.length() ; j++){
                                                JSONObject jsonpic = jsonPics.getJSONObject(j);
                                                DetailsActivity.Pics.add(jsonpic.getString("picture"));
                                            }
                                            System.out.println("//////////////////////////stepsActivity getStory/////////////\n"+
                                                    steps.get(finalI).getStory());
                                            DetailsActivity.story = steps.get(finalI).getStory();

                                            Toast.makeText(StepsActivity.this, "Details found", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(StepsActivity.this, DetailsActivity.class);
                                            editor.putInt("number",finalI+1);
                                            editor.putString("name",steps.get(finalI).getName());
                                            editor.putInt("idStep", steps.get(finalI).getId());
                                            editor.commit();
                                            stepBuffer.setVisibility(View.GONE);
                                            startActivity(intent);

                                        } catch (JSONException e){
                                            e.printStackTrace();
                                            Toast.makeText(StepsActivity.this,"Error finding details", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("Error find Details : " + error);
                                Toast.makeText(StepsActivity.this,"Error to find Details", Toast.LENGTH_SHORT).show();
                            }
                        });

                        RequestQueue queue = Volley.newRequestQueue(HomePageActivity.context);
                        queue.add(request);

                        return false;
                    } else {
                        for(int j = 0 ; j < areClicked.size() ; j++){
                            if(areClicked.get(j)){
                                Marker m = (Marker) mapView.getOverlays().get(j+1); //j+1 car premier élément est la ligne
                                m.setIcon(resize(getDrawable(R.drawable.marker), 100, 100));
                                areClicked.set(j,false);
                            }
                        }

                        Bitmap bm = BitmapFactory.decodeFile(steps.get(finalI).getCoverPic());
                        Drawable image = new BitmapDrawable(getResources(), bm);
                        marker.setIcon(resize(image, 300, 200));
                        mapController.animateTo(marker.getPosition());

                        areClicked.set(finalI, true);
                        return false;
                    }
                }
            });

            mapView.getOverlays().add(marker);
            centerPoint = steps.get(i).getGeo();
        }
        mapController.setCenter(centerPoint);
        mapController.setZoom(10.0);

        //Ajout d'une étape

        addButton = (FloatingActionButton) findViewById(R.id.floatBtn);

        if(!pref.getBoolean("isMyTrip",false)){
            addButton.setVisibility(View.GONE);
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StepsActivity.this,StepAddActivity.class);
                intent.putExtra("idTrip",idTrip);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_top,R.anim.slide_out_top);
            }
        });
    }


    private Drawable resize(Drawable image, int width, int height){
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width, height, false);
        return new BitmapDrawable(getResources(),bitmapResized);
    }

    @Override
    public void onBackPressed() {
        steps.clear();
        areClicked.clear();
        finish();
    }

}
