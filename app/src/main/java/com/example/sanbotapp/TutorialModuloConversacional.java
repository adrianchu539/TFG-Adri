package com.example.sanbotapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.function.beans.SpeakOption;

public class TutorialModuloConversacional extends TopBaseActivity {

    private TextView textoTutorial;
    private Button botonAtras;
    private Button botonSiguiente;

    private static int paso = 0;

    @Override
    public void onResume(){
        super.onResume();
        SanbotSpeechControl sanbotSpeechControl = new SanbotSpeechControl();
        SpeakOption so = new SpeakOption();
        switch(paso){
            case 0:
                botonAtras.setVisibility(View.INVISIBLE);
                textoTutorial.setText("Hola. Soy Sanbot. Bienvenido al ....");
                sanbotSpeechControl.hablar(textoTutorial.getText().toString(), so);
                break;
            case 1:
                botonAtras.setVisibility(View.VISIBLE);
                textoTutorial.setText("A continuación...");
                break;
            case 2:
                botonSiguiente.setText("Hecho");
                Drawable done = getContext().getResources().getDrawable(R.drawable.baseline_done_24);
                botonSiguiente.setCompoundDrawablesWithIntrinsicBounds(done, null, null, null);
                Intent moduloConversacional = new Intent(TutorialModuloConversacional.this, com.example.sanbotapp.ModuloConversacional.class);
                startActivity(moduloConversacional);
                break;

        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        SanbotSpeechControl sanbotSpeechControl = new SanbotSpeechControl();

        textoTutorial = findViewById(R.id.textoTutorial);
        botonAtras = findViewById(R.id.botonAtras);
        botonSiguiente = findViewById(R.id.botonSiguiente);

        try {
            textoTutorial.setText("Hola. Soy Sanbot. Bienvenido al ....");
            botonSiguiente.setText("Siguiente");
            Drawable next = getContext().getResources().getDrawable(R.drawable.baseline_arrow_forward_24);
            botonSiguiente.setCompoundDrawablesWithIntrinsicBounds(null, null, next, null);

            botonSiguiente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paso++;
                    Intent tutorialModuloConversacional = new Intent(TutorialModuloConversacional.this, TutorialModuloConversacional.class);
                    startActivity(tutorialModuloConversacional);
                }
            });
            botonAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paso--;
                    Intent tutorialModuloConversacional = new Intent(TutorialModuloConversacional.this, TutorialModuloConversacional.class);
                    startActivity(tutorialModuloConversacional);
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
