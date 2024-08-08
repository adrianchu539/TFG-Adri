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

public class MenuPruebas extends TopBaseActivity {
    private Button botonPruebaSanbot;
    private Button botonPruebaOpenAI;
    private Button botonPruebaVoces;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pruebas);


        try {

            botonPruebaSanbot = findViewById(R.id.botonPruebaSanbot);
            botonPruebaOpenAI = findViewById(R.id.botonPruebaOpenAI);
            botonPruebaVoces = findViewById(R.id.botonPruebaVoces);

            SharedPreferences sharedVozSeleccionada = this.getSharedPreferences("vozSeleccionada", MODE_PRIVATE);
            SharedPreferences.Editor editorVozSeleccionada = sharedVozSeleccionada.edit();

            SharedPreferences sharedInterpretacionEmocionalActivada = this.getSharedPreferences("interpretacionEmocionalActivada", MODE_PRIVATE);
            SharedPreferences.Editor editorInterpretacionEmocionalActivada = sharedInterpretacionEmocionalActivada.edit();

            botonPruebaSanbot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    emptySharedPreferences("vozSeleccionada");
                    emptySharedPreferences("nombreUsuario");
                    emptySharedPreferences("edadUsuario");
                    emptySharedPreferences("generoRobotPersonalizacion");
                    emptySharedPreferences("grupoEdadRobotPersonalizacion");
                    emptySharedPreferences("contextoPersonalizacion");
                    emptySharedPreferences("conversacionAutomatica");
                    emptySharedPreferences("modoTeclado");
                    emptySharedPreferences("personalizacionActivada");
                    emptySharedPreferences("contextualizacionActivada");
                    emptySharedPreferences("interpretacionEmocionalActivada");
                    emptySharedPreferences("contextoVacio");

                    editorVozSeleccionada.putString("vozSeleccionada", "sanbot");
                    //Intent tutorialModuloConversacionalActivity = new Intent(MenuPruebas.this, TutorialModuloConversacional.class);
                    Intent tutorialModuloConversacionalActivity = new Intent(MenuPruebas.this, TutorialModuloConversacional.class);
                    startActivity(tutorialModuloConversacionalActivity);
                    finish();
                }
            });
            botonPruebaOpenAI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    emptySharedPreferences("vozSeleccionada");
                    emptySharedPreferences("nombreUsuario");
                    emptySharedPreferences("edadUsuario");
                    emptySharedPreferences("generoRobotPersonalizacion");
                    emptySharedPreferences("grupoEdadRobotPersonalizacion");
                    emptySharedPreferences("contextoPersonalizacion");
                    emptySharedPreferences("conversacionAutomatica");
                    emptySharedPreferences("modoTeclado");
                    emptySharedPreferences("personalizacionActivada");
                    emptySharedPreferences("contextualizacionActivada");
                    emptySharedPreferences("interpretacionEmocionalActivada");
                    emptySharedPreferences("contextoVacio");
                    editorInterpretacionEmocionalActivada.putBoolean("interpretacionEmocionalActivada", true);
                    editorInterpretacionEmocionalActivada.apply();
                    Intent cuestionarioNombreActivity = new Intent(MenuPruebas.this, CuestionarioNombre.class);
                    startActivity(cuestionarioNombreActivity);
                    finish();
                }
            });
            botonPruebaVoces.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent cuestionarioNombreActivity = new Intent(MenuPruebas.this, PruebaVoces.class);
                    startActivity(cuestionarioNombreActivity);
                    finish();
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
