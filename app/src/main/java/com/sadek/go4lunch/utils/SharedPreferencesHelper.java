package com.sadek.go4lunch.utils;

import android.content.Context;

public class SharedPreferencesHelper {

    private static final String SHARED_PREF_RESTAURANT_INFO = "SHARED_PREF_RESTAURANT_INFO";
    private static final String SHARED_PREF_RESTAURANT_INFO_NAME = "SHARED_PREF_RESTAURANT_INFO_NAME";
    private static final String SHARED_PREF_RESTAURANT_INFO_ADDRESS = "SHARED_PREF_RESTAURANT_INFO_ADDRESS";


    public static void setData(Context context,String name,String address) {
        context.getSharedPreferences(SHARED_PREF_RESTAURANT_INFO,Context.MODE_PRIVATE)
                .edit()
                .putString(SHARED_PREF_RESTAURANT_INFO_NAME,name)
                .putString(SHARED_PREF_RESTAURANT_INFO_ADDRESS,address)
                .apply();
    }

    public static String getRestaurantName(Context context) {
        return context.getSharedPreferences(SHARED_PREF_RESTAURANT_INFO,Context.MODE_PRIVATE)
                .getString(SHARED_PREF_RESTAURANT_INFO_NAME,null);
    }

    public static String getRestaurantAddress(Context context) {
        return context.getSharedPreferences(SHARED_PREF_RESTAURANT_INFO,Context.MODE_PRIVATE)
                .getString(SHARED_PREF_RESTAURANT_INFO_ADDRESS,null);
    }
}
