package com.example.sanbotapp;

import android.os.Bundle;
import android.view.WindowManager;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.interfaces.speech.SpeakListener;

public class SanbotSpeechControl extends TopBaseActivity {
    SpeechManager speechManager;

    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onCreate(savedInstanceState);

        // Inicialización de las unidades del robot

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
    }

    public void hablar(String respuesta, SpeakOption so){
        speechManager.startSpeak(respuesta, so);
    }

    public void gestionSpeakers(SpeakListener speakListener){
        speechManager.setOnSpeechListener(new SpeakListener() {

            @Override
            public void onSpeakFinish() {

            }

            @Override
            public void onSpeakProgress(int i) {

            }
        });
    }

    @Override
    protected void onMainServiceConnected() {

    }
}
