package com.traceandtrust;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Joey on 8/9/2014.
 */
public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private ScrollView myScrollView;
    public ArrayAdapter<String> productCategoryAdapter;
    public ArrayAdapter<String> productAdapter;
    public ArrayAdapter<String> farmOrVesselAdapter;
    public ArrayAdapter<String> harvestLocationAdapter;
    public ArrayAdapter<String> associationsAdapter;
    public ArrayAdapter<String> withinMilesAdapter;

    public Spinner productCategorySpinner;
    public Spinner productSpinner;
    public Spinner farmOrVesselSpinner;
    public Spinner harvestLocationSpinner;
    public Spinner associationsSpinner;
    public Spinner withinMilesSpinner;

    private ArrayList<String> arrayWithinMiles;
    private Filter myFilterInformation;

    public SearchFragment()
    {
        myFilterInformation = new Filter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.search_fragment_holder, container, false);
        View view = inflater.inflate(R.layout.activity_search, null);
        myScrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        myScrollView.addView(view);

        initializeVariables(rootView);
        productCategorySpinner.setOnItemSelectedListener(this);
        productSpinner.setOnItemSelectedListener(this);
        associationsSpinner.setOnItemSelectedListener(this);
        farmOrVesselSpinner.setOnItemSelectedListener(this);
        harvestLocationSpinner.setOnItemSelectedListener(this);
        withinMilesSpinner.setOnItemSelectedListener(this);

        return rootView;
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    public void initializeVariables(View rootView)
    {
        //initialize ArrayList's to be used for spinner items
        arrayWithinMiles = new ArrayList<String>();
        //Initialize Filter Class used for storing Filter Data

        setUpFilters();

        //associate spinners
        productCategorySpinner = (Spinner) rootView.findViewById(R.id.spinnerProductCategory);
        productSpinner = (Spinner) rootView.findViewById(R.id.spinnerProduct);
        farmOrVesselSpinner = (Spinner) rootView.findViewById(R.id.spinnerFarmOrVessel);
        harvestLocationSpinner = (Spinner) rootView.findViewById(R.id.spinnerHarvestLocation);
        associationsSpinner = (Spinner) rootView.findViewById(R.id.spinnerAssociations);
        withinMilesSpinner = (Spinner) rootView.findViewById(R.id.spinnerWithinMiles);

        //initialize adapters with ArrayList's
        //Starts by including ALL Product Categories, ANY Product (too long to include all so wait for product category to be chosen to update),
        //ALL Associations, ALL Producers, and ALL harvestLocations

        //previously used android.R.layout.simple_list_item_activated_1

        productCategoryAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, myFilterInformation.getProductCategories());
        productAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, myFilterInformation.getProducts("Any"));
        associationsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, myFilterInformation.getAllAssociations());
        farmOrVesselAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, myFilterInformation.getAllProducers());
        harvestLocationAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, myFilterInformation.getAllHarvestLocations());
        withinMilesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, arrayWithinMiles);

        //set corresponding Adapter to each Spinner
        productCategorySpinner.setAdapter(productCategoryAdapter);
        productSpinner.setAdapter(productAdapter);
        farmOrVesselSpinner.setAdapter(farmOrVesselAdapter);
        harvestLocationSpinner.setAdapter(harvestLocationAdapter);
        associationsSpinner.setAdapter(associationsAdapter);
        withinMilesSpinner.setAdapter(withinMilesAdapter);
    }

    public void setUpFilters()
    {
        //Get Shared Preferences
        SharedPreferences preferences = getActivity().getSharedPreferences(AppConstants.FILTER_PREFERENCES, getActivity().MODE_WORLD_READABLE);
        //Get Category Stream
        String tempCategory = preferences.getString(AppConstants.CATEGORY_FILTER, null);
        //Get Product Stream
        String tempProducts = preferences.getString(AppConstants.PRODUCT_FILTER, null);
        //Get Association Stream
        String tempAssociations = preferences.getString(AppConstants.ASSOCIATION_FILTER, null);
        //Get Producer Stream
        String tempProducers = preferences.getString(AppConstants.PRODUCER_FILTER, null);
        //Get Harvest Stream
        String tempHarvest = preferences.getString(AppConstants.HARVEST_FILTER, null);

        myFilterInformation.parseAndSetCategories(tempCategory);
        myFilterInformation.parseAndSetProducts(tempProducts);
        myFilterInformation.parseAndSetAssociations(tempAssociations);
        myFilterInformation.parseAndSetProducers(tempProducers);
        myFilterInformation.parseAndSetHarvestLocations(tempHarvest);

        //add Miles to arrayWithinMiles
        arrayWithinMiles.add("Any");
        arrayWithinMiles.add("5 Miles");
        arrayWithinMiles.add("10 Miles");
        arrayWithinMiles.add("25 Miles");
        arrayWithinMiles.add("50 Miles");
        arrayWithinMiles.add("75 Miles");
        arrayWithinMiles.add("100 Miles");
        arrayWithinMiles.add("150 Miles");
        arrayWithinMiles.add("200 Miles");
        arrayWithinMiles.add("300 Miles");
        arrayWithinMiles.add("500 Miles");
        arrayWithinMiles.add("1000 Miles");
    }
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.spinnerProductCategory)
        {
            //get value
            String catValue = myFilterInformation.getProductCategories().get(pos);
            //set category value
            myFilterInformation.setCurrentCategory(catValue);

            if (catValue.equals("Any"))
            {
                ArrayAdapter<String> prod = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, myFilterInformation.getProducts("Any"));
                ArrayAdapter<String> asso = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, myFilterInformation.getAllAssociations());
                ArrayAdapter<String> farm = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, myFilterInformation.getAllProducers());
                ArrayAdapter<String> harv= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, myFilterInformation.getAllHarvestLocations());
                productSpinner.setAdapter(prod);
                associationsSpinner.setAdapter(asso);
                farmOrVesselSpinner.setAdapter(farm);
                harvestLocationSpinner.setAdapter(harv);
            }
            else
            {
                //Toast toast = Toast.makeText(this, myFilterInformation.getProducts(categoryKey).get(1), Toast.LENGTH_SHORT);
                //toast.show();

                ArrayAdapter<String> prod = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, myFilterInformation.getProducts(catValue));
                ArrayAdapter<String> asso = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, myFilterInformation.getAssociations(catValue));
                ArrayAdapter<String> farm = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, myFilterInformation.getProducers(catValue));
                ArrayAdapter<String> harv= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, myFilterInformation.getHarvestLocations(catValue));

                productSpinner.setAdapter(prod);
                associationsSpinner.setAdapter(asso);
                farmOrVesselSpinner.setAdapter(farm);
                harvestLocationSpinner.setAdapter(harv);
            }
            SharedPreferences preferences = getActivity().getSharedPreferences(AppConstants.FILTER_PREFERENCES, Activity.MODE_WORLD_READABLE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(AppConstants.PRODUCT_CATEGORY_URL, myFilterInformation.getCategoryURL());
            editor.commit();

        }
        if(spinner.getId() == R.id.spinnerProduct)
        {
            myFilterInformation.setCurrentProductFromPosition(pos);
            SharedPreferences preferences = getActivity().getSharedPreferences(AppConstants.FILTER_PREFERENCES, Activity.MODE_WORLD_READABLE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(AppConstants.PRODUCT_URL, myFilterInformation.getProductURL());
            editor.commit();
        }

        if(spinner.getId() == R.id.spinnerFarmOrVessel)
        {
            myFilterInformation.setCurrentProducerFromPostition(pos);
            SharedPreferences preferences = getActivity().getSharedPreferences(AppConstants.FILTER_PREFERENCES, Activity.MODE_WORLD_READABLE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(AppConstants.PRODUCER_URL, myFilterInformation.getProducerURL());
            editor.commit();
        }

        if(spinner.getId() == R.id.spinnerAssociations)
        {
            myFilterInformation.setCurrentAssociationFromPosition(pos);
            SharedPreferences preferences = getActivity().getSharedPreferences(AppConstants.FILTER_PREFERENCES, Activity.MODE_WORLD_READABLE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(AppConstants.ASSOCIATION_URL, myFilterInformation.getAssociationURL());
            editor.commit();
        }

        else if(spinner.getId() == R.id.spinnerHarvestLocation)
        {
            myFilterInformation.setCurrentHarvestLocationFromPosition(pos);
            SharedPreferences preferences = getActivity().getSharedPreferences(AppConstants.FILTER_PREFERENCES, Activity.MODE_WORLD_READABLE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(AppConstants.HARVEST_LOCATION_URL, myFilterInformation.getHarvestLocationURL());
            editor.commit();
        }

        else if (spinner.getId() == R.id.spinnerWithinMiles)
        {
            myFilterInformation.setMilesURL(pos);
            SharedPreferences preferences = getActivity().getSharedPreferences(AppConstants.FILTER_PREFERENCES, Activity.MODE_WORLD_READABLE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(AppConstants.MILES_URL, myFilterInformation.getMilesURL());
            editor.commit();

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
