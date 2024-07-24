package com.example.sanbotapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.qihancloud.opensdk.base.TopBaseActivity;


public class PersonalizacionActivity extends TopBaseActivity {

    private Button botonAceptar;

    private Button botonOmitir;

    private EditText edadRobot;

    private EditText contextoPersonalizacion;

    private int dropdownIndex;

    String generoSeleccionado;

    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalizacion);

        SharedPreferences sharedPrefGenero = this.getSharedPreferences("personalizacionGenero", MODE_PRIVATE);
        SharedPreferences.Editor editorGenero = sharedPrefGenero.edit();

        SharedPreferences sharedPrefEdad = this.getSharedPreferences("personalizacionEdad", MODE_PRIVATE);
        SharedPreferences.Editor editorEdad = sharedPrefEdad.edit();

        SharedPreferences sharedPrefContexto = this.getSharedPreferences("personalizacionContexto", MODE_PRIVATE);
        SharedPreferences.Editor editorContexto = sharedPrefContexto.edit();



        Spinner dropdown = findViewById(R.id.dropdownGenero);
        edadRobot = findViewById(R.id.edadRobot);
        contextoPersonalizacion = findViewById(R.id.textoContexto);

        botonAceptar = findViewById(R.id.botonAceptar);
        botonOmitir = findViewById(R.id.botonOmitir);

        dropdownIndex = sharedPrefGenero.getInt("dropdownIndex", 0);

        //create a list of items for the spinner.
        String[] items = new String[]{"Masculino","Femenino", "Otro"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(PersonalizacionActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        dropdown.setSelection(dropdownIndex);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d("dropdown", items[position]);
                generoSeleccionado = items[position];
                dropdownIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> selectedItemView) {
                Log.d("dropdown", "no he seleccionado nada");
            }

        });

        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                Log.d("Genero a guardar en SP", generoSeleccionado);
                editorGenero.putString("personalizacionGenero", generoSeleccionado);
                editorGenero.apply();
                int agnosRobot = Integer.parseInt(String.valueOf(edadRobot.getText()));
                editorEdad.putInt("personalizacionEdad", agnosRobot);
                editorEdad.apply();
                String contexto = String.valueOf(contextoPersonalizacion.getText());
                editorContexto.putString("personalizacionContexto", contexto);
                editorContexto.apply();
                Intent moduloConversacionalActivity = new Intent(PersonalizacionActivity.this, ModuloConversacional.class);
                startActivity(moduloConversacionalActivity);
                finish();
            }
        });

        botonOmitir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                Intent moduloConversacionalActivity = new Intent(PersonalizacionActivity.this, ModuloConversacional.class);
                startActivity(moduloConversacionalActivity);
                finish();
            }
        });
    }

    @Override
    protected void onMainServiceConnected() {

    }
}
