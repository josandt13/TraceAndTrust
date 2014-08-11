package com.traceandtrust;



import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;


public class RestaurantInfoFragment extends Fragment {

    public RestaurantInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_restaurant_info, container, false);

        //Get the restaurant information
        SharedPreferences preferences = getActivity().getSharedPreferences(AppConstants.RESTAURANT_PREFERENCES, getActivity().MODE_WORLD_READABLE);
        //String imageThumb = preferences.getString(AppConstants.CURRENT_RESTAURANT_IMAGETHUMB, null);
        String name = preferences.getString(AppConstants.CURRENT_RESTAURANT_NAME, null);
        String address = preferences.getString(AppConstants.CURRENT_RESTAURANT_ADDRESS, null);
        String imageLarge = preferences.getString(AppConstants.CURRENT_RESTAURANT_IMAGELARGE, null);
        String phone = preferences.getString(AppConstants.CURRENT_RESTAURANT_PHONE, null);
        //String id = preferences.getString(AppConstants.CURRENT_RESTAURANT_ID, null);

        //Get description
        String description = ((RestaurantViewPage)getActivity()).getDescription();

        //Set ActionBar title as restaurants name
        ((RestaurantViewPage)getActivity()).setRestaurantTitle(name);

        //Set title
        TextView titleText = (TextView) rootView.findViewById(R.id.textViewTitle);
        titleText.setText(name);

        //Set image
        ImageView imageHolder = (ImageView) rootView.findViewById(R.id.restaurantImageView);
        DrawableBackgroundDownloader pics = new DrawableBackgroundDownloader();
        Drawable img = null;
        pics.loadDrawable(imageLarge, imageHolder, img);

        //Set description
        TextView descriptionText = (TextView) rootView.findViewById(R.id.textViewDescription);
        descriptionText.setText(description);

        //Set location information

        /*working
        TextView locationText = (TextView) rootView.findViewById(R.id.textViewLocation);
        String addressStart = "Address: ";
        String phoneStart = "Phone: ";
        locationText.setText(addressStart + address + "\n" + phoneStart + phone);
        */
        TextView locationText = (TextView) rootView.findViewById(R.id.textViewLocation);
        Spanned addressStart = Html.fromHtml("<b>Address: </b> ");
        Spanned phoneStart = Html.fromHtml("<b>Phone: </b> ");
        locationText.setText(addressStart + address + "\n" + phoneStart + phone);
        return rootView;
    }


}
