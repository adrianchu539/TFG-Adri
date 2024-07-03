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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private Button botonAjustes;

    private EditText mensajeAEnviar;

    private TextView textBox;

    private String vozSeleccionada;

    private List<Map<String, String>> messages = new ArrayList<>();

    private MediaPlayer mediaPlayer = new MediaPlayer();

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

        SharedPreferences sharedPref = this.getSharedPreferences("voces", MODE_PRIVATE);
        Log.d("preferencias", sharedPref.getString("voz", ""));
        String defaultValue = "Sanbot";
        vozSeleccionada = sharedPref.getString("voz", defaultValue);

        Map<String, String> roleSystem = new HashMap<>();
        roleSystem.put("role", "system");
        roleSystem.put("content", "You are a helpful assistant.");

        messages.add(roleSystem);

        try {
            botonAjustes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent settingsActivity = new Intent(ModuloConversacional.this, SettingsActivity.class);
                    startActivity(settingsActivity);
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
                    if(mensajeAEnviar!=null) {
                        preguntaChatGPT = mensajeAEnviar.getText().toString();
                        try {
                            APIChatGPT(preguntaChatGPT, vozSeleccionada);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
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

                mensajeAEnviar.setText(cadenaReconocida);

                preguntaChatGPT = cadenaReconocida;

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

    // funcion que muestra la emoción pasada por parametro
    public void changeEmotion(EmotionsType emotion) {
        currentEmotion = emotion;
        systemManager.showEmotion(currentEmotion);
    }

    public void registrarPregunta() {
        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);

        if(mensajeAEnviar!=null){
            preguntaChatGPT = "";
            mensajeAEnviar.setText("");
        }

        speechManager.doWakeUp();
        reconocerRespuesta();


        if (preguntaChatGPT != null) {
            Log.d("pregunta", preguntaChatGPT);
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
                try{
                    request.put("model", "gpt-3.5-turbo");
                    request.put("messages", jsonArray);

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

                    textBox.post(new Runnable() {
                        public void run() {
                            textBox.setText(r);
                        }
                    });
                    if(voz=="Sanbot"){
                        speechManager.startSpeak(r, speakOption);
                    }
                    else {
                        Log.d("la voz de prueba es", voz);
                        APIChatGPTVoz(r, voz.toLowerCase());
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

    @Override
    protected void onMainServiceConnected() {

    }

}
