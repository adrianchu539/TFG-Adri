package com.example.sanbotapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.sanbot.opensdk.base.TopBaseActivity;
import com.sanbot.opensdk.beans.FuncConstant;
import com.sanbot.opensdk.function.beans.EmotionsType;
import com.sanbot.opensdk.function.beans.FaceRecognizeBean;
import com.sanbot.opensdk.function.beans.LED;
import com.sanbot.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.sanbot.opensdk.function.beans.handmotion.NoAngleHandMotion;
import com.sanbot.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.sanbot.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.sanbot.opensdk.function.beans.wheelmotion.DistanceWheelMotion;
import com.sanbot.opensdk.function.beans.wheelmotion.NoAngleWheelMotion;
import com.sanbot.opensdk.function.beans.wheelmotion.RelativeAngleWheelMotion;
import com.sanbot.opensdk.function.unit.HandMotionManager;
import com.sanbot.opensdk.function.unit.HardWareManager;
import com.sanbot.opensdk.function.unit.HeadMotionManager;
import com.sanbot.opensdk.function.unit.SpeechManager;
import com.sanbot.opensdk.function.unit.SystemManager;
import com.sanbot.opensdk.function.unit.WheelMotionManager;
import com.sanbot.opensdk.function.unit.interfaces.media.FaceRecognizeListener;

import java.util.Arrays;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;




public class PresentacionActivity extends TopBaseActivity {

    private SpeechManager speechManager; //voice, speechRec
    private HeadMotionManager headMotionManager;    //head movements
    private HandMotionManager handMotionManager;    //hands movements
    private SystemManager systemManager; //emotions
    private HardWareManager hardWareManager; //leds //touch sensors //voice locate //gyroscope
    private WheelMotionManager wheelMotionManager;
    private MediaManager mediaManager;

    private Button btnpresentacion;

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

        btnpresentacion = findViewById(R.id.btnpresentacion);

        btnpresentacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPresentation();

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

                    if(user != ""){
                        if(user.equals("Adri")){
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

    private void startPresentation() {
                //intro
                speechManager.startSpeak("Hola, soy SanBot, un robot de integración sensorial y robótica. Me encanta interactuar con las personas y ayudarlas en lo que necesiten. ");
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //DISPLAY
                speechManager.startSpeak("No solo puedo hablar, ");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //show emotion
                systemManager.showEmotion(EmotionsType.KISS);
                speechManager.startSpeak("Puedo cambiar mi expresión facial");

                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                speechManager.startSpeak(" el color de mi cuerpo, ");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //flicker led
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RANDOM));

                // speechManager.startSpeak(" también puedo reproducir cosas en mi tablet y proyectarlas ");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //ACTUATORS
                speechManager.startSpeak("También puedo mover mi cabeza, ");
                //head movement
                AbsoluteAngleHeadMotion absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(
                        AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,130
                );

                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //head movement
                absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(
                        AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,90
                );
                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                speechManager.startSpeak("mis brazos...");
                //hand up
                AbsoluteAngleHandMotion absoluteAngleWingMotion = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_LEFT, 5, 70);
                handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
                absoluteAngleWingMotion = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT, 5, 70);
                handMotionManager.doAbsoluteAngleMotion(absoluteAngleWingMotion);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //hands down (reset position)
                handMotionManager.doNoAngleMotion(new NoAngleHandMotion(NoAngleHandMotion.PART_BOTH, 5,NoAngleHandMotion.ACTION_RESET));

                speechManager.startSpeak("también puedo moverme, puedo bailar!!");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //play music
                int maxVolume = 100;
                int currVolume = 50;
                float log1=(float)(Math.log(maxVolume-currVolume)/Math.log(maxVolume));
                mp1.setVolume(log1,log1);
                mp1.start();

                systemManager.showEmotion(EmotionsType.SMILE);
                hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RANDOM_THREE_GROUP));
                absoluteAngleWingMotion = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH, 5, 0);
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

                speechManager.startSpeak(" ¿ sabeís que también dispongo de un módulo de reconocimiento facial ? ¿ Adrián, puedes acercarte a mi ? ");
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_VERTICAL, 30);
                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


               // speechManager.startSpeak(" es un placer conoceros, espero que disfruteis de mi compañía. ");






    }


    @Override
    protected void onMainServiceConnected() {

    }

}
