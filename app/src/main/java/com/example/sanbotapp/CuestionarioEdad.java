package com.example.sanbotapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qihancloud.opensdk.base.TopBaseActivity;

import kotlin.experimental.ExperimentalObjCName;

public class CuestionarioEdad extends TopBaseActivity {

    private TextView textoEdad;

    private Button aceptarEdad;

    private EditText edadUsuario;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //register(PresentacionActivity.class);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SharedPreferences sharedPref = this.getSharedPreferences("edad", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuestionario_previo_edad);

        try {
            textoEdad = findViewById(R.id.textoEdad);
            aceptarEdad = findViewById(R.id.okEdad);
            edadUsuario = findViewById(R.id.edadUsuario);
            //create an adapter to describe how the items are displayed, adapters are used in several places in android.
            //There are multiple variations of this, but this is the basic variant.
            aceptarEdad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    editor.putInt("edad", Integer.parseInt(String.valueOf(edadUsuario.getText())));
                    editor.apply();
                    Intent personalizacionActivity = new Intent(CuestionarioEdad.this, PersonalizacionActivity.class);
                    startActivity(personalizacionActivity);
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
