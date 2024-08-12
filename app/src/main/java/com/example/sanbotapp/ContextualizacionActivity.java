package com.example.sanbotapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.speech.Grammar;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.interfaces.speech.RecognizeListener;
import com.qihancloud.opensdk.function.unit.interfaces.speech.SpeakListener;

import java.io.IOException;


public class ContextualizacionActivity extends TopBaseActivity {

    private SpeechManager speechManager; //voice, speechRec
    private static String vozSeleccionada;
    private Button botonAceptar;
    private Button botonAtras;
    private Button botonOmitir;
    private Button botonGrabar;
    private EditText contextoPersonalizacion;
    private String cadena;
    private Spinner dropdownVoz;
    private Spinner dropdownGeneroRobot;
    private Spinner dropdownGrupoEdadRobot;
    private int dropdownIndexVoz;
    private int dropdownIndexGeneroRobot;
    private int dropdownIndexGrupoEdadRobot;
    private String generoSeleccionado;
    private String grupoEdadSeleccionado;

    private SpeechControl speechControl;
    public void onResume(){
        super.onResume();
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuConfiguracionActivity = new Intent(ContextualizacionActivity.this, MenuConfiguracion.class);
                startActivity(menuConfiguracionActivity);
                finish();
            }
        });
    }

    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo_contextualizacion);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        SpeakOption speakOption = new SpeakOption();
        speechControl = new SpeechControl(speechManager, speakOption);

        // Creo una sección de almacenamiento local donde se guardará la voz seleccionada del robot
        SharedPreferences sharedPrefVoz = this.getSharedPreferences("vozSeleccionada", MODE_PRIVATE);
        SharedPreferences.Editor editorVoz = sharedPrefVoz.edit();

        // Creo una sección de almacenamiento local donde se guardará el género del robot
        SharedPreferences sharedPrefGenero = this.getSharedPreferences("generoRobotPersonalizacion", MODE_PRIVATE);
        SharedPreferences.Editor editorGenero = sharedPrefGenero.edit();

        // Creo una sección de almacenamiento local donde se guardará el grupo de edad del robot
        SharedPreferences sharedPrefGrupoEdad = this.getSharedPreferences("grupoEdadRobotPersonalizacion", MODE_PRIVATE);
        SharedPreferences.Editor editorGrupoEdad = sharedPrefGrupoEdad.edit();

        // Creo una sección de almacenamiento local donde se guardará el contexto
        SharedPreferences sharedPrefContexto = this.getSharedPreferences("contextoPersonalizacion", MODE_PRIVATE);
        SharedPreferences.Editor editorContexto = sharedPrefContexto.edit();

        dropdownIndexVoz = sharedPrefVoz.getInt("dropdownIndexVoz", 0);
        dropdownIndexGeneroRobot = sharedPrefGenero.getInt("dropdownIndexGeneroRobot", 0);
        dropdownIndexGrupoEdadRobot = sharedPrefGrupoEdad.getInt("dropdownIndexGrupoEdadRobot", 0);

        try {
            dropdownVoz = findViewById(R.id.spinnerVoces);
            contextoPersonalizacion = findViewById(R.id.textoContexto);
            botonAceptar = findViewById(R.id.botonAceptar);
            botonAtras = findViewById(R.id.botonAtras);
            botonOmitir = findViewById(R.id.botonOmitir);
            botonGrabar = findViewById(R.id.botonGrabarRespuesta);
            dropdownGeneroRobot = findViewById(R.id.spinnerGeneroRobot);
            dropdownGrupoEdadRobot = findViewById(R.id.spinnerGrupoEdadRobot);

            // Creo el seleccionador de voces
            String[] items = new String[]{"Sanbot","Alloy", "Echo", "Fable", "Onyx", "Nova", "Shimmer"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ContextualizacionActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
            dropdownVoz.setAdapter(adapter);

            dropdownVoz.setSelection(dropdownIndexVoz);

            dropdownVoz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    vozSeleccionada = items[position];
                    dropdownIndexVoz = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> selectedItemView) {
                    Log.d("dropdown", "no he seleccionado nada");
                }

            });


            // Creo el seleccionador de género de robot
            String[] itemsGeneroRobot = new String[]{"-", "Masculino", "Femenino"};
            ArrayAdapter<String> adapterGeneroRobot = new ArrayAdapter<>(ContextualizacionActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsGeneroRobot);
            dropdownGeneroRobot.setAdapter(adapterGeneroRobot);

            dropdownGeneroRobot.setSelection(dropdownIndexGeneroRobot);

            dropdownGeneroRobot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    generoSeleccionado = itemsGeneroRobot[position];
                    dropdownIndexGeneroRobot = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> selectedItemView) {
                    Log.d("dropdown", "no he seleccionado nada");
                }

            });

            // Creo el seleccionador de grupo de edad del robot
            String[] itemsGrupoEdadRobot = new String[]{"-", "Niño", "Adolescente", "Adulto", "Anciano"};
            ArrayAdapter<String> adapterGrupoEdadRobot = new ArrayAdapter<>(ContextualizacionActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsGrupoEdadRobot);
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

            botonAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("voz", "voy a meter " + vozSeleccionada);
                    editorVoz.putString("vozSeleccionada", vozSeleccionada);
                    editorVoz.putInt("dropdownIndexVoz", dropdownIndexVoz);
                    editorVoz.apply();

                    if (grupoEdadSeleccionado.equals("-")) {
                        editorGrupoEdad.putString("grupoEdadRobotPersonalizacion", null);
                        editorGrupoEdad.putInt("dropdownIndexGrupoEdadRobot", 0);
                        editorGrupoEdad.apply();
                    } else {
                        editorGrupoEdad.putString("grupoEdadRobotPersonalizacion", grupoEdadSeleccionado);
                        editorGrupoEdad.putInt("dropdownIndexGrupoEdadRobot", dropdownIndexGrupoEdadRobot);
                        editorGrupoEdad.apply();
                    }

                    if (generoSeleccionado.equals("-")) {
                        editorGenero.putString("generoRobotPersonalizacion", null);
                        editorGenero.putInt("dropdownIndexGeneroRobot", 0);
                        editorGenero.apply();
                    } else {
                        editorGenero.putString("generoRobotPersonalizacion", generoSeleccionado);
                        editorGenero.putInt("dropdownIndexGeneroRobot", dropdownIndexGeneroRobot);
                        editorGenero.apply();
                    }
                    if (contextoPersonalizacion.getText().equals(null)) {
                        editorContexto.putString("contextoPersonalizacion", null);
                        editorContexto.apply();
                    } else {
                        String contexto = String.valueOf(contextoPersonalizacion.getText());
                        editorContexto.putString("contextoPersonalizacion", contexto);
                        editorContexto.apply();
                    }
                    // Pasamos a la actividad de modulo conversacional
                    Intent moduloConversacionalActivity = new Intent(ContextualizacionActivity.this, ModuloConversacional.class);
                    startActivity(moduloConversacionalActivity);
                    finish();
                }
            });

            botonGrabar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    speechManager.doWakeUp();
                    while(!speechControl.reconocerRespuesta());
                    String respuesta = speechControl.getRespuesta();
                }
            });
            botonOmitir.setOnClickListener(new View.OnClickListener() {
                // Pasamos a la actividad de modulo conversacional
                @Override
                public void onClick(View v) {
                    Intent moduloConversacionalActivity = new Intent(ContextualizacionActivity.this, TutorialModuloConversacional.class);
                    startActivity(moduloConversacionalActivity);
                    finish();
                }
            });
            botonAtras.setOnClickListener(new View.OnClickListener() {
                // Pasamos a la actividad de cuestionario edad
                @Override
                public void onClick(View v) {
                    Intent cuestionarioEdadActivity = new Intent(ContextualizacionActivity.this, CuestionarioEdad.class);
                    startActivity(cuestionarioEdadActivity);
                    finish();
                }
            });
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onMainServiceConnected() {

    }
}
