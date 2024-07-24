package com.example.sanbotapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.qihancloud.opensdk.base.TopBaseActivity;

public class CuestionarioNombre extends TopBaseActivity {

    private TextView textoNombre;

    private Button aceptarNombre;

    private EditText nombreUsuario;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //register(PresentacionActivity.class);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SharedPreferences sharedPref = this.getSharedPreferences("nombre", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuestionario_previo_nombre);

        try {
            textoNombre = findViewById(R.id.textoNombre);
            aceptarNombre = findViewById(R.id.okNombre);
            nombreUsuario = findViewById(R.id.nombreUsuario);
            //create an adapter to describe how the items are displayed, adapters are used in several places in android.
            //There are multiple variations of this, but this is the basic variant.
            aceptarNombre.setOnClickListener(new View.OnClickListener() {
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
