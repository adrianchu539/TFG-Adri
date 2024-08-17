package com.example.sanbotapp.activities;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.sanbotapp.R;
import com.example.sanbotapp.modulos.moduloOpenAI.ModuloOpenAIAudioSpeech;
import com.example.sanbotapp.robotControl.SpeechControl;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.unit.SpeechManager;

public class PruebaVocesActivity extends TopBaseActivity {
    private Button botonVoz1;
    private Button botonVoz2;
    private Button botonVoz3;
    private Button botonVoz4;
    private Button botonVoz5;
    private Button botonVoz6;

    private SpeechManager speechManager;
    private SpeechControl speechControl;

    private ModuloOpenAIAudioSpeech moduloOpenAISpeechVoice = new ModuloOpenAIAudioSpeech();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_voces);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);

        speechControl = new SpeechControl(speechManager);

        try {

            botonVoz1 = findViewById(R.id.boton4);
            botonVoz2 = findViewById(R.id.boton1);
            botonVoz3 = findViewById(R.id.boton4);
            botonVoz4 = findViewById(R.id.boton2);
            botonVoz5 = findViewById(R.id.boton6);
            botonVoz6 = findViewById(R.id.boton3);

            botonVoz1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    hablar("nova", "Hola. Soy Sanbot. Espero que hayas" +
                            " disfrutado de esta prueba del módulo conversacional");
                }
            });
            botonVoz2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    hablar("alloy", "Hola. Soy Sanbot. Espero que hayas" +
                            " disfrutado de esta prueba del módulo conversacional");
                }
            });
            botonVoz3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    hablar("onyx", "Hola. Soy Sanbot. Espero que hayas" +
                            " disfrutado de esta prueba del módulo conversacional");
                }
            });
            botonVoz4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    hablar("sanbot", "Hola. Soy Sanbot. Espero que hayas" +
                            " disfrutado de esta prueba del módulo conversacional");
                }
            });
            botonVoz5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    hablar("shimmer", "Hola. Soy Sanbot. Espero que hayas" +
                            " disfrutado de esta prueba del módulo conversacional");
                }
            });
            botonVoz6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    hablar("echo", "Hola. Soy Sanbot. Espero que hayas" +
                            " disfrutado de esta prueba del módulo conversacional");
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // Función para pronunciar frases con la voz de Sanbot o la voz de OpenAI
    private void hablar(String voz, String cadena){
        if(voz.equals("sanbot")){
            speechControl.hablar(cadena);
        }
        else{
            moduloOpenAISpeechVoice.peticionVozOpenAI(cadena, voz);
        }
    }

    @Override
    protected void onMainServiceConnected() {

    }
}
