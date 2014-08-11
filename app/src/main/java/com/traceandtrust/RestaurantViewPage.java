package com.traceandtrust;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class RestaurantViewPage extends Activity {
    ActionBar actionBar;
    ActionBar.Tab infoTab;
    ActionBar.Tab deliveriesTab;
    Fragment infoFragment;
    Fragment recentDeliveriesFragment;
    JSONObject json;
    JSONObject restaurant_information;
    JSONArray recent_delivery_informaiton;
    String message,status;
    StringBuffer restaurantURL;
    StringBuffer restaurantDescription;
    StringBuffer restaurantWebsite;

    //*************NOTE*************
    //-Set ActionBar title to the restaurant's name
    //-Configure and look at ActionBar styling for
    // more customization
    //*************NOTE*************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_view_page);
        restaurantURL = new StringBuffer(AppConstants.RESTAURANT_DATA_FOR_PROFILE);
        restaurantDescription = new StringBuffer();
        restaurantWebsite = new StringBuffer();
        setUpRestaurantDetails();

         //assign ActionBar and configure NavigationMode for tabs
        actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //testing
        actionBar.setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        //

        //initialize tabs
        infoTab = actionBar.newTab();
        infoTab.setText(AppConstants.RESTAURANT_INFO_TAB);
        deliveriesTab = actionBar.newTab();
        deliveriesTab.setText(AppConstants.RESTAURANT_DELIVERIES_TAB);

        try{
            //Starting Server Task for getting the Search Filters from Web Service

            new restaurantProfileAsync(this).execute();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void setUpRestaurantDetails()
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(AppConstants.RESTAURANT_PREFERENCES, Activity.MODE_WORLD_READABLE);
        String id = preferences.getString(AppConstants.CURRENT_RESTAURANT_ID, null);
        restaurantURL.append(id);
    }

    public void handleData()
    {
        //initialize Fragment classes
        infoFragment = new RestaurantInfoFragment();
        recentDeliveriesFragment = new RestaurantRecentDeliveriesFragment();

        //set up TabListener for tabs
        infoTab.setTabListener(new MyTabListener(infoFragment));
        deliveriesTab.setTabListener(new MyTabListener(recentDeliveriesFragment));

        //add tabs to ActionBar
        actionBar.addTab(infoTab);
        actionBar.addTab(deliveriesTab);
    }

    public String getDescription()
    {
        return restaurantDescription.toString();
    }


    /**
     * Sets the ActionBar title
     * @param name is the name of the restaurant being displayed
     *
     */
    public void setRestaurantTitle(String name)
    {
        actionBar.setTitle(name);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.restaurant_view_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    public class MyTabListener implements ActionBar.TabListener {
        Fragment fragment;

        public MyTabListener(Fragment fragment) {
            this.fragment = fragment;
        }

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(R.id.fragment_frame, fragment);
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.remove(fragment);
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            // nothing done here
        }
    }

    //AsyncTask Class GETTING SERVICE FROM URL
    public class restaurantProfileAsync extends AsyncTask<Void, Void, Void> {
        Context context;
        ProgressDialog dialog;


        public restaurantProfileAsync(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                //POST METHOD URL Request in BACKGROUND
                json = WebServices.getSearchFilterPOSTMethod(restaurantURL.toString());

                JSONObject statusObject = json.getJSONObject("Status");

                status = statusObject.getString("status");
                message = statusObject.getString("message");


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //Checking if Internet Connection exist or not
            if (WebServices.isNetworkAvailable(context)) {
                try {
                    if (status.equalsIgnoreCase("0") && message.equalsIgnoreCase("success")) {
                        try {

                            //Getting JsonObjects from the result
                            JSONObject data = json.getJSONObject("DeliveryResults");
                            restaurant_information = data.getJSONObject("restaurant_inforamation");
                            //recent_delivery_informaiton = json.getJSONArray("");

                            //Looping through the hash map and storing in categories_buffer
                            //Key is followed by value on new line
                            try {
                                String desc = restaurant_information.getString("res_desc");
                                restaurantDescription.append(desc);
                            }catch(Exception e)
                            {
                                restaurantDescription.append("None");
                            }
                            try {
                                String web = restaurant_information.getString("website");
                                restaurantWebsite.append(web);
                            }catch(Exception e)
                            {
                                restaurantDescription.append("None");
                            }

                            dialog.dismiss();
                            handleData();

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    } else if (status.equalsIgnoreCase("-1")) {
                        try {
                            //Shows Error Message in case of any failure
                            Log.d("Error Message", message);
                            WebServices.alertbox(AppConstants.ALERT_TITLE, message, context);
                            Toast toast = Toast.makeText(context, "AYAYAYAYA", Toast.LENGTH_LONG);
                            toast.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast toast = Toast.makeText(context, "AYAYAYAYA", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    } else {
                        Log.d("Error Message", "Internal Server Error");
                        WebServices.alertbox(AppConstants.ALERT_TITLE, AppConstants.SERVER_ERROR, context);
                        Toast toast = Toast.makeText(context, "AYAYAYAYA", Toast.LENGTH_LONG);
                        toast.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(context, "AYAYAYAYA", Toast.LENGTH_LONG);
                    toast.show();
                }
            } else {
                Log.d("Error Message", "Network Error");
                WebServices.alertbox(AppConstants.ALERT_TITLE, AppConstants.NETWORK_ERROR, context);
                Toast toast = Toast.makeText(context, "AYAYAYAYA", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }


}

