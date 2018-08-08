package com.mycalculator20;

import android.content.Context;
import android.content.SharedPreferences;

class AppPreferences {

    private SharedPreferences sharedPreferences;
    private Context context;
    private SharedPreferences.Editor editor;

    final String SHARED_PREF_STRING = "com.mycalculator20";
    final  static String APP_THEME = "appTheme";
    final static String APP_FIRST_LAUNCH = "AppFirstLaunch";
    final static String APP_ANSWER_PRECISION="precision";

    public AppPreferences(Context context) {
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(SHARED_PREF_STRING,Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public String getStringPreference(String key){
        return sharedPreferences.getString(key,"");
    }

    public void setStringPreference(String key, String value){
        editor.putString(key,value);
        editor.commit();
    }

    public static AppPreferences getInstance(Context context){
        return new AppPreferences(context);
    }

    public boolean getBooleanPreference(String key){
        return sharedPreferences.getBoolean(key,true);
    }

    public void setBooleanPreference(String key, Boolean value){
        editor.putBoolean(key,value);
        editor.commit();
    }
}


