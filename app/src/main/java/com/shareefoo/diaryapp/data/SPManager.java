package com.shareefoo.diaryapp.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by shareefoo
 */

public class SPManager {

    private static final String SP_NAME = "diary_prefs";

    public static final String USER_ID = "user_id";

    private static SPManager instance = null;

    private SharedPreferences mSharedPreferences;

    private SPManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static SPManager getInstance(Context context) {
        if (instance == null) {
            instance = new SPManager(context);
        }
        return instance;
    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    public boolean putString(String key, String value) {
        return mSharedPreferences.edit().putString(key, value).commit();
    }

    public String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public boolean putInt(String key, int value) {
        return mSharedPreferences.edit().putInt(key, value).commit();
    }

    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    public boolean putBoolean(String key, boolean value) {
        return mSharedPreferences.edit().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    public boolean remove(String key) {
        return mSharedPreferences.edit().remove(key).commit();
    }

}
