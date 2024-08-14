package com.example.sanbotapp.gestion;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;

import android.content.SharedPreferences;

public class GestionSharedPreferences {
    private Context context;

    // Constructor
    public GestionSharedPreferences(Context context){
        this.context = context;
    }

    // Permite obtener String del almacenamiento local del programa
    public String getStringSharedPreferences(String nombreSharedPreferences, String defaultValue){
        SharedPreferences sp = context.getSharedPreferences(nombreSharedPreferences, MODE_PRIVATE);
        Log.d("getStringPreferences", "el valor de " + nombreSharedPreferences + " es " + sp.getString(nombreSharedPreferences, defaultValue));
        return sp.getString(nombreSharedPreferences, defaultValue);
    }

    // Permite obtener entero del almacenamiento local del programa
    public int getIntSharedPreferences(String nombreSharedPreferences, int defaultValue){
        SharedPreferences sp = context.getSharedPreferences(nombreSharedPreferences, MODE_PRIVATE);
        Log.d("getIntPreferences", "el valor de " + nombreSharedPreferences + " es " + sp.getInt(nombreSharedPreferences, defaultValue));
        return sp.getInt(nombreSharedPreferences, defaultValue);
    }

    // Permite obtener booleano del almacenamiento local del programa
    public boolean getBooleanSharedPreferences(String nombreSharedPreferences, boolean defaultValue){
        android.content.SharedPreferences sp = context.getSharedPreferences(nombreSharedPreferences, MODE_PRIVATE);
        Log.d("getBooleanPreferences", "el valor de " + nombreSharedPreferences + " es " + sp.getBoolean(nombreSharedPreferences, defaultValue));
        return sp.getBoolean(nombreSharedPreferences, defaultValue);
    }

    // Permite almacenar una String en el almacenamiento local del programa
    public void putStringSharedPreferences(String nombreSharedPreferences, String nombreValor, String valor){
        SharedPreferences sp = context.getSharedPreferences(nombreSharedPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(nombreValor, valor);
        editor.apply();
    }

    // Permite almacenar un entero en el almacenamiento local del programa
    public void putIntSharedPreferences(String nombreSharedPreferences, String nombreValor, int valor){
        SharedPreferences sp = context.getSharedPreferences(nombreSharedPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(nombreValor, valor);
        editor.apply();
    }

    // Permite almacenar un booleano en el almacenamiento local del programa
    public void putBooleanSharedPreferences(String nombreSharedPreferences, String nombreValor, boolean valor){
        SharedPreferences sp = context.getSharedPreferences(nombreSharedPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(nombreValor, valor);
        editor.apply();
    }

    // Permite eliminar una variable del almacenamiento local del programa
    public void clearSharedPreferences(String nombreSharedPreferences){
        SharedPreferences sp = context.getSharedPreferences(nombreSharedPreferences, 0);
        sp.edit().clear().commit();
    }

}
