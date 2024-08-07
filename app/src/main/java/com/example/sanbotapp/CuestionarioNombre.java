package com.example.sanbotapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.qihancloud.opensdk.base.TopBaseActivity;

public class CuestionarioNombre extends TopBaseActivity {

    private EditText nombreUsuario;
    private String nombre;
    private Button botonContinuar;
    private Button botonAtras;

    @Override
    public void onResume() {
        super.onResume();

        if(nombre!=null){
            nombreUsuario.setText(nombre);
        }
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuConfiguracionActivity = new Intent(CuestionarioNombre.this, MenuConfiguracion.class);
                startActivity(menuConfiguracionActivity);
                finish();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuestionario_previo_nombre);

        // Creo una sección de almacenamiento local donde se guardará el nombre del usuario
        SharedPreferences sharedPref = this.getSharedPreferences("nombreUsuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        nombre = sharedPref.getString("nombreUsuario", null);

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
                    editor.putString("nombreUsuario", String.valueOf(nombreUsuario.getText()));
                    editor.apply();
                    Intent cuestionarioEdadActivity = new Intent(CuestionarioNombre.this, CuestionarioEdad.class);
                    startActivity(cuestionarioEdadActivity);
                    finish();
                }
            });
            botonAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent menuPruebasActivity = new Intent(CuestionarioNombre.this, MenuPruebas.class);
                    startActivity(menuPruebasActivity);
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
