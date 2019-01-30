package com.example.maxime.tripshare.pageDetails;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.maxime.tripshare.R;
import com.example.maxime.tripshare.homePage.HomePageActivity;
import com.example.maxime.tripshare.pageSteps.StepsActivity;
import com.example.maxime.tripshare.pageTrips.MyTripsFragment;

public class DisplayFragment extends Fragment {

    ImageView ivDisplay;
    LinearLayout llStory;
    EditText etStory;
    Button btnSave;
    ProgressBar storyBuffer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root =  inflater.inflate(R.layout.fragment_display, container, false);

        ivDisplay = (ImageView) root.findViewById(R.id.ivDiplay);
        ivDisplay.setVisibility(View.GONE);
        llStory = (LinearLayout) root.findViewById(R.id.llStory);
        llStory.setVisibility(View.GONE);
        etStory = (EditText) root.findViewById(R.id.etStory);
        btnSave = (Button) root.findViewById(R.id.btnSave);
        storyBuffer = (ProgressBar) root.findViewById(R.id.storyBuffer);

        final Bundle b = getArguments();
        if(b.getBoolean("pic")){
            String element = b.getString("element");
            Bitmap bm = BitmapFactory.decodeFile(element);
            Drawable image = new BitmapDrawable(DetailsActivity.resources, bm);
            ivDisplay.setVisibility(View.VISIBLE);
            ivDisplay.setImageDrawable(image);
        } else {
            String story = DetailsActivity.story;
            llStory.setVisibility(View.VISIBLE);
            etStory.setText(story);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storyBuffer.setVisibility(View.VISIBLE);

                StringRequest request = new StringRequest(Request.Method.POST,
                        getString(R.string.WSurl) + "traveller-web/rest/step/setStory/" + DetailsActivity.currentStepId,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println("Response save story : " + response);
                                Toast.makeText(DetailsActivity.context, "Story saved", Toast.LENGTH_SHORT).show();
                                DetailsActivity.tvStory.setText(etStory.getText().toString());
                                StepsActivity.steps.get(b.getInt("position")).setStory(etStory.getText().toString());

                                storyBuffer.setVisibility(View.GONE);
                                getFragmentManager().beginTransaction().remove(DisplayFragment.this);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error save story : " + error);
                        Toast.makeText(DetailsActivity.context, "Error to save story", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return etStory.getText().toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "text/plain";
                    }
                } ;

                RequestQueue queue = Volley.newRequestQueue(HomePageActivity.context);
                queue.add(request);

            }
        });

        return root;
    }

}
