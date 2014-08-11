package com.traceandtrust;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * MapFragment Class
 */
public class PlanetFragment extends Fragment {
    MapView mapView;
    GoogleMap map;
    JSONObject json;
    JSONArray restaurant_information;
    String message,status;
    Restaurant myRestaurant;
    public StringBuffer searchURL;
    boolean update;
    boolean doneWriting;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.forage_fragment_holder, container, false);
        Log.e("MAPFRAGMENT", "in onCreateViewMethod...");

        doneWriting = false;
        if (searchURL == null) searchURL = new StringBuffer(AppConstants.INITIAL_RESTAURANT_URL);

        update = false;
        myRestaurant = new Restaurant();
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(getActivity());
            //MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        map = mapView.getMap();
        Log.e("MAPFRAGMENT", "Map is good...");

        try{
            //Starting Server Task for getting the Search Filters from Web Service

            new restaurantAsync(getActivity()).execute();
        }catch(Exception e){
            e.printStackTrace();
        }
        //map.getUiSettings().setMyLocationButtonEnabled(true);
        //map.setMyLocationEnabled(true);
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(39.5, -98.3), 2);
        //map.animateCamera(cameraUpdate);
        return rootView;
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void toastMe(String a)
    {
        Toast toast = Toast.makeText(getActivity(), a, Toast.LENGTH_SHORT);
        toast.show();
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.e("MAPFRAGMENT", "in onHiddenChangedMethod...");
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(39.5, -98.3), 2);
        map.animateCamera(cameraUpdate);

        //if hidden==false values on the map should be changed
        if (hidden == false)
        {
            SharedPreferences preferences = getActivity().getSharedPreferences(AppConstants.FILTER_PREFERENCES, getActivity().MODE_WORLD_READABLE);
            update = preferences.getBoolean(AppConstants.UPDATE_MAP_BOOL, true);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(AppConstants.UPDATE_MAP_BOOL, false);
            editor.commit();
        }
        if(update)
        {
            //clear current map
            map.clear();
            //reset searchURL
            int lengthBuffer = searchURL.length();
            searchURL.delete(0, lengthBuffer);
            //get starting URL
            searchURL.append(AppConstants.SEARCH_START_URL);
            //get preferences
            SharedPreferences preferences = getActivity().getSharedPreferences(AppConstants.FILTER_PREFERENCES, getActivity().MODE_WORLD_READABLE);
            //get keys
            String associationKey = preferences.getString(AppConstants.ASSOCIATION_URL, null);
            String categoryKey = preferences.getString(AppConstants.PRODUCT_CATEGORY_URL, null);
            String harvestLocationKey = preferences.getString(AppConstants.HARVEST_LOCATION_URL, null);
            String milesKey = preferences.getString(AppConstants.MILES_URL,null);
            String producerKey = preferences.getString(AppConstants.PRODUCER_URL, null);
            String productKey = preferences.getString(AppConstants.PRODUCT_URL, null);

            //set Association
            if (!associationKey.equals("Any"))
            {
                searchURL.append("associationId=" + associationKey + "&");
            }
            //set Category
            if (!categoryKey.equals("Any"))
            {
                searchURL.append("categoryId=" + categoryKey + "&");
            }
            //set Harvest Location
            if (!harvestLocationKey.equals("Any"))
            {
                searchURL.append("harvest_location=" + harvestLocationKey + "&");
            }
            //set Miles
            searchURL.append("harvest_location_max=" + milesKey + "&");
            //set Producer
            if (!producerKey.equals("Any"))
            {
                searchURL.append("producerId=" + producerKey + "&");
            }
            //set Product
            if (!productKey.equals("Any"))
            {
                searchURL.append("productId=" + productKey + "&");
            }
            //set Latitude
            searchURL.append("latitude=" + AppConstants.MIDDLE_USA_LATITUDE + "&");
            //set Longitude
            searchURL.append("longitude=" + AppConstants.MIDDLE_USA_LONGITUDE);

            myRestaurant = new Restaurant();

            try{
                //Starting Server Task for getting the Search Filters from Web Service

                new restaurantAsync(getActivity()).execute();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        super.onHiddenChanged(hidden);
    }

    public void finish()
    {
        Log.e("MAPFRAGMENT", "in finish()...");
        try {
            myRestaurant.parseForMap(restaurant_information);
            //Toast toast = Toast.makeText(getActivity(), myRestaurant.getRestaurant(0) + myRestaurant.getLattitude(0) + myRestaurant.getLongitude(0), Toast.LENGTH_LONG);
            //toast.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        displayRestaurant();
    }
    public void displayRestaurant()
    {
        Log.e("MAPFRAGMENT", "in displayRestaurant()...");

        int size = myRestaurant.getNumberOfRestaurants();
        for (int i = 0; i < size; i++)
        {
            MarkerOptions marker = new MarkerOptions();
            LatLng position = new LatLng(myRestaurant.getLattitude(i), myRestaurant.getLongitude(i));
            marker.position(position);
            marker.title(myRestaurant.getRestaurant(i));
            //marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pinner));

            Bitmap b = scaleImage(getResources(), R.drawable.pinner, 100);
            marker.icon(BitmapDescriptorFactory.fromBitmap(b));

            map.addMarker(marker);
        }
        saveRestaurants();
    }
    private Bitmap scaleImage(Resources res, int id, int lessSideSize) {
        Bitmap b = null;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(res, id, o);

        float sc = 0.0f;
        int scale = 1;
        // if image height is greater than width
        if (o.outHeight > o.outWidth) {
            sc = o.outHeight / lessSideSize;
            scale = Math.round(sc);
        }
        // if image width is greater than height
        else {
            sc = o.outWidth / lessSideSize;
            scale = Math.round(sc);
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        b = BitmapFactory.decodeResource(res, id, o2);
        return b;
    }
    public void saveRestaurants()
    {
        Log.e("MAPFRAGMENT", "in saveRestaurants()...");
        StringBuffer data = new StringBuffer();
        int size = myRestaurant.getNumberOfRestaurants();
        for (int i = 0; i < size; i++)
        {
            data.append("START\n");
            data.append("Id\n");
            data.append(myRestaurant.getRestaurantId(i) + "\n");
            data.append("Name\n");
            data.append(myRestaurant.getRestaurant(i) + "\n");
            data.append("Image\n");
            data.append(myRestaurant.getImageThumb(i) + "\n");
            data.append("LargeImage\n");
            data.append(myRestaurant.getimageLarges(i) + "\n");
            data.append("Address\n");
            data.append(myRestaurant.getAddress(i) + "\n");
            data.append("Phone\n");
            data.append(myRestaurant.getPhone(i) + "\n");
        }
        data.append(AppConstants.END_KEY_VALUES);
        writeToFile(data.toString());
        Log.e("MAPFRAGMENT", "Data was written...");
    }



    private void writeToFile(String data) {
        Log.e("MAPFRAGMENT", "Preparing to write data...");
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().openFileOutput(AppConstants.RESTAURANTS_FILE, Context.MODE_WORLD_READABLE));
            outputStreamWriter.write(data);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            Log.e("MAPFRAGMENT", "Data written...");
            ((homeActivity)getActivity()).updateTheList();
        }

        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }


    //AsyncTask Class GETTING SERVICE FROM URL
    public class restaurantAsync extends AsyncTask<Void, Void, Void> {
        Context context;
        ProgressDialog dialog;


        public restaurantAsync(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            Log.e("MAPFRAGMENT", "in onPreExecute() of asyncTastk...");
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Log.e("MAPFRAGMENT", "in doInBackground() of asyncTastk...");
                //POST METHOD URL Request in BACKGROUND
                //json = WebServices.getSearchFilterPOSTMethod(AppConstants.INITIAL_RESTAURANT_URL);
                json = WebServices.getSearchFilterPOSTMethod(searchURL.toString());

                JSONObject statusObject = json.getJSONObject("Status");

                status = statusObject.getString("status");
                message = statusObject.getString("message");
                restaurant_information = json.getJSONArray("restaurants");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();
            Log.e("MAPFRAGMENT", "in onPostExecute() of asyncTastk...");
            finish();
        }
    }

}