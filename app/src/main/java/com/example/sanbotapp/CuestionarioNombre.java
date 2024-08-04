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

    private Button botonContinuar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //register(PresentacionActivity.class);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SharedPreferences sharedPref = this.getSharedPreferences("nombre", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuestionario_previo_nombre);

        try {
            nombreUsuario = findViewById(R.id.nombreUsuario);
            botonContinuar = findViewById(R.id.botonContinuar);
            //create an adapter to describe how the items are displayed, adapters are used in several places in android.
            //There are multiple variations of this, but this is the basic variant.
            botonContinuar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    editor.putString("nombre", String.valueOf(nombreUsuario.getText()));
                    editor.apply();
                    Intent cuestionarioEdadActivity = new Intent(CuestionarioNombre.this, CuestionarioEdad.class);
                    startActivity(cuestionarioEdadActivity);
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
