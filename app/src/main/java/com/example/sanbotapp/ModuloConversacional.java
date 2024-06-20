package com.example.sanbotapp;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.gson.Gson;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
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

import java.io.IOException;
import java.util.List;
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
    private WheelMotionManager wheelMotionManager;
    private MediaManager mediaManager;
    private ProjectorManager projectorManager;
    private AudioManager audioManager;

    private String preguntaChatGPT;

    private WheelControlActivity wheelControlActivity;

    private MainActivity mainActivity;

    private Button btn_iniciar_conversacion;
    private Button btnreconocimientofacial;

    private Button btnPresentacionAffectiveLab;

    private Button btnDialogoPlanetario;

    private Button btnPruebaEmociones;

    public Boolean reconocimientoFacial = false;

    private volatile long tiempoRestante;
    private volatile int distanciaRestante = 0;
    private final AtomicBoolean movimientoDetectado = new AtomicBoolean(false);

    private boolean pirdetection = false;

    // array con las preguntas que el robot tiene que reconocer, las respuestas que tiene que dar,
    // el tiempo aproximado de espera

    private String[][] pregunta_respuesta =
            {
                    {"hola quien eres tu", "Me llamo Arturito y estoy buscando a Drako, habíamos quedado en vernos en un planeta" +
                            "del sistema solar pero no recuerdo en cual habiamos quedado", "7"},
                    {"mala memoria", "Chicos y chicas, ¿ podéis ayudarme a encontrar el planeta en el que está Drako ?", "5"},
                    {"si", "Genial, muchas gracias", "3"},
                    {"planeta mas cercano al sol", "¿ Sabéis cuál es ?", "3"},
                    {"mercurio", "¿ Y cómo es Mercurio ? ¿Es el planeta más, ?", "3"},
                    {"pequeno", "¿Y es el planeta más, ?", "3"},
                    {"rapido", "muy bien", "3"},
                    {"el planeta en el que has quedado con draco", "No, no me suena, además en Mercurio no hay atmósfera y hace mucho calor", "5"},
                    {"bueno sigamos buscando", "¿ sabéis qué planeta viene después ?", "3"},
                    {"venus", "¿ Venus ? No creo, no me suena, tengo entendido que hace mucho calor", "5"},
                    {"tienes razon", "Entonces, ¿ qué planeta viene luego ?", "3"},
                    {"marte", "¿ y qué color tiene ?", "3"},
                    {"rojo", "Pues tampoco me suena, no recuerdo que hubiéramos hablado nada de color rojo", "5"},
                    {"planetas gaseosos", "¿ gaseosos ? ¿ qué quieres decir ?", "3"},
                    {"globos gigantes", "y ¿ cuál es el primero de los gaseosos ?", "3"},
                    {"jupiter", "¿ y qué característica tiene ?", "3"},
                    {"mas grande", "quizá, podría ser, ¿ hay más planetas gaseosos ?", "3"},
                    {"claro", "niños, ¿ cuál es el siguiente ?", "3"},
                    {"saturno", "¿ Saturno ? Me suena, tiene algo especial pero no recuerdo qué es", "4"},
                    {"los anillos", "Pues sí, recuerdo haber hablado con Drako algo sobre planetas con anillos pero Saturno no me suena, ¿ cuál es el siguiente ?", "7"},
                    {"urano", "Urano, es cierto, pero le pasa lo contrario que a Venus, ¿ que es el planeta más, ?", "5"},
                    {"frio", "No, no creo que Drako esté allí, sólo falta uno, ¿ cuál es ?", "4"},
                    {"neptuno", "Sí, es cierto, es el último, pero está muy lejos, ¿ y allí acaba el sistema solar ?", "5"},
                    {"profes estudiaron otro como planeta", "¿ alguien sabe cuál es ?", "3"},
                    {"pluton", "Ay, sí, es un planeta enano o planetoide", "3"},
                    {"fin conversacion"}
            };



    MediaPlayer mp1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //register(PresentacionActivity.class);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);
        projectorManager = (ProjectorManager) getUnitManager(FuncConstant.PROJECTOR_MANAGER);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        emotions = new EmotionsType[]{EmotionsType.ARROGANCE, EmotionsType.SURPRISE, EmotionsType.WHISTLE, EmotionsType.LAUGHTER, EmotionsType.GOODBYE,
                EmotionsType.SHY, EmotionsType.SWEAT, EmotionsType.SNICKER, EmotionsType.PICKNOSE, EmotionsType.CRY, EmotionsType.ABUSE,
                EmotionsType.ANGRY, EmotionsType.KISS, EmotionsType.SLEEP, EmotionsType.SMILE, EmotionsType.GRIEVANCE, EmotionsType.QUESTION,
                EmotionsType.FAINT, EmotionsType.PRISE, EmotionsType.NORMAL};

        btn_iniciar_conversacion = findViewById(R.id.btn_iniciar_conversacion);
        btn_iniciar_conversacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_modulo_conversacional);
            }
        });


        mp1 = MediaPlayer.create(ModuloConversacional.this,R.raw.musica);
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
                RelativeAngleWheelMotion.TURN_LEFT, 3,360
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

    // PRESENTACIÓN AFFECTIVE LAB --------------------------------------------------------------------------------------------------------------------------
    public void presentacionAffectiveLab() throws InterruptedException {
        // definimos la velocidad y entonación para la presentación
        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(55);
        speakOption.setIntonation(50);

        // cambio la emoción a sonreír
        changeEmotion(EmotionsType.SMILE);

        controlBrazos(PresentacionActivity.TipoBrazo.DERECHO, 8, 70);
        speechManager.startSpeak("Hola, buenos días, mi nombre es Sanbot y os voy a presentar a nuestro grupo Affectif Lab", speakOption);
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        controlBasicoBrazos(PresentacionActivity.AccionesBrazos.BAJAR_BRAZO, PresentacionActivity.TipoBrazo.DERECHO);

        speechManager.startSpeak("Nuestro equipo es reconocido como un grupo de referencia por el Gobierno de Aragón", speakOption);
        try {
            Thread.sleep(5500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Además forma parte del I3A", speakOption);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Affectif Lab es un grupo multidisciplinar", speakOption);
        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("En él hay investigadores procedentes de áreas como la ingeniería, la psicología, la sociología, la educación, y la geriatría", speakOption);
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
        speechManager.startSpeak("También con interfaces tangibles, como nuestras mesas interactivas", speakOption);
        try {
            Thread.sleep(6500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("En éstas interfaces los periféricos habituales como el ratón o el teclado" +
                "son sustituidos por objetos de uso común", speakOption);
        try {
            Thread.sleep(9500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Y no olvidemos las interfaces afectivas basadas en el reconocimiento" +
                "del estado emocional del usuario", speakOption);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Realizándolo por medio de reconocimiento facial o medidas fisiológicas", speakOption);
        try {
            Thread.sleep(7500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("En cuanto accesibilidad nuestro objetivo es favorecer la inclusión" +
                "de usuarios con necesidades especiales", speakOption);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("Con ello conseguimos explorar los beneficios terapéuticos, educativos, y sociales de los nuevos" +
                "modos de interacción digital", speakOption);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speechManager.startSpeak("Actualmente nuestro grupo lidera un proyecto nacional coordinado por la universidad de Zaragoza llamado PLEISAR", speakOption);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("En él, participan investigadores que pertenecen a la Universidad de las Islas Baleares, a la Universidad de Granada," +
                "y a la universidad de La Laguna", speakOption);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechManager.startSpeak("En éste proyecto se ha comenzado a trabajar con asistentes por voz como Alexa y con robots sociales como...", speakOption);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // el robot "se ve" en pantalla
        speechManager.startSpeak("¡Espera!");
        // cambia su emoción a sorpresa
        changeEmotion(EmotionsType.PRISE);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // avisa al publico
        speechManager.startSpeak("¡Mirad eso!");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // se gira CUANTO EXACTAMENTE (??)
        controlTronco(PresentacionActivity.TipoDireccion.DERECHA, 5, 90);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // levanta el brazo
        controlBrazos(PresentacionActivity.TipoBrazo.DERECHO, 8, 70);
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

        // recupera su posición CUANTOS GRADOS EXACTAMENTE (??)
        controlBasicoTronco(PresentacionActivity.AccionesTronco.GIRO_90, PresentacionActivity.TipoDireccion.IZQUIERDA);
        controlBasicoBrazos(PresentacionActivity.AccionesBrazos.BAJAR_BRAZO, PresentacionActivity.TipoBrazo.DERECHO);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // cambia su emoción
        changeEmotion(EmotionsType.SNICKER);
        speechManager.startSpeak("Ah, y tambien tengo a uno de mis primos en Teruel, trabajando con los Amantes", speakOption);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speechManager.startSpeak("Y bueno, eso es todo", speakOption);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // cambia su emoción
        changeEmotion(EmotionsType.SLEEP);
        // baja la cabeza
        controlBasicoCabeza(PresentacionActivity.AccionesCabeza.ABAJO);
        // pone los brazos atrás
        controlBrazos(PresentacionActivity.TipoBrazo.AMBOS, 7, 230);
        // se despide del publico
        speechManager.startSpeak("Muchas gracias por vuestra atención", speakOption);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // recupera su posición
        controlBasicoCabeza(PresentacionActivity.AccionesCabeza.CENTRO);
        controlBasicoBrazos(PresentacionActivity.AccionesBrazos.BAJAR_BRAZO, PresentacionActivity.TipoBrazo.AMBOS);
        changeEmotion(EmotionsType.SMILE);
    }

    // DIALOGO ----------------------------------------------------------------------------------------------------------------------------------
    public boolean reconocerRespuesta(SpeakOption so){
        speechManager.setOnSpeechListener(new RecognizeListener() {
            // variable (no utilizada)
            boolean preguntaEncontrada = false;
            @Override
            public boolean onRecognizeResult(Grammar grammar) {

                // paso la gramática reconocida a String
                String cadenaReconocida = grammar.getText();

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
                //Log.i("Cris", "onError: i="+i+" i1="+i1);
            }
        });
        return true;
    }

    public void dialogoPlanetario() {
        // definimos la velocidad y entonación de la conversación
        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);
        // el robot dice el dialogo inciial
        speechManager.startSpeak("Hola, Estoy buscando a Drako, ¿ Le habéis visto ? ", speakOption);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // empieza el reconocimiento de voz
        speechManager.doWakeUp();
        reconocerRespuesta(speakOption);
    }

    // EXTRA ----
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

    public boolean controlBasicoBrazos(PresentacionActivity.AccionesBrazos accion, PresentacionActivity.TipoBrazo brazo) {
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

    public boolean controlBrazos(PresentacionActivity.TipoBrazo brazo, int velocidad, int angulo) {
        byte[] absolutePart = new byte[]{AbsoluteAngleHandMotion.PART_LEFT, AbsoluteAngleHandMotion.PART_RIGHT, AbsoluteAngleHandMotion.PART_BOTH};
        if (brazo.equals(PresentacionActivity.TipoBrazo.DERECHO)) {
            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[1], velocidad, angulo);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        } else if (brazo.equals(PresentacionActivity.TipoBrazo.IZQUIERDO)) {
            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], velocidad, angulo);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        } else if (brazo.equals(PresentacionActivity.TipoBrazo.AMBOS)) {
            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], velocidad, angulo);
            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        }
        return true;
    }

    public boolean controlBasicoTronco(PresentacionActivity.AccionesTronco accion, PresentacionActivity.TipoDireccion direccion){
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

    public boolean controlTronco(PresentacionActivity.TipoDireccion direccion, int velocidad, int angulo) {
        RelativeAngleWheelMotion movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 360);
        if (direccion.equals(PresentacionActivity.TipoDireccion.DERECHA)) {
            movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, velocidad, angulo);
            wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
        } else{
            movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, velocidad, angulo);
            wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
        }
        return true;
    }

    public boolean controlBasicoCabeza(PresentacionActivity.AccionesCabeza accion){
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

    // funcion que muestra la emoción pasada por parametro
    public void changeEmotion(EmotionsType emotion) {
        currentEmotion = emotion;
        systemManager.showEmotion(currentEmotion);
    }

    public void presentacionNinos() throws InterruptedException {
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
                RelativeAngleWheelMotion.TURN_LEFT, 3,360
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


        controlBasicoTronco(PresentacionActivity.AccionesTronco.GIRO_180, PresentacionActivity.TipoDireccion.IZQUIERDA);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        controlBasicoTronco(PresentacionActivity.AccionesTronco.GIRO_180, PresentacionActivity.TipoDireccion.IZQUIERDA);

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
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        speechManager.startSpeak("Cuando quiero participar con los niños hago así", speakOption);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        speechManager.startSpeak("Y bueno, eso es todo", speakOption);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // cambia su emoción
        changeEmotion(EmotionsType.SLEEP);
        // baja la cabeza
        controlBasicoCabeza(PresentacionActivity.AccionesCabeza.ABAJO);
        // pone los brazos atrás
        controlBrazos(PresentacionActivity.TipoBrazo.AMBOS, 7, 230);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // se despide del publico
        speechManager.startSpeak("Muchas gracias por vuestra atención", speakOption);
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // recupera su posición
        controlBasicoCabeza(PresentacionActivity.AccionesCabeza.CENTRO);
        controlBasicoBrazos(PresentacionActivity.AccionesBrazos.BAJAR_BRAZO, PresentacionActivity.TipoBrazo.AMBOS);
        changeEmotion(EmotionsType.SMILE);

    }

    public void APIChatGPT() throws IOException, InterruptedException {

        SpeakOption speakOption = new SpeakOption();
        speakOption.setSpeed(60);
        speakOption.setIntonation(50);

        new Thread(new Runnable() {
            public void run() {

                speechManager.doWakeUp();
                reconocerRespuesta(speakOption);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


                if (preguntaChatGPT != null) {
                    Log.d("pregunta", preguntaChatGPT);
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                final OkHttpClient client = new OkHttpClient();




                JSONObject roleSystem = new JSONObject();
                try{
                    roleSystem.put("role", "system");
                    roleSystem.put("content", "You are a helpful assistant.");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("respuestaJSON", roleSystem.toString());

                JSONObject roleUser = new JSONObject();
                try{
                    roleUser.put("role", "user");
                    roleUser.put("content", preguntaChatGPT);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("respuestaJSON", roleUser.toString());

                JSONArray messages = new JSONArray();
                messages.put(roleSystem);
                messages.put(roleUser);

                Log.d("respuestaJSON", messages.toString());

                JSONObject request = new JSONObject();
                try{
                    request.put("model", "gpt-3.5-turbo");
                    request.put("messages", messages);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("respuestaJSON", request.toString());

                RequestBody peticion = RequestBody.create(
                        MediaType.parse("application/json"), String.valueOf(request));

                Log.d("requestBody", peticion.toString());


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
                    speechManager.startSpeak(r, speakOption);
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
