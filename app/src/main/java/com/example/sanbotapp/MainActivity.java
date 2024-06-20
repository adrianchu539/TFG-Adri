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

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.headmotion.LocateAbsoluteAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.wheelmotion.DistanceWheelMotion;
import com.qihancloud.opensdk.function.beans.wheelmotion.RelativeAngleWheelMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.ProjectorManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;
import com.qihancloud.opensdk.function.unit.interfaces.hardware.TouchSensorListener;

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
    Button btnWheelControl;
    Button btnMediaControl;
    Button btnPresentacion;

    private Boolean respuestaCorrectaReconocida;


    @Override
    protected void onMainServiceConnected() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //register(MainActivity.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.activity_inicio);
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
        btnWheelControl = findViewById(R.id.btnWheelControl);
        btnMediaControl = findViewById(R.id.btnMediaControl);
        btnPresentacion = findViewById(R.id.btnPresentacion);

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

        btnPresentacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent para iniciar la actividad Presentacion
                Intent intent = new Intent(MainActivity.this, PresentacionActivity.class);

                // Inicia la actividad Presentacion
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

        btnWheelControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent para iniciar la actividad FaceRecognition
                Intent intent = new Intent(MainActivity.this, WheelControlActivity.class);

                // Inicia la actividad WheelControl
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
