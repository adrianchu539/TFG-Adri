package com.example.sanbotapp;

import android.content.Intent;
import android.database.DataSetObserver;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.interfaces.speech.SpeakListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModuloConversacional extends TopBaseActivity {

    // Componentes módulo conversacional
    private Button botonConfiguracion;
    private Button botonNuevaConversacion;
    private Button botonDetener;
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

    // Modulos del programa
    private SpeechControl speechControl;
    private ModuloOpenAICompletions moduloOpenAI;
    private ModuloEmocional moduloEmocional;
    private ModuloOpenAISpeechVoice moduloOpenAISpeechVoice;
    private GestionMediaPlayer gestionMediaPlayer;
    private ModuloPeticionesExternas moduloPeticionesExternas;
    private HandsControl handsControl;
    private HeadControl headControl;
    private SystemControl systemControl;
    private HardwareControl hardwareControl;
    private GestionSharedPreferences gestionSharedPreferences;

    // Gestión MediaPlayer
    private MediaPlayer mediaPlayer = new MediaPlayer();

    // Variables usadas en el modulo

    private String consultaChatGPT; // Consulta realizada por el usuario
    private String respuestaGPT; // Respuesta dada a la consulta realizada por el usuario
    private byte[] respuestaGPTVoz;
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


    private static SpeakOption speakOption = new SpeakOption();

    // ------------------- PRUEBAS CHAT -----------------

    //to scroll the list view to bottom on data change

    private ChatArrayAdapter chatArrayAdapter;

    private List<ChatMessage> conversacion;


    @Override
    public void onResume() {
        super.onResume();


        // Recuperación de las variables guardadas en el almacenamiento local

        vozSeleccionada = gestionSharedPreferences.getStringSharedPreferences("vozSeleccionada", "sanbot").toLowerCase();
        nombreUsuario = gestionSharedPreferences.getStringSharedPreferences("nombreUsuario", null);
        edadUsuario = gestionSharedPreferences.getIntSharedPreferences("edadUsuario", 0);
        generoRobot = gestionSharedPreferences.getStringSharedPreferences("generoRobotPersonalizacion", null);
        grupoEdadRobot = gestionSharedPreferences.getStringSharedPreferences("grupoEdadRobotPersonalizacion", null);
        contexto = gestionSharedPreferences.getStringSharedPreferences("contextoPersonalizacion", null);
        conversacionAutomatica = gestionSharedPreferences.getBooleanSharedPreferences("conversacionAutomatica", true);
        modoTeclado = gestionSharedPreferences.getBooleanSharedPreferences("modoTeclado", false);

        personalizacionActivada = gestionSharedPreferences.getBooleanSharedPreferences("personalizacionActivada", false);
        contextualizacionActivada = gestionSharedPreferences.getBooleanSharedPreferences("contextualizacionActivada", false);
        interpretacionEmocionalActivada = gestionSharedPreferences.getBooleanSharedPreferences("interpretacionEmocionalActivada", false);
        contextoVacio = gestionSharedPreferences.getBooleanSharedPreferences("contextoVacio", false);

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

        gestionarPantallaModoTeclado();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Configuración de la aplicación
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onCreate(savedInstanceState);

        // Establecer pantalla
        setContentView(R.layout.activity_modulo_conversacional);

        // Instanciación de componentes
        botonConfiguracion = findViewById(R.id.botonConfiguracion);
        botonTutorial = findViewById(R.id.botonTutorial);
        botonDetener = findViewById(R.id.botonDetener);
        botonRepetir = findViewById(R.id.botonRepetir);
        dialogo = findViewById(R.id.burbujaDialogo);
        botonHablar = findViewById(R.id.botonHablar);
        botonHablarTeclado = findViewById(R.id.botonHablarTeclado);
        botonEnviarTeclado = findViewById(R.id.botonEnviarTeclado);
        textoConsulta = findViewById(R.id.textoConsultaTeclado);
        botonNuevaConversacion = findViewById(R.id.botonNuevaConversacion);

        // Gestionamos la pantalla en función de si está activado el modo teclado
        gestionarPantallaModoTeclado();

        // -------------- CHAT --------------
        conversacion = new ArrayList<>();
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

        speakOption.setSpeed(50);
        speakOption.setIntonation(50);

        speechControl = new SpeechControl(speechManager, speakOption);
        moduloOpenAI = new ModuloOpenAICompletions();
        headControl = new HeadControl(headMotionManager);
        handsControl = new HandsControl(handMotionManager);
        systemControl = new SystemControl(systemManager);
        hardwareControl = new HardwareControl(hardWareManager);
        gestionSharedPreferences = new GestionSharedPreferences(this);

        moduloEmocional = new ModuloEmocional(handsControl, headControl, hardwareControl, systemControl);
        moduloOpenAISpeechVoice = new ModuloOpenAISpeechVoice();
        gestionMediaPlayer = new GestionMediaPlayer();
        moduloPeticionesExternas = new ModuloPeticionesExternas();


        // Obtención de las variables guardadas en el almacenamiento local

        vozSeleccionada = gestionSharedPreferences.getStringSharedPreferences("vozSeleccionada", "sanbot").toLowerCase();
        nombreUsuario = gestionSharedPreferences.getStringSharedPreferences("nombreUsuario", null);
        edadUsuario = gestionSharedPreferences.getIntSharedPreferences("edadUsuario", 0);
        generoRobot = gestionSharedPreferences.getStringSharedPreferences("generoRobotPersonalizacion", null);
        grupoEdadRobot = gestionSharedPreferences.getStringSharedPreferences("grupoEdadRobotPersonalizacion", null);
        contexto = gestionSharedPreferences.getStringSharedPreferences("contextoPersonalizacion", null);
        conversacionAutomatica = gestionSharedPreferences.getBooleanSharedPreferences("conversacionAutomatica", true);
        personalizacionActivada = gestionSharedPreferences.getBooleanSharedPreferences("personalizacionActivada", false);
        contextualizacionActivada = gestionSharedPreferences.getBooleanSharedPreferences("contextualizacionActivada", false);
        interpretacionEmocionalActivada = gestionSharedPreferences.getBooleanSharedPreferences("interpretacionEmocionalActivada", false);
        contextoVacio = gestionSharedPreferences.getBooleanSharedPreferences("personalizacionActivada", false);
        modoTeclado = gestionSharedPreferences.getBooleanSharedPreferences("modoTeclado", false);


        // Gestión de las características del modulo a añadir
        gestionModuloConversacional(nombreUsuario, edadUsuario, generoRobot, grupoEdadRobot, contexto);

        String contentPersonalizacion = "También quiero que a veces me llames por mi nombre que es " + nombreUsuario + " y " +
                "que adaptes la conversación teniendo en cuenta que mi edad es de " + edadUsuario + " años";


        String contentContextualizacionSinContexto = "Además quiero que actúes como que tu genero es " + generoRobot + ", que tienes " + grupoEdadRobot;

        String contentContextualizacionConContexto = "Además quiero que actúes como que tu genero es " + generoRobot + ", que tienes " + grupoEdadRobot + " y además " + contexto;


        clasificarRoleSystem(contentPersonalizacion, contentContextualizacionSinContexto, contentContextualizacionConContexto);


        try {

            botonNuevaConversacion.setOnClickListener(new View.OnClickListener() {
                // Al pulsarlo muestra la pantalla de ajustes
                @Override
                public void onClick (View v){
                    moduloOpenAI.clearRoleSystem();
                    moduloOpenAI.clearMessages();
                    clasificarRoleSystem(contentPersonalizacion, contentContextualizacionSinContexto, contentContextualizacionConContexto);
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
            botonConfiguracion.setOnClickListener(new View.OnClickListener() {
                // Al pulsarlo muestra la pantalla de ajustes
                @Override
                public void onClick (View v){
                    Intent menuConfiguracionActivity = new Intent(ModuloConversacional.this, MenuConfiguracion.class);
                    startActivity(menuConfiguracionActivity);
                }
            });

            // Gestión de la pulsación del botón de silenciar
            botonDetener.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {

                    // Si el robot está hablando (voz Sanbot) se pausa
                    // sino, se reanuda
                    if (speechControl.robotHablando()) {
                        try {
                            gestionVoz(vozSeleccionada, AccionReproduccionVoz.DETENER);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        try {
                            gestionVoz(vozSeleccionada, AccionReproduccionVoz.DETENER);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // Si el mediaPlayer está reproduciéndose (voz OpenAI) se pausa,
                    // sino, se reanuda
                    if (gestionMediaPlayer.mediaPlayerReproduciendose()) {
                        try {
                            gestionVoz(vozSeleccionada, AccionReproduccionVoz.DETENER);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
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
                    try {
                        registrarConsulta();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // Gestión de la pulsación del botón de hablar
            botonHablarTeclado.setOnClickListener(new View.OnClickListener() {
                // Al pulsarlo se empieza a escuchar al usuario
                // y se interpreta su consulta hablada
                @Override
                public void onClick (View v){
                    try {
                        registrarConsulta();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
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

                    try {
                        enviarConsulta();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

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

    // Función que reconoce la consulta del usuario
    protected void reconocerConsulta() throws IOException, InterruptedException {
        // Hace uso del modulo SpeechControl para interpretar la consulta del usuario
        String respuesta = speechControl.reconocerRespuesta();

        respuesta = capitalizeCadena(respuesta);
        consultaRobot = false;
        consultaPeliculas = false;

        // Consultas derivadas a las acciones internas del robot
        if(respuesta.startsWith("Robot")){
            consultaRobot = true;
        }
        // Consultas derivadas a la API de películas
        else if(respuesta.startsWith("Películas")){
            consultaPeliculas = true;
        }
        // Consultas derivadas a la API de OpenAI
        else{
            consultaChatGPT = respuesta;
        }

        // Mostramos la cadena reconocida en el EditText de la vista
        textoConsulta.setText(respuesta);

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
    }


    public void registrarConsulta() throws IOException, InterruptedException {

        Log.d("prueba", "registrando consulta...");

        // Vacío la consulta de ChatGPT
        consultaChatGPT = "";
        textoConsulta.setText("");


        // El robot se pone en modo escucha
        speechControl.modoEscucha();

        // Interpreto lo que dice el usuario
        reconocerConsulta();
    }

    // Función en la que se le pasa la respuesta del ChatGPT y la voz con la que el usuario
    // desea que la reproduzca
    public void APIChatGPTVoz(String respuesta, String voz)  {
        moduloOpenAISpeechVoice.peticionVozOpenAI(respuesta, voz);
        respuestaGPTVoz = moduloOpenAISpeechVoice.getGPTVoz();
        gestionMediaPlayer.reproducirMediaPlayer(respuestaGPTVoz);
    }

    public void APIChatGPT(String pregunta) throws IOException, InterruptedException {
        moduloOpenAI.consultaOpenAI(pregunta);
        respuestaGPT = moduloOpenAI.getRespuestaGPT();
        if(interpretacionEmocionalActivada) {
            moduloEmocional.gestionEmocional(respuestaGPT);
        }
        // DEBUG!!
        //dialogoRobot.setText(respuestaGPT);
        chatArrayAdapter.add(new ChatMessage(true, respuestaGPT));
        conversacion.add(new ChatMessage(true, respuestaGPT));
        //dialogoRobot.setText(respuestaGPT + "\nSENTIMIENTO RECONOCIDO POR EL ROBOT:" +
        //       emocionesUsuario + "\nSENTIMIENTO QUE TRANSMITE EL ROBOT:" + emocionesRobot);
        speechControl.gestionHabla(vozSeleccionada, respuestaGPT);
        Thread.sleep(5000);
    }

    private String capitalizeCadena(String c){
        if (c.length() > 0) return c.substring(0,1).toUpperCase() + c.substring(1);
        else return "";
    }

    private void gestionVoz(String voz, AccionReproduccionVoz accionVoz) throws IOException {
        if(voz.equals("sanbot")){
            switch (accionVoz) {
                case DETENER:
                    Log.d("Le estoy dando", "robot hablando, intentando callar");
                    // Se silencia
                    forzarParada=true;
                    speechControl.pararHabla();
                    break;
                case REPETIR:
                    forzarParada=true;
                    speechControl.hablar(respuestaGPT);
                    break;
            }
        }
        else{
            switch (accionVoz) {
                case DETENER:
                    Log.d("Le estoy dando", "mediaplauer habladno, intentando parar");
                    // Se detiene
                    forzarParada = true;
                    gestionMediaPlayer.pararMediaPlayer();
                    break;
                case REPETIR:
                    forzarParada=true;
                    gestionMediaPlayer.reproducirMediaPlayer(respuestaGPTVoz);
            }
        }
    }

    private enum AccionReproduccionVoz {
        DETENER,
        REPETIR
    }

    private void enviarConsulta() throws IOException, InterruptedException {
        // Muestro por pantalla la consulta del usuario
        // e indico que la respuesta se está cargando
        //dialogoUsuario.setVisibility(View.VISIBLE);
        //dialogoUsuario.setText(consultaChatGPT);
        chatArrayAdapter.add(new ChatMessage(false, consultaChatGPT));
        conversacion.add(new ChatMessage(false, consultaChatGPT));
        //dialogoRobot.setVisibility(View.VISIBLE);
        //dialogoRobot.setText("Cargando...");

        textoConsulta.setText("");

        clasificarConsulta();
    }

    private void gestionarPantallaModoTeclado(){
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
    }

    private void gestionModuloConversacional(String nombreUsuario, int edadUsuario, String generoRobot, String grupoEdadRobot, String contexto){
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
    }

    private void clasificarRoleSystem(String contentPersonalizacion, String contentContextualizacionSinContexto, String contentContextualizacionConContexto){
        // PERSONALIZACIÓN + CONVERSACIÓN + INTERPRETACIÓN EMOCIONAL + CONTEXTUALIZACIÓN CON CONTEXTO

        if(personalizacionActivada && interpretacionEmocionalActivada && contextualizacionActivada && !contextoVacio){
            moduloOpenAI.anadirRoleSystem(contentConversacion + "," + contentPersonalizacion + contentInterpretacionEmocional + contentContextualizacionConContexto);
        }
        // PERSONALIZACIÓN + CONVERSACIÓN + INTERPRETACIÓN EMOCIONAL + CONTEXTUALIZACIÓN SIN CONTEXTO
        else if(personalizacionActivada && interpretacionEmocionalActivada && contextualizacionActivada && contextoVacio){
            moduloOpenAI.anadirRoleSystem(contentConversacion + "," + contentPersonalizacion + contentInterpretacionEmocional + contentContextualizacionSinContexto);
        }
        // PERSONALIZACIÓN + CONVERSACIÓN + INTERPRETACIÓN EMOCIONAL
        else if(personalizacionActivada && interpretacionEmocionalActivada && !contextualizacionActivada){
            moduloOpenAI.anadirRoleSystem(contentConversacion + "," + contentPersonalizacion + contentInterpretacionEmocional);
        }
        // PERSONALIZACIÓN + CONVERSACIÓN
        else if(personalizacionActivada && !interpretacionEmocionalActivada){
            moduloOpenAI.anadirRoleSystem(contentPersonalizacion);
        }
        // CONVERSACIÓN
        else{
            moduloOpenAI.anadirRoleSystem(contentConversacion);
        }
    }

    private void clasificarConsulta() throws IOException, InterruptedException {
        if(consultaRobot){
            speechControl.gestionHabla(vozSeleccionada, moduloPeticionesExternas.funcionesRobot());
        }
        else if(consultaPeliculas){
            speechControl.gestionHabla(vozSeleccionada,  "Hola, las novedades de peliculas son las siguientes");
            for(String nP: moduloPeticionesExternas.peliculasAPI()){
                System.out.println("Voy a decir la pelicula " + nP);
                speechControl.gestionHabla(vozSeleccionada, nP + ",");
                Thread.sleep(2000);
            }
        }
        else{
            APIChatGPT(consultaChatGPT);
        }
    }
    @Override
    protected void onMainServiceConnected() {

    }

}
