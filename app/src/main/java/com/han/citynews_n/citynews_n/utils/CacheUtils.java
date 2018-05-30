package com.han.citynews_n.citynews_n.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by han on 2018/5/11.
 */

public class CacheUtils {

    public static final String CACHE_FILE_NAME = "com.han.citynews_n.citynews_n";
    private static SharedPreferences sharedPreferences;

    public static void putBoolean(Context context, String key, boolean value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public  static boolean getBoolean(Context context,String key,boolean value){
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getBoolean(key,value);
    }

    public static void putString(Context context, String key, String value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putString(key,value).commit();
    }

    public static String getString(Context context, String key, String value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key,value);
    }

}
