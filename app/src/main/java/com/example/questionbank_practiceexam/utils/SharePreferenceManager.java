package com.example.questionbank_practiceexam.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceManager {

    private static final String PRE_NAME = "app_preference";
    private static final String KEY_IS_FIRST_TIME = "key_is_first_time";


    public static boolean isFirstTime(Context context) {

        SharedPreferences preferences = context.getSharedPreferences(PRE_NAME,Context.MODE_PRIVATE);
    return preferences.getBoolean(KEY_IS_FIRST_TIME,true);
    }

    public static void setFirstTimeCompleted(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(PRE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_IS_FIRST_TIME,false);
        editor.apply();
    }

}
