package com.example.sanbotapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.beans.OperationResult;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.SpeakOption;
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

public class ModuloConversacionalTeclado extends TopBaseActivity {

    // Componentes módulo conversacional
    private Button botonAjustes;
    private Button botonPlayPause;
    private Button botonRepetir;
    private TextView dialogoUsuario;
    private TextView dialogoRobot;
    private EditText textoConsulta;
    private Button botonHablar;
    private Button botonEnviar;

    // Modulos del robot

    private SpeechManager speechManager;
    private HeadMotionManager headMotionManager;
    private HandMotionManager handMotionManager;
    private SystemManager systemManager;
    private HardWareManager hardWareManager;
    private AudioManager audioManager;


    // Gestión emociones robot

    private EmotionsType currentEmotion;
    private String emociones[] = {"ÉXTASIS", "ALEGRÍA", "SERENIDAD", "ADMIRACIÓN", "CONFIANZA", "APROBACIÓN",
            "TERROR", "MIEDO", "TEMOR", "ASOMBRO", "SORPRESA", "DISTRACCIÓN", "PENA", "TRISTEZA", "MELANCOLÍA",
            "AVERSIÓN", "ASCO", "ABURRIMIENTO", "FURIA", "IRA", "ENFADO", "VIGILANCIA", "ANTICIPACIÓN", "INTERÉS", "OPTIMISMO",
            "AMOR", "SUMISIÓN", "SUSTO", "DECEPCIÓN", "REMORDIMIENTO", "DESPRECIO", "AGRESIVIDAD", "ESPERANZA", "CULPA", "CURIOSIDAD",
            "DESESPERACIÓN", "INCREDULIDAD", "ENVIDIA", "CINISMO", "ORGULLO", "ANSIEDAD", "DELEITE", "SENTIMENTALISMO", "VERGÜENZA",
            "INDIGNACIÓN", "PESIMISMO", "MORBOSIDAD", "DOMINANCIA"};
    private static int indexEmociones = 0;

    // Gestión MediaPlayer
    private MediaPlayer mediaPlayer = new MediaPlayer();

    // Variables usadas en el modulo

    private String consultaChatGPT; // Consulta realizada por el usuario
    private String respuestaGPT; // Respuesta dada a la consulta realizada por el usuario
    private String vozSeleccionada; // Voz seleccionada por el usuario
    private String nombreUsuario; // Nombre del usuario
    private int edadUsuario; // Edad del usuario
    private boolean consultaRobot = false; // Variable para consultas internas del robot
    private boolean consultaPeliculas = false; // Variable para consultas API de películas
    private boolean conversacionAutomatica; // Variable para indicar si la conversación está en modo automático

    // Lista de variables necesarias para el envío de requests en la API de ChatGPT

    private Map<String, String> roleSystem = new HashMap<>();

    Map<String, String> roleUser = new HashMap<>();
    private List<Map<String, String>> messages = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();

        // Recuperación de las variables guardadas en el almacenamiento local

        SharedPreferences sharedPref = this.getSharedPreferences("voces", MODE_PRIVATE);
        Log.d("preferencias", sharedPref.getString("voz", ""));
        String defaultValue = "Sanbot";
        vozSeleccionada = sharedPref.getString("voz", defaultValue);
        SharedPreferences sharedPrefConversacionAutomatica = this.getSharedPreferences("conversacionAutomatica", MODE_PRIVATE);
        Log.d("preferenciasCA", String.valueOf(sharedPrefConversacionAutomatica.getBoolean("conversacionAutomatica", false)));
        conversacionAutomatica = sharedPrefConversacionAutomatica.getBoolean("conversacionAutomatica", false);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modulo_conversacional);

        // Inicialización de las unidades del robot

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Inicialización de los componentes de la vista del módulo conversacional

        botonAjustes = findViewById(R.id.botonAjustes);
        botonPlayPause = findViewById(R.id.botonPlayPause);
        botonRepetir = findViewById(R.id.botonRepetir);
        dialogoUsuario = findViewById(R.id.burbujaDialogoUsuario);
        dialogoRobot = findViewById(R.id.burbujaDialogoRobot);
        textoConsulta = findViewById(R.id.textoConsulta);
        botonHablar = findViewById(R.id.botonHablar);;
        botonEnviar = findViewById(R.id.botonEnviar);

        botonPlayPause.setText("Pausar");
        Drawable pause = getContext().getResources().getDrawable(R.drawable.baseline_pause_24);
        botonPlayPause.setCompoundDrawablesWithIntrinsicBounds(pause, null, null, null);

        // Velocidad y entonación de la voz propia del robot

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);


        // Obtención de las variables guardadas en el almacenamiento local

        SharedPreferences sharedPrefVoz = this.getSharedPreferences("voces", MODE_PRIVATE);
        Log.d("preferencias", sharedPrefVoz.getString("voz", ""));
        String defaultValueVoz = "Sanbot";
        vozSeleccionada = sharedPrefVoz.getString("voz", defaultValueVoz);

        SharedPreferences sharedPrefNombre = this.getSharedPreferences("nombre", MODE_PRIVATE);
        Log.d("preferencias", sharedPrefNombre.getString("nombre", ""));
        String defaultValueNombre = "Pepe";
        nombreUsuario = sharedPrefNombre.getString("nombre", defaultValueNombre);

        SharedPreferences sharedPrefEdad = this.getSharedPreferences("edad", MODE_PRIVATE);
        Log.d("preferencias", String.valueOf(sharedPrefEdad.getInt("edad", 0)));
        int defaultValueEdad = 50;
        edadUsuario = sharedPrefEdad.getInt("edad", defaultValueEdad);

        SharedPreferences sharedPrefPersonalizacionGenero = this.getSharedPreferences("personalizacionGenero", MODE_PRIVATE);
        Log.d("preferencias", sharedPrefPersonalizacionGenero.getString("personalizacionGenero", null));
        String generoRobot = sharedPrefPersonalizacionGenero.getString("personalizacionGenero", null);

        SharedPreferences sharedPrefPersonalizacionEdad = this.getSharedPreferences("personalizacionEdad", MODE_PRIVATE);
        Log.d("preferencias", String.valueOf(sharedPrefPersonalizacionEdad.getInt("personalizacionEdad", 0)));
        int edadRobot = sharedPrefPersonalizacionEdad.getInt("personalizacionEdad", 0);

        SharedPreferences sharedPrefPersonalizacionContexto = this.getSharedPreferences("personalizacionContexto", MODE_PRIVATE);
        Log.d("preferencias", sharedPrefPersonalizacionContexto.getString("personalizacionContexto", null));
        String contexto = sharedPrefPersonalizacionContexto.getString("personalizacionContexto", null);

        SharedPreferences sharedPrefConversacionAutomatica = this.getSharedPreferences("conversacionAutomatica", MODE_PRIVATE);
        Log.d("preferenciasCA", String.valueOf(sharedPrefConversacionAutomatica.getBoolean("conversacionAutomatica", false)));
        conversacionAutomatica = sharedPrefConversacionAutomatica.getBoolean("conversacionAutomatica", false);

        Log.d("info", "genero: " + generoRobot + " edad " + edadRobot + " contexto " + contexto);

        // ROLESYSTEM API OPENAI
        roleSystem.put("role", "system");
        if(generoRobot == null || edadRobot == 0) {
            roleSystem.put("content", "quiero que mantengamos una conversación, en cada respuesta que te envíe quiero que me envíes al principio de tu respuesta entre corchetes" +
                    "un número o varios entre paréntesis en función de la emoción que transmiten mis respuestas: 1 éxtasis, 2 alegría, 3 serenidad, 4 admiración, 5 confianza " +
                    "6 aceptación, 7 terror, 8 miedo, 9 temor, 10 asombro, 11 sorpresa, 12 distracción, 13 aflicción, 14 tristeza, 15 melancolía, 16 aversión, 17 asco, 18 aburrimiento," +
                    "19 furia, 20 ira, 21 enfado, 22 vigilancia, 23 anticipación, 24 interés, 25 optimismo, 26 amor, 27 sumisión, 28 susto, 29 decepción, 30 remordimiento, 31 desprecio, 32 agresividad," +
                    "33 esperanza, 34 culpa, 35 curiosidad, 36 desesperación, 37 incredulidad, 38 envidia, 39 cinismo, 40 orgullo, 41 ansiedad, 42 deleite, 43 sentimentalismo, 44 vergüenza, 45 indignación, " +
                    "46 pesimismo, 47 morbosidad y 48 dominancia, añadas un guión y un número en función de la emoción que quieres intentar transmitir con tu respuesta " +
                    "siguiendo el mismo código numérico. Es decir seguirá el siguiente patrón: [(<número o números de emoción o emociones separados por guiones de mi respuesta>)" +
                    "/ (<número o números de emoción o emociones de la respuesta que quieres transmitir>)] + tu respuesta a la conversación." + "Quiero que reconduzcas la conversación en función de la emoción que interpretes y que trates de empatizar" +
                    "lo máximo posible con mis respuestas. Aquí te dejo algunos ejemplos: Si te digo algo triste, tú puedes tratar de animarme siendo optimista y mostrarás curiosidad por saber lo que me pasa, así que [(14)/(25-35)]," +
                    "si mi respuesta es de enfado, tú tratarás de calmarme y mostrarás curiosidad por saber qué me ocurre, asi que [(21)/(3-35)], si te digo que me gusta alguien" +
                    "mi respuesta será de amor y vergüenza, y tú puedes sentir sorpresa, así que [(26-44)/(11)]. También quiero que a veces me llames por mi nombre que es " + nombreUsuario + " y " +
                    "que adaptes la conversación teniendo en cuenta que mi edad es de " + edadUsuario + " años");

        }
        else if (contexto == ""){
            roleSystem.put("content", "quiero que mantengamos una conversación, en cada respuesta que te envíe quiero que me envíes al principio de tu respuesta entre corchetes" +
                    "un número o varios entre paréntesis en función de la emoción que transmiten mis respuestas: 1 éxtasis, 2 alegría, 3 serenidad, 4 admiración, 5 confianza " +
                    "6 aceptación, 7 terror, 8 miedo, 9 temor, 10 asombro, 11 sorpresa, 12 distracción, 13 aflicción, 14 tristeza, 15 melancolía, 16 aversión, 17 asco, 18 aburrimiento," +
                    "19 furia, 20 ira, 21 enfado, 22 vigilancia, 23 anticipación, 24 interés, 25 optimismo, 26 amor, 27 sumisión, 28 susto, 29 decepción, 30 remordimiento, 31 desprecio, 32 agresividad," +
                    "33 esperanza, 34 culpa, 35 curiosidad, 36 desesperación, 37 incredulidad, 38 envidia, 39 cinismo, 40 orgullo, 41 ansiedad, 42 deleite, 43 sentimentalismo, 44 vergüenza, 45 indignación, " +
                    "46 pesimismo, 47 morbosidad y 48 dominancia, añadas un guión y un número en función de la emoción que quieres intentar transmitir con tu respuesta " +
                    "siguiendo el mismo código numérico. Es decir seguirá el siguiente patrón: [(<número o números de emoción o emociones separados por guiones de mi respuesta>)" +
                    "/ (<número o números de emoción o emociones de la respuesta que quieres transmitir>)] + tu respuesta a la conversación." + "Quiero que reconduzcas la conversación en función de la emoción que interpretes y que trates de empatizar" +
                    "lo máximo posible con mis respuestas. Aquí te dejo algunos ejemplos: Si te digo algo triste, tú puedes tratar de animarme siendo optimista y mostrarás curiosidad por saber lo que me pasa, así que [(14)/(25-35)]," +
                    "si mi respuesta es de enfado, tú tratarás de calmarme y mostrarás curiosidad por saber qué me ocurre, asi que [(21)/(3-35)], si te digo que me gusta alguien" +
                    "mi respuesta será de amor y vergüenza, y tú puedes sentir sorpresa, así que [(26-44)/(11)]. También quiero que a veces me llames por mi nombre que es " + nombreUsuario + " y " +
                    "que adaptes la conversación teniendo en cuenta que mi edad es de " + edadUsuario + " años. Además quiero que actúes como que tu genero es " + generoRobot + ", que tienes " + edadRobot);
        }
        else{
            roleSystem.put("content", "quiero que mantengamos una conversación, en cada respuesta que te envíe quiero que me envíes al principio de tu respuesta entre corchetes" +
                    "un número o varios entre paréntesis en función de la emoción que transmiten mis respuestas: 1 éxtasis, 2 alegría, 3 serenidad, 4 admiración, 5 confianza " +
                    "6 aceptación, 7 terror, 8 miedo, 9 temor, 10 asombro, 11 sorpresa, 12 distracción, 13 aflicción, 14 tristeza, 15 melancolía, 16 aversión, 17 asco, 18 aburrimiento," +
                    "19 furia, 20 ira, 21 enfado, 22 vigilancia, 23 anticipación, 24 interés, 25 optimismo, 26 amor, 27 sumisión, 28 susto, 29 decepción, 30 remordimiento, 31 desprecio, 32 agresividad," +
                    "33 esperanza, 34 culpa, 35 curiosidad, 36 desesperación, 37 incredulidad, 38 envidia, 39 cinismo, 40 orgullo, 41 ansiedad, 42 deleite, 43 sentimentalismo, 44 vergüenza, 45 indignación, " +
                    "46 pesimismo, 47 morbosidad y 48 dominancia, añadas un guión y un número en función de la emoción que quieres intentar transmitir con tu respuesta " +
                    "siguiendo el mismo código numérico. Es decir seguirá el siguiente patrón: [(<número o números de emoción o emociones separados por guiones de mi respuesta>)" +
                    "/ (<número o números de emoción o emociones de la respuesta que quieres transmitir>)] + tu respuesta a la conversación." + "Quiero que reconduzcas la conversación en función de la emoción que interpretes y que trates de empatizar" +
                    "lo máximo posible con mis respuestas. Aquí te dejo algunos ejemplos: Si te digo algo triste, tú puedes tratar de animarme siendo optimista y mostrarás curiosidad por saber lo que me pasa, así que [(14)/(25-35)]," +
                    "si mi respuesta es de enfado, tú tratarás de calmarme y mostrarás curiosidad por saber qué me ocurre, asi que [(21)/(3-35)], si te digo que me gusta alguien" +
                    "mi respuesta será de amor y vergüenza, y tú puedes sentir sorpresa, así que [(26-44)/(11)]. También quiero que a veces me llames por mi nombre que es " + nombreUsuario + " y " +
                    "que adaptes la conversación teniendo en cuenta que mi edad es de " + edadUsuario + " años. Además quiero que actúes como que tu genero es " + generoRobot + ", que tienes " + edadRobot + " y además " + contexto);
        }
        messages.add(roleSystem);


        try {
            // Gestión de los speakers del robot mediante el módulo SpeechManager
            speechManager.setOnSpeechListener(new SpeakListener() {

                // Acción que se ejecuta cuando el robot termina de hablar
                @Override
                public void onSpeakFinish() {
                    if(conversacionAutomatica){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        botonHablar.performClick();
                    }
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
                    if(conversacionAutomatica){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        botonHablar.performClick();
                    }
                }
            });

            // Gestión de la pulsación del botón de ajustes
            botonAjustes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent settingsActivity = new Intent(ModuloConversacionalTeclado.this, SettingsActivity.class);
                    startActivity(settingsActivity);
                }
            });

            // Gestión de la pulsación del botón de silenciar
            botonPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Log.d("Le estoy dando", "dando");

                    if(vozSeleccionada.equals("Sanbot")) {

                        // Si el robot está hablando (voz Sanbot)
                        OperationResult or = speechManager.isSpeaking();
                        if (or.getResult().equals("1")) {
                            Log.d("Le estoy dando", "robot hablando, intentando callar");
                            // Se silencia
                            speechManager.pauseSpeak();
                            Drawable play = getContext().getResources().getDrawable(R.drawable.baseline_play_arrow_24);
                            botonPlayPause.setCompoundDrawablesWithIntrinsicBounds(play, null, null, null);
                            botonPlayPause.setText("Reanudar");
                        } else {
                            Log.d("Le estoy dando", "robot sin hablar, intentando reaundar");
                            speechManager.resumeSpeak();
                            Drawable pause = getContext().getResources().getDrawable(R.drawable.baseline_pause_24);
                            botonPlayPause.setCompoundDrawablesWithIntrinsicBounds(pause, null, null, null);
                            botonPlayPause.setText("Pausar");
                        }
                    }
                    else{

                        // Si el mediaPlayer está reproduciéndose (voz OpenAI)
                        if (mediaPlayer.isPlaying()) {
                            Log.d("Le estoy dando", "mediaplauer habladno, intentando parar");
                            // Se detiene
                            mediaPlayer.stop();
                            Drawable play = getContext().getResources().getDrawable(R.drawable.baseline_play_arrow_24);
                            botonPlayPause.setCompoundDrawablesWithIntrinsicBounds(play, null, null, null);
                            botonPlayPause.setText("Reanudar");
                        } else {
                            Log.d("Le estoy dando", "mediaplauer sin hablar, intentando reaundar");
                            mediaPlayer.start();
                            Drawable pause = getContext().getResources().getDrawable(R.drawable.baseline_pause_24);
                            botonPlayPause.setCompoundDrawablesWithIntrinsicBounds(pause, null, null, null);
                            botonPlayPause.setText("Pausar");
                        }
                    }
                }
            });

            // Gestión de la pulsación del botón de hablar
            botonHablar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    registrarConsulta();
                }
            });

            // Gestión de la pulsación del botón de enviar
            botonEnviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){

                    if(!conversacionAutomatica){
                        consultaChatGPT = textoConsulta.getText().toString();
                    }

                    // Muestro por pantalla la consulta del usuario
                    // e indico que la respuesta se está cargando

                    dialogoUsuario.setVisibility(View.VISIBLE);
                    dialogoUsuario.setText(consultaChatGPT);
                    dialogoRobot.setVisibility(View.VISIBLE);
                    dialogoRobot.setText("Cargando...");
                    textoConsulta.setText("");
                    if(consultaRobot){
                        Calendar cal = Calendar.getInstance();
                        int dia = cal.get(Calendar.DAY_OF_MONTH);
                        int mes = cal.get(Calendar.MONTH);
                        int agno = cal.get(Calendar.YEAR);
                        int hora = cal.get(Calendar.HOUR_OF_DAY);
                        int minutos = cal.get(Calendar.MINUTE);
                        int segundos = cal.get(Calendar.SECOND);

                        try {
                            APIChatGPTVoz("Hoy es " + dia + " del " + mes + " de " + agno + " y son las " + hora + minutos + segundos, vozSeleccionada.toLowerCase());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else if(consultaPeliculas){
                        peliculasAPI();
                    }

                    try {
                        APIChatGPT(consultaChatGPT, vozSeleccionada);
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
                    if(vozSeleccionada.equals("Sanbot")){
                        speechManager.startSpeak(respuestaGPT, speakOption);
                    }
                    else{
                        try {
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        mediaPlayer.start();
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
                    if(!consultaChatGPT.toLowerCase().equals("fin")) {
                        botonEnviar.performClick();
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

        // Vacío la consulta de ChatGPT

        consultaChatGPT = "";
        textoConsulta.setText("");


        // El robot se pone en modo escucha

        speechManager.doWakeUp();

        // Interpreto lo que dice el usuario

        reconocerRespuesta();

        /*

        if(consultaRobot){
            DateTimeFormatter dtf = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dtf = DateTimeFormatter.ofPattern("dd/MM/uuuu");
            }
            LocalDate localDate = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localDate = LocalDate.now();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("Fecha", dtf.format(localDate));
            }
        }

         */
    }

    // Función en la que se le pasa la respuesta del ChatGPT y la voz con la que el usuario
    // desea que la reproduzca
    public void APIChatGPTVoz(String respuesta, String voz) throws IOException {

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
                        mediaPlayer.reset();
                        MediaDataSource mediaDataSource = new ByteArrayMediaDataSource(response.body().bytes());
                        mediaPlayer.setDataSource(mediaDataSource);
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                            }
                        });
                        mediaPlayer.prepareAsync();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();


    }

    public void APIChatGPT(String pregunta, String voz) throws IOException, InterruptedException {

        // Velocidad y entonación de la voz propia del robot

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);

        new Thread(new Runnable() {
            public void run() {

                // ----------- DATOS PARA REALIZAR REQUESTS HTTP -------------

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                final OkHttpClient client = new OkHttpClient();

                // ----------- DATOS PARA REALIZAR PETICIÓN A LA API DE OPENAI ---------

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

                    // -------------- POR REVISAR --------------

                    // TRATANDO DE SEPARAR LA STRING
                    List<Integer> codigoEmocionesRobot = new ArrayList<Integer>();
                    List<Integer> codigoEmocionesUsuario = new ArrayList<Integer>();
                    // Respuesta sin valoración emocional
                    respuestaGPT = r.substring(r.indexOf("]")+1, r.length());
                    Log.d("Respuesta GPT", respuestaGPT);

                    // Apartamos la valoración emocional para utilizarla más tarde
                    String valoracionEmocional = r.substring(0, r.indexOf("]")+1);
                    Log.d("valoracion emocional", valoracionEmocional);
                    String segmentos[] = valoracionEmocional.split("/");

                    // Sentimiento del usuario
                    String usuario = segmentos[0];
                    Log.d("Respuesta usuario", usuario);
                    String usuarioFinal = usuario.substring(usuario.indexOf("(") + 1, usuario.indexOf(")"));
                    Log.d("Respuesta usuario final", usuarioFinal);
                    String[] sentimientosUsuario = usuarioFinal.split("-");
                    for(String su : sentimientosUsuario){
                        Log.d("Sentimientos usuario", su);
                    }
                    // Sentimiento del robot
                    String robot = segmentos[1];
                    Log.d("Respuesta robot", robot);
                    String robotFinal = robot.substring(robot.indexOf("(") + 1, robot.indexOf(")"));
                    Log.d("Respuesta robot final", robotFinal);
                    String[] sentimientosRobot = robotFinal.split("-");
                    for(String sr : sentimientosRobot){
                        Log.d("Sentimientos robot", sr);
                    }

                    for(String sentimientos : sentimientosRobot){
                        codigoEmocionesRobot.add(Integer.valueOf(sentimientos));
                    }

                    for(String sentimientos : sentimientosUsuario){
                        codigoEmocionesUsuario.add(Integer.valueOf(sentimientos));
                    }


                    int indexSentimiento;
                    if(codigoEmocionesRobot.size()>1){
                        indexSentimiento = codigoEmocionesRobot.get((int) Math.floor(Math.random() * codigoEmocionesRobot.size()));
                    }
                    else{
                        indexSentimiento = codigoEmocionesRobot.get(0);
                    }
                    if(indexSentimiento>=0 && indexSentimiento<=2){
                        cambiarEmocion(EmotionsType.SMILE);
                    }
                    else if(indexSentimiento>=3 && indexSentimiento<=5){
                        cambiarEmocion(EmotionsType.PRISE);
                    }
                    else if(indexSentimiento>=6 && indexSentimiento<=8){
                        cambiarEmocion(EmotionsType.GRIEVANCE);
                    }
                    else if(indexSentimiento>=9 && indexSentimiento<=10){
                        cambiarEmocion(EmotionsType.SURPRISE);
                    }
                    else if(indexSentimiento==11){
                        cambiarEmocion(EmotionsType.PICKNOSE); // aaaaaaa
                    }
                    else if(indexSentimiento>=12 && indexSentimiento<=13){
                        cambiarEmocion(EmotionsType.GOODBYE);
                    }
                    else if(indexSentimiento==14){
                        cambiarEmocion(EmotionsType.GRIEVANCE);
                    }
                    else if(indexSentimiento>=15 && indexSentimiento<=17){
                        cambiarEmocion(EmotionsType.ARROGANCE);
                    }
                    else if(indexSentimiento>=18 && indexSentimiento<=19){
                        cambiarEmocion(EmotionsType.ANGRY);
                    }
                    else if(indexSentimiento==20){
                        cambiarEmocion(EmotionsType.ANGRY); // aaaaaa QUIERO PONER SHOUT
                    }
                    else if(indexSentimiento>=21 && indexSentimiento<=23){
                        cambiarEmocion(EmotionsType.SNICKER);
                    }
                    else if(indexSentimiento==24){
                        cambiarEmocion(EmotionsType.LAUGHTER);
                    }
                    else if(indexSentimiento==25){
                        cambiarEmocion(EmotionsType.SMILE); // aaaaaa QUIERO PONER SHOUT
                    }
                    else if(indexSentimiento==26){
                        cambiarEmocion(EmotionsType.GRIEVANCE);
                    }
                    else if(indexSentimiento==27){
                        cambiarEmocion(EmotionsType.GRIEVANCE); // aaaaaa QUIERO PONER ASHAMED
                    }
                    else if(indexSentimiento==28){
                        cambiarEmocion(EmotionsType.GOODBYE);
                    }
                    else if(indexSentimiento==29){
                        cambiarEmocion(EmotionsType.GOODBYE); // aaaaaa QUIERO PONER ASHAMED
                    }
                    else if(indexSentimiento==30){
                        cambiarEmocion(EmotionsType.ANGRY); // aaaaaa QUIERO PONER SHOUT
                    }

                    ArrayList<String> emocionesRobot = new ArrayList<String>();
                    ArrayList<String> emocionesUsuario = new ArrayList<String>();
                    //-------
                    dialogoRobot.post(new Runnable() {
                        public void run() {
                            for(int i=0; i<codigoEmocionesRobot.size(); i++){
                                emocionesRobot.add(" " + emociones[codigoEmocionesRobot.get(i)-1]);
                            }
                            for(int i=0; i<codigoEmocionesUsuario.size(); i++){
                                emocionesUsuario.add(emociones[codigoEmocionesUsuario.get(i)-1]);
                            }


                            dialogoRobot.setVisibility(View.VISIBLE);

                            // DEBUG!!
                            dialogoRobot.setText(respuestaGPT);
                            //dialogoRobot.setText(respuestaGPT + "\nSENTIMIENTO RECONOCIDO POR EL ROBOT:" +
                            //       emocionesUsuario + "\nSENTIMIENTO QUE TRANSMITE EL ROBOT:" + emocionesRobot);
                        }
                    });
                    Log.d("voz", voz);
                    if(voz.equals("Sanbot")){
                        speechManager.startSpeak(respuestaGPT, speakOption);
                    }
                    else {
                        Log.d("la voz de prueba es", voz);
                        APIChatGPTVoz(respuestaGPT, voz.toLowerCase());
                    }

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
            SpeakOption speakOption = new SpeakOption();
            speakOption.setSpeed(50);
            speakOption.setIntonation(50);
            System.out.println("Configurado opcion de voz");
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            System.out.println("Primera frase a decir");
            speechManager.startSpeak("Hola, las novedades de peliculas son las siguientes", speakOption);
            ArrayList<String> nombresPeliculas = new ArrayList<String>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONArray results = new JSONArray(jsonObject.getString("results"));
                for(int i=0; i<results.length(); i++){
                    JSONObject pelicula = results.getJSONObject(i);
                    nombresPeliculas.add(pelicula.getString("title"));
                }



                for(String nP: nombresPeliculas){
                    System.out.println("Voy a decir la pelicula " + nP);
                    speechManager.startSpeak(nP + ",", speakOption);
                    Thread.sleep(3000);
                }
                //System.out.println(jsonObject.getString("results"));
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
    @Override
    protected void onMainServiceConnected() {

    }

}
