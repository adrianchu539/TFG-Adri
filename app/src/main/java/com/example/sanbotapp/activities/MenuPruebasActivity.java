package com.example.sanbotapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.sanbotapp.activities.personalizacionUsuario.NombreUsuarioActivity;
import com.example.sanbotapp.R;
import com.example.sanbotapp.gestion.GestionSharedPreferences;
import com.qihancloud.opensdk.base.TopBaseActivity;

public class MenuPruebasActivity extends TopBaseActivity {
    private Button botonPruebaSanbot;
    private Button botonPruebaOpenAI;
    private Button botonPruebaVoces;
    private GestionSharedPreferences gestionSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pruebas);

        gestionSharedPreferences = new GestionSharedPreferences(this);

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
                    gestionSharedPreferences.clearSharedPreferences("vozSeleccionada");
                    gestionSharedPreferences.clearSharedPreferences("nombreUsuario");
                    gestionSharedPreferences.clearSharedPreferences("edadUsuario");
                    gestionSharedPreferences.clearSharedPreferences("generoRobotPersonalizacion");
                    gestionSharedPreferences.clearSharedPreferences("grupoEdadRobotPersonalizacion");
                    gestionSharedPreferences.clearSharedPreferences("contextoPersonalizacion");
                    gestionSharedPreferences.clearSharedPreferences("conversacionAutomatica");
                    gestionSharedPreferences.clearSharedPreferences("modoTeclado");
                    gestionSharedPreferences.clearSharedPreferences("personalizacionActivada");
                    gestionSharedPreferences.clearSharedPreferences("contextualizacionActivada");
                    gestionSharedPreferences.clearSharedPreferences("interpretacionEmocionalActivada");
                    gestionSharedPreferences.clearSharedPreferences("contextoVacio");

                    editorVozSeleccionada.putString("vozSeleccionada", "sanbot");
                    //Intent tutorialModuloConversacionalActivity = new Intent(MenuPruebas.this, TutorialModuloConversacional.class);
                    Intent tutorialModuloConversacionalActivity = new Intent(MenuPruebasActivity.this, TutorialModuloConversacionalActivity.class);
                    startActivity(tutorialModuloConversacionalActivity);
                    finish();
                }
            });
            botonPruebaOpenAI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    gestionSharedPreferences.clearSharedPreferences("vozSeleccionada");
                    gestionSharedPreferences.clearSharedPreferences("nombreUsuario");
                    gestionSharedPreferences.clearSharedPreferences("edadUsuario");
                    gestionSharedPreferences.clearSharedPreferences("generoRobotPersonalizacion");
                    gestionSharedPreferences.clearSharedPreferences("grupoEdadRobotPersonalizacion");
                    gestionSharedPreferences.clearSharedPreferences("contextoPersonalizacion");
                    gestionSharedPreferences.clearSharedPreferences("conversacionAutomatica");
                    gestionSharedPreferences.clearSharedPreferences("modoTeclado");
                    gestionSharedPreferences.clearSharedPreferences("personalizacionActivada");
                    gestionSharedPreferences.clearSharedPreferences("contextualizacionActivada");
                    gestionSharedPreferences.clearSharedPreferences("interpretacionEmocionalActivada");
                    gestionSharedPreferences.clearSharedPreferences("contextoVacio");
                    editorInterpretacionEmocionalActivada.putBoolean("interpretacionEmocionalActivada", true);
                    editorInterpretacionEmocionalActivada.apply();
                    Intent cuestionarioNombreActivity = new Intent(MenuPruebasActivity.this, NombreUsuarioActivity.class);
                    startActivity(cuestionarioNombreActivity);
                    finish();
                }
            });
            botonPruebaVoces.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent cuestionarioNombreActivity = new Intent(MenuPruebasActivity.this, PruebaVocesActivity.class);
                    startActivity(cuestionarioNombreActivity);
                    finish();
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
