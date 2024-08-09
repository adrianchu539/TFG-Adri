package com.example.sanbotapp;

import android.media.MediaDataSource;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.qihancloud.opensdk.base.TopBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModuloOpenAISpeechVoice {
    private byte[] respuestaGPTVoz;
    public ModuloOpenAISpeechVoice(){
    }

    protected void peticionVozOpenAI(String respuesta, String voz){

            // ----------- DATOS PARA REALIZAR REQUESTS HTTP -------------

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            final OkHttpClient client = new OkHttpClient();

            // ----------- DATOS PARA REALIZAR PETICIÓN A LA API DE OPENAI ---------
            JSONObject request = new JSONObject();
            try{
                request.put("model", "tts-1");
                request.put("input", respuesta);
                request.put("voice", voz);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("respuestaJSON", request.toString());

            RequestBody peticion = RequestBody.create(
                    MediaType.parse("application/json"), String.valueOf(request));

            Log.d("requestBody", peticion.toString());


            Request requestOpenAI = new Request.Builder()
                    .url("https://api.openai.com/v1/audio/speech")
                    .post(peticion)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer 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")
                    .build();

            Log.d("requestBody", requestOpenAI.toString());

            try (Response response = client.newCall(requestOpenAI).execute()) {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                // String respuesta = response.body().string();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    respuestaGPTVoz = response.body().bytes();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    protected byte[] getGPTVoz(){
        return respuestaGPTVoz;
    }

}
