package com.traceandtrust;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONObject;

public class loadData extends ActionBarActivity {

    JSONObject json,category,filter_results;
    JSONObject product;
    JSONObject productFinal;
    JSONObject association;
    JSONObject associationsFinal;
    JSONObject producer;
    JSONObject producersFinal;
    JSONObject harvestLocation;
    JSONObject harvestLocationFinal;
    String message,status;
    StringBuffer categories_buffer;
    StringBuffer products_buffer;
    StringBuffer associations_buffer;
    StringBuffer producers_buffer;
    StringBuffer harvest_buffer;
    public ArrayList<String> finalProductCategoryKeys;
    public int productCategoriesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data);

        categories_buffer = new StringBuffer();
        products_buffer = new StringBuffer();
        associations_buffer = new StringBuffer();
        producers_buffer = new StringBuffer();
        harvest_buffer = new StringBuffer();
        productCategoriesCount = 0;
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
        finalProductCategoryKeys = new ArrayList<String>();

        try{
            //Starting Server Task for getting the Search Filters from Web Service
            new StartAsyncTask(this).execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_load, container,
                    false);
            return rootView;
        }
    }

    //AsyncTask Class GETTING SERVICE FROM URL
    public class StartAsyncTask extends AsyncTask<Void, Void, Void> {
        Context context;
        ProgressDialog dialog;


        public StartAsyncTask(Context context) {
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
                json = WebServices.getSearchFilterPOSTMethod(AppConstants.SEARCH_FILTER_URL);

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
            dialog.dismiss();

            //Checking if Internet Connection exist or not
            if (WebServices.isNetworkAvailable(context)) {
                try {
                    if (status.equalsIgnoreCase("0") && message.equalsIgnoreCase("success")) {
                        try {

                            //Getting JsonObjects from the result
                            filter_results = json.getJSONObject(AppConstants.FILTER_RESULTS_OBJECT_NAME);
                            category = filter_results.getJSONObject(AppConstants.CATEGORY_OBJECT_NAME);
                            product = filter_results.getJSONObject(AppConstants.PRODUCTS_OBJECT_NAME);
                            association = filter_results.getJSONObject(AppConstants.ASSOCIATION_OBJECT_NAME);
                            producer = filter_results.getJSONObject(AppConstants.PRODUCER_OBJECT_NAME);
                            harvestLocation = filter_results.getJSONObject(AppConstants.HARVEST_OBJECT_NAME);

                            /**** START Getting "Product Category" Key,Value pairs ****/
                            //Creating Hash Map for storing Key,Value for JsonObject
                            Map<String, String> out = new TreeMap<String, String>();

                            //Calls parse method for getting Key,Value pairs
                            WebServices.parse(category, out);

                            //Looping through the hash map and storing in categories_buffer
                            //Key is followed by value on new line
                            for (Map.Entry<String, String> keyPairs : out.entrySet()) {
                                String myKey = keyPairs.getKey();
                                categories_buffer.append(myKey + "\n" + keyPairs.getValue() + "\n");
                                finalProductCategoryKeys.add(myKey);
                                productCategoriesCount++;
                            }

                            //Add ending string to help with later parsing
                            categories_buffer.append(AppConstants.END_KEY_VALUES);
                            /**** END Getting "Product Category" Key,Value pairs ****/


                            /**** START Getting "Product" Key,Value pairs ****/
                            //Creating Hash Map for storing Key,Value for JsonObject
                            Map<String, String> out2 = new TreeMap<String, String>();

                            /*
                                Use try/catch blocks because of how T&T API is set up. Products object has
                                a Product Category Key associated with a set of Key/Value pairs which correspond
                                to the actual products. The way the API is set up, if a Product Category Key in
                                the Products object has no products associated with it, then the output is "[]".
                                The JSONObject class does not recognize this as null, nor does it recognize it as
                                empty. Therefore, an isNull() or isEmpty() check can not be run.

                                When getting the product object associated with each Product Category Key, JSONObject
                                throws an exception for the empty case. Therefore, productFinal object is set in a
                                try block, and the boolean isValue acts as a flag to determine if the object corresponding
                                to the Product Category Key is empty.

                                NOTE: This will need to be updated in the future if T&T API includes a NULL value for empty objects.
                             */

                            //run through each Product Category
                            for (int n = 0; n < productCategoriesCount; n++)
                            {
                                boolean isValue; //true if Product Category object is not "[]"
                                try {
                                    productFinal = product.getJSONObject(finalProductCategoryKeys.get(n));
                                    //if productFinal is initialized without error then there is an object for the Product Category
                                    isValue = true;
                                }catch(Exception e){
                                    //if exception is thrown then we know that this Product Category has no products
                                    isValue = false;
                                }
                                if (isValue)
                                {
                                    //Calls parse method for getting Key,Value pairs
                                    WebServices.parse(productFinal, out2);

                                    //Each Product Category begins with "start" followed by \n followed by Product Category Key and another \n
                                    //This will help keep track when parsing the string in homeActivity
                                    products_buffer.append("start\n" + finalProductCategoryKeys.get(n) + "\n");

                                    //Looping through the hash map and storing in products_buffer
                                    //Key is followed by value on new line
                                    for (Map.Entry<String, String> kP : out2.entrySet()) {
                                        products_buffer.append(kP.getKey() + "\n" + kP.getValue() + "\n");
                                    }

                                    //clear the hash map for next set of values
                                    out2.clear();
                                }
                                else
                                {
                                    //Keep track that this Product Category has no products. Add NULL to help with parsing in HomeActivity
                                    products_buffer.append("start\n" + finalProductCategoryKeys.get(n) + "\n" + "NULL" +"\n");
                                }
                            }
                            //Add ending string to help with later parsing
                            products_buffer.append(AppConstants.END_KEY_VALUES);
                            /**** END Getting "Product" Key,Value pairs ****/


                            /**** START Getting "Association" Key,Value pairs ****/

                            //Creating Hash Map for storing Key,Value for JsonObject
                            Map<String, String> out3 = new TreeMap<String, String>();
                            //run through each Product Category
                            for (int n = 0; n < productCategoriesCount; n++)
                            {
                                boolean isValueA; //true if Product Category object is not "[]"
                                try {
                                    associationsFinal = association.getJSONObject(finalProductCategoryKeys.get(n));
                                    //if associationFinal is initialized without error then there is an object for the Product Category
                                    isValueA = true;
                                }catch(Exception e){
                                    //if exception is thrown then we know that this Product Category has no Associations
                                    isValueA = false;
                                }
                                if (isValueA)
                                {
                                    //Calls parse method for getting Key,Value pairs
                                    WebServices.parse(associationsFinal, out3);

                                    //Each Product Category begins with "start" followed by \n followed by Product Category Key and another \n
                                    //This will help keep track when parsing the string in homeActivity
                                    associations_buffer.append("start\n" + finalProductCategoryKeys.get(n) + "\n");

                                    //Looping through the hash map and storing in associations_buffer
                                    //Key is followed by value on new line
                                    for (Map.Entry<String, String> kA : out3.entrySet()) {
                                        associations_buffer.append(kA.getKey() + "\n" + kA.getValue() + "\n");
                                    }
                                    //clear the hash map for next set of values
                                    out3.clear();
                                }
                                else
                                {
                                    //Keep track that this Product Category has no associations. Add NULL to help with parsing in HomeActivity
                                    associations_buffer.append("start\n" + finalProductCategoryKeys.get(n) + "\n" + "NULL" +"\n");
                                }
                            }
                            //Add ending string to help with later parsing
                            associations_buffer.append(AppConstants.END_KEY_VALUES);
                            /**** END Getting "Association" Key,Value pairs ****/

                            /**** START Getting "Producer" Key,Value pairs ****/

                            //Creating Hash Map for storing Key,Value for JsonObject
                            Map<String, String> out4 = new TreeMap<String, String>();
                            //run through each Product Category
                            for (int n = 0; n < productCategoriesCount; n++)
                            {
                                boolean isValueP; //true if Product Category object is not "[]"
                                try {
                                    producersFinal = producer.getJSONObject(finalProductCategoryKeys.get(n));
                                    //if associationFinal is initialized without error then there is an object for the Product Category
                                    isValueP = true;
                                }catch(Exception e){
                                    //if exception is thrown then we know that this Product Category has no Associations
                                    isValueP = false;
                                }
                                if (isValueP)
                                {
                                    //Calls parse method for getting Key,Value pairs
                                    WebServices.parse(producersFinal, out4);

                                    //Each Product Category begins with "start" followed by \n followed by Product Category Key and another \n
                                    //This will help keep track when parsing the string in homeActivity
                                    producers_buffer.append("start\n" + finalProductCategoryKeys.get(n) + "\n");

                                    //Looping through the hash map and storing in associations_buffer
                                    //Key is followed by value on new line
                                    for (Map.Entry<String, String> kP : out4.entrySet()) {
                                        producers_buffer.append(kP.getKey() + "\n" + kP.getValue() + "\n");
                                    }
                                    //clear the hash map for next set of values
                                    out4.clear();
                                }
                                else
                                {
                                    //Keep track that this Product Category has no associations. Add NULL to help with parsing in HomeActivity
                                    producers_buffer.append("start\n" + finalProductCategoryKeys.get(n) + "\n" + "NULL" +"\n");
                                }
                            }
                            //Add ending string to help with later parsing
                            producers_buffer.append(AppConstants.END_KEY_VALUES);
                            /**** END Getting "Producer" Key,Value pairs ****/

                            /**** START Getting "HarvestLocation" Values ****/

                            //Creating Hash Map for storing Key,Value for JsonObject
                            Map<String, String> out5 = new TreeMap<String, String>();
                            //run through each Product Category
                            for (int n = 0; n < productCategoriesCount; n++)
                            {
                                boolean isValueH; //true if Product Category object is not "[]"
                                try {
                                    harvestLocationFinal = harvestLocation.getJSONObject(finalProductCategoryKeys.get(n));
                                    //if associationFinal is initialized without error then there is an object for the Product Category
                                    isValueH = true;
                                }catch(Exception e){
                                    //if exception is thrown then we know that this Product Category has no Associations
                                    isValueH = false;
                                }
                                if (isValueH)
                                {
                                    //Calls parse method for getting Key,Value pairs
                                    WebServices.parse(harvestLocationFinal, out5);

                                    //Each Product Category begins with "start" followed by \n followed by Product Category Key and another \n
                                    //This will help keep track when parsing the string in homeActivity
                                    harvest_buffer.append("start\n" + finalProductCategoryKeys.get(n) + "\n");

                                    //Looping through the hash map and storing in associations_buffer
                                    //Key is followed by value on new line
                                    for (Map.Entry<String, String> kH : out5.entrySet()) {
                                        harvest_buffer.append(kH.getKey() + "\n");
                                    }
                                    //clear the hash map for next set of values
                                    out5.clear();
                                }
                                else
                                {
                                    //Keep track that this Product Category has no associations. Add NULL to help with parsing in HomeActivity
                                    harvest_buffer.append("start\n" + finalProductCategoryKeys.get(n) + "\n" + "NULL" +"\n");
                                }
                            }
                            //Add ending string to help with later parsing
                            harvest_buffer.append(AppConstants.END_KEY_VALUES);
                            /**** END Getting "HarvestLocation" Values ****/

                            /*
                                Code to start HomeActivity...
                                NOTE: look for alternative to Activity.MODE_WORLD_READABLE
                             */
                            if (categories_buffer.length() > 0) {
                                SharedPreferences savedSession = getApplicationContext().getSharedPreferences(AppConstants.FILTER_PREFERENCES,
                                        Activity.MODE_WORLD_READABLE);
                                SharedPreferences.Editor editor = savedSession.edit();
                                editor.putString(AppConstants.CATEGORY_FILTER, categories_buffer.toString());
                                editor.putInt(AppConstants.PRODUCT_CATEGORIES_COUNT, productCategoriesCount);
                                editor.putString(AppConstants.PRODUCT_FILTER, products_buffer.toString());
                                editor.putString(AppConstants.ASSOCIATION_FILTER, associations_buffer.toString());
                                editor.putString(AppConstants.PRODUCER_FILTER, producers_buffer.toString());
                                editor.putString(AppConstants.HARVEST_FILTER, harvest_buffer.toString());
                                editor.commit();
                                //startActivity(new Intent(context, homeActivity.class));
                                Intent intent_name = new Intent();
                                intent_name.setClass(getApplicationContext(),homeActivity.class);
                                startActivity(intent_name);
                                //dialog = new ProgressDialog(context);
                                //dialog.setMessage("Uhh...");
                                //dialog.setCancelable(false);
                                //dialog.show();

                            }

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
