package com.example.sanbotapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.unit.SpeechManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PruebaVoces extends TopBaseActivity {
    private Button botonVoz1;
    private Button botonVoz2;
    private Button botonVoz3;
    private Button botonVoz4;
    private Button botonVoz5;
    private Button botonVoz6;

    private SpeechManager speechManager;
    private SpeakOption speakOption;
    private SpeechControl speechControl;

    private ModuloConversacional moduloConversacional;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_voces);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);

        speakOption.setSpeed(50);
        speakOption.setIntonation(50);

        speechControl = new SpeechControl(speechManager, speakOption);

        try {

            botonVoz1 = findViewById(R.id.boton1);
            botonVoz2 = findViewById(R.id.boton2);
            botonVoz3 = findViewById(R.id.boton3);
            botonVoz4 = findViewById(R.id.boton4);
            botonVoz5 = findViewById(R.id.boton5);
            botonVoz6 = findViewById(R.id.boton6);

            botonVoz1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    try {
                        speechControl.gestionHabla("nova", "Hola. Soy Sanbot. Espero que hayas" +
                                " disfrutado de esta prueba del módulo conversacional");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            botonVoz2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    try {
                        speechControl.gestionHabla("alloy", "Hola. Soy Sanbot. Espero que hayas" +
                                " disfrutado de esta prueba del módulo conversacional");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            botonVoz3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    try {
                        speechControl.gestionHabla("onyx", "Hola. Soy Sanbot. Espero que hayas" +
                                " disfrutado de esta prueba del módulo conversacional");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            botonVoz4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    try {
                        speechControl.gestionHabla("sanbot", "Hola. Soy Sanbot. Espero que hayas" +
                                " disfrutado de esta prueba del módulo conversacional");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            botonVoz5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    try {
                        speechControl.gestionHabla("shimmer", "Hola. Soy Sanbot. Espero que hayas" +
                                " disfrutado de esta prueba del módulo conversacional");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            botonVoz6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    try {
                        speechControl.gestionHabla("echo", "Hola. Soy Sanbot. Espero que hayas" +
                                " disfrutado de esta prueba del módulo conversacional");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void emptySharedPreferences(String nombreSharedPreferences){
        SharedPreferences sp = getSharedPreferences(nombreSharedPreferences, 0);
        sp.edit().clear().commit();
    }

    @Override
    protected void onMainServiceConnected() {

    }
}
