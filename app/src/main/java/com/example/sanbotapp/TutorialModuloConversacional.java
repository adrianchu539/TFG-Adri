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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.motion.MotionUtils;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.unit.SpeechManager;

import java.io.IOException;

public class TutorialModuloConversacional extends TopBaseActivity {

    private TextView textoTutorial;
    private TextView textoPasos;
    private Button botonAtras;
    private Button botonSiguiente;
    private Button botonOmitir;
    private int pasosTotales = 14;
    private ImageView imagen;

    private static int paso = 0;
    private SpeechControl speechControl;
    private SpeechManager speechManager;
    private SpeakOption speakOption;
    private String vozSeleccionada;
    private GestionMediaPlayer gestionMediaPlayer;
    private GestionSharedPreferences gestionSharedPreferences;

    @Override
    public void onResume(){
        super.onResume();
        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        speakOption = new SpeakOption();
        speechControl = new SpeechControl(speechManager, speakOption);
        vozSeleccionada = gestionSharedPreferences.getStringSharedPreferences("vozSeleccionada", "echo").toLowerCase();

        SpeakOption so = new SpeakOption();
        switch(paso){
            case 0:
                botonAtras.setVisibility(View.INVISIBLE);
                textoPasos.setText("PASO " + (paso+1) + "/" + pasosTotales);
                textoTutorial.setText("Hola. Soy Sanbot. Te doy la bienvenida al módulo conversacional cuyo objetivo es la mejora de la interacción entre los humanos y los robots.");
                try {
                    speechControl.gestionHabla(vozSeleccionada, textoTutorial.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 1:
                botonAtras.setVisibility(View.VISIBLE);
                textoPasos.setText("PASO " + (paso+1) + "/" + pasosTotales);
                textoTutorial.setText("Todavía no sé responder a consultas que requieren datos actuales, por lo que no puedo contestar a preguntas de qué dia es hoy, qué tiempo hizo ayer o similares" +
                        " ya que únicamente guardo datos hasta finales del año 2023." + " Pero no te preocupes, en caso de que no sepa darte la respuesta te lo indicaré de forma clara durante la conversación");
                try {
                    speechControl.gestionHabla(vozSeleccionada, textoTutorial.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 2:
                botonAtras.setVisibility(View.VISIBLE);
                textoPasos.setText("PASO " + (paso+1) + "/" + pasosTotales);
                textoTutorial.setText("En la parte inferior de la pantalla podrás observar el botón HABLAR. Al pulsarlo, haré un sonido de confirmación y " +
                        "mis orejas se encenderán de color verde, lo que significa que te estaré escuchando. Es entonces cuando podrás iniciar la conversación.");
                imagen.setImageResource(R.drawable.senalar_hablar);
                try {
                    speechControl.gestionHabla(vozSeleccionada, textoTutorial.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 3:
                botonAtras.setVisibility(View.VISIBLE);
                textoPasos.setText("PASO " + (paso+1) + "/" + pasosTotales);
                textoTutorial.setText("En el momento en el que dejes de hablar, tu respuesta se enviará. Cuando yo dé mi respuesta, automáticamente volveré a encender" +
                        " mis orejas para escuchar de nuevo tu respuesta y repetir el proceso.");
                imagen.setImageResource(R.drawable.senalar_hablar);
                try {
                    speechControl.gestionHabla(vozSeleccionada, textoTutorial.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 4:
                botonAtras.setVisibility(View.VISIBLE);
                textoPasos.setText("PASO " + (paso+1) + "/" + pasosTotales);
                textoTutorial.setText("Si has dejado de hablar o se produce algún fallo, significa que no he escuchado tu consulta. En este caso," +
                        " vuelve a pulsar el botón hablar para reanudar la conversación.");
                imagen.setImageResource(R.drawable.senalar_hablar);
                try {
                    speechControl.gestionHabla(vozSeleccionada, textoTutorial.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 5:
                botonAtras.setVisibility(View.VISIBLE);
                textoPasos.setText("PASO " + (paso+1) + "/" + pasosTotales);
                textoTutorial.setText("La conversación se irá almacenando y aparecerá en todo momento en el centro de la pantalla");
                imagen.setImageResource(R.drawable.senalar_conversacion);
                try {
                    speechControl.gestionHabla(vozSeleccionada, textoTutorial.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 6:
                botonAtras.setVisibility(View.VISIBLE);
                textoPasos.setText("PASO " + (paso+1) + "/" + pasosTotales);
                textoTutorial.setText("En el lado derecho, de color verde, aparecerán tus mensajes");
                imagen.setImageResource(R.drawable.senalar_conversacion_usuario);
                try {
                    speechControl.gestionHabla(vozSeleccionada, textoTutorial.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 7:
                botonAtras.setVisibility(View.VISIBLE);
                textoPasos.setText("PASO " + (paso+1) + "/" + pasosTotales);
                textoTutorial.setText("Y en el lado izquierdo, de color azul, aparecerán mis mensajes");
                imagen.setImageResource(R.drawable.senalar_conversacion_robot);
                try {
                    speechControl.gestionHabla(vozSeleccionada, textoTutorial.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 8:
                botonAtras.setVisibility(View.VISIBLE);
                textoPasos.setText("PASO " + (paso+1) + "/" + pasosTotales);
                textoTutorial.setText("Si quieres ver mensajes anteriores puedes deslizar con tu dedo hacia abajo");
                imagen.setImageResource(R.drawable.senalar_conversacion_robot);
                try {
                    speechControl.gestionHabla(vozSeleccionada, textoTutorial.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 9:
                botonAtras.setVisibility(View.VISIBLE);
                textoPasos.setText("PASO " + (paso+1) + "/" + pasosTotales);
                textoTutorial.setText("En caso de que quieras repetir la última respuesta del robot, podrás pulsar" +
                        " el botón Repetir situado en la parte superior derecha de la pantalla");
                imagen.setImageResource(R.drawable.senalar_repetir);
                try {
                    speechControl.gestionHabla(vozSeleccionada, textoTutorial.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 10:
                botonAtras.setVisibility(View.VISIBLE);
                textoPasos.setText("PASO " + (paso+1) + "/" + pasosTotales);
                textoTutorial.setText("En caso de que quieras detener el habla del robot, podrás pulsar" +
                        " el botón Detener situado en la parte superior derecha de la pantalla");
                imagen.setImageResource(R.drawable.senalar_detener);
                try {
                    speechControl.gestionHabla(vozSeleccionada, textoTutorial.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 11:
                botonAtras.setVisibility(View.VISIBLE);
                textoPasos.setText("PASO " + (paso+1) + "/" + pasosTotales);
                textoTutorial.setText("Pulsando el botón Configuración situado en la parte superior izquierda" +
                        " de la pantalla, podrás cambiar diversas opciones del módulo conversacional");
                imagen.setImageResource(R.drawable.senalar_configuracion);
                try {
                    speechControl.gestionHabla(vozSeleccionada, textoTutorial.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            case 12:
                botonAtras.setVisibility(View.VISIBLE);
                textoPasos.setText("PASO " + (paso+1) + "/" + pasosTotales);
                textoTutorial.setText("Si quieres iniciar una nueva conversación desde el principio, podrás pulsar" +
                        " el botón Nueva Conversación situado en la parte superior izquierda de la pantalla");
                imagen.setImageResource(R.drawable.senalar_nueva_conversacion);
                try {
                    speechControl.gestionHabla(vozSeleccionada, textoTutorial.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            case 13:
                botonAtras.setVisibility(View.VISIBLE);
                textoPasos.setText("PASO " + (paso+1) + "/" + pasosTotales);
                textoTutorial.setText("Por último, si necesitas ver de nuevo este tutorial, pulsar el botón Tutorial" +
                        " situado en la parte superior izquierda de la pantalla.");
                imagen.setImageResource(R.drawable.senalar_tutorial);
                try {
                    speechControl.gestionHabla(vozSeleccionada, textoTutorial.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            case 14:
                botonAtras.setVisibility(View.VISIBLE);
                textoPasos.setText("PASO " + (paso+1) + "/" + pasosTotales);
                textoTutorial.setText("Muchas gracias y feliz conversación.");
                botonSiguiente.setText("Aceptar");
                Drawable done = getContext().getResources().getDrawable(R.drawable.baseline_done_24);
                botonSiguiente.setCompoundDrawablesWithIntrinsicBounds(done, null, null, null);
                try {
                    speechControl.gestionHabla(vozSeleccionada, textoTutorial.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
        botonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(botonSiguiente.getText().equals("Aceptar")){
                    if(vozSeleccionada.equals("sanbot")){
                        speechControl.pararHabla();
                    }
                    else{
                        gestionMediaPlayer.pararMediaPlayer();
                    }
                    Intent moduloConversacional = new Intent(TutorialModuloConversacional.this, com.example.sanbotapp.ModuloConversacional.class);
                    startActivity(moduloConversacional);
                }
            }
        });
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        gestionSharedPreferences = new GestionSharedPreferences(this);

        textoTutorial = findViewById(R.id.textoTutorial);
        textoPasos = findViewById(R.id.TutorialPasos);
        botonAtras = findViewById(R.id.botonAtras);
        botonSiguiente = findViewById(R.id.botonSiguiente);
        botonOmitir = findViewById(R.id.botonOmitir);
        imagen= findViewById(R.id.imagen);

        vozSeleccionada = gestionSharedPreferences.getStringSharedPreferences("vozSeleccionada", "echo").toLowerCase();

        try {

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
            botonOmitir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paso--;
                    Intent moduloConversacional = new Intent(TutorialModuloConversacional.this, ModuloConversacional.class);
                    startActivity(moduloConversacional);
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
