package com.traceandtrust;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Joey on 8/5/2014.
 */
public class RestaurantPreview {
    private ArrayList<String> restaurantId;
    private ArrayList<String> names;
    private ArrayList<String> images;
    private ArrayList<String> imageLarges;
    private ArrayList<String> address;
    private ArrayList<String> phones;

    public RestaurantPreview()
    {
        restaurantId = new ArrayList<String>();
        names = new ArrayList<String>();
        images = new ArrayList<String>();
        imageLarges= new ArrayList<String>();
        address = new ArrayList<String>();
        phones = new ArrayList<String>();

    }

    //*******************NOTE*******************
    public void setUp(String infoStream)
    {
        String a[] = infoStream.split("\n");
        int count = 0;
        while (!a[count].equals(AppConstants.END_KEY_VALUES))
        {
            if (a[count].equals("START"))
            {
                count++;
            }
            else if (a[count].equals("Id"))
            {
                count++;
                restaurantId.add(a[count]);
                count++;
            }
            else if (a[count].equals("Name"))
            {
                count++;
                names.add(a[count]);
                count++;
            }
            else if (a[count].equals("Image"))
            {
                count++;
                images.add(a[count]);
                count++;
            }
            else if (a[count].equals("LargeImage"))
            {
                count++;
                imageLarges.add(a[count]);
                count++;
            }
            else if (a[count].equals("Address"))
            {
                count++;
                address.add(a[count]);
                count++;
            }
            else if (a[count].equals("Phone"))
            {
                count++;
                phones.add(a[count]);
                count++;
            }
        }

    }

    public ArrayList<String> getDisplayData(int pos)
    {
        ArrayList<String> hold = new ArrayList<String>();
        hold.add(getImage(pos));
        hold.add(getName(pos));
        hold.add(getAddress(pos));
        hold.add(getImageLarge(pos));
        hold.add(getPhone(pos));
        hold.add(getId(pos));
        return hold;
    }


    public String getAddress(int pos)
    {
        return address.get(pos);
    }

    public String getName(int pos)
    {
        return names.get(pos);
    }

    public String getImage(int pos)
    {
        return images.get(pos);
    }

    public String getImageLarge(int pos)
    {
        return imageLarges.get(pos);
    }

    public String getPhone(int pos)
    {
        return phones.get(pos);
    }

    public String getId(int pos)
    {
        return restaurantId.get(pos);
    }

    public ArrayList<String> getAllNames()
    {

        return names;
    }

    public int getUrlSize()
    {
        return images.size();
    }




}
