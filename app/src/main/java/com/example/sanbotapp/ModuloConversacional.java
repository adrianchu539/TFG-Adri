package com.example.sanbotapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.beans.OperationResult;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.speech.Grammar;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.interfaces.speech.RecognizeListener;
import com.qihancloud.opensdk.function.unit.interfaces.speech.SpeakListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModuloConversacional extends TopBaseActivity {

    // Componentes módulo conversacional
    private Button botonAjustes;
    private Button botonNuevaConversacion;
    private Button botonPlayPause;
    private Button botonTutorial;
    private Button botonRepetir;
    private ListView dialogo;
    private Button botonHablar;
    private Button botonHablarTeclado;
    private Button botonEnviarTeclado;
    private EditText textoConsulta;

    // Modulos del robot
    private SpeechManager speechManager;
    private HeadMotionManager headMotionManager;
    private HandMotionManager handMotionManager;
    private SystemManager systemManager;
    private HardWareManager hardWareManager;


    // Gestión emociones robot

    private EmotionsType currentEmotion;
    private String emociones[] = {"ÉXTASIS", "ALEGRÍA", "SERENIDAD", "ADMIRACIÓN", "CONFIANZA", "APROBACIÓN",
            "TERROR", "MIEDO", "TEMOR", "ASOMBRO", "SORPRESA", "DISTRACCIÓN", "PENA", "TRISTEZA", "MELANCOLÍA",
            "AVERSIÓN", "ASCO", "ABURRIMIENTO", "FURIA", "IRA", "ENFADO", "VIGILANCIA", "ANTICIPACIÓN", "INTERÉS", "OPTIMISMO",
            "AMOR", "SUMISIÓN", "SUSTO", "DECEPCIÓN", "REMORDIMIENTO", "DESPRECIO", "AGRESIVIDAD", "ESPERANZA", "CULPA", "CURIOSIDAD",
            "DESESPERACIÓN", "INCREDULIDAD", "ENVIDIA", "CINISMO", "ORGULLO", "ANSIEDAD", "DELEITE", "SENTIMENTALISMO", "VERGÜENZA",
            "INDIGNACIÓN", "PESIMISMO", "MORBOSIDAD", "DOMINANCIA"};

    // Gestión MediaPlayer
    private static MediaPlayer mediaPlayer = new MediaPlayer();

    // Variables usadas en el modulo

    private String consultaChatGPT; // Consulta realizada por el usuario
    private static String respuestaGPT; // Respuesta dada a la consulta realizada por el usuario
    private static byte[] respuestaGPTVoz;
    private String vozSeleccionada; // Voz seleccionada por el usuario
    private String nombreUsuario; // Nombre del usuario
    private int edadUsuario; // Edad del usuario
    private String generoRobot;
    private String grupoEdadRobot;
    private String contexto;
    private boolean consultaRobot = false; // Variable para consultas internas del robot
    private boolean consultaPeliculas = false; // Variable para consultas API de películas
    private boolean conversacionAutomatica = true; // Variable para indicar si la conversación está en modo automático
    private boolean modoTeclado; // Variable para indicar si la conversación está en modo automático
    private boolean personalizacionActivada; // Variable para indicar si la conversación está en modo automático
    private boolean contextualizacionActivada;
    private boolean interpretacionEmocionalActivada;
    private boolean contextoVacio;

    // Lista de variables necesarias para el envío de requests en la API de ChatGPT

    private Map<String, String> roleSystem = new HashMap<>();
    private List<Map<String, String>> messages = new ArrayList<>();

    private boolean forzarParada = false;

    private String contentConversacion = "quiero que mantengamos una conversación";

    private String contentInterpretacionEmocional = "en cada respuesta que te envíe quiero que me envíes al principio de tu respuesta entre corchetes" +
            "un número o varios entre paréntesis en función de la emoción que transmiten mis respuestas: 1 éxtasis, 2 alegría, 3 serenidad, 4 admiración, 5 confianza " +
            "6 aceptación, 7 terror, 8 miedo, 9 temor, 10 asombro, 11 sorpresa, 12 distracción, 13 aflicción, 14 tristeza, 15 melancolía, 16 aversión, 17 asco, 18 aburrimiento," +
            "19 furia, 20 ira, 21 enfado, 22 vigilancia, 23 anticipación, 24 interés, 25 optimismo, 26 amor, 27 sumisión, 28 susto, 29 decepción, 30 remordimiento, 31 desprecio, 32 agresividad," +
            "33 esperanza, 34 culpa, 35 curiosidad, 36 desesperación, 37 incredulidad, 38 envidia, 39 cinismo, 40 orgullo, 41 ansiedad, 42 deleite, 43 sentimentalismo, 44 vergüenza, 45 indignación, " +
            "46 pesimismo, 47 morbosidad y 48 dominancia, añadas un guión y un número en función de la emoción que quieres intentar transmitir con tu respuesta " +
            "siguiendo el mismo código numérico. Es decir seguirá el siguiente patrón: [(<número o números de emoción o emociones separados por guiones de mi respuesta>)" +
            "/ (<número o números de emoción o emociones de la respuesta que quieres transmitir>)] + tu respuesta a la conversación." + "Quiero que reconduzcas la conversación en función de la emoción que interpretes y " +
            "que trates de empatizar lo máximo posible con mis respuestas. Aquí te dejo algunos ejemplos: Si te digo algo triste, tú puedes tratar de animarme siendo optimista y mostrarás curiosidad por saber lo que me pasa, " +
            "así que [(14)/(25-35)], si mi respuesta es de enfado, tú tratarás de calmarme y mostrarás curiosidad por saber qué me ocurre, asi que [(21)/(3-35)], si te digo que me gusta alguien" +
            "mi respuesta será de amor y vergüenza, y tú puedes sentir sorpresa, así que [(26-44)/(11)]. ";


    private static SpeakOption so = new SpeakOption();

    // ------------------- PRUEBAS CHAT -----------------




    //to scroll the list view to bottom on data change

    private ChatArrayAdapter chatArrayAdapter;

    private List<ChatMessage> conversacion;


    @Override
    public void onResume() {
        super.onResume();


        // Recuperación de las variables guardadas en el almacenamiento local

        vozSeleccionada = getStringSharedPreferences("vozSeleccionada", "sanbot").toLowerCase();
        nombreUsuario = getStringSharedPreferences("nombreUsuario", null);
        edadUsuario = getIntSharedPreferences("edadUsuario", 0);
        generoRobot = getStringSharedPreferences("generoRobotPersonalizacion", null);
        grupoEdadRobot = getStringSharedPreferences("grupoEdadRobotPersonalizacion", null);
        contexto = getStringSharedPreferences("contextoPersonalizacion", null);
        conversacionAutomatica = getBooleanSharedPreferences("conversacionAutomatica", true);
        modoTeclado = getBooleanSharedPreferences("modoTeclado", false);

        personalizacionActivada = getBooleanSharedPreferences("personalizacionActivada", false);
        contextualizacionActivada = getBooleanSharedPreferences("contextualizacionActivada", false);
        interpretacionEmocionalActivada = getBooleanSharedPreferences("interpretacionEmocionalActivada", false);
        contextoVacio = getBooleanSharedPreferences("contextoVacio", false);

        // ------------------- PRUEBAS CHAT -----------------
        dialogo.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);
        if(chatArrayAdapter.isEmpty()){
            for(ChatMessage cm : conversacion){
                Log.d("cm", cm.toString());
                chatArrayAdapter.add(cm);
            }
            Log.d("ca", String.valueOf(chatArrayAdapter.getCount() - 1));
            dialogo.setSelection(chatArrayAdapter.getCount() - 1);
        }
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.d("ca", String.valueOf(chatArrayAdapter.getCount() - 1));
                dialogo.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
        dialogo.setAdapter(chatArrayAdapter);

        if(modoTeclado){
            botonEnviarTeclado.setVisibility(View.VISIBLE);
            botonHablarTeclado.setVisibility(View.VISIBLE);
            textoConsulta.setVisibility(View.VISIBLE);
            botonHablar.setVisibility(View.INVISIBLE);
        }
        else{
            botonEnviarTeclado.setVisibility(View.INVISIBLE);
            botonHablarTeclado.setVisibility(View.INVISIBLE);
            textoConsulta.setVisibility(View.INVISIBLE);
            botonHablar.setVisibility(View.VISIBLE);
        }
        if(nombreUsuario == null || edadUsuario==0){
            personalizacionActivada = false;
            Log.d("personalizacionActivada", "personalizacionActivada es " + personalizacionActivada);
        }
        else{
            personalizacionActivada = true;
            Log.d("personalizacionActivada", "personalizacionActivada es " + personalizacionActivada);
        }
        if(generoRobot == null || grupoEdadRobot == null){
            contextualizacionActivada = false;
            Log.d("contextualizacionActiva", "contextualizacionActivada es " + contextualizacionActivada);
        }
        else{
            contextualizacionActivada = true;
            Log.d("contextualizacionActiva", "contextualizacionActivada es " + contextualizacionActivada);
        }
        if(contexto == null || contexto.equals("")){
            contextoVacio = true;
            Log.d("contextoVacio", "contextoVacio es " + contextoVacio);
        }
        else{
            contextoVacio = false;
            Log.d("contextoVacio", "contextoVacio es " + contextoVacio);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modulo_conversacional_prueba);

        conversacion = new ArrayList<>();

        botonAjustes = findViewById(R.id.botonAjustes);
        botonTutorial = findViewById(R.id.botonTutorial);
        botonPlayPause = findViewById(R.id.botonDetener);
        botonRepetir = findViewById(R.id.botonRepetir);
        //dialogoUsuario = findViewById(R.id.burbujaDialogoUsuario); // LISTVIEW ??
        dialogo = findViewById(R.id.burbujaDialogo); // LISTVIEW ??
        botonHablar = findViewById(R.id.botonHablar);
        botonHablarTeclado = findViewById(R.id.botonHablarTeclado);
        botonEnviarTeclado = findViewById(R.id.botonEnviarTeclado);
        textoConsulta = findViewById(R.id.textoConsultaTeclado);
        botonNuevaConversacion = findViewById(R.id.botonNuevaConversacion);

        if(modoTeclado){
            botonEnviarTeclado.setVisibility(View.VISIBLE);
            botonHablarTeclado.setVisibility(View.VISIBLE);
            textoConsulta.setVisibility(View.VISIBLE);
            botonHablar.setVisibility(View.INVISIBLE);
        }
        else{
            botonEnviarTeclado.setVisibility(View.INVISIBLE);
            botonHablarTeclado.setVisibility(View.INVISIBLE);
            textoConsulta.setVisibility(View.INVISIBLE);
            botonHablar.setVisibility(View.VISIBLE);
        }

        dialogo.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);
        if(chatArrayAdapter.isEmpty()){
            for(ChatMessage cm : conversacion){
                Log.d("cm", cm.toString());
                chatArrayAdapter.add(cm);
            }
            Log.d("ca", String.valueOf(chatArrayAdapter.getCount() - 1));
            dialogo.setSelection(chatArrayAdapter.getCount() - 1);
        }
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.d("ca", String.valueOf(chatArrayAdapter.getCount() - 1));
                dialogo.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
        dialogo.setAdapter(chatArrayAdapter);

        // Inicialización de las unidades del robot

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);

        // Velocidad y entonación de la voz propia del robot (!!)

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);

        /*

        botonPlayPause.setText("Pausar");
        Drawable pause = getContext().getResources().getDrawable(R.drawable.baseline_pause_24);
        botonPlayPause.setCompoundDrawablesWithIntrinsicBounds(pause, null, null, null);

         */

        // Velocidad y entonación de la voz propia del robot

        // POR CAMBIAR !!
        so.setSpeed(60);
        so.setIntonation(50);


        // Obtención de las variables guardadas en el almacenamiento local

        vozSeleccionada = getStringSharedPreferences("vozSeleccionada", "sanbot").toLowerCase();
        nombreUsuario = getStringSharedPreferences("nombreUsuario", null);
        edadUsuario = getIntSharedPreferences("edadUsuario", 0);
        generoRobot = getStringSharedPreferences("generoRobotPersonalizacion", null);
        grupoEdadRobot = getStringSharedPreferences("grupoEdadRobotPersonalizacion", null);
        contexto = getStringSharedPreferences("contextoPersonalizacion", null);
        conversacionAutomatica = getBooleanSharedPreferences("conversacionAutomatica", true);
        personalizacionActivada = getBooleanSharedPreferences("personalizacionActivada", false);
        contextualizacionActivada = getBooleanSharedPreferences("contextualizacionActivada", false);
        interpretacionEmocionalActivada = getBooleanSharedPreferences("interpretacionEmocionalActivada", false);
        contextoVacio = getBooleanSharedPreferences("personalizacionActivada", false);
        modoTeclado = getBooleanSharedPreferences("modoTeclado", false);


        if(nombreUsuario == null || edadUsuario==0){
            personalizacionActivada = false;
            Log.d("personalizacionActivada", "personalizacionActivada es " + personalizacionActivada);
        }
        else{
            personalizacionActivada = true;
            Log.d("personalizacionActivada", "personalizacionActivada es " + personalizacionActivada);
        }
        if(generoRobot == null || grupoEdadRobot == null){
            contextualizacionActivada = false;
            Log.d("contextualizacionActiva", "contextualizacionActivada es " + contextualizacionActivada);
        }
        else{
            contextualizacionActivada = true;
            Log.d("contextualizacionActiva", "contextualizacionActivada es " + contextualizacionActivada);
        }
        if(contexto == null){
            contextoVacio = true;
            Log.d("contextoVacio", "contextoVacio es " + contextoVacio);
        }
        else{
            contextoVacio = false;
            Log.d("contextoVacio", "contextoVacio es " + contextoVacio);
        }

        String contentPersonalizacion = "También quiero que a veces me llames por mi nombre que es " + nombreUsuario + " y " +
                "que adaptes la conversación teniendo en cuenta que mi edad es de " + edadUsuario + " años";


        String contentContextualizacionSinContexto = "Además quiero que actúes como que tu genero es " + generoRobot + ", que tienes " + grupoEdadRobot;

        String contentContextualizacionConContexto = "Además quiero que actúes como que tu genero es " + generoRobot + ", que tienes " + grupoEdadRobot + " y además " + contexto;

        // ROLESYSTEM API OPENAI

        roleSystem.put("role", "system");

        // PERSONALIZACIÓN + CONVERSACIÓN + INTERPRETACIÓN EMOCIONAL + CONTEXTUALIZACIÓN CON CONTEXTO

        if(personalizacionActivada && interpretacionEmocionalActivada && contextualizacionActivada && !contextoVacio){
            roleSystem.put("content", contentConversacion + "," + contentPersonalizacion + contentInterpretacionEmocional + contentContextualizacionConContexto);
        }
        // PERSONALIZACIÓN + CONVERSACIÓN + INTERPRETACIÓN EMOCIONAL + CONTEXTUALIZACIÓN SIN CONTEXTO
        else if(personalizacionActivada && interpretacionEmocionalActivada && contextualizacionActivada && contextoVacio){
            roleSystem.put("content", contentConversacion + "," + contentPersonalizacion + contentInterpretacionEmocional + contentContextualizacionSinContexto);
        }
        // PERSONALIZACIÓN + CONVERSACIÓN + INTERPRETACIÓN EMOCIONAL
        else if(personalizacionActivada && interpretacionEmocionalActivada && !contextualizacionActivada){
            roleSystem.put("content", contentConversacion + "," + contentPersonalizacion + contentInterpretacionEmocional);
        }
        // PERSONALIZACIÓN + CONVERSACIÓN
        else if(personalizacionActivada && !interpretacionEmocionalActivada){
            roleSystem.put("content", contentPersonalizacion);
        }
        // CONVERSACIÓN
        else{
            roleSystem.put("content", contentConversacion);
        }

        messages.add(roleSystem);


        try {

            botonNuevaConversacion.setOnClickListener(new View.OnClickListener() {
                // Al pulsarlo muestra la pantalla de ajustes
                @Override
                public void onClick (View v){
                    roleSystem.clear();
                    // ROLESYSTEM API OPENAI

                    roleSystem.put("role", "system");

                    // PERSONALIZACIÓN + CONVERSACIÓN + INTERPRETACIÓN EMOCIONAL + CONTEXTUALIZACIÓN CON CONTEXTO

                    if(personalizacionActivada && interpretacionEmocionalActivada && contextualizacionActivada && !contextoVacio){
                        roleSystem.put("content", contentConversacion + "," + contentPersonalizacion + contentInterpretacionEmocional + contentContextualizacionConContexto);
                    }
                    // PERSONALIZACIÓN + CONVERSACIÓN + INTERPRETACIÓN EMOCIONAL + CONTEXTUALIZACIÓN SIN CONTEXTO
                    else if(personalizacionActivada && interpretacionEmocionalActivada && contextualizacionActivada && contextoVacio){
                        roleSystem.put("content", contentConversacion + "," + contentPersonalizacion + contentInterpretacionEmocional + contentContextualizacionSinContexto);
                    }
                    // PERSONALIZACIÓN + CONVERSACIÓN + INTERPRETACIÓN EMOCIONAL
                    else if(personalizacionActivada && interpretacionEmocionalActivada && !contextualizacionActivada){
                        roleSystem.put("content", contentConversacion + "," + contentPersonalizacion + contentInterpretacionEmocional);
                    }
                    // PERSONALIZACIÓN + CONVERSACIÓN
                    else if(personalizacionActivada && !interpretacionEmocionalActivada){
                        roleSystem.put("content", contentPersonalizacion);
                    }
                    // CONVERSACIÓN
                    else{
                        roleSystem.put("content", contentConversacion);
                    }

                    messages.clear();
                    messages.add(roleSystem);
                    chatArrayAdapter.clear();
                    dialogo.setAdapter(chatArrayAdapter);
                }
            });

            // Gestión de los speakers del robot mediante el módulo SpeechManager
            speechManager.setOnSpeechListener(new SpeakListener(){

                // Acción que se ejecuta cuando el robot termina de hablar
                @Override
                public void onSpeakFinish() {
                    // Si está en modo conversación automática
                    if(conversacionAutomatica && !forzarParada){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        // "Pulsa" el botón hablar
                        botonHablar.performClick();
                    }
                    forzarParada = false;
                }

                @Override
                public void onSpeakProgress(int i) {
                    // ...
                }
            });

            // Gestión de acciones del mediaPlayer
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                // Acción que se ejecuta al terminar la reproducción el mediaPlayer
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    // Si está en modo conversación automática
                    if(conversacionAutomatica && !forzarParada){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        // "Pulsa" el botón hablar
                        botonHablar.performClick();
                    }
                    forzarParada = false;
                }
            });

            // Gestión de la pulsación del botón de ajustes
            botonTutorial.setOnClickListener(new View.OnClickListener() {
                // Al pulsarlo muestra la pantalla de ajustes
                @Override
                public void onClick (View v){
                    Intent menuConfiguracionActivity = new Intent(ModuloConversacional.this, TutorialModuloConversacional.class);
                    startActivity(menuConfiguracionActivity);
                }
            });
            // Gestión de la pulsación del botón de ajustes
            botonAjustes.setOnClickListener(new View.OnClickListener() {
                // Al pulsarlo muestra la pantalla de ajustes
                @Override
                public void onClick (View v){
                    Intent menuConfiguracionActivity = new Intent(ModuloConversacional.this, MenuConfiguracion.class);
                    startActivity(menuConfiguracionActivity);
                }
            });

            // Gestión de la pulsación del botón de silenciar
            botonPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {

                    // Si el robot está hablando (voz Sanbot) se pausa
                    // sino, se reanuda
                    if (robotHablando(speechManager)) {
                        try {
                            //gestionVoz(vozSeleccionada, AccionReproduccionVoz.PAUSAR);
                            gestionVoz(vozSeleccionada, AccionReproduccionVoz.DETENER);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        try {
                            //gestionVoz(vozSeleccionada, AccionReproduccionVoz.REANUDAR);
                            gestionVoz(vozSeleccionada, AccionReproduccionVoz.DETENER);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // Si el mediaPlayer está reproduciéndose (voz OpenAI) se pausa,
                    // sino, se reanuda
                    if (mediaPlayer.isPlaying()) {
                        try {
                            //gestionVoz(vozSeleccionada, AccionReproduccionVoz.PAUSAR);
                            gestionVoz(vozSeleccionada, AccionReproduccionVoz.DETENER);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            //gestionVoz(vozSeleccionada, AccionReproduccionVoz.REANUDAR);
                            gestionVoz(vozSeleccionada, AccionReproduccionVoz.DETENER);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

            // Gestión de la pulsación del botón de hablar
            botonHablar.setOnClickListener(new View.OnClickListener() {
                // Al pulsarlo se empieza a escuchar al usuario
                // y se interpreta su consulta hablada
                @Override
                public void onClick (View v){
                    registrarConsulta();
                }
            });

            // Gestión de la pulsación del botón de hablar
            botonHablarTeclado.setOnClickListener(new View.OnClickListener() {
                // Al pulsarlo se empieza a escuchar al usuario
                // y se interpreta su consulta hablada
                @Override
                public void onClick (View v){
                    registrarConsulta();
                }
            });

            // Gestión de la pulsación del botón de enviar
            botonEnviarTeclado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){

                // Si la conversación no es automática, se obtiene
                // lo que hay en el texto consulta
                if(!conversacionAutomatica){
                    consultaChatGPT = textoConsulta.getText().toString();
                }

                enviarConsulta();

                }
            });

            // Gestión de la pulsación del botón repetir

            botonRepetir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    try {
                        gestionVoz(vozSeleccionada, AccionReproduccionVoz.REPETIR);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean reconocerRespuesta(){

        // Gestión del reconocimiento de la voz mediante el módulo SpeechManager
        speechManager.setOnSpeechListener(new RecognizeListener() {

            // Intercepta el diálogo hablado por el usuario
            @Override
            public boolean onRecognizeResult(Grammar grammar) {

                Log.d("prueba", "reconociendo consulta...");


                String cadenaReconocida = grammar.getText();

                cadenaReconocida = capitalizeCadena(cadenaReconocida);

                consultaRobot = false;
                consultaPeliculas = false;

                // Consultas derivadas a las acciones internas del robot
                if(cadenaReconocida.startsWith("Robot")){
                    consultaRobot = true;
                }
                // Consultas derivadas a la API de películas
                else if(cadenaReconocida.startsWith("Películas")){
                    consultaPeliculas = true;
                }
                // Consultas derivadas a la API de OpenAI
                else{
                    consultaChatGPT = cadenaReconocida;
                }

                // Mostramos la cadena reconocida en el EditText de la vista
                textoConsulta.setText(cadenaReconocida);

                // Si la conversación está en modo automático, se realizará la
                // acción de pulsar el botón de enviar a no ser de que el usuario
                // indique que quiere terminar la conversación
                if(conversacionAutomatica){
                    Log.d("prueba", "es conversacion automatica");
                    if(!consultaChatGPT.toLowerCase().equals("fin")) {
                        Log.d("prueba", "no es fin");
                        Log.d("prueba", "enviando consulta...");
                        enviarConsulta();
                    }
                }
                return true;
            }

            // Intercepta el volumen hablado por el usuario
            @Override
            public void onRecognizeVolume(int i) {
                // ...
            }

            public void onStartRecognize() {
                // ...
            }

            public void onStopRecognize() {
                // ...
            }

            public void onError(int i, int i1) {
                // ...
            }
        });
        return true;
    }

    // Función utilizada para cambiar la expresión facial del robot
    // por alguna de las emociones definidas en el sistema
    public void cambiarEmocion(EmotionsType emotion) {
        currentEmotion = emotion;
        systemManager.showEmotion(currentEmotion);
    }

    // ------------------ voy por aqui -------------------

    public void registrarConsulta() {

        Log.d("prueba", "registrando consulta...");

        // Vacío la consulta de ChatGPT
        consultaChatGPT = "";
        textoConsulta.setText("");


        // El robot se pone en modo escucha
        speechManager.doWakeUp();

        // Interpreto lo que dice el usuario
        reconocerRespuesta();
    }

    // Función en la que se le pasa la respuesta del ChatGPT y la voz con la que el usuario
    // desea que la reproduzca
    public static void APIChatGPTVoz(String respuesta, String voz) throws IOException {

        new Thread(new Runnable() {
            public void run() {

                // ----------- DATOS PARA REALIZAR REQUESTS HTTP -------------

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                final OkHttpClient client = new OkHttpClient();

                // ----------- DATOS PARA REALIZAR PETICIÓN A LA API DE OPENAI ---------
                JSONObject request = new JSONObject();
                try{
                    request.put("model", "tts-1");
                    request.put("input", respuesta);
                    request.put("voice", voz);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("respuestaJSON", request.toString());

                RequestBody peticion = RequestBody.create(
                        MediaType.parse("application/json"), String.valueOf(request));

                Log.d("requestBody", peticion.toString());


                Request requestOpenAI = new Request.Builder()
                        .url("https://api.openai.com/v1/audio/speech")
                        .post(peticion)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer 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")
                        .build();

                Log.d("requestBody", requestOpenAI.toString());

                try (Response response = client.newCall(requestOpenAI).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    // String respuesta = response.body().string();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        respuestaGPTVoz = response.body().bytes();
                        reproducirMediaPlayer();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();


    }

    public void APIChatGPT(String pregunta) throws IOException, InterruptedException {

        new Thread(new Runnable() {
            public void run() {

                // ----------- DATOS PARA REALIZAR REQUESTS HTTP -------------

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                final OkHttpClient client = new OkHttpClient();

                // ----------- DATOS PARA REALIZAR PETICIÓN A LA API DE OPENAI ---------

                Map<String, String> roleUser = new HashMap<>();
                roleUser.put("role", "user");
                roleUser.put("content", pregunta);

                messages.add(roleUser);

                JSONArray jsonArray = new JSONArray();

                for (Map<String, String> message : messages) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("role", message.get("role"));
                        jsonObject.put("content", message.get("content"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(jsonObject);
                }

                JSONObject request = new JSONObject();

                try{
                    request.put("model", "gpt-4o-mini");
                    request.put("messages", jsonArray);
                    request.put("max_tokens", 800);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("respuestaJSON", request.toString());

                RequestBody peticion = RequestBody.create(
                        MediaType.parse("application/json"), String.valueOf(request));


                Request requestOpenAI = new Request.Builder()
                        .url("https://api.openai.com/v1/chat/completions")
                        .post(peticion)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer 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")
                        .build();

                try (Response response = client.newCall(requestOpenAI).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    String respuesta = response.body().string();
                    Log.d("Response", "He recibido:");
                    Log.d("Response", respuesta);
                    JSONObject res = new JSONObject(respuesta);
                    JSONArray choices = res.getJSONArray("choices");
                    Log.d("Response", "Choices se compone de " + choices);
                    String res2 = null;
                    for (int i = 0; i < choices.length(); i++) {
                        try {
                            JSONObject m = choices.getJSONObject(i);
                            Log.d("Response", "Mensajes se compone de " + m);
                            // Pulling items from the array
                            res2 = m.getString("message");
                        } catch (JSONException e) {
                            // Oops
                        }
                    }
                    JSONObject mess = new JSONObject(res2);
                    Log.d("Response", "Messages es " + mess);
                    String r = mess.getString("content");
                    Log.d("Response", "La respuesta es " + r);

                    Map<String, String> roleAssistant = new HashMap<>();
                    roleAssistant.put("role", "assistant");
                    roleAssistant.put("content", r);
                    messages.add(roleAssistant);

                    respuestaGPT = r;

                    // -------------- POR REVISAR --------------
                    List<Integer> codigoEmocionesRobot = new ArrayList<Integer>();
                    List<Integer> codigoEmocionesUsuario = new ArrayList<Integer>();
                    ArrayList<String> emocionesRobot = new ArrayList<String>();
                    ArrayList<String> emocionesUsuario = new ArrayList<String>();

                    if(interpretacionEmocionalActivada) {
                        String[] cadena = separarEmociones(r);

                        sentimientosUsuario(cadena[0]);

                        sentimientosRobot(cadena[1]);
                    }
                    //-------
                    dialogo.post(new Runnable() {
                        public void run() {
                            if(interpretacionEmocionalActivada) {

                                for (int i = 0; i < codigoEmocionesRobot.size(); i++) {
                                    emocionesRobot.add(" " + emociones[codigoEmocionesRobot.get(i) - 1]);
                                }
                                for (int i = 0; i < codigoEmocionesUsuario.size(); i++) {
                                    emocionesUsuario.add(emociones[codigoEmocionesUsuario.get(i) - 1]);
                                }
                            }


                            //dialogo.setVisibility(View.VISIBLE);

                            // DEBUG!!
                            //dialogoRobot.setText(respuestaGPT);
                            chatArrayAdapter.add(new ChatMessage(true, respuestaGPT));
                            conversacion.add(new ChatMessage(true, respuestaGPT));
                            //dialogoRobot.setText(respuestaGPT + "\nSENTIMIENTO RECONOCIDO POR EL ROBOT:" +
                            //       emocionesUsuario + "\nSENTIMIENTO QUE TRANSMITE EL ROBOT:" + emocionesRobot);
                        }
                    });
                    hablar(vozSeleccionada, respuestaGPT);
                    Thread.sleep(5000);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();


    }

    public void peliculasAPI(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/movie/popular?language=es-ES&page=1")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZjRhZTM4MjY5ZWEzODY2Yzc4MjcyZTMzNDc1ZTQwNiIsInN1YiI6IjY2MDMzNDhjZDM4YjU4MDE3ZDFiNzExMiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.sGinrHt3C4Ko683tmaYIHZNeUgK87Vg8FSmJfiwvyRI")
                .header("accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            System.out.println("Primera frase a decir");
            hablar(vozSeleccionada,  "Hola, las novedades de peliculas son las siguientes");
            ArrayList<String> nombresPeliculas = new ArrayList<String>();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONArray results = new JSONArray(jsonObject.getString("results"));
                for(int i=0; i<results.length(); i++){
                    JSONObject pelicula = results.getJSONObject(i);
                    nombresPeliculas.add(pelicula.getString("title"));
                }

                for(String nP: nombresPeliculas){
                    System.out.println("Voy a decir la pelicula " + nP);
                    hablar(vozSeleccionada, nP + ",");
                    Thread.sleep(3000);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String capitalizeCadena(String c){
        if (c.length() > 0) return c.substring(0,1).toUpperCase() + c.substring(1);
        else return "";
    }

    public String getStringSharedPreferences(String nombreSharedPreferences, String defaultValue){
        SharedPreferences sp = this.getSharedPreferences(nombreSharedPreferences, MODE_PRIVATE);
        Log.d("getStringPreferences", "el valor de " + nombreSharedPreferences + " es " + sp.getString(nombreSharedPreferences, defaultValue));
        return sp.getString(nombreSharedPreferences, defaultValue);
    }
    private int getIntSharedPreferences(String nombreSharedPreferences, int defaultValue){
        SharedPreferences sp = this.getSharedPreferences(nombreSharedPreferences, MODE_PRIVATE);
        Log.d("getIntPreferences", "el valor de " + nombreSharedPreferences + " es " + sp.getInt(nombreSharedPreferences, defaultValue));
        return sp.getInt(nombreSharedPreferences, defaultValue);
    }
    private boolean getBooleanSharedPreferences(String nombreSharedPreferences, boolean defaultValue){
        SharedPreferences sp = this.getSharedPreferences(nombreSharedPreferences, MODE_PRIVATE);
        Log.d("getBooleanPreferences", "el valor de " + nombreSharedPreferences + " es " + sp.getBoolean(nombreSharedPreferences, defaultValue));
        return sp.getBoolean(nombreSharedPreferences, defaultValue);
    }

    public void gestionVoz(String voz, AccionReproduccionVoz accionVoz) throws IOException {
        if(voz.equals("sanbot")){
            switch (accionVoz) {
                case DETENER:
                    Log.d("Le estoy dando", "robot hablando, intentando callar");
                    // Se silencia
                    forzarParada=true;
                    speechManager.stopSpeak();
                    break;
                case REPETIR:
                    forzarParada=true;
                    hablar("sanbot", respuestaGPT);
                    break;
            }
        }
        else{
            switch (accionVoz) {
                case DETENER:
                    Log.d("Le estoy dando", "mediaplauer habladno, intentando parar");
                    // Se detiene
                    forzarParada = true;
                    mediaPlayer.stop();
                    break;
                case REPETIR:
                    forzarParada=true;
                    reproducirMediaPlayer();
            }
        }
    }

    private enum AccionReproduccionVoz {
        DETENER,
        REPETIR
    }

    private boolean robotHablando(SpeechManager sm){
        OperationResult or = sm.isSpeaking();
        if (or.getResult().equals("1")) {
            return true;
        }
        else{
            return false;
        }
    }
    private String diaYHora(){
        Calendar cal = Calendar.getInstance();
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        int mes = cal.get(Calendar.MONTH);
        int agno = cal.get(Calendar.YEAR);
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int minutos = cal.get(Calendar.MINUTE);
        int segundos = cal.get(Calendar.SECOND);
        return "Hoy es " + dia + " del " + mes + " de " + agno + " y son las " + hora + minutos + segundos;
    }

    private String[] separarEmociones(String respuesta){
        // TRATANDO DE SEPARAR LA STRING
        // Respuesta sin valoración emocional
        respuestaGPT = respuesta.substring(respuesta.indexOf("]")+1, respuesta.length());
        Log.d("Respuesta GPT", respuestaGPT);

        // Apartamos la valoración emocional para utilizarla más tarde
        String valoracionEmocional = respuesta.substring(0, respuesta.indexOf("]")+1);
        Log.d("valoracion emocional", valoracionEmocional);
        String segmentos[] = valoracionEmocional.split("/");

        return segmentos;
    }

    private void sentimientosUsuario(String emocionesUsuario){
        ArrayList<Integer> codigoEmocionesUsuario = new ArrayList<>();
        String usuarioFinal = emocionesUsuario.substring(emocionesUsuario.indexOf("(") + 1, emocionesUsuario.indexOf(")"));
        Log.d("Respuesta usuario final", usuarioFinal);
        String[] sentimientosUsuario = usuarioFinal.split("-");
        for(String sentimientos : sentimientosUsuario){
            codigoEmocionesUsuario.add(Integer.valueOf(sentimientos));
        }
    };

    private void sentimientosRobot(String emocionesRobot) throws InterruptedException {
        ArrayList<Integer> codigoEmocionesRobot = new ArrayList<>();
        String robotFinal = emocionesRobot.substring(emocionesRobot.indexOf("(") + 1, emocionesRobot.indexOf(")"));
        Log.d("Respuesta robot final", robotFinal);
        String[] sentimientosRobot = robotFinal.split("-");
        for(String sentimientos : sentimientosRobot){
            codigoEmocionesRobot.add(Integer.valueOf(sentimientos));
        }
        expresividadFacial(codigoEmocionesRobot);
    };
    public void hablar(String voz, String respuesta) throws IOException {
        Log.d("hablar", respuesta);
        if(voz.equals("sanbot")){
            speechManager.startSpeak(respuesta, so);
        }
        else{
            APIChatGPTVoz(respuesta, voz.toLowerCase());
        }
    }

    private void expresividadFacial(List<Integer> codigoEmocionesRobot) throws InterruptedException {
        int indexSentimiento;
        if(codigoEmocionesRobot.size()>1){
            indexSentimiento = codigoEmocionesRobot.get((int) Math.floor(Math.random() * codigoEmocionesRobot.size()));
        }
        else{
            indexSentimiento = codigoEmocionesRobot.get(0);
        }
        Log.d("indexSentimiento", String.valueOf(indexSentimiento));
        if(indexSentimiento>=0 && indexSentimiento<=2){ // ALEGRÍA
            cambiarEmocion(EmotionsType.SMILE);
            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_YELLOW));
            controlBasicoBrazos(AccionesBrazos.LEVANTAR_BRAZO, TipoBrazo.AMBOS);
        }
        else if(indexSentimiento>=3 && indexSentimiento<=5){ // CONFIANZA
            cambiarEmocion(EmotionsType.PRISE);
        }
        else if(indexSentimiento>=6 && indexSentimiento<=8){ // MIEDO
            cambiarEmocion(EmotionsType.GRIEVANCE);
            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_PURPLE));
        }
        else if(indexSentimiento>=9 && indexSentimiento<=10){ // SORPRESA
            cambiarEmocion(EmotionsType.SURPRISE);
            controlBasicoBrazos(AccionesBrazos.LEVANTAR_BRAZO, TipoBrazo.AMBOS);
        }
        else if(indexSentimiento==11){ // DISTRACCIÓN
            cambiarEmocion(EmotionsType.SWEAT);
        }
        else if(indexSentimiento>=12 && indexSentimiento<=13){ // TRISTEZA
            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_BLUE));
            controlBasicoCabeza(AccionesCabeza.ABAJO);
            cambiarEmocion(EmotionsType.GOODBYE);
        }
        else if(indexSentimiento==14){ // MELANCOLIA
            cambiarEmocion(EmotionsType.GRIEVANCE);
        }
        else if(indexSentimiento>=15 && indexSentimiento<=16){ // ASCO
            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_GREEN));
            cambiarEmocion(EmotionsType.ARROGANCE);
        }
        else if(indexSentimiento==17){ // ABURRIMIENTO
            cambiarEmocion(EmotionsType.SLEEP);
        }
        else if(indexSentimiento>=18 && indexSentimiento<=19){ // IRA
            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_RED));
            cambiarEmocion(EmotionsType.ANGRY);
        }
        else if(indexSentimiento==20){ // ENFADO
            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_RED));
            cambiarEmocion(EmotionsType.ABUSE);
        }
        else if(indexSentimiento>=21 && indexSentimiento<=23){ // INTERÉS
            cambiarEmocion(EmotionsType.QUESTION);
        }
        else if(indexSentimiento==24){ // OPTIMISMO
                cambiarEmocion(EmotionsType.SMILE);
        }
        else if(indexSentimiento==25){ // AMOR
            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_PINK));
            controlBasicoCabeza(AccionesCabeza.ABAJO);
            cambiarEmocion(EmotionsType.LAUGHTER);
        }
        else if(indexSentimiento==26){ // SUMISION
            cambiarEmocion(EmotionsType.GRIEVANCE);
        }
        else if(indexSentimiento==27){ // SUSTO
            cambiarEmocion(EmotionsType.SWEAT);
        }
        else if(indexSentimiento==28){ // DECEPCION
            cambiarEmocion(EmotionsType.GOODBYE);
        }
        else if(indexSentimiento==29){ // REMORDIMIENTO
            cambiarEmocion(EmotionsType.SWEAT);
        }
        else if(indexSentimiento==30){ // DESPRECIO
            cambiarEmocion(EmotionsType.ARROGANCE);
        }
        else if(indexSentimiento==31){ // AGRESIVIDAD
            cambiarEmocion(EmotionsType.ANGRY);
        }
        else if(indexSentimiento==32){ // ESPERANZA
            cambiarEmocion(EmotionsType.SMILE);
        }
        else if(indexSentimiento==33){ // CULPA
            cambiarEmocion(EmotionsType.SWEAT);
        }
        else if(indexSentimiento==34){ // CURIOSIDAD
            cambiarEmocion(EmotionsType.SNICKER);
        }
        else if(indexSentimiento==35){ // DESESPERACION
            cambiarEmocion(EmotionsType.GRIEVANCE);
        }
        else if(indexSentimiento==36){ // INCREDULIDAD
            cambiarEmocion(EmotionsType.SURPRISE);
        }
        else if(indexSentimiento==37){ // ENVIDIA
            cambiarEmocion(EmotionsType.ARROGANCE);
        }
        else if(indexSentimiento==38){ // CINISMO
            cambiarEmocion(EmotionsType.ARROGANCE);
        }
        else if(indexSentimiento==39){ // ORGULLO
            cambiarEmocion(EmotionsType.SMILE);
        }
        else if(indexSentimiento==40){ // ANSIEDAD
            cambiarEmocion(EmotionsType.GRIEVANCE);
        }
        else if(indexSentimiento==41){ // DELEITE
            cambiarEmocion(EmotionsType.SMILE);
        }
        else if(indexSentimiento==42){ // SENTIMENTALISMO
            cambiarEmocion(EmotionsType.SMILE);
        }
        else if(indexSentimiento==43){ // VERGUENZA
            cambiarEmocion(EmotionsType.SHY);
            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_PINK));
            controlBasicoCabeza(AccionesCabeza.ABAJO);
        }
        else if(indexSentimiento==44){ // INDIGNACIÓN
            cambiarEmocion(EmotionsType.ABUSE);
        }
        else if(indexSentimiento==45){ // PESIMISMO
            cambiarEmocion(EmotionsType.GOODBYE);
            controlBasicoCabeza(AccionesCabeza.ABAJO);
        }
        else if(indexSentimiento==46){ // MORBOSIDAD
            cambiarEmocion(EmotionsType.FAINT);
        }
        else if(indexSentimiento==47){ // DOMINANCIA
            cambiarEmocion(EmotionsType.ARROGANCE);
        }
    }

    private void enviarConsulta(){
        // Muestro por pantalla la consulta del usuario
        // e indico que la respuesta se está cargando
        //dialogoUsuario.setVisibility(View.VISIBLE);
        //dialogoUsuario.setText(consultaChatGPT);
        chatArrayAdapter.add(new ChatMessage(false, consultaChatGPT));
        conversacion.add(new ChatMessage(false, consultaChatGPT));
        //dialogoRobot.setVisibility(View.VISIBLE);
        //dialogoRobot.setText("Cargando...");

        textoConsulta.setText("");

        if(consultaRobot){
            try {
                hablar(vozSeleccionada, diaYHora());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if(consultaPeliculas){
            peliculasAPI();
        }

        try {
            // Se envía la consulta del usuario con la voz seleccionada
            APIChatGPT(consultaChatGPT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reproducirMediaPlayer(){
        mediaPlayer.reset();
        MediaDataSource mediaDataSource = new ByteArrayMediaDataSource(respuestaGPTVoz);
        mediaPlayer.setDataSource(mediaDataSource);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.prepareAsync();
    }

    public enum AccionesBrazos {
        LEVANTAR_BRAZO,
        BAJAR_BRAZO,
    }

    public enum TipoBrazo {
        DERECHO,
        IZQUIERDO,
        AMBOS;
    }

    public enum AccionesCabeza {
        DERECHA,
        IZQUIERDA,
        ARRIBA,
        ABAJO,
        CENTRO;
    }

    private boolean controlBasicoBrazos(AccionesBrazos accion, TipoBrazo brazo) throws InterruptedException {
        byte[] absolutePart = new byte[]{AbsoluteAngleHandMotion.PART_LEFT, AbsoluteAngleHandMotion.PART_RIGHT, AbsoluteAngleHandMotion.PART_BOTH};
        AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], 10, 0);
        switch(accion) {
            case LEVANTAR_BRAZO:
                switch (brazo) {
                    case IZQUIERDO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], 7, 10);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case DERECHO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[1], 7, 10);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case AMBOS:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], 7, 10);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                }
                break;
            case BAJAR_BRAZO:
                switch (brazo) {
                    case IZQUIERDO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], 7, 170);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case DERECHO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[1], 7, 170);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case AMBOS:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], 7, 170);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                }
                break;
        }
        Thread.sleep(3000);
        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], 7, 170);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        return true;
    }

    public boolean controlBasicoCabeza(AccionesCabeza accion) throws InterruptedException {
        RelativeAngleHeadMotion relativeAngleHeadMotion;
        AbsoluteAngleHeadMotion absoluteAngleHeadMotion;
        switch (accion) {
            case IZQUIERDA:
                relativeAngleHeadMotion = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_LEFT, 180);
                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
                break;
            case DERECHA:
                relativeAngleHeadMotion = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_RIGHT, 180);
                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
                break;
            case ARRIBA:
                relativeAngleHeadMotion = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_UP, 30);
                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
                break;
            case ABAJO:
                relativeAngleHeadMotion = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_DOWN, 30);
                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
                break;
            case CENTRO:
                absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,90);
                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
                break;
        }
        Thread.sleep(3000);
        absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,90);
        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
        return true;
    }
    @Override
    protected void onMainServiceConnected() {

    }

}
