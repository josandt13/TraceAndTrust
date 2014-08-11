package com.traceandtrust;

/**
 * Created by Joey on 7/25/2014.
 */
public class AppConstants {
    //BASE URL for the whole app
    public static final String SEARCH_FILTER_URL = "http://traceandtrust.redteal.com/mobile_api/services/search_filter";
    public static final String INITIAL_RESTAURANT_URL = "http://traceandtrust.redteal.com/mobile_api/services/restaurant_list?harvest_location_max=4000&latitude=39.5&longitude=-98.3";
    public static final String SEARCH_START_URL = "http://traceandtrust.redteal.com/mobile_api/services/restaurant_list?";
    public static final String RESTAURANT_DATA_FOR_PROFILE = "http://traceandtrust.redteal.com/mobile_api/services/get_recent_deliveries_by_restaurant?restaurantId=";

    public static final String MIDDLE_USA_LATITUDE = "39.5";
    public static final String MIDDLE_USA_LONGITUDE = "-98.3";

    public static final String PRODUCT_CATEGORY_URL = "productCategoryURL";
    public static final String PRODUCT_URL = "productURL";
    public static final String ASSOCIATION_URL = "associationURL";
    public static final String PRODUCER_URL = "producerURL";
    public static final String HARVEST_LOCATION_URL = "harvestLocationURL";
    public static final String MILES_URL = "milesURL";


    public static String NETWORK_ERROR = "Please check your Internet Connection.";
    public static String SERVER_ERROR = "Internal Server Error.Please Try Again Later.";
    public static String ALERT_TITLE = "Alert";
    public static final String CATEGORY_FILTER = "categoryFilter";
    public static final String PRODUCT_FILTER = "productFilter";
    public static final String PRODUCT_CATEGORIES_COUNT = "productCategoriesCount";
    public static final String FILTER_PREFERENCES = "filters";
    public static final String RESTAURANT_PREFERENCES = "currentrestaurant";
    public static final String END_KEY_VALUES = "endForSure123";

    public static final String FILTER_RESULTS_OBJECT_NAME = "filterResults";
    public static final String CATEGORY_OBJECT_NAME = "category";
    public static final String PRODUCTS_OBJECT_NAME = "products";
    public static final String ASSOCIATION_OBJECT_NAME = "associations";
    public static final String ASSOCIATION_FILTER = "associationFilter";
    public static final String PRODUCER_OBJECT_NAME = "producers";
    public static final String PRODUCER_FILTER = "producerFilter";
    public static final String HARVEST_OBJECT_NAME = "harvest_locations";
    public static final String HARVEST_FILTER = "harvestFilter";
    public static final String RESTAURANTS_FILE = "restaurants.txt";

    public static final String CURRENT_RESTAURANT_IMAGETHUMB = "restaurant_current_image_thumb";
    public static final String CURRENT_RESTAURANT_NAME = "restaurant_current_name";
    public static final String CURRENT_RESTAURANT_ADDRESS = "restaurant_current_address";
    public static final String CURRENT_RESTAURANT_IMAGELARGE = "restaurant_current_image_large";
    public static final String CURRENT_RESTAURANT_PHONE = "restaurant_current_phone";
    public static final String CURRENT_RESTAURANT_ID = "restaurant_current_id";

    public static final String UPDATE_MAP_BOOL = "updateMap";
    public static final String UPDATE_LIST_BOOL = "updateList";
    public static final String WRITTEN_TO_FILE = "writtenToFle";
    public static final String RESTAURANT_INFO_TAB = "About";
    public static final String RESTAURANT_DELIVERIES_TAB = "Recent Deliveries";
    public static final String RESTAURANT_PROFILE_INFO_JSON = "DeliveryResults";
    public static final String RESTAURANT_PROFILE_RECENT_DELIVERIES_JSON = "recent_deliveries";


}
