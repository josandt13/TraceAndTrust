package com.traceandtrust;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class homeActivity extends ActionBarActivity {

    /****block from android development sample****/
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
    private boolean firstTime; //only true in onCreate, if true initialize fragments
    private boolean isMapView; //true if displaying map fragment
    private Fragment myListFragment;
    private Fragment myMapFragment;
    private Fragment mySearchFragment;
    private boolean shouldDisplayActionBar; //true if drawer open or filter open
    private FrameLayout mySearchLayout;
    public FrameLayout myFrameLayout;
    private FrameLayout myMapHider;
    public boolean isSaveable; //true if user's filter settings should be saveable
    public boolean isResetable; //true unless all filter spinners are at default value
    private FragmentManager fragmentManager;
    private boolean firstSearch;
    private boolean firstTimeListView;
    private boolean toUpdateList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeVariables();

            // ActionBarDrawerToggle ties together the the proper interactions
            // between the sliding drawer and the action bar app icon
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                  /* host Activity */
                    mDrawerLayout,         /* DrawerLayout object */
                    R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                    R.string.drawer_open,  /* "open drawer" description for accessibility */
                    R.string.drawer_close  /* "close drawer" description for accessibility */
            ) {
                public void onDrawerClosed(View view) {
                    getSupportActionBar().setTitle(mTitle);
                    shouldDisplayActionBar = true;
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }

                public void onDrawerOpened(View drawerView) {
                    getSupportActionBar().setTitle(mDrawerTitle);
                    shouldDisplayActionBar = false;
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);

            if (savedInstanceState == null) {
                Log.e("HOMEACTIVITY", "Preparing to selectItem(0)...");
                selectItem(0);
            }

        }

        /**
     * Initializes Variables for OnCreate Method
     */
    public void initializeVariables()
    {
        firstTime = true;
        isMapView = true;
        isSaveable = false;
        isResetable = false;
        shouldDisplayActionBar = true;
        firstTimeListView = true;
        firstSearch = true;
        toUpdateList = false;

        SharedPreferences savedSession = getApplicationContext().getSharedPreferences(AppConstants.FILTER_PREFERENCES,
                Activity.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = savedSession.edit();
        editor.putBoolean(AppConstants.UPDATE_MAP_BOOL, false);
        editor.putBoolean(AppConstants.UPDATE_LIST_BOOL, false);
        editor.putString(AppConstants.PRODUCT_CATEGORY_URL, "Any");
        editor.putString(AppConstants.PRODUCT_URL, "Any");
        editor.putString(AppConstants.ASSOCIATION_URL, "Any");
        editor.putString(AppConstants.PRODUCER_URL, "Any");
        editor.putString(AppConstants.HARVEST_LOCATION_URL, "Any");
        editor.putString(AppConstants.MILES_URL, "4000");
        editor.commit();

        //initialize fragments

        if (myMapFragment == null) myMapFragment = new PlanetFragment();
        if (myListFragment == null) myListFragment = new ListResultsFragment();
        if (mySearchFragment == null) mySearchFragment = new SearchFragment();

        //associate views
        mySearchLayout = (FrameLayout) findViewById(R.id.searchHolder);
        myFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        myMapHider = (FrameLayout) findViewById(R.id.mapHider);


        /****block from android development sample****/
        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        /****end block from android development sample****/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Called whenever we call invalidateOptionsMenu()
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        if (!shouldDisplayActionBar)
        {
            for (int i = 0; i < menu.size(); i++) {
                if (i == 1)
                {
                    if(isMapView) menu.getItem(i).setIcon(R.drawable.darkresults);
                    else menu.getItem(i).setIcon(R.drawable.map_dark);
                }
                if (i == 2) menu.getItem(i).setIcon(R.drawable.darksearch);
                menu.getItem(i).setEnabled(false);
            }
        }
        else{
            if (isResetable) menu.getItem(0).setEnabled(true);
            else menu.getItem(0).setEnabled(false);

            if (isSaveable) menu.getItem(3).setEnabled(true);
            else menu.getItem(3).setEnabled(false);

            if(isMapView) menu.getItem(1).setIcon(R.drawable.results);
            else menu.getItem(1).setIcon(R.drawable.map_icon);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
    }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.list:
                if(isMapView) {
                    isMapView=false;
                    fragmentManager = getFragmentManager();
                    if(firstTimeListView)
                    {
                        fragmentManager.beginTransaction().add(R.id.abc, myListFragment).commit();
                        fragmentManager.beginTransaction().hide(myMapFragment).commit();
                        fragmentManager.beginTransaction().show(myListFragment).commit();
                        firstTimeListView = false;
                    }
                    else
                    {
                        fragmentManager.beginTransaction().hide(myMapFragment).commit();
                        fragmentManager.beginTransaction().show(myListFragment).commit();
                    }
                }
                else{
                    isMapView=true;
                    fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().hide(myListFragment).commit();
                    fragmentManager.beginTransaction().show(myMapFragment).commit();
                }
                mDrawerLayout.closeDrawer(mDrawerList);
                invalidateOptionsMenu();
                return true;
            case R.id.save:
                mDrawerLayout.closeDrawer(mDrawerList);
                return true;
            case R.id.reset:
                mDrawerLayout.closeDrawer(mDrawerList);
                return true;
            case R.id.search:
                mDrawerLayout.closeDrawer(mDrawerList);
                shouldDisplayActionBar = false;
                disableFragmentView();
                invalidateOptionsMenu();
                if(firstSearch)
                {
                    fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().add(R.id.searchHolder, mySearchFragment).commit();
                    firstSearch = false;
                }
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().show(mySearchFragment).commit();
                return true;
            default:
                invalidateOptionsMenu();
                return super.onOptionsItemSelected(item);
        }
    }

    public void cancelSearch(View view)
    {
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().hide(mySearchFragment).commit();
        myMapHider.setClickable(false);
        myMapHider.setVisibility(View.INVISIBLE);
        shouldDisplayActionBar = true;
        invalidateOptionsMenu();
    }

    public void applySearch(View view)
    {
        myMapHider.setClickable(false);
        myMapHider.setVisibility(View.INVISIBLE);
        shouldDisplayActionBar = true;
        SharedPreferences preferences = getSharedPreferences(AppConstants.FILTER_PREFERENCES, Activity.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(AppConstants.UPDATE_MAP_BOOL, true);
        editor.putBoolean(AppConstants.UPDATE_LIST_BOOL, true);
        editor.commit();
        invalidateOptionsMenu();
        if(isMapView) {
            if(firstTimeListView)
            {
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().hide(mySearchFragment).commit();
                fragmentManager.beginTransaction().hide(myMapFragment).commit();
                fragmentManager.beginTransaction().show(myMapFragment).commit();
            }
            else {
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().hide(mySearchFragment).commit();
                fragmentManager.beginTransaction().hide(myMapFragment).commit();
                fragmentManager.beginTransaction().hide(myListFragment).commit();
                fragmentManager.beginTransaction().show(myMapFragment).commit();
            }
        }
        else {
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().hide(mySearchFragment).commit();
            fragmentManager.beginTransaction().hide(myListFragment).commit();
            fragmentManager.beginTransaction().hide(myMapFragment).commit();
            fragmentManager.beginTransaction().show(myMapFragment).commit();
            fragmentManager.beginTransaction().hide(myMapFragment).commit();
            fragmentManager.beginTransaction().show(myListFragment).commit();
            toUpdateList = true;
        }

    }
    public void updateTheList()
    {
        if(toUpdateList)
        {
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().hide(mySearchFragment).commit();
            fragmentManager.beginTransaction().hide(myListFragment).commit();
            fragmentManager.beginTransaction().hide(myMapFragment).commit();
            fragmentManager.beginTransaction().show(myMapFragment).commit();
            fragmentManager.beginTransaction().hide(myMapFragment).commit();
            fragmentManager.beginTransaction().show(myListFragment).commit();
            toUpdateList = false;
        }
    }

    /**
     * Displays the Search and block's map/list fragment from being clicked
     */
    public void disableFragmentView()
    {
        myMapHider.setVisibility(View.VISIBLE);
        myMapHider.setClickable(true);
    }

    //The click listener for ListView in the navigation drawer
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments

        //initializes both fragments and sets the current view to Map Fragment
        if(position==0 && firstTime)
        {
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().add(R.id.content_frame, myMapFragment).commit();
            fragmentManager.beginTransaction().show(myMapFragment).commit();
            firstTime = false;
            isMapView=true;

            //The data file that ListFragment needs to receive data is downloaded in PlanetFragment.
            //Because of this, we need to wait until the file has been written until the ListFragment
            //is created. To solve this, ListView is not initialized until the user requests the view.
            //*****************************************NOTE*****************************************
            //This is inefficient and should be one of the first issues optimized when time allows.
            //The reason the data is being written to the file in PlanetFragment and not here is because
            //the data is being downloaded and parsed in PlanetFragment.
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



}
