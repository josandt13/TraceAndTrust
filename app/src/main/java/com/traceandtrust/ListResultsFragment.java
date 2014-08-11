package com.traceandtrust;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * List Fragment Class
 */
public class ListResultsFragment extends Fragment implements AdapterView.OnItemClickListener {

    public RestaurantPreview restaurantPreview;
    public Boolean update;
    public CustomListAdapter adapter;
    public ListView myList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_results_holder, container, false);
        update = false;
        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        String info = readFromFile();

        restaurantPreview = new RestaurantPreview();
        restaurantPreview.setUp(info);
        ArrayList<ArrayList<String>> dataForDisplaying = new ArrayList<ArrayList<String>>();

        DrawableBackgroundDownloader pics = new DrawableBackgroundDownloader();
        //int i = 0;
        for (int i = 0; i < restaurantPreview.getUrlSize(); i++)
        {
            dataForDisplaying.add(restaurantPreview.getDisplayData(i));
        }

        adapter = new CustomListAdapter(getActivity(), dataForDisplaying, pics);
        myList = (ListView)rootView.findViewById(R.id.listView);
        myList.setAdapter(adapter);
        myList.setOnItemClickListener(this);
        return rootView;
    }



    private String readFromFile() {

        String ret = "";
        Log.e("LISTFRAGMENT", "Preparing to read data...");
        try {
             InputStream inputStream = getActivity().openFileInput(AppConstants.RESTAURANTS_FILE);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                //StringBuilder stringBuilder = new StringBuilder();
                StringBuffer info = new StringBuffer();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    info.append(receiveString);
                    info.append("\n");
                }

                inputStream.close();
                ret = info.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("ListFragment", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("ListFragment", "Can not read file: " + e.toString());
        }

        return ret;
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden == false)
        {
            SharedPreferences preferences = getActivity().getSharedPreferences(AppConstants.FILTER_PREFERENCES, getActivity().MODE_WORLD_READABLE);
            update = preferences.getBoolean(AppConstants.UPDATE_LIST_BOOL, true);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(AppConstants.UPDATE_LIST_BOOL, false);
            editor.commit();
        }
        //**********OLD WORKING**************

        if (update)
        {
            String info = readFromFile();
            restaurantPreview = new RestaurantPreview();
            restaurantPreview.setUp(info);
            ArrayList<ArrayList<String>> dataForDisplaying = new ArrayList<ArrayList<String>>();

            DrawableBackgroundDownloader pics = new DrawableBackgroundDownloader();
            pics.Reset();
            //int i = 0;
            for (int i = 0; i < restaurantPreview.getUrlSize(); i++)
            {
                dataForDisplaying.add(restaurantPreview.getDisplayData(i));
            }

            adapter = new CustomListAdapter(getActivity(), dataForDisplaying, pics);
            myList.setAdapter(adapter);
        }
        super.onHiddenChanged(hidden);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArrayList<String> data = restaurantPreview.getDisplayData(position);
        SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences(AppConstants.RESTAURANT_PREFERENCES, getActivity().MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AppConstants.CURRENT_RESTAURANT_IMAGETHUMB, data.get(0));
        editor.putString(AppConstants.CURRENT_RESTAURANT_NAME, data.get(1));
        editor.putString(AppConstants.CURRENT_RESTAURANT_ADDRESS, data.get(2));
        editor.putString(AppConstants.CURRENT_RESTAURANT_IMAGELARGE, data.get(3));
        editor.putString(AppConstants.CURRENT_RESTAURANT_PHONE, data.get(4));
        editor.putString(AppConstants.CURRENT_RESTAURANT_ID, data.get(5));
        editor.commit();
        Intent i = new Intent(getActivity(), RestaurantViewPage.class);//////////////////////////////////////////////////////////////////////
        startActivity(i);////////////////////////////////////////////////////////////////////////////////////////////////
        // Toast.makeText(getActivity(), data.get(5), Toast.LENGTH_LONG).show();
    }
}
