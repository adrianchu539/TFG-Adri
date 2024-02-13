package com.example.sanbotapp;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sanbot.opensdk.base.TopBaseActivity;
import com.sanbot.opensdk.beans.FuncConstant;
import com.sanbot.opensdk.function.beans.SpeakOption;
import com.sanbot.opensdk.function.beans.speech.Grammar;
import com.sanbot.opensdk.function.beans.speech.SpeakStatus;
import com.sanbot.opensdk.function.unit.SpeechManager;
import com.sanbot.opensdk.function.unit.interfaces.speech.RecognizeListener;
import com.sanbot.opensdk.function.unit.interfaces.speech.SpeakListener;
import com.sanbot.opensdk.function.unit.interfaces.speech.WakenListener;


/*

EJEMPLO DE USO DE LA UNIDAD DE RECONOCIMIENTO DE VOZ
speechManager.setOnSpeechListener(new RecognizeListener() {
    @Override
    public boolean onRecognizeResult(Grammar grammar) {
        String recognizedText = grammar.getText();
        String recognizedTopic = grammar.getTopic();
        float confidence = grammar.getConfidence();

        // Aquí puedes procesar el texto reconocido y el tema asociado según tus necesidades
        Log.i("SpeechRecognition", "Texto reconocido: " + recognizedText);
        Log.i("SpeechRecognition", "Tema reconocido: " + recognizedTopic);
        Log.i("SpeechRecognition", "Confianza del reconocimiento: " + confidence);

        // Lógica adicional según el texto y el tema reconocidos
        if (recognizedTopic.equals("saludo")) {
            // Si el tema es "saludo", realizar acciones específicas para saludar al usuario
            speechManager.startSpeak("¡Hola! ¿Cómo estás?");
            return true; // Devuelve true para detener el reconocimiento
        }

        // Devuelve false para continuar con el reconocimiento
        return false;
    }

    // Otros métodos de la interfaz RecognizeListener
});

 */
public class SpeechControl extends TopBaseActivity {


    EditText etText;
    RadioButton rbEnglish;
    RadioButton rbChinese;
    RadioGroup rgLang;
    EditText etSpeed;
    EditText etTone;
    Button tvSpeechSynthesizeStart;
    Button tvSpeechSynthesizePause;
    Button tvSpeechSynthesizeContinue;
    Button tvSpeechSynthesizeStop;
    TextView tvSpeechSynthesizeProgress;
    Button tvSpeechSleep;
    Button tvSpeechWakeup;
    TextView tvSpeechStatus;
    TextView tvSpeechRecognizeVolume;
    TextView tvSpeechRecognizeResult;
    CheckBox cbInterceptMessage;
    TextView tvSpeaking;
    TextView tvSpeakingResult;
    private SpeechManager speechManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        register(SpeechControl.class);
        //屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_control);
        //初始化变量
        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);

        // Inicializar vistas
        etText = findViewById(R.id.et_text);
        rgLang = findViewById(R.id.rg_lang);
        etSpeed = findViewById(R.id.et_speed);
        etTone = findViewById(R.id.et_tone);
        tvSpeechSynthesizeStart = findViewById(R.id.tv_speech_synthesize_start);
        tvSpeechSynthesizePause = findViewById(R.id.tv_speech_synthesize_pause);
        tvSpeechSynthesizeContinue = findViewById(R.id.tv_speech_synthesize_continue);
        tvSpeechSynthesizeStop = findViewById(R.id.tv_speech_synthesize_stop);
        tvSpeechSleep = findViewById(R.id.tv_speech_sleep);
        tvSpeechWakeup = findViewById(R.id.tv_speech_wakeup);
        tvSpeechStatus = findViewById(R.id.tv_speech_status);
        cbInterceptMessage = findViewById(R.id.cb_intercept_message);
        tvSpeechRecognizeVolume = findViewById(R.id.tv_speech_recognize_volume);
        tvSpeechRecognizeResult = findViewById(R.id.tv_speech_recognize_result);

        initListener();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        //设置唤醒，休眠回调
        speechManager.setOnSpeechListener(new WakenListener() {
            @Override
            public void onWakeUp() {
                tvSpeechStatus.setText(R.string.speech_wakeup_status);
            }

            @Override
            public void onSleep() {
                tvSpeechStatus.setText(R.string.speech_sleep_status);
            }
        });
        //语音识别回调

        speechManager.setOnSpeechListener(new RecognizeListener() {
            @Override
            public boolean onRecognizeResult(Grammar grammar) {
                //Log.i("reconocimiento：", "onRecognizeResult: "+grammar.getText());
                //只有在配置了RECOGNIZE_MODE为1，且返回为true的情况下，才会拦截
                tvSpeechRecognizeResult.setText(grammar.getText());
                if (grammar.getText().equals("hola soy Adrián")) {
                    speechManager.startSpeak("hola Adrian, ¿qué tal estás?");
                    return true;
                }
                else if(grammar.getText().equals("hola soy Loreto")) {
                    speechManager.startSpeak("hola Loreto, ¿cómo va tu día?");
                    return true;
                }
                else if(grammar.getText().equals("hola soy Eva")) {
                    speechManager.startSpeak("hola Eva, espero que tengas un bonito día");
                    return true;
                }
                else if(grammar.getText().equals("hola soy Sandra")) {
                    speechManager.startSpeak("hola Sandra, ¿cómo te va?");
                    return true;
                }
                return cbInterceptMessage.isChecked();
            }

            @Override
            public void onRecognizeVolume(int i) {
                tvSpeechRecognizeVolume.setText(String.valueOf(i));
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
        });
        //语音合成状态回调
        speechManager.setOnSpeechListener(new SpeakListener() {
            @Override
            public void onSpeakStatus(SpeakStatus speakStatus) {
                if (speakStatus != null) {
                    Log.e("pgq", "" + speakStatus.getProgress());
                    tvSpeechSynthesizeProgress.setText("" + speakStatus.getProgress());
                }
            }
        });
    }

    @Override
    protected void onMainServiceConnected() {
    }

    /**
     * 处理所有的点击事件
     *
     * @param view
     */
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //开始合成语音
            case R.id.tv_speech_synthesize_start:
                SpeakOption speakOption = new SpeakOption();
                //设置合成语言
                if (rgLang.getCheckedRadioButtonId() == R.id.rb_chinese) {
                    speakOption.setLanguageType(SpeakOption.LAG_CHINESE);
                } else if (rgLang.getCheckedRadioButtonId() == R.id.rb_english) {
                    speakOption.setLanguageType(SpeakOption.LAG_ENGLISH_US);
                }
                //设置合成语速
                String speed = etSpeed.getText().toString();
                if (!TextUtils.isEmpty(speed) && Integer.parseInt(speed) >= 0 && Integer.parseInt(speed) <= 100) {
                    speakOption.setSpeed(Integer.parseInt(speed));
                }
                //设置合成声调
                String tone = etTone.getText().toString();
                if (!TextUtils.isEmpty(tone) && Integer.parseInt(tone) >= 0 && Integer.parseInt(tone) <= 100) {
                    speakOption.setIntonation(Integer.parseInt(tone));
                }
                speechManager.startSpeak(etText.getText().toString(), speakOption);
                break;
            //暂停合成语音
            case R.id.tv_speech_synthesize_pause:
                speechManager.pauseSpeak();
                break;
            //继续合成语音
            case R.id.tv_speech_synthesize_continue:
                speechManager.resumeSpeak();
                break;
            //停止合成语音
            case R.id.tv_speech_synthesize_stop:
                speechManager.stopSpeak();
                break;
            //休眠
            case R.id.tv_speech_sleep:
                speechManager.doSleep();
                break;
            //唤醒
            case R.id.tv_speech_wakeup:
                speechManager.doWakeUp();
                break;
            //机器人是否正在说话
            case R.id.tv_speech_speaking:
                if (speechManager.isSpeaking().getResult().equals("1")) {
                    tvSpeakingResult.setText(R.string.speaking);
                } else if (speechManager.isSpeaking().getResult().equals("0")) {
                    tvSpeakingResult.setText(R.string.not_speaking);
                }
                break;
        }
    }
}
