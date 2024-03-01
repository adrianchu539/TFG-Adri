package com.example.sanbotapp;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import org.w3c.dom.Text;

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

        /*
        btnMediaControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent para iniciar la actividad MediaControl
                Intent intent = new Intent(MainActivity.this, MediaControlActivity.class);

                // Inicia la actividad MediaControl
                startActivity(intent);
            }
        });
         */

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
    // Prueba de varios módulos del robot

    public void reaccionRespuestaCorrecta() {
        // cambiar emoción a feliz
        changeEmotion(EmotionsType.SMILE);
        // respuesta para notificar que la respuesta es correcta
        speechManager.startSpeak("Muy bien, respuesta correcta");
        // subir los brazos para simular alegría
        controlBasicoBrazos(AccionesBrazos.LEVANTAR_AMBOS_BRAZOS);
    }

    public void reaccionRespuestaIncorrecta() {
        // cambiar emoción a triste
        changeEmotion(EmotionsType.GOODBYE);
        // respuesta para notificar que la respuesta es incorrecta
        speechManager.startSpeak("La respuesta es incorrecta");
        // bajar los brazos para simular tristeza
        controlBasicoBrazos(AccionesBrazos.BAJAR_AMBOS_BRAZOS);
    }

    public void reaccionChocarCinco() throws InterruptedException {
        // cambiar emoción a sorprendido
        changeEmotion(EmotionsType.PRISE);
        // respuesta para notificar que choque los cinco
        speechManager.startSpeak("Genial. Choca esos cinco");
        // sube uno de los brazos
        controlBrazos(TipoBrazo.DERECHO, 10, 70);
        Thread.sleep(10000);
        controlBasicoBrazos(AccionesBrazos.BAJAR_BRAZO_DERECHO);
    }

    public void hacerOla() throws InterruptedException {
        // bajar los brazos en caso de que no los tenga ya bajados
        controlBasicoBrazos(AccionesBrazos.BAJAR_AMBOS_BRAZOS);
        // cambiar emoción a feliz
        changeEmotion(EmotionsType.SMILE);
        // respuesta para notificar que va a hacer la ola
        speechManager.startSpeak("Hagamos una ola");
        Thread.sleep(2000);
        narrarCuentaAtras(5);
        Thread.sleep(2000);
        // subir los brazos para simular la ola
        controlBasicoBrazos(AccionesBrazos.LEVANTAR_AMBOS_BRAZOS);
        Thread.sleep(2000);
        // bajar los brazos para simular la ola
        controlBasicoBrazos(AccionesBrazos.BAJAR_AMBOS_BRAZOS);
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
        LEVANTAR_BRAZO_DERECHO,
        LEVANTAR_BRAZO_IZQUIERDO,
        LEVANTAR_AMBOS_BRAZOS,
        BAJAR_BRAZO_DERECHO,
        BAJAR_BRAZO_IZQUIERDO,
        BAJAR_AMBOS_BRAZOS;
    }

    public enum TipoBrazo {
        DERECHO,
        IZQUIERDO,
        AMBOS;
    }

    public void saludo() {
        // cambiar emoción a contento
        changeEmotion(EmotionsType.LAUGHTER);
        // saludar con el brazo derecho
        controlBrazos(TipoBrazo.DERECHO, 10, 65);
        controlBrazos(TipoBrazo.DERECHO, 10, 60);
        controlBrazos(TipoBrazo.DERECHO, 10, 65);
        controlBrazos(TipoBrazo.DERECHO, 10, 60);
    }

    public void controlBasicoBrazos(AccionesBrazos accion) {
        byte[] absolutePart = new byte[]{AbsoluteAngleHandMotion.PART_LEFT, AbsoluteAngleHandMotion.PART_RIGHT, AbsoluteAngleHandMotion.PART_BOTH};
        if (accion.equals(AccionesBrazos.LEVANTAR_BRAZO_DERECHO)) {
            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[1], 10, 0);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        } else if (accion.equals(AccionesBrazos.LEVANTAR_BRAZO_IZQUIERDO)) {
            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], 10, 0);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        } else if (accion.equals(AccionesBrazos.LEVANTAR_AMBOS_BRAZOS)) {
            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], 10, 0);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        } else if (accion.equals(AccionesBrazos.BAJAR_BRAZO_DERECHO)) {
            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[1], 10, 170);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        } else if (accion.equals(AccionesBrazos.BAJAR_BRAZO_IZQUIERDO)) {
            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], 10, 170);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        } else if (accion.equals(AccionesBrazos.BAJAR_AMBOS_BRAZOS)) {
            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], 10, 170);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        }
    }

    public void controlBrazos(TipoBrazo brazo, int velocidad, int angulo) {
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
    }

    public void pruebaModulos() throws InterruptedException {
        hacerOla();
        /*
        // Despertamos al robot para que se ponga en modo escucha
        speechManager.doWakeUp();
        // Utilizamos la función setOnSpeechListener para definir acciones tras reconocer una frase fija
        speechManager.setOnSpeechListener(new RecognizeListener() {
            @Override
            public boolean onRecognizeResult(Grammar grammar) {
                // prueba de sentimiento felicidad
                // si reconoce la frase "prueba feliz"
                if (grammar.getText().equals("prueba feliz")) {
                    // el robot responderá "estoy muy feliz"
                    speechManager.startSpeak("estoy muy feliz");
                    // levantará los brazos
                    byte[] absolutePart = new byte[]{AbsoluteAngleHandMotion.PART_LEFT, AbsoluteAngleHandMotion.PART_RIGHT, AbsoluteAngleHandMotion.PART_BOTH};
                    AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], 10, 0);
                    handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                    // y cambiará su cara a feliz
                    changeEmotion(EmotionsType.LAUGHTER);
                    return true;
                }
                // prueba de sentimiento tristeza
                // si reconoce la frase "prueba triste"
                else if (grammar.getText().equals("prueba triste")) {
                    // el robot responderá "estoy muy triste"
                    speechManager.startSpeak("estoy muy triste");
                    // bajará los brazos
                    byte[] absolutePart = new byte[]{AbsoluteAngleHandMotion.PART_LEFT, AbsoluteAngleHandMotion.PART_RIGHT, AbsoluteAngleHandMotion.PART_BOTH};
                    AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], 10, 170);
                    handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                    // y cambiará su cara a triste
                    changeEmotion(EmotionsType.CRY);
                    return true;
                }
                else{
                    return false;
                }


            }


            @Override
            public void onRecognizeVolume(int i) {
                Log.i("Cris", "onRecognizeVolume: ");
            }

            @Override
            public void onStartRecognize() {
                Log.i("Cris", "onStartRecognize: ");
            }

            @Override
            public void onStopRecognize() {
                Log.i("Cris", "onStopRecognize: ");
            }

            @Override
            public void onError(int i, int i1) {
                Log.i("Cris", "onError: i="+i+" i1="+i1);
            }
        }
        */
    }
}
