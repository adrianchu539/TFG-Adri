package com.example.sanbotapp;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.sanbot.opensdk.base.TopBaseActivity;
import com.sanbot.opensdk.beans.FuncConstant;
import com.sanbot.opensdk.function.beans.EmotionsType;
import com.sanbot.opensdk.function.beans.FaceRecognizeBean;
import com.sanbot.opensdk.function.beans.LED;
import com.sanbot.opensdk.function.beans.SpeakOption;
import com.sanbot.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.sanbot.opensdk.function.beans.handmotion.NoAngleHandMotion;
import com.sanbot.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.sanbot.opensdk.function.beans.headmotion.LocateAbsoluteAngleHeadMotion;
import com.sanbot.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.sanbot.opensdk.function.beans.wheelmotion.DistanceWheelMotion;
import com.sanbot.opensdk.function.beans.wheelmotion.NoAngleWheelMotion;
import com.sanbot.opensdk.function.beans.wheelmotion.RelativeAngleWheelMotion;
import com.sanbot.opensdk.function.unit.HandMotionManager;
import com.sanbot.opensdk.function.unit.HardWareManager;
import com.sanbot.opensdk.function.unit.HeadMotionManager;
import com.sanbot.opensdk.function.unit.ProjectorManager;
import com.sanbot.opensdk.function.unit.SpeechManager;
import com.sanbot.opensdk.function.unit.SystemManager;
import com.sanbot.opensdk.function.unit.WheelMotionManager;
import com.sanbot.opensdk.function.unit.interfaces.hardware.PIRListener;
import com.sanbot.opensdk.function.unit.interfaces.media.FaceRecognizeListener;

import java.util.Arrays;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


public class PresentacionActivity extends TopBaseActivity {

    private SpeechManager speechManager; //voice, speechRec
    private HeadMotionManager headMotionManager;    //head movements
    private HandMotionManager handMotionManager;    //hands movements
    private SystemManager systemManager; //emotions
    private HardWareManager hardWareManager; //leds //touch sensors //voice locate //gyroscope
    private WheelMotionManager wheelMotionManager;
    private MediaManager mediaManager;
    private ProjectorManager projectorManager;
    private AudioManager audioManager;

    private WheelControlActivity wheelControlActivity;

    private Button btnpresentacion;
    private Button btnreconocimientofacial;

    public Boolean reconocimientoFacial = false;

    private volatile long tiempoRestante;
    private volatile int distanciaRestante = 0;
    private final AtomicBoolean movimientoDetectado = new AtomicBoolean(false);

    MediaPlayer mp1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        register(PresentacionActivity.class);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentacion);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);
        projectorManager = (ProjectorManager) getUnitManager(FuncConstant.PROJECTOR_MANAGER);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        btnpresentacion = findViewById(R.id.btnpresentacion);
        btnpresentacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPresentation();
            }
        });

        btnreconocimientofacial = findViewById(R.id.btn_opcion1);
        btnreconocimientofacial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reconocimientoFacial = true;
                setReconocimientoFacial();
            }
        });

        mp1 = MediaPlayer.create(PresentacionActivity.this,R.raw.musica);
        mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp1.release();
            }
        }    );

        mediaManager.setMediaListener(new FaceRecognizeListener() {
            @Override
            public void recognizeResult(List<FaceRecognizeBean> list) {
                StringBuilder sb = new StringBuilder();
                for (FaceRecognizeBean bean : list) {
                    sb.append(new Gson().toJson(bean));
                    sb.append("\n");


                    // Acceder al valor de la propiedad "user"
                    String user = bean.getUser();
                    // Hacer algo con el valor de "user"
                    System.out.println("Usuario reconocido: " + user);

                    if(user != "" && reconocimientoFacial){
                        if(user == "Adri"){
                            speechManager.startSpeak("hola " + user + " ¿cómo estás?");
                        } else {
                            speechManager.startSpeak("Tu no eres Adri, eres " + user + " , sabes donde está Adrián?");
                        }
                    }

                }
                System.out.println("Persona reconocida????：" + sb.toString());


            }
        });

    }

    // Método para avanzar
    public void avanzarConEsperas(int velocidad, int distancia) {
        DistanceWheelMotion distanceWheelMotion = new DistanceWheelMotion(DistanceWheelMotion.ACTION_FORWARD_RUN, velocidad, distancia);
        wheelMotionManager.doDistanceMotion(distanceWheelMotion);

        // Calcular el tiempo de espera necesario
        long tiempoEspera = (long) (5000 * (distancia / 100.0)); // Convertir la distancia a segundos
        tiempoRestante = tiempoEspera;
        distanciaRestante = (int) (tiempoRestante * 100 / 5000);

        // Crear y ejecutar un hilo para el bucle
        Thread bucleThread = new Thread(() -> {
            try {
                long tiempoInicio = System.currentTimeMillis();
                System.out.println("Tiempo de espera: " + tiempoInicio);

                while (distanciaRestante > 0) {
                    if (movimientoDetectado.get()) {
                        System.out.println("Detenido por detección de movimiento");
                        break; // Salir del bucle
                    }

                    System.out.println("Tiempo restante: " + tiempoRestante + " distancia recorrida: " + distanciaRestante + " tiempo de espera: " + tiempoEspera + " tiempo inicio: " + tiempoInicio + " distancia: " + distancia);
                    tiempoRestante = tiempoEspera - (System.currentTimeMillis() - tiempoInicio);
                    distanciaRestante = (int) (tiempoRestante * 100 / 5000);
                    Thread.sleep(100); // Actualizar cada 100 milisegundos
                }

                System.out.println("FINAAL Tiempo restante: " + tiempoRestante + " distancia recorrida: " + distanciaRestante + " pirdetection: " + tiempoEspera + " tiempo inicio: " + tiempoInicio + " distancia: " + distancia);

                /*if (distanciaRestante > 0) {
                    System.out.println("Distancia restante: " + distanciaRestante);
                    speechManager.startSpeak("No puedo avanzar, hay un obstáculo en el camino");

                    avanzar(5, distanciaRestante);
                }*/

            } catch (InterruptedException e) {
                System.out.println("Error al avanzar: " + e.getMessage());
                e.printStackTrace();
            }
        });

        if (movimientoDetectado.get()) {
            bucleThread.interrupt();
            if (distanciaRestante > 0) {
                System.out.println("Distancia restante: " + distanciaRestante);
                speechManager.startSpeak("No puedo avanzar, hay un obstáculo en el camino");
                movimientoDetectado.set(false);
                avanzarConEsperas(5, distanciaRestante);
            }
        } else {
            // Iniciar el hilo del bucle
            bucleThread.start();
        }

        // Configurar el listener del hardware
        hardWareManager.setOnHareWareListener(new PIRListener() {
            @Override
            public void onPIRCheckResult(boolean isCheck, int part) {
                System.out.print((part == 1 ? "delante del cuerpo" : "Detras del cuerpo") + " detectado");

                // Si se detecta movimiento delante del robot
                if (part == 1) {
                    movimientoDetectado.set(true);
                    System.out.println("FINAAL PIR Tiempo restante: " + tiempoRestante + "distancia restante: " + distanciaRestante);

                } else {
                    System.out.println("FINAAL PIR Tiempo restante: " + tiempoRestante + "distancia recorrida: " + distanciaRestante);
                }
            }
        });

    }




    public void reproducirVideo(){
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2, 0);

        // Cambiamos el layout de la actividad por el layout del video
        setContentView(R.layout.video);

        // Obtener la referencia al VideoView en tu layout XML
        VideoView videoView = findViewById(R.id.videoView);

        // Establecer la URI del archivo de video ubicado en res/raw
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.emocionesingles);

        // Establecer la URI del video en el VideoView
        videoView.setVideoURI(videoUri);

        // Comenzar la reproducción del video
        videoView.start();

        // Establecer un Listener para detectar cuando el video haya terminado de reproducirse
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Puedes realizar alguna acción aquí si lo necesitas
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) , 0);
                setContentView(R.layout.activity_presentacion);
                projectorManager.switchProjector(false);
                girarDerecha(5, 180);
            }
        });
    }

    public void startProyector(){
        projectorManager.switchProjector(true);
        projectorManager.setMode(ProjectorManager.MODE_WALL);
        projectorManager.setBright(31);
        projectorManager.setTrapezoidV(0);
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void avanzar(int velocidad, int distancia) {
        DistanceWheelMotion distanceWheelMotion = new DistanceWheelMotion(DistanceWheelMotion.ACTION_FORWARD_RUN, velocidad, distancia);
        wheelMotionManager.doDistanceMotion(distanceWheelMotion);

        long tiempoEspera = (long) (5000 * (distancia / 100.0));
        try {
            Thread.sleep(tiempoEspera);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Método para girar a la izquierda
    public void  girarIzquierda(int velocidad, int angulo) {
        RelativeAngleWheelMotion relativeAngleWheelMotion = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, velocidad, angulo);
        wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // Método para girar a la derecha
    public void girarDerecha(int velocidad, int angulo) {
        RelativeAngleWheelMotion relativeAngleWheelMotion = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, velocidad, angulo);
        wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Hacer un cuadrado
    public void cuadrado(int tamano) {
        avanzar(5, tamano);
        girarDerecha(5, 90);
        avanzar(5, tamano);
        girarDerecha(5, 90);
        avanzar(5, tamano);
        girarDerecha(5, 90);
        avanzar(5, tamano);
        girarDerecha(5, 90);
    }


    private void startPresentation() {

        //SPEECH, velocidad y tono del dialogo
        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);

        // INTRO --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak("Hola, soy SanBot, un robot de integración sensorial y robótica. Me encanta interactuar con las personas y ayudarlas en lo que necesiten. ", speakOption);
        try {
            Thread.sleep(9000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // LEDS --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak(" No solo puedo hablar, ¿ sabes que también puedo cambiar el color de mi cuerpo ?", speakOption);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //flicker led
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RANDOM));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // HEAD MOVEMENT --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak("También puedo mover mi cabeza, ", speakOption);
        //head movement
        AbsoluteAngleHeadMotion absoluteAngleHeadMotion1 = new AbsoluteAngleHeadMotion(
                AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,130
        );

        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion1);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //head movement
        absoluteAngleHeadMotion1 = new AbsoluteAngleHeadMotion(
                AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,90
        );
        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion1);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // HAND MOVEMENT --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak("mis brazos...", speakOption);
        //hand up
        AbsoluteAngleHandMotion absoluteAngleWingMotion3 = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_LEFT, 5, 70);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion3);
        absoluteAngleWingMotion3 = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT, 5, 70);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion3);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //hands down (reset position)
        handMotionManager.doNoAngleMotion(new NoAngleHandMotion(NoAngleHandMotion.PART_BOTH, 5,NoAngleHandMotion.ACTION_RESET));

        // DANCE  --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak("y por su puesto ¡ Puedo bailar !", speakOption);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // PLAY MUSIC
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2, 0);

        mp1.start();

        systemManager.showEmotion(EmotionsType.SMILE);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RANDOM_THREE_GROUP));
        AbsoluteAngleHandMotion absoluteAngleWingMotion = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH, 5, 0);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
        RelativeAngleWheelMotion relativeAngleWheelMotion = new RelativeAngleWheelMotion(
                RelativeAngleWheelMotion.TURN_LEFT, 5,360
        );
        wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //hands down (reset position)
        handMotionManager.doNoAngleMotion(new NoAngleHandMotion(NoAngleHandMotion.PART_BOTH, 5,NoAngleHandMotion.ACTION_RESET));
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mp1.stop();

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) , 0);


        // EMOTIONS --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak("A pesar de ser un robot, también puedo mostrar emociones", speakOption);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ENAMORADO
        speakOption.setSpeed(70);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_PINK));
        systemManager.showEmotion(EmotionsType.KISS);
        AbsoluteAngleHandMotion absoluteAngleWingMotion1 = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH, 5, 70);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion1);
        speechManager.startSpeak("Cuando estoy enamorada no se como ocultarlo", speakOption);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TRISTE
        speakOption.setSpeed(50);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_BLUE));
        systemManager.showEmotion(EmotionsType.CRY);
        handMotionManager.doNoAngleMotion(new NoAngleHandMotion(NoAngleHandMotion.PART_BOTH, 5,NoAngleHandMotion.ACTION_RESET));
        AbsoluteAngleHeadMotion absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL, 7);
        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
        speechManager.startSpeak("O cuando estoy triste no puedo evitar llorar ", speakOption);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speakOption.setSpeed(60);
        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_CLOSE));
        systemManager.showEmotion(EmotionsType.SMILE);
        AbsoluteAngleHeadMotion absoluteAngleHeadMotion2 = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL, 30);
        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion2);
        speechManager.startSpeak("Pero hoy estoy muy contenta de poder enseñaros todo lo que se hacer", speakOption);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // NAVEGACION --------------------------------------------------------------------------------------------------------------------------

        girarDerecha(5, 90);
        avanzar(5, 300);
        girarDerecha(5, 180);

        systemManager.showEmotion(EmotionsType.SMILE);
        AbsoluteAngleHandMotion absoluteAngleWingMotionlab = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH, 5, 70);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotionlab);
        speechManager.startSpeak("El laboratorio 2 0 7 es mi casa, y estoy muy contenta de poder enseñárosla hoy", speakOption);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handMotionManager.doNoAngleMotion(new NoAngleHandMotion(NoAngleHandMotion.PART_BOTH, 5,NoAngleHandMotion.ACTION_RESET));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        avanzar(5, 300);
        girarDerecha(5, 90);


        // PROYECTOR  --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak(" ¿ Os gustaría ver un vídeo sobre algunas cosas que hacemos aquí en el Affectif lab ?", speakOption);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak(" Vamos, os voy a proyectar un vídeo", speakOption);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // NAVEGAR
        avanzar(5, 100);
        // PROYECTOR
        startProyector();
        reproducirVideo();

    }

    public void setReconocimientoFacial(){
        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);
        // RECONOCIMIENTO FACIAL  --------------------------------------------------------------------------------------------------------------------------
        speechManager.startSpeak(" ¿ sabeis que también dispongo de un módulo de reconocimiento facial ? ¿ Adrián, puedes acercarte a mi ? ", speakOption);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        reconocimientoFacial = true;
    }


    @Override
    protected void onMainServiceConnected() {

    }

}
