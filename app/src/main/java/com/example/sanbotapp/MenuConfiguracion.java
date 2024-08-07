package com.example.sanbotapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.qihancloud.opensdk.base.TopBaseActivity;

public class MenuConfiguracion extends TopBaseActivity {
    private Button botonAjustes;
    private Button botonPersonalizacion;
    private Button botonContextualizacion;
    private Button botonAtras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_configuracion);


        try {
            botonAjustes = findViewById(R.id.botonAjustes);
            botonPersonalizacion = findViewById(R.id.botonPersonalizacion);
            botonContextualizacion = findViewById(R.id.botonContextualizacion);
            botonAtras = findViewById(R.id.botonAtras);

            botonAjustes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent settingsActivity = new Intent(MenuConfiguracion.this, SettingsActivity.class);
                    startActivity(settingsActivity);
                    finish();
                }
            });
            botonContextualizacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent contextualizacionActivity = new Intent(MenuConfiguracion.this, ContextualizacionActivity.class);
                    startActivity(contextualizacionActivity);
                    finish();
                }
            });
            botonPersonalizacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent cuestionarioNombreActivity = new Intent(MenuConfiguracion.this, CuestionarioNombre.class);
                    startActivity(cuestionarioNombreActivity);
                    finish();
                }
            });
            botonAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent moduloConversacionalActivity = new Intent(MenuConfiguracion.this, ModuloConversacional.class);
                    startActivity(moduloConversacionalActivity);
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
