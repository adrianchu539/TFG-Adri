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
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.speech.Grammar;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.interfaces.speech.RecognizeListener;


public class PersonalizacionActivity extends TopBaseActivity {

    private SpeechManager speechManager; //voice, speechRec

    private Button botonAceptar;

    private Button botonOmitir;

    private Button botonGrabar;

    private EditText edadRobot;

    private EditText contextoPersonalizacion;

    private String cadena;

    private int dropdownIndex;

    String generoSeleccionado;

    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalizacion);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);

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
        botonGrabar = findViewById(R.id.grabarRespuesta);

        dropdownIndex = sharedPrefGenero.getInt("dropdownIndex", 0);

        //create a list of items for the spinner.
        String[] items = new String[]{"Masculino","Femenino"};
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
                Log.d("edadRobot", String.valueOf(edadRobot.getText()));
                if(String.valueOf(edadRobot.getText()).equals("")){
                    editorEdad.putInt("personalizacionEdad", 0);
                    editorEdad.apply();
                    editorContexto.putString("personalizacionContexto", "");
                    editorContexto.apply();
                    Intent moduloConversacionalActivity = new Intent(PersonalizacionActivity.this, ModuloConversacional.class);
                    startActivity(moduloConversacionalActivity);
                    finish();
                }
                else{
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
            }
        });

        botonGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                speechManager.doWakeUp();
                reconocerRespuesta();
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

    public boolean reconocerRespuesta(){
        speechManager.setOnSpeechListener(new RecognizeListener() {
            @Override
            public boolean onRecognizeResult(Grammar grammar) {
                // paso la gramática reconocida a String
                cadena = grammar.getText();
                contextoPersonalizacion.setText(cadena);
                return true;
            }

            @Override
            public void onRecognizeVolume(int i) {
            }

            public void onStartRecognize() {
                //Log.i("Cris", "onStartRecognize: ");
            }

            public void onStopRecognize() {
                //Log.i("Cris", "onStopRecognize: ");
            }

            public void onError(int i, int i1) {
                Log.d("errorReconocimiento", "Ha habido un error en reconocimiento");
            }
        });
        return true;
    }

    @Override
    protected void onMainServiceConnected() {

    }
}
