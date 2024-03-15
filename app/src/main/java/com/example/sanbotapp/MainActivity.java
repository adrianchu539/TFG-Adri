package com.example.sanbotapp;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.LogWriter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.qihancloud.opensdk.function.unit.MediaManager;
import com.sanbot.opensdk.base.TopBaseActivity;
import com.sanbot.opensdk.beans.FuncConstant;
import com.sanbot.opensdk.beans.OperationResult;
import com.sanbot.opensdk.function.beans.EmotionsType;
import com.sanbot.opensdk.function.beans.LED;
import com.sanbot.opensdk.function.beans.SpeakOption;
import com.sanbot.opensdk.function.beans.StreamOption;
import com.sanbot.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.sanbot.opensdk.function.beans.handmotion.NoAngleHandMotion;
import com.sanbot.opensdk.function.beans.handmotion.RelativeAngleHandMotion;
import com.sanbot.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.sanbot.opensdk.function.beans.headmotion.LocateAbsoluteAngleHeadMotion;
import com.sanbot.opensdk.function.beans.headmotion.LocateRelativeAngleHeadMotion;
import com.sanbot.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.sanbot.opensdk.function.beans.speech.Grammar;
import com.sanbot.opensdk.function.beans.speech.SpeakStatus;
import com.sanbot.opensdk.function.beans.wheelmotion.DistanceWheelMotion;
import com.sanbot.opensdk.function.beans.wheelmotion.NoAngleWheelMotion;
import com.sanbot.opensdk.function.beans.wheelmotion.RelativeAngleWheelMotion;
import com.sanbot.opensdk.function.unit.HandMotionManager;
import com.sanbot.opensdk.function.unit.HardWareManager;
import com.sanbot.opensdk.function.unit.HeadMotionManager;
import com.sanbot.opensdk.function.unit.ModularMotionManager;
import com.sanbot.opensdk.function.unit.ProjectorManager;
import com.sanbot.opensdk.function.unit.SpeechManager;
import com.sanbot.opensdk.function.unit.SystemManager;
import com.sanbot.opensdk.function.unit.WheelMotionManager;
import com.sanbot.opensdk.function.unit.interfaces.hardware.HardWareListener;
import com.sanbot.opensdk.function.unit.interfaces.hardware.InfrareListener;
import com.sanbot.opensdk.function.unit.interfaces.hardware.PIRListener;
import com.sanbot.opensdk.function.unit.interfaces.hardware.TouchSensorListener;
import com.sanbot.opensdk.function.unit.interfaces.hardware.VoiceLocateListener;
import com.sanbot.opensdk.function.unit.interfaces.speech.RecognizeListener;
import com.sanbot.opensdk.function.unit.interfaces.speech.SpeakListener;
import com.sanbot.opensdk.function.unit.interfaces.speech.WakenListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class MainActivity extends TopBaseActivity {

    HardWareManager hardWareManager;
    SpeechManager speechManager;
    HeadMotionManager headMotionManager;
    MediaManager mediaManager;
    RelativeAngleHeadMotion relativeAngleHeadMotion;
    AbsoluteAngleHeadMotion absoluteAngleHeadMotion;
    LocateAbsoluteAngleHeadMotion locateAbsoluteAngleHeadMotion;
    ProjectorManager projectorManager;
    WheelMotionManager wheelMotionManager;
    SystemManager systemManager;
    EmotionsType currentEmotion, emotions[];
    SpeakOption speakOption = new SpeakOption();
    Button ledOn, ledOff, headLeft, headRight,
            headUp, headDown, buttonSayHi, buttonWheelForward,
            setEmotion, headAbsoluteLeft, headAbsoluteRight, headCenter,
            headAbsoluteUp, headAbsoluteDown, moveForward,
            videoStream, closeStream;

    //Loreto 09/02/2024
    HandMotionManager handMotionManager;
    Button btnNavigateToHandControl;
    Button btnNavigateToSpeechControl;

    // se ha añadido un botón de prueba
    Button btnPrueba;

    //Boton para reconocimiento facial
    Button btnFaceRecognition;
    Button btnMediaControl;

    private Boolean respuestaCorrectaReconocida;


    @Override
    protected void onMainServiceConnected() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        register(MainActivity.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.activity_main);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        projectorManager = (ProjectorManager) getUnitManager(FuncConstant.PROJECTOR_MANAGER);
        mediaManager = (MediaManager) getUnitManager(FuncConstant.BR_FACE_RECOGNIZE);

        //Loreto 09/02/2024
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        btnNavigateToHandControl = findViewById(R.id.btnNavigateToHandControl);
        btnNavigateToSpeechControl = findViewById(R.id.btnNavigateToSpeechControl);
        btnPrueba = findViewById(R.id.btnPrueba);
        btnFaceRecognition = findViewById(R.id.btnFaceRecognition);
        btnMediaControl = findViewById(R.id.btnMediaControl);

        // Adrian 01/03/2024
        //btnRespuestaCorrecta = findViewById(R.id.btnResCorrecta);
        //btnRespuestaIncorrecta = findViewById(R.id.btnResIncorrecta);
        //btnPruebaRespuestas = findViewById(R.id.btnPruebaRespuestas);



        speakOption.setSpeed(30);
        ledOn = findViewById(R.id.ledOn);
        videoStream = findViewById(R.id.videoStream);
        ledOff = findViewById(R.id.ledOff);
        headLeft = findViewById(R.id.headLeft);
        headRight = findViewById(R.id.headRight);
        closeStream = findViewById(R.id.closeStream);
        headUp = findViewById(R.id.headUp);
        headDown = findViewById(R.id.headDown);
        headAbsoluteLeft = findViewById(R.id.headAbsoluteLeft);
        headAbsoluteRight = findViewById(R.id.headAbsoluteRight);
        headAbsoluteUp = findViewById(R.id.headAbsoluteUp);
        headAbsoluteDown = findViewById(R.id.headAbsoluteDown);
        buttonSayHi = findViewById(R.id.buttonSayHi);
        buttonWheelForward = findViewById(R.id.buttonWheelForward);
        setEmotion = findViewById(R.id.setEmotion);
        headCenter = findViewById(R.id.headCenter);
        // se han añadido todos los posibles sentimientos
        emotions = new EmotionsType[]{EmotionsType.ARROGANCE, EmotionsType.SURPRISE, EmotionsType.WHISTLE, EmotionsType.LAUGHTER, EmotionsType.GOODBYE,
                EmotionsType.SHY, EmotionsType.SWEAT, EmotionsType.SNICKER, EmotionsType.PICKNOSE, EmotionsType.CRY, EmotionsType.ABUSE,
                EmotionsType.ANGRY, EmotionsType.KISS, EmotionsType.SLEEP, EmotionsType.SMILE, EmotionsType.GRIEVANCE, EmotionsType.QUESTION,
                EmotionsType.FAINT, EmotionsType.PRISE, EmotionsType.NORMAL};
        moveForward = findViewById(R.id.moveForward);
        setonClicks();
        touchTest();

        respuestaCorrectaReconocida = false;

    }

    public void setonClicks() {
        //Loreto 09/02/2024
        btnNavigateToHandControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent para iniciar la actividad HandControl
                Intent intent = new Intent(MainActivity.this, HandControl.class);

                // Inicia la actividad HandControl
                startActivity(intent);
            }
        });

        btnNavigateToSpeechControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent para iniciar la actividad SpeechControl
                Intent intent = new Intent(MainActivity.this, SpeechControl.class);

                // Inicia la actividad SpeechControl
                startActivity(intent);
            }
        });
        btnPrueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pruebaModulos();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnFaceRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent para iniciar la actividad FaceRecognition
                Intent intent = new Intent(MainActivity.this, FaceRecognitionControl.class);

                // Inicia la actividad FaceRecognition
                startActivity(intent);
            }
        });

        videoStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVideoStream();
            }
        });

        // TODO: 2024-02-07
        // Prueba cambio de emociones
        setEmotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentEmotion();
            }
        });
//        closeStream.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                closeStream();
//            }
//        });
        ledOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWhiteLightOn(view);

            }
        });
        ledOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLightsOff(view);
            }
        });
        headLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnHead("left");
            }
        });
        headRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnHead("right");
            }
        });
        headUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnHead("up");
            }
        });
        headDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnHead("down");
            }
        });
        headCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnHead("center");
            }
        });
        headAbsoluteLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnHead("absoluteLeft");
            }
        });
        headAbsoluteRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnHead("absoluteRight");
            }
        });
        headAbsoluteUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnHead("absoluteUp");
            }
        });
        headAbsoluteDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnHead("absoluteDown");
            }
        });
        buttonSayHi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testSpeech();
            }
        });
        buttonWheelForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wheelGoForward();
            }
        });
        moveForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveWheel("forwardOneMeter");
            }
        });


    }

    public void currentEmotion() {
        Random rand = new Random();
        currentEmotion = EmotionsType.ANGRY;
        systemManager.showEmotion(currentEmotion);
    }

    // funcion que muestra la emoción pasada por parametro
    public void changeEmotion(EmotionsType emotion) {
        currentEmotion = emotion;
        systemManager.showEmotion(currentEmotion);
    }

    public void wheelGoForward() {
        DistanceWheelMotion distanceWheelMotion = new DistanceWheelMotion(DistanceWheelMotion.ACTION_FORWARD_RUN, 5, 50);
        wheelMotionManager.doDistanceMotion(distanceWheelMotion);
    }

    public void setWhiteLightOn(View view) {
        hardWareManager.setWhiteLightLevel(3);
        hardWareManager.switchWhiteLight(true);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_PINK));

        Snackbar.make(view, R.string.white_light_on, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    public void testSpeech() {
        speechManager.startSpeak("Hello there, my name is San Bot", speakOption);
    }

    public void setLightsOff(View view) {
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));
        hardWareManager.switchWhiteLight(false);
    }

    public void touchTest() {
        hardWareManager.setOnHareWareListener(new TouchSensorListener() {
            @Override
            public void onTouch(int part) {
                if (part == 11 || part == 12 || part == 13 || part == 10 || part == 9) {
                    hardWareManager.switchWhiteLight(true);

                }
            }
        });
    }

    public void turnHead(String headMovement) {
        switch (headMovement) {
            case "right":
                relativeAngleHeadMotion = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_RIGHT, 30);
                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
                break;
            case "left":
                relativeAngleHeadMotion = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_LEFT, 30);
                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
                break;
            case "up":
                relativeAngleHeadMotion = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_LEFTUP, 80);
                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
                break;
            case "down":
                relativeAngleHeadMotion = new RelativeAngleHeadMotion(RelativeAngleHeadMotion.ACTION_DOWN, 30);
                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
                break;
            case "absoluteRight":
                absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL, 180);
                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
                break;
            case "absoluteLeft":
                absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL, 0);
                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
                break;
            case "absoluteUp":
                absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL, 30);
                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
                break;
            case "absoluteDown":
                absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL, 7);
                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
                break;
            case "center":
                locateAbsoluteAngleHeadMotion = new LocateAbsoluteAngleHeadMotion(LocateAbsoluteAngleHeadMotion.ACTION_BOTH_LOCK, 90, 15);
                headMotionManager.doAbsoluteLocateMotion(locateAbsoluteAngleHeadMotion);
                break;
        }

    }

    public void moveWheel(String moveSanbot) {
        switch (moveSanbot) {
            case "forwardOneMeter":
                RelativeAngleWheelMotion distanceWheelMotion = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 50);
                wheelMotionManager.doRelativeAngleMotion(distanceWheelMotion);
                break;
        }
    }

    public void startVideoStream() {
        projectorManager.switchProjector(true);
        projectorManager.setMode(ProjectorManager.MODE_WALL);

    }
//    public void closeStream(){
//        mediaManager.closeStream();
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void reaccionRespuestaCorrecta() {
        // cambiar emoción a feliz
        changeEmotion(EmotionsType.SMILE);
        // respuesta para notificar que la respuesta es correcta
        speechManager.startSpeak("Muy bien, respuesta correcta");
        // subir los brazos para simular alegría
        controlBasicoBrazos(AccionesBrazos.LEVANTAR_BRAZO, TipoBrazo.AMBOS);
    }

    public void reaccionRespuestaIncorrecta() {
        // cambiar emoción a triste
        changeEmotion(EmotionsType.GOODBYE);
        // respuesta para notificar que la respuesta es incorrecta
        speechManager.startSpeak("La respuesta es incorrecta");
        // bajar los brazos para simular tristeza
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.AMBOS);
    }

    public void reaccionRespuestaIncorrectaPruebaOtraVez() throws InterruptedException {
        // cambiar emoción a triste
        changeEmotion(EmotionsType.GOODBYE);
        // respuesta para notificar que la respuesta es incorrecta
        speechManager.startSpeak("La respuesta es incorrecta");
        // bajar los brazos para simular tristeza
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.AMBOS);
        // anima al niño a responder otra vez
        Thread.sleep(2000);
        speechManager.startSpeak("Prueba otra vez");
    }

    public void reaccionTriste(){
        // cambiar emoción a triste
        changeEmotion(EmotionsType.GOODBYE);
        // bajar los brazos para simular tristeza
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.AMBOS);
    }

    // POR COMPLETAR
    public void reaccionAlegre() throws InterruptedException {
        // dar vueltas
        controlBasicoTronco(AccionesTronco.GIRO_360, TipoDireccion.IZQUIERDA);
        Thread.sleep(3000);
        controlBasicoTronco(AccionesTronco.GIRO_360, TipoDireccion.IZQUIERDA);
        Thread.sleep(3000);
        controlBasicoBrazos(AccionesBrazos.LEVANTAR_BRAZO, TipoBrazo.AMBOS);
        // cambiar cara
        changeEmotion(EmotionsType.PRISE);
        // poner luces
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RANDOM));
    }

    public void reaccionChocarCinco() throws InterruptedException {
        // cambiar emoción a sorprendido
        changeEmotion(EmotionsType.PRISE);
        // respuesta para notificar que choque los cinco
        speechManager.startSpeak("Genial. Choca esos cinco");
        // sube uno de los brazos
        controlBrazos(TipoBrazo.DERECHO, 10, 70);
        hardWareManager.setOnHareWareListener(new TouchSensorListener() {
            @Override
            public void onTouch(int part) {
                if (part == 10) {
                    controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.DERECHO);
                }
            }
        });
    }

    public void hacerOla() throws InterruptedException {
        // bajar los brazos en caso de que no los tenga ya bajados
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.AMBOS);
        // cambiar emoción a feliz
        changeEmotion(EmotionsType.LAUGHTER);
        // respuesta para notificar que va a hacer la ola
        speechManager.startSpeak("Hagamos una ola");
        Thread.sleep(2000);
        narrarCuentaAtras(5);
        Thread.sleep(2000);
        // subir los brazos para simular la ola
        controlBasicoBrazos(AccionesBrazos.LEVANTAR_BRAZO, TipoBrazo.AMBOS);
        Thread.sleep(2000);
        // bajar los brazos para simular la ola
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.AMBOS);
    }

    // Robot narra una cuenta atrás del tiempo que se pase como parametro.
    // IDEA: Utilizarlo como cronómetro para acciones como tiempos de respuesta a preguntas, juegos, etc.
    public void narrarCuentaAtras(int segundos) throws InterruptedException {
        while (segundos > 0) {
            speechManager.startSpeak(String.valueOf(segundos));
            Thread.sleep(1000);
            segundos--;
        }
    }

    public enum AccionesBrazos {
        LEVANTAR_BRAZO,
        BAJAR_BRAZO,
    }

    public enum AccionesTronco {
        GIRO_90,
        GIRO_180,
        GIRO_360;
    }

    public enum AccionesCabeza {
        DERECHA,
        IZQUIERDA,
        ARRIBA,
        ABAJO,
        CENTRO;
    }

    public enum TipoBrazo {
        DERECHO,
        IZQUIERDO,
        AMBOS;
    }

    public enum TipoDireccion {
        DERECHA,
        IZQUIERDA;
    }

    public void elegirEquipo(List<String> listaEquipos) throws InterruptedException {
        speechManager.startSpeak("Le toca responder al equipo");
        Random rand = new Random();
        String randomElement = listaEquipos.get(rand.nextInt(listaEquipos.size()));
        Thread.sleep(2000);
        speechManager.startSpeak(randomElement);
    }

    public void saludo() {
        // cambiar emoción a contento
        changeEmotion(EmotionsType.LAUGHTER);
        // saludar con el brazo derecho
        speechManager.startSpeak("Hola, soy Sanbot");
        if(controlBrazos(TipoBrazo.DERECHO, 10, 30)){
            if(controlBrazos(TipoBrazo.DERECHO, 10, 50)){
                if(controlBrazos(TipoBrazo.DERECHO, 10, 30)){
                    controlBrazos(TipoBrazo.DERECHO, 10, 50);
                }
            }
        }
    }

    public boolean controlBasicoBrazos(AccionesBrazos accion, TipoBrazo brazo) {
        byte[] absolutePart = new byte[]{AbsoluteAngleHandMotion.PART_LEFT, AbsoluteAngleHandMotion.PART_RIGHT, AbsoluteAngleHandMotion.PART_BOTH};
        AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], 10, 0);
        switch(accion) {
            case LEVANTAR_BRAZO:
                switch (brazo) {
                    case IZQUIERDO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], 10, 10);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case DERECHO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[1], 10, 10);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case AMBOS:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], 10, 10);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                }
                break;
            case BAJAR_BRAZO:
                switch (brazo) {
                    case IZQUIERDO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], 10, 170);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case DERECHO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[1], 10, 170);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case AMBOS:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], 10, 170);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                }
                break;
        }
        return true;
    }

    public boolean controlBrazos(TipoBrazo brazo, int velocidad, int angulo) {
        byte[] absolutePart = new byte[]{AbsoluteAngleHandMotion.PART_LEFT, AbsoluteAngleHandMotion.PART_RIGHT, AbsoluteAngleHandMotion.PART_BOTH};
        if (brazo.equals(TipoBrazo.DERECHO)) {
            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[1], velocidad, angulo);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        } else if (brazo.equals(TipoBrazo.IZQUIERDO)) {
            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], velocidad, angulo);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        } else if (brazo.equals(TipoBrazo.AMBOS)) {
            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], velocidad, angulo);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        }
        return true;
    }

    public boolean controlBasicoTronco(AccionesTronco accion, TipoDireccion direccion){
        RelativeAngleWheelMotion movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 360);
        switch (accion){
            case GIRO_90:
                switch (direccion){
                    case DERECHA:
                        movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, 5, 90);
                        wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
                        break;
                    case IZQUIERDA:
                        movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 90);
                        wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
                        break;
                }
                break;
            case GIRO_180:
                switch (direccion){
                    case DERECHA:
                        movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, 5, 180);
                        wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
                        break;
                    case IZQUIERDA:
                        movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 180);
                        wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
                        break;
                }
                break;
            case GIRO_360:
                switch (direccion){
                    case DERECHA:
                        movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, 5, 360);
                        wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
                        break;
                    case IZQUIERDA:
                        movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 360);
                        wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
                        break;
                }
                break;
        }
        return true;
    }

    public boolean controlTronco(TipoDireccion direccion, int velocidad, int angulo) {
        RelativeAngleWheelMotion movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 360);
        if (direccion.equals(TipoDireccion.DERECHA)) {
            movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, velocidad, angulo);
            wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
        } else{
            movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, velocidad, angulo);
            wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
        }
        return true;
    }

    public boolean controlBasicoCabeza(AccionesCabeza accion){
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
        return true;
    }

    public void senalarCielo() throws InterruptedException {
        controlBasicoCabeza(AccionesCabeza.ARRIBA);
        Thread.sleep(2000);
        controlBrazos(TipoBrazo.DERECHO, 8, 70);
        speechManager.startSpeak("Mi caaaasa");
        Thread.sleep(3000);
        controlBasicoCabeza(AccionesCabeza.CENTRO);
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.DERECHO);
    }

    public void formularPregunta(String pregunta, String respuestaAReconocer, int intentos) throws InterruptedException {
        System.out.println("Digo la pregunta");
        speechManager.startSpeak(pregunta);
        Thread.sleep(5000);
        System.out.println("Selecciono equipo");
        elegirEquipo(Arrays.asList("AZUL", "AMARILLO", "VERDE", "ROJO"));
        //Thread.sleep(3000);
        System.out.println("Voy a detectar la respuesta");
        if(esperarRespuesta(respuestaAReconocer, intentos)){
            reaccionRespuestaCorrecta();
        }
        else{
            reaccionRespuestaIncorrecta();
            speechManager.startSpeak("La respuesta era");
            Thread.sleep(1500);
            speechManager.startSpeak(respuestaAReconocer);
        }
    }

    public boolean reconocimientoVoz(String respuestaAReconocer) throws InterruptedException {
        final boolean[] reconocimientoCorrecto = {false};
        System.out.println("Se despierta al robot");
        speechManager.setOnSpeechListener(new RecognizeListener() {
            @Override
            public boolean onRecognizeResult(Grammar grammar) {
                System.out.println("He reconocido " + grammar.getText());
                if (grammar.getText().equals(respuestaAReconocer)) { // MIRAR COMO RECONOCER PAUSA (!!)
                    System.out.println("Se ha reconocido la respuesta correcta");
                    reconocimientoCorrecto[0] = true;
                    return true;
                }
                else{
                    System.out.println("No se ha reconocido la respuesta incorrecta");
                    reconocimientoCorrecto[0] = false;
                    return false;
                }
            }
            @Override
            public void onRecognizeVolume(int i) {

            }

            @Override
            public void onStartRecognize() {

            }

            @Override
            public void onStopRecognize() {

            }

            @Override
            public void onError(int i, int i1) {

            }
        });
        Thread.sleep(5000);
        System.out.println("Voy a evaluar la respuesta");
        if(reconocimientoCorrecto[0]==true){
            System.out.println("Respuesta correcta reconocida");
            return true;
        }
        else{
            System.out.println("Respuesta incorrecta reconocida" + reconocimientoCorrecto[0]);
            return false;
        }
    }

    public void ejecutarInteraccion(String respuestaAReconocer) throws InterruptedException {
        /*
        while(!(interaccionRobot(interaccion))){
            interaccionRobot(interaccion);
        }

         */
        /*
        if(reconocimientoVoz(respuestaAReconocer)){
            speechManager.startSpeak("Muy bien");
        }
        else{
            speechManager.startSpeak("Muy mal");
        }

         */
    }

    public boolean esperarRespuesta(String respuestaAReconocer, int intentos) throws InterruptedException {
        boolean respuestaCorrecta = false;
        while(intentos>0 && !respuestaCorrecta){
            System.out.println("Intento" + intentos);
            speechManager.doWakeUp();
            /*
            if(!reconocimientoVoz(respuestaAReconocer)){
                Thread.sleep(2000);
                System.out.println("Se ha fallado la respuesta");
                //reaccionRespuestaIncorrectaPruebaOtraVez();
                speechManager.startSpeak("Prueba otra vez");
                intentos--;
            }
            else{
                Thread.sleep(2000);
                System.out.println("Se ha acertado la respuesta");
                respuestaCorrecta = true;
            }

             */
        }
        return true;
    }

    public boolean pruebaDialogoPlanetario() throws InterruptedException {
        speechManager.startSpeak("Hola. Estoy buscando a Drako. ¿Le habéis visto?");
        ejecutarInteraccion("hola quien eres tu"); // COMPROBAR PAUSA (!!)
        //speechManager.startSpeak("Me llamo Arturito y estoy buscando a Drako. Habíamos quedado en vernos en un planeta del sistema solar. Pero no recuerdo en cual");
        //saludo();
        // TIEMPO DE ESPERA HABLAR MONITOR O PREGUNTAR EL PROPIO ROBOT (!!)
        Thread.sleep(5000);
        // OPCION 1: EL ROBOT PREGUNTA Y RECONOCE RESPUESTA
        // COMPROBAR RESPUESTA PROLONGADA (!!)
        //reaccionTriste();
        speechManager.startSpeak("¿Podéis ayudarme?");
        ejecutarInteraccion("si");
        // OPCION 2: EL ROBOT PREGUNTA Y ESPERA RESPUESTA
        //reaccionTriste();
        //speechManager.startSpeak("¿Podéis ayudarme?");
        Thread.sleep(5000);
        // OPCION 3: MONITOR PREGUNTA Y ROBOT ESPERA RESPUESTA
        //Thread.sleep(6000);
        // .......
        return true;

    }

    public void repetirAccionTronco(AccionesTronco accion, TipoDireccion direccion, int repeticiones) throws InterruptedException {
        while(repeticiones>0){
            repeticiones--;
            if(controlBasicoTronco(accion, direccion)){
                continue;
            };
            Thread.sleep(3000);
        }
    }

    public void presentacionAffectiveLab() throws InterruptedException {
        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(55);
        speakOption.setIntonation(50);

        changeEmotion(EmotionsType.SMILE);
        speechManager.startSpeak("Hola, buenos días, mi nombre es Sanbot y os voy a presentar a nuestro grupo Affectif Lab", speakOption);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Nuestro equipo es reconocido como un grupo de referencia por el Gobierno de Aragón y forma parte del I3A", speakOption);
        try {
            Thread.sleep(6500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // el grupo affectivelab es un grupo multidisciplinar en el que hay investigadores procedentes de areas como ingenieria, ...
        speechManager.startSpeak("Affectif Lab es un grupo multidisciplinar en el que hay investigadores procedentes de areas como la ingeniería, la psicología, la sociología, la educación, y la geriatría", speakOption);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Nuestra área de investigación es la interacción y el desarrollo de interfaces", speakOption);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Hemos trabajado con interfaces multimodales mediante personajes 3D virtuales", speakOption);
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speechManager.startSpeak("También con interfaces tangibles, como nuestras mesas interactivas, en éstas interfaces los periféricos habituales como el ratón o el teclado" +
                "son sustituidos por objetos de uso común", speakOption);
        try {
            Thread.sleep(13000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speechManager.startSpeak("Y no olvidemos las interfaces afectivas basadas en el reconocimiento" +
                "del estado emocional del usuario a partir de reconocimiento facial o medidas fisiológicas", speakOption);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("En cuanto accesibilidad nuestro objetivo es favorecer la inclusión" +
                "de usuarios con necesidades especiales y explorar los beneficios terapéuticos, educativos, y sociales de los nuevos" +
                "modos de interacción digital", speakOption);
        try {
            Thread.sleep(17000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speechManager.startSpeak("Actualmente nuestro grupo lidera un proyecto nacional coordinado por la universidad de zaragoza llamado PLEISAR," +
                "en el que participan investigadores de la Universidad de las Islas Baleares, Universidad de Granada," +
                "e investigadores de la universidad de La Laguna", speakOption);
        try {
            Thread.sleep(18000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("En éste proyecto se ha comenzado a trabajar con asistentes por voz como Alexa y con robots sociales como...", speakOption);
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speechManager.startSpeak("¡Espera!");
        changeEmotion(EmotionsType.PRISE);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speechManager.startSpeak("¡Mirad eso!");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        controlTronco(TipoDireccion.DERECHA, 5, 90);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        controlBrazos(TipoBrazo.DERECHO, 8, 70);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speechManager.startSpeak("¡Ese soy yo!", speakOption);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        controlBasicoTronco(AccionesTronco.GIRO_90, TipoDireccion.IZQUIERDA);
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.DERECHO);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        changeEmotion(EmotionsType.SNICKER);
        speechManager.startSpeak("Ah, y tambien tengo a uno de mis primos en Teruel, trabajando con los Amantes", speakOption);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speechManager.startSpeak("Y bueno, eso es todo", speakOption);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        changeEmotion(EmotionsType.SLEEP);
        controlBasicoCabeza(AccionesCabeza.ABAJO);
        controlBrazos(TipoBrazo.AMBOS, 7, 230);
        speechManager.startSpeak("Muchas gracias por vuestra atención", speakOption);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        controlBasicoCabeza(AccionesCabeza.CENTRO);
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.AMBOS);
        changeEmotion(EmotionsType.LAUGHTER);
    }

    /*
    public void pruebaAPITwitter(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create URL object
                    URL url = new URL("https://api.twitter.com/2/tweets");


                    // Open a connection to the URL
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();


                    // Set the request method to POST
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);


                    /*
                    // Create JSON payload
                    JSONObject jsonInput = new JSONObject();
                    jsonInput.put("somevalue", someValue);
                    jsonInput.put("sentence", sentence);


                    // Write data to the connection output stream
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonInput.toString().getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }


                    // Get the response from the API
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }


                        String response_toString = response.toString();
                        Log.i(TAG, "API Response: " + response_toString);


                        try {
                            JSONObject jsonResponse = new JSONObject(response_toString);
                            String message = jsonResponse.getString("message");
                            messageApi = message;
                            Log.i(TAG, "Message: " + message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.e(TAG, "Error logging API response: " + e.getMessage());
                    }


                    // Close the connection
                    connection.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

     */
    public void pruebaModulos() throws InterruptedException {
        //presentacionAffectiveLab();
        /*
        controlBasicoTronco(AccionesTronco.GIRO_360, TipoDireccion.IZQUIERDA);
        Thread.sleep(5000);
        controlBasicoTronco(AccionesTronco.GIRO_360, TipoDireccion.DERECHA);
        Thread.sleep(5000);
        controlBasicoTronco(AccionesTronco.GIRO_180, TipoDireccion.IZQUIERDA);
        Thread.sleep(5000);
        controlBasicoTronco(AccionesTronco.GIRO_180, TipoDireccion.DERECHA);
        Thread.sleep(5000);
        controlBasicoTronco(AccionesTronco.GIRO_90, TipoDireccion.IZQUIERDA);
        Thread.sleep(5000);
        controlBasicoTronco(AccionesTronco.GIRO_90, TipoDireccion.DERECHA);
        Thread.sleep(5000);
        controlBasicoBrazos(AccionesBrazos.LEVANTAR_BRAZO, TipoBrazo.DERECHO);
        Thread.sleep(5000);
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.DERECHO);
        Thread.sleep(5000);
        controlBasicoBrazos(AccionesBrazos.LEVANTAR_BRAZO, TipoBrazo.IZQUIERDO);
        Thread.sleep(5000);
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.IZQUIERDO);
        Thread.sleep(5000);
        controlBasicoBrazos(AccionesBrazos.LEVANTAR_BRAZO, TipoBrazo.AMBOS);
        Thread.sleep(5000);
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO, TipoBrazo.AMBOS);
        Thread.sleep(5000);
         */
        senalarCielo();
        Thread.sleep(8000);
        reaccionChocarCinco();
        Thread.sleep(5000);
        saludo();
        Thread.sleep(9000);
        reaccionTriste();
        Thread.sleep(5000);
        reaccionAlegre();
        Thread.sleep(10000);
        hacerOla();

        /*
        speechManager.startSpeak("de que color es el caballo blanco de santiago");
        Thread.sleep(1000);
        speechManager.doWakeUp();
        Thread.sleep(3000);
        reconocimientoVoz("blanco");
        if((reconocimientoVoz("blanco"))){
            System.out.println("Reconocido bien");
            speechManager.startSpeak("bien");
        }
        else{
            speechManager.startSpeak("mal");
            System.out.println("Reconocido mal");
        }

         */
    }
}
