package com.example.sanbotapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;

import android.content.SharedPreferences;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.unit.SystemManager;

public class GestionSharedPreferences {
    private Context context;

    public GestionSharedPreferences(Context context){
        this.context = context;
    }

    protected String getStringSharedPreferences(String nombreSharedPreferences, String defaultValue){
        SharedPreferences sp = context.getSharedPreferences(nombreSharedPreferences, MODE_PRIVATE);
        Log.d("getStringPreferences", "el valor de " + nombreSharedPreferences + " es " + sp.getString(nombreSharedPreferences, defaultValue));
        return sp.getString(nombreSharedPreferences, defaultValue);
    }
    protected int getIntSharedPreferences(String nombreSharedPreferences, int defaultValue){
        SharedPreferences sp = context.getSharedPreferences(nombreSharedPreferences, MODE_PRIVATE);
        Log.d("getIntPreferences", "el valor de " + nombreSharedPreferences + " es " + sp.getInt(nombreSharedPreferences, defaultValue));
        return sp.getInt(nombreSharedPreferences, defaultValue);
    }
    protected boolean getBooleanSharedPreferences(String nombreSharedPreferences, boolean defaultValue){
        android.content.SharedPreferences sp = context.getSharedPreferences(nombreSharedPreferences, MODE_PRIVATE);
        Log.d("getBooleanPreferences", "el valor de " + nombreSharedPreferences + " es " + sp.getBoolean(nombreSharedPreferences, defaultValue));
        return sp.getBoolean(nombreSharedPreferences, defaultValue);
    }

}
