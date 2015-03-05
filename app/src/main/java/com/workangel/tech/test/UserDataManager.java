package com.workangel.tech.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Utility class used to store and retrieve user-related informations
 */
public class UserDataManager {

    private static final String TAG = UserDataManager.class.getSimpleName();


    public static final String KEY_SHARED_PREFERENCES = "PREFS";
    private static final String KEY_DOWNLOADED_ONCE = "key_downloaded_once";
    private static final String KEY_STOP_ASKING_CONTACTS_SAVE = "key_stop_asking_save_contacts";


    /**
     * Save the current value in shared preferences
     * @param context
     * @param key
     * @param value
     */
    private static void saveValue(Context context, String key, String value){
        SharedPreferences prefs;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_MULTI_PROCESS);
        }
        else {
            prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Save the current value in shared preferences
     * @param context
     * @param key
     * @param value
     */
    private static void saveValue(Context context, String key, boolean value){
        SharedPreferences prefs;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_MULTI_PROCESS);
        }
        else {
            prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(key, value);
        editor.commit();
    }



    /**
     * Save the current value in shared preferences
     * @param context
     * @param key
     * @param value
     */
    private static void saveValue(Context context, String key, int value){
        SharedPreferences prefs;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_MULTI_PROCESS);
        }
        else {
            prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * Save the current value in shared preferences
     * @param context
     * @param key
     * @param value
     */
    private static void saveValue(Context context, String key, long value){
        SharedPreferences prefs;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_MULTI_PROCESS);
        }
        else {
            prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * Read a value from shared preferences.
     * @param context
     * @param key
     * @return the value associated to the key or null if the value do not exists
     */
    private static String getValue(Context context, String key) {
        String returnString;
        try{
            SharedPreferences prefs;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_MULTI_PROCESS);
            }
            else {
                prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_PRIVATE);
            }
            returnString = prefs.getString(key, null);
        }
        catch (Exception e){
            returnString = null;
        }

        return returnString;
    }



    /**
     * Read a value from shared preferences.
     * @param context
     * @param key
     * @return the value associated to the key or null if the value do not exists
     */
    private static int getValue(Context context, String key, int defaultValue) {
        int returnInt;
        try{
            SharedPreferences prefs;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_MULTI_PROCESS);
            }
            else {
                prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_PRIVATE);
            }
            returnInt = prefs.getInt(key, defaultValue);
        }
        catch (Exception e){
            returnInt = defaultValue;
        }

        return returnInt;
    }

    private static boolean getValue(Context context, String key, boolean defaultValue) {
        boolean returnInt;
        try{
            SharedPreferences prefs;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_MULTI_PROCESS);
            }
            else {
                prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_PRIVATE);
            }
            returnInt = prefs.getBoolean(key, defaultValue);
        }
        catch (Exception e){
            returnInt = defaultValue;
        }

        return returnInt;
    }

    /**
     * Read a value from shared preferences.
     * @param context
     * @param key
     * @return the value associated to the key or null if the value do not exists
     */
    private static long getValue(Context context, String key, long defaultValue) {
        long returnInt;
        try{
            SharedPreferences prefs;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_MULTI_PROCESS);
            }
            else {
                prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_PRIVATE);
            }
            returnInt = prefs.getLong(key, defaultValue);
        }
        catch (Exception e){
            returnInt = defaultValue;
        }

        return returnInt;
    }

    /**
     * Read a value from shared preferences.
     * @param key the key to check
     * @return the value associated to the key or null if the value do not exists
     */
    private static boolean isValuePresent(Context context, String key) {
        SharedPreferences prefs;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_MULTI_PROCESS);
        }
        else {
            prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES,Context.MODE_PRIVATE);
        }
        return prefs.contains(key);
    }

    public static boolean getDownloadedOnce(Context context) {
        return getValue(context,KEY_DOWNLOADED_ONCE,false);
    }

    public static boolean getStopAskingForSavingContacts(Context context) {
        return getValue(context,KEY_STOP_ASKING_CONTACTS_SAVE,false);
    }

    public static void setStopAskingForSavingContacts(Context context, boolean stop) {
        saveValue(context,KEY_STOP_ASKING_CONTACTS_SAVE,stop);
    }

    public static void setDownloadedOnce(Context context, boolean downloaded) {
        saveValue(context,KEY_DOWNLOADED_ONCE, downloaded);
    }

}