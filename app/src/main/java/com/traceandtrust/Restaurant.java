package com.traceandtrust;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joey on 8/5/2014.
 */
public class Restaurant {
    public JSONObject json;
    private ArrayList<String> restaurantId;
    private ArrayList<String> restaurantNames;
    private ArrayList<String> latitudes;
    private ArrayList<String> longitudes;
    private ArrayList<String> imageThumbs;
    private ArrayList<String> imageLarges;
    private ArrayList<String> address;
    private ArrayList<String> phones;

    private int numberOfRestaurants;

    public Restaurant()
    {
        restaurantId = new ArrayList<String>();
        restaurantNames = new ArrayList<String>();
        latitudes = new ArrayList<String>();
        longitudes = new ArrayList<String>();
        imageThumbs = new ArrayList<String>();
        imageLarges = new ArrayList<String>();
        address = new ArrayList<String>();
        phones = new ArrayList<String>();

    }

    public void parseForMap(JSONArray restaurant_information) throws JSONException {
        numberOfRestaurants = restaurant_information.length();
        for (int i = 0; i < numberOfRestaurants; i++)
        {
            //BASIC INFO
            JSONObject currentParse = restaurant_information.getJSONObject(i);
            String id = currentParse.getString("res_id");
            String name = currentParse.getString("res_name");
            String currentLatitude = currentParse.getString("latitude");
            String currentLongitude = currentParse.getString("longitude");
            restaurantId.add(id);
            restaurantNames.add(name);///////////////////////////////////////////////////////////////////////
            latitudes.add(currentLatitude);
            longitudes.add(currentLongitude);

            //ADDITIONAL INFO
            String imageThumb = currentParse.getString("img_url_thumbnail");///////////////////////////////////////////////////////
            String imageLarge = currentParse.getString("img_url");
            String restaurantAddress = currentParse.getString("res_address");//////////////////////////////////////////////////////
            String phone = currentParse.getString("res_phone");////////////////////////////////////////////////////////////////////

            imageLarges.add(imageLarge);
            imageThumbs.add(imageThumb);
            address.add(restaurantAddress);
            phones.add(phone);
        }
    }

    public int getNumberOfRestaurants()
    {
        return numberOfRestaurants;
    }
    public String getRestaurantId(int pos)
    {
        return restaurantId.get(pos);
    }
    public String getRestaurant(int pos)
    {
        return restaurantNames.get(pos);
    }

    public String getImageThumb(int pos)
    {
        return imageThumbs.get(pos);
    }

    public String getAddress(int pos)
    {
        return address.get(pos);
    }

    public String getPhone(int pos)
    {
        return phones.get(pos);
    }

    public String getimageLarges(int pos)
    {
        return imageLarges.get(pos);
    }

    public Double getLattitude(int pos)
    {
        String temp = latitudes.get(pos);
        Double realLattitude = Double.parseDouble(temp);
        return realLattitude;
    }

    public Double getLongitude(int pos)
    {
        String temp = longitudes.get(pos);
        Double realLongitude = Double.parseDouble(temp);
        return realLongitude;
    }


}
