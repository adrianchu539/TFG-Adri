package com.example.sanbotapp.activities;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sanbotapp.R;
import com.example.sanbotapp.robotControl.SpeechControl;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.unit.SpeechManager;

public class TutorialModuloConversacionalActivity extends TopBaseActivity {

    private TextView textoTutorial;
    private TextView tutorialTitulo;
    private Button botonAtras;
    private Button botonSiguiente;
    private Button botonOmitir;
    private Button botonEmpezar;
    private int pasosTotales = 15;
    private ImageView imagen;
    private SpeechControl speechControl;
    private int paso = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        SpeechManager speechManager;
        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);

        textoTutorial = findViewById(R.id.textoTutorial);
        tutorialTitulo = findViewById(R.id.tutorialTitulo);
        botonAtras = findViewById(R.id.botonAtras);
        botonSiguiente = findViewById(R.id.botonSiguiente);
        botonOmitir = findViewById(R.id.botonOmitir);
        botonEmpezar = findViewById(R.id.botonEmpezar);
        imagen= findViewById(R.id.imagen);

        speechControl = new SpeechControl(speechManager);

        update();

        botonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paso++;
                Log.d("paso", String.valueOf(paso));
                update();
            }
        });
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paso--;
                update();
            }
        });
        botonOmitir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechControl.pararHabla();
                Intent moduloConversacional = new Intent(TutorialModuloConversacionalActivity.this, ModuloConversacionalActivity.class);
                startActivity(moduloConversacional);
                finish();
            }
        });

        botonEmpezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paso++;
                update();
            }
        });
    }

    private void update(){
        switch(paso) {
            case 0:
                Log.d("paso", String.valueOf(paso));
                botonOmitir.setVisibility(View.INVISIBLE);
                imagen.setVisibility(View.INVISIBLE);
                imagen.setVisibility(View.GONE);
                botonAtras.setVisibility(View.INVISIBLE);
                botonSiguiente.setVisibility(View.INVISIBLE);
                botonEmpezar.setVisibility(View.VISIBLE);
                textoTutorial.setVisibility(View.INVISIBLE);
                textoTutorial.setVisibility(View.GONE);
                tutorialTitulo.setText("TUTORIAL");
                break;
            case 1:
                Log.d("paso", String.valueOf(paso));
                botonOmitir.setVisibility(View.VISIBLE);
                botonAtras.setVisibility(View.VISIBLE);
                botonSiguiente.setVisibility(View.VISIBLE);
                botonEmpezar.setVisibility(View.INVISIBLE);
                textoTutorial.setVisibility(View.VISIBLE);
                tutorialTitulo.setText("PASO " + paso + "/" + pasosTotales);
                textoTutorial.setText("Hola. Soy Sanbot. Te doy la bienvenida al módulo conversacional cuyo objetivo es la mejora de la interacción entre los humanos y los robots.");
                speechControl.hablar(textoTutorial.getText().toString());
                break;
            case 2:
                Log.d("paso", String.valueOf(paso));
                tutorialTitulo.setText("PASO " + paso + "/" + pasosTotales);
                textoTutorial.setText("Todavía no sé responder a consultas que requieren datos actuales, por lo que no puedo contestar a preguntas de qué dia es hoy, qué tiempo hizo ayer o similares" +
                        " ya que únicamente guardo datos hasta finales del año 2023." + " Pero no te preocupes, en caso de que no sepa darte la respuesta te lo indicaré de forma clara durante la conversación");
                speechControl.hablar(textoTutorial.getText().toString());
                break;
            case 3:
                Log.d("paso", String.valueOf(paso));
                tutorialTitulo.setText("PASO " + paso + "/" + pasosTotales);
                textoTutorial.setText("En la parte inferior de la pantalla podrás observar el botón HABLAR. Al pulsarlo, haré un sonido de confirmación y " +
                        "mis orejas se encenderán de color verde, lo que significa que te estaré escuchando. Es entonces cuando podrás iniciar la conversación.");
                imagen.setVisibility(View.VISIBLE);
                imagen.setImageResource(R.drawable.senalar_hablar);
                speechControl.hablar(textoTutorial.getText().toString());
                break;
            case 4:
                Log.d("paso", String.valueOf(paso));
                tutorialTitulo.setText("PASO " + paso + "/" + pasosTotales);
                textoTutorial.setText("En el momento en el que dejes de hablar, tu respuesta se enviará. Cuando yo dé mi respuesta, automáticamente volveré a encender" +
                        " mis orejas para escuchar de nuevo tu respuesta y repetir el proceso.");
                imagen.setImageResource(R.drawable.senalar_hablar);
                speechControl.hablar(textoTutorial.getText().toString());
                break;
            case 5:
                Log.d("paso", String.valueOf(paso));
                tutorialTitulo.setText("PASO " + paso + "/" + pasosTotales);
                textoTutorial.setText("Si has dejado de hablar o se produce algún fallo, significa que no he escuchado tu consulta. En este caso," +
                        " vuelve a pulsar el botón hablar para reanudar la conversación.");
                imagen.setImageResource(R.drawable.senalar_hablar);
                speechControl.hablar(textoTutorial.getText().toString());
                break;
            case 6:
                Log.d("paso", String.valueOf(paso));
                tutorialTitulo.setText("PASO " + paso + "/" + pasosTotales);
                textoTutorial.setText("La conversación se irá almacenando y aparecerá en todo momento en el centro de la pantalla");
                imagen.setImageResource(R.drawable.senalar_conversacion);
                speechControl.hablar(textoTutorial.getText().toString());
                break;
            case 7:
                Log.d("paso", String.valueOf(paso));
                tutorialTitulo.setText("PASO " + paso + "/" + pasosTotales);
                textoTutorial.setText("En el lado derecho, de color verde, aparecerán tus mensajes");
                imagen.setImageResource(R.drawable.senalar_conversacion_usuario);
                speechControl.hablar(textoTutorial.getText().toString());
                break;
            case 8:
                Log.d("paso", String.valueOf(paso));
                tutorialTitulo.setText("PASO " + paso + "/" + pasosTotales);
                textoTutorial.setText("Y en el lado izquierdo, de color azul, aparecerán mis mensajes");
                imagen.setImageResource(R.drawable.senalar_conversacion_robot);
                speechControl.hablar(textoTutorial.getText().toString());
                break;
            case 9:
                Log.d("paso", String.valueOf(paso));
                botonAtras.setVisibility(View.VISIBLE);
                tutorialTitulo.setText("PASO " + paso + "/" + pasosTotales);
                textoTutorial.setText("Si quieres ver mensajes anteriores puedes deslizar con tu dedo hacia abajo");
                imagen.setImageResource(R.drawable.senalar_conversacion_robot);
                speechControl.hablar(textoTutorial.getText().toString());
                break;
            case 10:
                Log.d("paso", String.valueOf(paso));
                tutorialTitulo.setText("PASO " + paso + "/" + pasosTotales);
                textoTutorial.setText("En caso de que quieras repetir la última respuesta del robot, podrás pulsar" +
                        " el botón Repetir situado en la parte superior derecha de la pantalla");
                imagen.setImageResource(R.drawable.senalar_repetir);
                speechControl.hablar(textoTutorial.getText().toString());
                break;
            case 11:
                Log.d("paso", String.valueOf(paso));
                tutorialTitulo.setText("PASO " + paso + "/" + pasosTotales);
                textoTutorial.setText("En caso de que quieras detener el habla del robot, podrás pulsar" +
                        " el botón Detener situado en la parte superior derecha de la pantalla");
                imagen.setImageResource(R.drawable.senalar_detener);
                speechControl.hablar(textoTutorial.getText().toString());
                break;
            case 12:
                Log.d("paso", String.valueOf(paso));
                tutorialTitulo.setText("PASO " + paso + "/" + pasosTotales);
                textoTutorial.setText("Pulsando el botón Configuración situado en la parte superior izquierda" +
                        " de la pantalla, podrás cambiar diversas opciones del módulo conversacional");
                imagen.setImageResource(R.drawable.senalar_configuracion);
                speechControl.hablar(textoTutorial.getText().toString());
                break;
            case 13:
                Log.d("paso", String.valueOf(paso));
                tutorialTitulo.setText("PASO " + paso + "/" + pasosTotales);
                textoTutorial.setText("Si quieres iniciar una nueva conversación desde el principio, podrás pulsar" +
                        " el botón Nueva Conversación situado en la parte superior izquierda de la pantalla");
                imagen.setImageResource(R.drawable.senalar_nueva_conversacion);
                speechControl.hablar(textoTutorial.getText().toString());
                break;
            case 14:
                Log.d("paso", String.valueOf(paso));
                botonSiguiente.setVisibility(View.VISIBLE);
                tutorialTitulo.setText("PASO " + paso + "/" + pasosTotales);
                textoTutorial.setText("Por último, si necesitas ver de nuevo este tutorial, pulsar el botón Tutorial" +
                        " situado en la parte superior izquierda de la pantalla.");
                imagen.setImageResource(R.drawable.senalar_tutorial);
                botonOmitir.setText("Omitir");
                botonOmitir.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                speechControl.hablar(textoTutorial.getText().toString());
                break;
            case 15:
                Log.d("paso", String.valueOf(paso));
                botonAtras.setVisibility(View.VISIBLE);
                tutorialTitulo.setText("PASO " + paso + "/" + pasosTotales);
                textoTutorial.setText("Muchas gracias y feliz conversación.");
                botonSiguiente.setVisibility(View.INVISIBLE);
                Drawable done = getContext().getResources().getDrawable(R.drawable.baseline_done_24);
                botonOmitir.setText("Hecho");
                botonOmitir.setCompoundDrawablesWithIntrinsicBounds(done, null, null, null);
                speechControl.hablar(textoTutorial.getText().toString());
                break;
        }
    }

    @Override
    protected void onMainServiceConnected() {

    }

}
