package com.example.sanbotapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.sanbotapp.activities.personalizacionRobot.PersonalizacionRobotActivity;
import com.example.sanbotapp.activities.personalizacionUsuario.CuestionarioNombreActivity;
import com.example.sanbotapp.R;
import com.qihancloud.opensdk.base.TopBaseActivity;

public class MenuConfiguracionActivity extends TopBaseActivity {
    private Button botonAjustes;
    private Button botonPersonalizacionRobot;
    private Button botonPersonalizacionUsuario;
    private Button botonAtras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_configuracion);


        try {
            botonAjustes = findViewById(R.id.botonAjustes);
            botonPersonalizacionUsuario = findViewById(R.id.botonPersonalizacionUsuario);
            botonPersonalizacionRobot = findViewById(R.id.botonPersonalizacionRobot);
            botonAtras = findViewById(R.id.botonAtras);

            botonAjustes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent settingsActivity = new Intent(MenuConfiguracionActivity.this, AjustesActivity.class);
                    startActivity(settingsActivity);
                    finish();
                }
            });
            botonPersonalizacionRobot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent contextualizacionActivity = new Intent(MenuConfiguracionActivity.this, PersonalizacionRobotActivity.class);
                    startActivity(contextualizacionActivity);
                    finish();
                }
            });
            botonPersonalizacionUsuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent cuestionarioNombreActivity = new Intent(MenuConfiguracionActivity.this, CuestionarioNombreActivity.class);
                    startActivity(cuestionarioNombreActivity);
                    finish();
                }
            });
            botonAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
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
