package com.traceandtrust;

import java.util.ArrayList;
import java.util.HashMap;


public class Filter {
    //Category Lists
    public ArrayList<String> productCategoryKey;
    private ArrayList<String> arrayProductCategory;

    //Product Lists
    private HashMap<String, ArrayList<String>> possibleProducts;
    public HashMap<String, ArrayList<String>> productKeys;

    //Association Lists
    private HashMap<String, ArrayList<String>> possibleAssociations;
    public HashMap<String, ArrayList<String>> associationKeys;

    //Producer Lists
    private HashMap<String, ArrayList<String>> possibleProducers;
    private  HashMap<String, ArrayList<String>> producerKeys;

    //Harvest List
    private HashMap<String, ArrayList<String>> possibleHarvestLocations;

    //
    private ArrayList<String> milesList;
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private StringBuffer categoryVariable;
    private StringBuffer productURL;
    private StringBuffer producerURL;
    private StringBuffer associationURL;
    private StringBuffer harvestLocationURL;
    private StringBuffer milesURL;



    public Filter()
    {
        productCategoryKey = new ArrayList<String>();
        arrayProductCategory = new ArrayList<String>();
        possibleProducts = new HashMap<String, ArrayList<String>>();
        productKeys = new HashMap<String,ArrayList<String>>();
        possibleAssociations = new HashMap<String, ArrayList<String>>();
        associationKeys = new HashMap<String, ArrayList<String>>();
        possibleProducers = new HashMap<String, ArrayList<String>>();
        producerKeys = new HashMap<String, ArrayList<String>>();
        possibleHarvestLocations = new HashMap<String, ArrayList<String>>();
        //These should be any so it can be used for key when needed
        //When setting URL, check if these values equal "Any"
        categoryVariable = new StringBuffer("Any");
        productURL = new StringBuffer("Any");
        producerURL = new StringBuffer("Any");
        associationURL = new StringBuffer("Any");
        harvestLocationURL = new StringBuffer("Any");
        milesList = new ArrayList<String>();
        milesURL = new StringBuffer("4000");
        setMiles();
    }

    private void setMiles()
    {
        milesList.add("4000");
        milesList.add("5");
        milesList.add("10");
        milesList.add("25");
        milesList.add("50");
        milesList.add("75");
        milesList.add("100");
        milesList.add("150");
        milesList.add("200");
        milesList.add("300");
        milesList.add("500");
        milesList.add("1000");
    }

    /**
     *Parse Product Category Keys and Product Category Values into respective Array List
    **/
    public void parseAndSetCategories(String categoryStream)
    {
        //Add any to Key and Categories
        productCategoryKey.add("Any");
        arrayProductCategory.add("Any");

        String a[] = categoryStream.split("\n");
        int count = 0;
        boolean key = true;
        while(!a[count].equals(AppConstants.END_KEY_VALUES))
        {
            if(key) {
                productCategoryKey.add(a[count]);
                count++;
                key = false;
            }
            else {
                arrayProductCategory.add(a[count]);
                count++;
                key = true;
            }
        }
    }

    /**
     * Parse Product Category Keys With Respective Product Values into HashMap
     * Parse Product Keys into ArrayList
    **/
    public void parseAndSetProducts(String productStream)
    {
        //Add "Any" as Key and Value to possible products
        ArrayList<String> hold = new ArrayList<String>();
        hold.add("Any");
        possibleProducts.put("Any", hold);
        productKeys.put("Any", hold);
        String a[] = productStream.split("\n");
        int productCount = 0;
        while (!a[productCount].equals(AppConstants.END_KEY_VALUES)){
            ArrayList<String> tempList = new ArrayList<String>();
            ArrayList<String> keyHold = new ArrayList<String>();
            if(a[productCount].equals("start")) productCount++;
            else
            {
                tempList.add("Any");
                keyHold.add("Any");
                String cat = a[productCount];
                productCount++;
                if (a[productCount].equals("NULL"))
                {
                    possibleProducts.put(cat, tempList);
                    productKeys.put(cat, keyHold);
                    productCount++;
                }
                else
                {
                    while (!a[productCount].equals("start") && !a[productCount].equals(AppConstants.END_KEY_VALUES))
                    {
                        keyHold.add(a[productCount]);
                        productCount++;
                        tempList.add(a[productCount]);
                        productCount++;
                    }
                    possibleProducts.put(cat, tempList);
                    productKeys.put(cat, keyHold);
                }
            }
        }
    }

    /**
     * Responsible for parsing and storing Association data downloaded in loadData.java
     * @param associationStream is the association_buffer from loadData
     * @pre loadData has successfully downloaded data
     * @post All association keys are stored into associationKeys arrayList. The possibleAssociations
             map is filled with a key for every product category, and an arrayList of association values
             corresponding to that product category. In addition, an "Any" key is added to the map with
             arrayList value with one item named "Any", and an "All" key is added to the map with arrayList
             value of "Any" followed by every association available.
     */
    public void parseAndSetAssociations(String associationStream)
    {
        //Add "Any" as Key and Value to possible products
        ArrayList<String> hold = new ArrayList<String>();
        hold.add("Any");
        possibleAssociations.put("Any", hold);
        associationKeys.put("Any", hold);

        //Create an "all" list that will be used to display all available associations
        ArrayList<String> all = new ArrayList<String>();
        ArrayList<String> allKeys = new ArrayList<String>();
        all.add("Any");
        allKeys.add("Any");

        //create an array of the stream, new values are marked by \n
        String a[] = associationStream.split("\n");
        int associationCount = 0;
        while (!a[associationCount].equals(AppConstants.END_KEY_VALUES)){
            ArrayList<String> tempList = new ArrayList<String>();
            ArrayList<String> keyHold = new ArrayList<String>();
            if(a[associationCount].equals("start")) associationCount++; //eat start marker
            else
            {
                tempList.add("Any"); //always give user option to choose any
                keyHold.add("Any");
                String cat = a[associationCount];//Product Category which will be used as a key in possibleAssociations
                associationCount++;
                if (a[associationCount].equals("NULL"))
                {
                    possibleAssociations.put(cat, tempList); //if there are no values any will be the only option
                    associationKeys.put(cat, keyHold);
                    associationCount++;
                }
                else
                {
                    while (!a[associationCount].equals("start") && !a[associationCount].equals(AppConstants.END_KEY_VALUES))
                    {
                        keyHold.add(a[associationCount]);
                        if (allKeys.contains(a[associationCount]))
                        {
                            associationCount++;
                        }
                        else
                        {
                            allKeys.add(a[associationCount]);
                            associationCount++;
                        }
                        tempList.add(a[associationCount]);

                        //check to make sure duplicates are not being added to all list
                        if (all.contains(a[associationCount])){
                            associationCount++;
                        }
                        else {
                            all.add(a[associationCount]);
                            associationCount++;
                        }
                    }
                    possibleAssociations.put(cat, tempList);
                    associationKeys.put(cat, keyHold);
                }
            }
        }
        possibleAssociations.put("All", all);
        associationKeys.put("All", allKeys);
    }


    /**
     * Responsible for parsing and storing Producer data downloaded in loadData.java
     * @param producerStream is the producer_buffer from loadData
     * @pre loadData has successfully downloaded data
     * @post All producer keys are stored into producerKeys arrayList. The possibleProducers
             map is filled with a key for every product category, and an arrayList of producer values
             corresponding to that product category. In addition, an "Any" key is added to the map with
             arrayList value with one item named "Any", and an "All" key is added to the map with arrayList
             value of "Any" followed by every producer available.
     */
    public void parseAndSetProducers(String producerStream)
    {
        //Add "Any" as Key and Value to possible products
        ArrayList<String> hold = new ArrayList<String>();
        hold.add("Any");
        possibleProducers.put("Any", hold);
        producerKeys.put("Any", hold);
        //Create an "all" list that will be used to display all available producers
        ArrayList<String> all = new ArrayList<String>();
        all.add("Any");
        ArrayList<String> allKeys = new ArrayList<String>();
        allKeys.add("Any");

        //create an array of the stream, new values are marked by \n
        String a[] = producerStream.split("\n");
        int producerCount = 0;
        while (!a[producerCount].equals(AppConstants.END_KEY_VALUES)){
            ArrayList<String> keyHold = new ArrayList<String>();
            ArrayList<String> tempList = new ArrayList<String>();
            if(a[producerCount].equals("start")) producerCount++;
            else
            {
                tempList.add("Any");
                keyHold.add("Any");
                String cat = a[producerCount];
                producerCount++;
                if (a[producerCount].equals("NULL"))
                {
                    possibleProducers.put(cat, tempList);
                    producerKeys.put(cat, keyHold);
                    producerCount++;
                }
                else
                {
                    while (!a[producerCount].equals("start") && !a[producerCount].equals(AppConstants.END_KEY_VALUES))
                    {
                        keyHold.add(a[producerCount]);
                        if(allKeys.contains(a[producerCount]))
                        {
                            producerCount++;
                        }
                        else
                        {
                            allKeys.add(a[producerCount]);
                            producerCount++;
                        }
                        tempList.add(a[producerCount]);
                        //check to make sure duplicates are not being added to all list
                        if (all.contains(a[producerCount])){
                            producerCount++;
                        }
                        else {
                            all.add(a[producerCount]);
                            producerCount++;
                        }
                    }
                    possibleProducers.put(cat, tempList);
                    producerKeys.put(cat, keyHold);
                }
            }
        }
        possibleProducers.put("All", all);
        producerKeys.put("All", allKeys);
    }

    /**
     * Responsible for parsing and storing Harvest data downloaded in loadData.java
     * @param harvestStream is the harvest_buffer from loadData
     * @pre loadData has successfully downloaded data
     * @post The possibleHarvestLocations map is filled with a key for every product category, and an arrayList
             of harvest values corresponding to that product category. In addition, an "Any" key is added to the
             map with arrayList value with one item named "Any", and an "All" key is added to the map with arrayList
             value of "Any" followed by every harvest location available.
     */
    public void parseAndSetHarvestLocations(String harvestStream)
    {
        //Add "Any" as Key and Value to possible products
        ArrayList<String> hold = new ArrayList<String>();
        hold.add("Any");
        possibleHarvestLocations.put("Any", hold);

        //Create an "all" list that will be used to display all available harvest locations
        ArrayList<String> all = new ArrayList<String>();
        all.add("Any");

        //create an array of the stream, new values are marked by \n
        String a[] = harvestStream.split("\n");
        int harvestCount = 0;
        while (!a[harvestCount].equals(AppConstants.END_KEY_VALUES)){
            ArrayList<String> tempList = new ArrayList<String>();
            if(a[harvestCount].equals("start")) harvestCount++;
            else
            {
                tempList.add("Any");
                String cat = a[harvestCount];
                harvestCount++;
                if (a[harvestCount].equals("NULL"))
                {
                    possibleHarvestLocations.put(cat, tempList);
                    harvestCount++;
                }
                else
                {
                    while (!a[harvestCount].equals("start") && !a[harvestCount].equals(AppConstants.END_KEY_VALUES))
                    {
                        tempList.add(a[harvestCount]);
                        if (all.contains(a[harvestCount])){
                            harvestCount++;
                        }
                        else {
                            all.add(a[harvestCount]);
                            harvestCount++;
                        }
                    }
                    possibleHarvestLocations.put(cat, tempList);
                }
            }
        }
        possibleHarvestLocations.put("All", all);
    }

    /**********Product Category Methods**********/

    public ArrayList<String> getProductCategories()
    {
        return this.arrayProductCategory;
    }

    public String categoryValueToKey(String value)
    {
        int pos = this.arrayProductCategory.indexOf(value);
        return this.productCategoryKey.get(pos);
    }

    public void setCurrentCategory(String value)
    {
        //clear the current variable
        int fin = categoryVariable.length();
        categoryVariable.delete(0,fin);

        if(value.equals("Any")) {
            categoryVariable.append("Any");
        }
        else
        {
            String key = categoryValueToKey(value);
            categoryVariable.append(key); //get key
        }
    }

    public String getCategoryURL()
    {
        return categoryVariable.toString();
    }

    /**********Product Methods**********/

    //key must be category key not category value
    public ArrayList<String> getProducts(String value)
    {
        String key = categoryValueToKey(value);
        return this.possibleProducts.get(key);
    }

    public void setCurrentProductFromPosition(int pos)
    {
        //clear the current variable
        int end = productURL.length();
        productURL.delete(0,end);
        String curCat = categoryVariable.toString();
        if (curCat.equals("Any"))
        {
            ArrayList<String> wait = productKeys.get("Any");
            productURL.append(wait.get(pos));
        }
        else
        {
            ArrayList<String> wait = productKeys.get(curCat);
            productURL.append(wait.get(pos));
        }
    }

    public String getProductURL()
    {
        return productURL.toString();
    }

    /**********Association Methods**********/

    public ArrayList<String> getAssociations(String value)
    {
        String key = categoryValueToKey(value);
        return this.possibleAssociations.get(key);
    }

    public void setCurrentAssociationFromPosition(int pos)
    {
        //clear the current variable
        int end = associationURL.length();
        associationURL.delete(0,end);
        String curCat = categoryVariable.toString();
        if(curCat.equals("Any"))
        {
            ArrayList<String> wait = associationKeys.get("All");
            String a = wait.get(pos);
            associationURL.append(a);
        }
        else
        {
            ArrayList<String> wait = associationKeys.get(curCat);
            String a = wait.get(pos);
            associationURL.append(a);
        }
    }

    public ArrayList<String> getAllAssociations()
    {
        return this.possibleAssociations.get("All");
    }

    public String getAssociationURL()
    {
        return associationURL.toString();
    }

    /**********Producers Methods**********/

    public ArrayList<String> getProducers(String value)
    {
        String key = categoryValueToKey(value);
        return this.possibleProducers.get(key);
    }

    public ArrayList<String> getAllProducers()
    {
        return this.possibleProducers.get("All");
    }

    public void setCurrentProducerFromPostition(int pos)
    {
        //clear the current variable
        int end = producerURL.length();
        producerURL.delete(0,end);
        String curCat = categoryVariable.toString();
        if(curCat.equals("Any"))
        {
            ArrayList<String> wait = producerKeys.get("All");
            producerURL.append(wait.get(pos));
        }
        else
        {
            ArrayList<String> wait = producerKeys.get(curCat);
            producerURL.append(wait.get(pos));
        }

    }

    public String getProducerURL()
    {
        return producerURL.toString();
    }
    /**********Harvest Location Methods**********/

    public ArrayList<String> getHarvestLocations(String value)
    {
        String key = categoryValueToKey(value);
        return this.possibleHarvestLocations.get(key);
    }

    public ArrayList<String> getAllHarvestLocations()
    {
        return this.possibleHarvestLocations.get("All");
    }
    public void setCurrentHarvestLocationFromPosition(int pos)
    {
        //clear the current variable
        int end = harvestLocationURL.length();
        harvestLocationURL.delete(0,end);
        String curCat = categoryVariable.toString();
        if(curCat.equals("Any"))
        {
            ArrayList<String> wait = possibleHarvestLocations.get("All");
            harvestLocationURL.append(wait.get(pos));
        }
        else {
            ArrayList<String> wait = possibleHarvestLocations.get(curCat);
            harvestLocationURL.append(wait.get(pos));
        }
    }

    public String getHarvestLocationURL()
    {
        return harvestLocationURL.toString();
    }
    /**********Miles Methods**********/

    public void setMilesURL(int pos)
    {
        int end = milesURL.length();
        milesURL.delete(0,end);
        String val = milesList.get(pos);
        milesURL.append(val);
    }

    public String getMilesURL()
    {
        return milesURL.toString();
    }

}

