package com.example.sanbotapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.gson.Gson;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.beans.OperationResult;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.FaceRecognizeBean;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.beans.handmotion.NoAngleHandMotion;
import com.qihancloud.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.speech.Grammar;
import com.qihancloud.opensdk.function.beans.wheelmotion.RelativeAngleWheelMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.ProjectorManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;
import com.qihancloud.opensdk.function.unit.interfaces.media.FaceRecognizeListener;
import com.qihancloud.opensdk.function.unit.interfaces.speech.RecognizeListener;
import com.qihancloud.opensdk.function.unit.interfaces.speech.SpeakListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.LongFunction;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModuloConversacional extends TopBaseActivity {


    EmotionsType currentEmotion, emotions[];

    RelativeAngleHeadMotion relativeAngleHeadMotion;
    AbsoluteAngleHeadMotion absoluteAngleHeadMotion;

    private SpeechManager speechManager; //voice, speechRec
    private HeadMotionManager headMotionManager;    //head movements
    private HandMotionManager handMotionManager;    //hands movements
    private SystemManager systemManager; //emotions
    private HardWareManager hardWareManager; //leds //touch sensors //voice locate //gyroscope
    private AudioManager audioManager;

    private String preguntaChatGPT;

    private Button botonGrabar;

    private Button botonEnviar;

    private Button botonSilenciar;

    private Button botonAjustes;

    private EditText mensajeAEnviar;

    private TextView textBox;

    private String vozSeleccionada;

    private String nombreUsuario;

    private int edadUsuario;

    private List<Map<String, String>> messages = new ArrayList<>();

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private boolean consultaRobot = false;

    private boolean conversacionAutomatica;

    private boolean consultaGoogle = false;

    private boolean consultaPeliculas = false;

    private String emociones[] = {"ÉXTASIS", "ALEGRÍA", "SERENIDAD", "ADMIRACIÓN", "CONFIANZA", "ACEPTACIÓN",
    "TERROR", "MIEDO", "TEMOR", "ASOMBRO", "SORPRESA", "DISTRACCIÓN", "AFLICCIÓN", "TRISTEZA", "MELANCOLÍA",
    "AVERSIÓN", "ASCO", "ABURRIMIENTO", "FURIA", "IRA", "ENFADO", "VIGILANCIA", "ANTICIPACIÓN", "INTERÉS", "OPTIMISMO",
    "AMOR", "SUMISIÓN", "SUSTO", "DECEPCIÓN", "REMORDIMIENTO", "DESPRECIO", "AGRESIVIDAD", "ESPERANZA", "CULPA", "CURIOSIDAD",
    "DESESPERACIÓN", "INCREDULIDAD", "ENVIDIA", "CINISMO", "ORGULLO", "ANSIEDAD", "DELEITE", "SENTIMENTALISMO", "VERGÜENZA",
    "INDIGNACIÓN", "PESIMISMO", "MORBOSIDAD", "DOMINANCIA"};

    private static int index = 0;

    // array con las preguntas que el robot tiene que reconocer, las respuestas que tiene que dar,
    // el tiempo aproximado de espera

    @Override
    public void onResume() {
        super.onResume();
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
        //register(PresentacionActivity.class);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modulo_conversacional);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        emotions = new EmotionsType[]{EmotionsType.ARROGANCE, EmotionsType.SURPRISE, EmotionsType.WHISTLE, EmotionsType.LAUGHTER, EmotionsType.GOODBYE,
                EmotionsType.SHY, EmotionsType.SWEAT, EmotionsType.SNICKER, EmotionsType.PICKNOSE, EmotionsType.CRY, EmotionsType.ABUSE,
                EmotionsType.ANGRY, EmotionsType.KISS, EmotionsType.SLEEP, EmotionsType.SMILE, EmotionsType.GRIEVANCE, EmotionsType.QUESTION,
                EmotionsType.FAINT, EmotionsType.PRISE, EmotionsType.NORMAL};

        botonGrabar = findViewById(R.id.botonGrabar);
        botonEnviar = findViewById(R.id.botonEnviar);
        mensajeAEnviar = findViewById(R.id.cajaTexto);
        botonAjustes = findViewById(R.id.botonAjustes);
        botonSilenciar = findViewById(R.id.botonSilenciar);

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

        Map<String, String> roleSystem = new HashMap<>();
        roleSystem.put("role", "system");
        //roleSystem.put("content", "You are a helpful assistant.");
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
            speechManager.setOnSpeechListener(new SpeakListener() {
                @Override
                public void onSpeakFinish() {
                    if(conversacionAutomatica){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        botonGrabar.performClick();
                    }
                }

                @Override
                public void onSpeakProgress(int i) {

                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    if(conversacionAutomatica){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        botonGrabar.performClick();
                    }
                }
            });
            botonAjustes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent settingsActivity = new Intent(ModuloConversacional.this, SettingsActivity.class);
                    startActivity(settingsActivity);
                }
            });
            botonSilenciar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Log.d("silenciar", "entro");
                    OperationResult or = speechManager.isSpeaking();
                    if(or.getResult().equals("1")){
                        Log.d("robotHablando", "esta hablando");
                        speechManager.stopSpeak();
                    }
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                    }
                }
            });
            botonGrabar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){

                    registrarPregunta();
                }
            });

            botonEnviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    if(conversacionAutomatica && preguntaChatGPT.equals("fin conversacion"))
                    if(!conversacionAutomatica){
                        preguntaChatGPT = mensajeAEnviar.getText().toString();
                    }
                    mensajeAEnviar.setText("");
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
                        APIChatGPT(preguntaChatGPT, vozSeleccionada);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // DIALOGO ----------------------------------------------------------------------------------------------------------------------------------
    public boolean reconocerRespuesta(){
        speechManager.setOnSpeechListener(new RecognizeListener() {
            @Override
            public boolean onRecognizeResult(Grammar grammar) {
                // paso la gramática reconocida a String
                String cadenaReconocida = grammar.getText();

                consultaRobot = false;
                consultaGoogle = false;
                consultaPeliculas = false;

                if(cadenaReconocida.startsWith("robot")){
                    consultaRobot = true;
                }
                else if(cadenaReconocida.startsWith("google")){
                    consultaGoogle = true;
                }
                else if(cadenaReconocida.startsWith("películas")){
                    consultaPeliculas = true;
                }
                else{
                    preguntaChatGPT = cadenaReconocida;
                }
                Log.d("preguntaChatGPT", preguntaChatGPT);
                mensajeAEnviar.setText(cadenaReconocida);
                if(conversacionAutomatica){
                    if(!preguntaChatGPT.toLowerCase().equals("fin")) {
                        botonEnviar.performClick();
                    }
                }
                return true;
            }

            @Override
            public void onRecognizeVolume(int i) {
            }

            public void onStartRecognize() {
                //Log.i("Cris", "onStartRecognize: ");
            }

            public void onStopRecognize() {
                Log.d("StopRecognize", "acabé y preguntachatgpt es " + preguntaChatGPT);
            }

            public void onError(int i, int i1) {
                Log.d("errorReconocimiento", "Ha habido un error en reconocimiento");
            }
        });
        return true;
    }

    // funcion que muestra la emoción pasada por parametro
    public void changeEmotion(EmotionsType emotion) {
        currentEmotion = emotion;
        systemManager.showEmotion(currentEmotion);
    }

    public void registrarPregunta() {
        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);

        Log.d("preguntaChatGPT", "vacío preguntaChatGPT");
        preguntaChatGPT = "";
        mensajeAEnviar.setText("");


        speechManager.doWakeUp();
        reconocerRespuesta();

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

        if (preguntaChatGPT != "") {
            Log.d("preguntaChatGPT", "ahora la pregunta es " + preguntaChatGPT);
        }
    }

    public void APIChatGPTVoz(String respuesta, String voz) throws IOException {

        new Thread(new Runnable() {
            public void run() {

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                final OkHttpClient client = new OkHttpClient();

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
                    else Log.d("API LEVEL chiquito", "no puedorr:");
                    Log.d("Response", "He recibido:");
                    //Log.d("Response", respuesta);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();


    }

    public void APIChatGPT(String pregunta, String voz) throws IOException, InterruptedException {

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);

        new Thread(new Runnable() {
            public void run() {
                textBox = findViewById(R.id.textBox);

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                final OkHttpClient client = new OkHttpClient();

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

                /*

                JSONArray jsonArrayTools = new JSONArray();

                JSONObject jsonObjectAccion = new JSONObject();
                try {
                    jsonObjectAccion.put("type", "string");
                    jsonObjectAccion.put("description", "la acción que debe realizar el robot");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                JSONObject jsonObjectProperties = new JSONObject();
                try {
                    jsonObjectProperties.put("append", jsonObjectAccion);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                JSONObject jsonObjectParameters = new JSONObject();
                try {
                    jsonObjectParameters.put("type", "object");
                    jsonObjectParameters.put("properties", jsonObjectProperties);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                JSONArray jsonArrayRequired = new JSONArray();

                jsonArrayRequired.put("append");

                JSONObject jsonObjectFunction = new JSONObject();
                try {
                    jsonObjectFunction.put("name", "helloWorld");
                    jsonObjectFunction.put("description", "permite utilizar las funciones propias del robot");
                    jsonObjectFunction.put("parameters", jsonObjectParameters);
                    jsonObjectFunction.put("require", jsonArrayRequired);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                jsonArrayTools.put(jsonObjectFunction);

                Log.d("Tools", String.valueOf(jsonArrayTools));

                 */

                try{
                    request.put("model", "gpt-4o-mini");
                    request.put("messages", jsonArray);
                    //request.put("functions", jsonArrayTools);
                    //request.put("function_call", "auto");
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

                    // TRATANDO DE SEPARAR LA STRING
                    List<Integer> codigoEmocionesRobot = new ArrayList<Integer>();
                    List<Integer> codigoEmocionesUsuario = new ArrayList<Integer>();
                    // Respuesta sin valoración emocional
                    String respuestaGPT = r.substring(r.indexOf("]")+1, r.length());
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
                    switch (indexSentimiento){
                        case 1:
                            systemManager.showEmotion(EmotionsType.SMILE);
                            break;
                        case 2:
                            systemManager.showEmotion(EmotionsType.GOODBYE);
                            break;
                        case 3:
                            systemManager.showEmotion(EmotionsType.ANGRY);
                            break;
                        case 4:
                            systemManager.showEmotion(EmotionsType.ARROGANCE);
                            break;
                        case 5:
                            systemManager.showEmotion(EmotionsType.SWEAT);
                            break;
                        case 6:
                            systemManager.showEmotion(EmotionsType.SURPRISE);
                            break;
                        case 7:
                            systemManager.showEmotion(EmotionsType.SHY);
                            break;
                        case 8:
                            systemManager.showEmotion(EmotionsType.SLEEP);
                            break;
                        case 9:
                            systemManager.showEmotion(EmotionsType.SMILE);
                            break;
                        case 10:
                            systemManager.showEmotion(EmotionsType.SMILE);
                            break;
                        case 11:
                            systemManager.showEmotion(EmotionsType.LAUGHTER);
                            break;
                        case 12:
                            systemManager.showEmotion(EmotionsType.SMILE);
                            break;
                        case 13:
                            systemManager.showEmotion(EmotionsType.SNICKER);
                            break;
                        case 14:
                            systemManager.showEmotion(EmotionsType.PICKNOSE);
                            break;
                        case 15:
                            systemManager.showEmotion(EmotionsType.PICKNOSE);
                            break;
                    }

                    ArrayList<String> emocionesRobot = new ArrayList<String>();
                    ArrayList<String> emocionesUsuario = new ArrayList<String>();
                    //-------
                    textBox.post(new Runnable() {
                        public void run() {
                            for(int i=0; i<codigoEmocionesRobot.size(); i++){
                                emocionesRobot.add(" " + emociones[codigoEmocionesRobot.get(i)-1]);
                            }
                            for(int i=0; i<codigoEmocionesUsuario.size(); i++){
                                emocionesUsuario.add(emociones[codigoEmocionesUsuario.get(i)-1]);
                            }


                            textBox.setText(respuestaGPT + "\nSENTIMIENTO RECONOCIDO POR EL ROBOT:" +
                                   emocionesUsuario + "\nSENTIMIENTO QUE TRANSMITE EL ROBOT:" + emocionesRobot);
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

    /*
    public void helloWorld(String append){
        Log.d("HelloWorld", "Hello world " + append);
    }

     */

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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
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
            /*
            JSONObject obj = new JSONObject(response.body().string());
            JSONObject results = obj.getJSONObject("results");
            for(int i=0; i<results.length(); i++){
                nombresPeliculas[i] = results.getJSONObject("title").toString();
            }

             */
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onMainServiceConnected() {

    }

}
