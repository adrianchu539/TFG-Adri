package com.example.sanbotapp.modulos;

import android.os.StrictMode;
import android.util.Log;

import com.qihancloud.opensdk.base.TopBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModuloPeticionesExternas {

    // Constructor
    public ModuloPeticionesExternas(){
    }

    // Función que obtiene el día y la hora actual
    private String diaYHora(){
        Calendar cal = Calendar.getInstance();
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        int mes = cal.get(Calendar.MONTH);
        int agno = cal.get(Calendar.YEAR);
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int minutos = cal.get(Calendar.MINUTE);
        int segundos = cal.get(Calendar.SECOND);
        return "Hoy es " + dia + " del " + mes + " de " + agno + " y son las " + hora + minutos + segundos;
    }

    // Función para añadir posibles funciones del robot
    public String funcionesRobot(){
        return diaYHora();
    }

    // Función que realiza una consulta de las peliculas en cartelera
    public ArrayList<String> peliculasAPI(){
        ArrayList<String> nombresPeliculas = new ArrayList<String>();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/movie/popular?language=es-ES&page=1")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZjRhZTM4MjY5ZWEzODY2Yzc4MjcyZTMzNDc1ZTQwNiIsInN1YiI6IjY2MDMzNDhjZDM4YjU4MDE3ZDFiNzExMiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.sGinrHt3C4Ko683tmaYIHZNeUgK87Vg8FSmJfiwvyRI")
                .header("accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            System.out.println("Primera frase a decir");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONArray results = new JSONArray(jsonObject.getString("results"));
                for(int i=0; i<results.length(); i++){
                    JSONObject pelicula = results.getJSONObject(i);
                    nombresPeliculas.add(pelicula.getString("title"));
                }
            }
            return nombresPeliculas;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
