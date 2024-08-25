package com.example.sanbotapp.activities.personalizacionUsuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.sanbotapp.R;
import com.example.sanbotapp.activities.MenuConfiguracionActivity;
import com.example.sanbotapp.gestion.GestionSharedPreferences;
import com.qihancloud.opensdk.base.TopBaseActivity;

public class NombreUsuarioActivity extends TopBaseActivity {

    private EditText nombreUsuario;
    private String nombre;
    private Button botonContinuar;
    private Button botonAtras;

    private GestionSharedPreferences gestionSharedPreferences;
    /*
    @Override
    public void onResume() {
        super.onResume();

        if(nombre!=null){
            nombreUsuario.setText(nombre);
        }
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuConfiguracionActivity = new Intent(CuestionarioNombreActivity.this, MenuConfiguracionActivity.class);
                startActivity(menuConfiguracionActivity);
                finish();
            }
        });
    }

     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuestionario_previo_nombre);

        gestionSharedPreferences = new GestionSharedPreferences(this);

        nombre = gestionSharedPreferences.getStringSharedPreferences("nombreUsuario", null);

        try {
            nombreUsuario = findViewById(R.id.nombreUsuario);
            botonContinuar = findViewById(R.id.botonContinuar);
            botonAtras = findViewById(R.id.botonAtras);

            if(nombre!=null){
                nombreUsuario.setText(nombre);
            }
            botonContinuar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    gestionSharedPreferences.putStringSharedPreferences("nombreUsuario", "nombreUsuario", String.valueOf(nombreUsuario.getText()));
                    Intent cuestionarioEdadActivity = new Intent(NombreUsuarioActivity.this, EdadUsuarioActivity.class);
                    startActivity(cuestionarioEdadActivity);
                    finish();
                }
            });
            botonAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent menuConfiguracionActivity = new Intent(NombreUsuarioActivity.this, MenuConfiguracionActivity.class);
                    startActivity(menuConfiguracionActivity);
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
