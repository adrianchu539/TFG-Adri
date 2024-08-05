package com.example.sanbotapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.qihancloud.opensdk.base.TopBaseActivity;

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

            botonPruebaSanbot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent tutorialModuloConversacionalActivity = new Intent(MenuPruebas.this, TutorialModuloConversacional.class);
                    startActivity(tutorialModuloConversacionalActivity);
                    finish();
                }
            });
            botonPruebaOpenAI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent cuestionarioNombreActivity = new Intent(MenuPruebas.this, CuestionarioNombre.class);
                    startActivity(cuestionarioNombreActivity);
                    finish();
                }
            });
            botonPruebaVoces.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    // POR COMPLETAR
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    protected void onMainServiceConnected() {

    }
}
