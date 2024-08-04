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

    private Button botonGuardarCambios;

    private Button botonOmitir;

    private Button botonGrabar;

    private EditText contextoPersonalizacion;

    private String cadena;

    private Spinner dropdownGeneroRobot;

    private Spinner dropdownGrupoEdadRobot;
    private int dropdownIndexGeneroRobot;

    private int dropdownIndexGrupoEdadRobot;

    private String generoSeleccionado;

    private String grupoEdadSeleccionado;

    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo_personalizacion);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);

        SharedPreferences sharedPrefGenero = this.getSharedPreferences("personalizacionGenero", MODE_PRIVATE);
        SharedPreferences.Editor editorGenero = sharedPrefGenero.edit();

        SharedPreferences sharedPrefGrupoEdad = this.getSharedPreferences("personalizacionGrupoEdad", MODE_PRIVATE);
        SharedPreferences.Editor editorGrupoEdad = sharedPrefGrupoEdad.edit();

        SharedPreferences sharedPrefContexto = this.getSharedPreferences("personalizacionContexto", MODE_PRIVATE);
        SharedPreferences.Editor editorContexto = sharedPrefContexto.edit();

        contextoPersonalizacion = findViewById(R.id.textoContexto);

        botonGuardarCambios = findViewById(R.id.botonAceptar);
        botonOmitir = findViewById(R.id.botonOmitir);
        botonGrabar = findViewById(R.id.botonGrabarRespuesta);
        dropdownGeneroRobot = findViewById(R.id.spinnerGeneroRobot);
        dropdownGrupoEdadRobot = findViewById(R.id.spinnerGrupoEdadRobot);

        dropdownIndexGeneroRobot = sharedPrefGenero.getInt("dropdownIndexGeneroRobot", 0);


        //create a list of items for the spinner.
        String[] itemsGeneroRobot = new String[]{"Masculino","Femenino"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapterGeneroRobot = new ArrayAdapter<>(PersonalizacionActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsGeneroRobot);
        //set the spinners adapter to the previously created one.
        dropdownGeneroRobot.setAdapter(adapterGeneroRobot);

        dropdownGeneroRobot.setSelection(dropdownIndexGeneroRobot);

        dropdownGeneroRobot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d("dropdown", itemsGeneroRobot[position]);
                generoSeleccionado = itemsGeneroRobot[position];
                dropdownIndexGeneroRobot = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> selectedItemView) {
                Log.d("dropdown", "no he seleccionado nada");
            }

        });



        dropdownIndexGrupoEdadRobot = sharedPrefGenero.getInt("dropdownIndexGrupoEdadRobot", 0);

        //create a list of items for the spinner.
        String[] itemsGrupoEdadRobot = new String[]{"Niño","Adolescente", "Adulto", "Anciano"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapterGrupoEdadRobot = new ArrayAdapter<>(PersonalizacionActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsGrupoEdadRobot);
        //set the spinners adapter to the previously created one.
        dropdownGrupoEdadRobot.setAdapter(adapterGrupoEdadRobot);

        dropdownGrupoEdadRobot.setSelection(dropdownIndexGeneroRobot);

        dropdownGrupoEdadRobot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d("dropdown", itemsGrupoEdadRobot[position]);
                grupoEdadSeleccionado = itemsGrupoEdadRobot[position];
                dropdownIndexGrupoEdadRobot = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> selectedItemView) {
                Log.d("dropdown", "no he seleccionado nada");
            }

        });





        botonGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                /*
                if(grupoEdadSeleccionado.equals("-")){
                    editorGrupoEdad.putString("personalizacionGrupoEdad", null);
                    editorGrupoEdad.apply();
                }
                else {
                    editorGrupoEdad.putString("personalizacionGrupoEdad", grupoEdadSeleccionado);
                    editorGrupoEdad.apply();
                }

                 */
                if(generoSeleccionado.equals("-")){
                    editorGenero.putString("personalizacionGrupoEdad", null);
                    editorGenero.apply();
                }
                else {
                    editorGenero.putString("personalizacionGenero", generoSeleccionado);
                    editorGenero.apply();
                }
                if(contextoPersonalizacion.getText().equals(null)) {
                    editorContexto.putString("personalizacionContexto", null);
                    editorContexto.apply();
                }
                else {
                    String contexto = String.valueOf(contextoPersonalizacion.getText());
                    editorContexto.putString("personalizacionContexto", contexto);
                    editorContexto.apply();
                }
                Intent moduloConversacionalActivity = new Intent(PersonalizacionActivity.this, ModuloConversacional.class);
                startActivity(moduloConversacionalActivity);
                finish();
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
