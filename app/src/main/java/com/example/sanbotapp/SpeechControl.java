package com.example.sanbotapp;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.OperationResult;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.speech.Grammar;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.interfaces.speech.RecognizeListener;
import com.qihancloud.opensdk.function.unit.interfaces.speech.SpeakListener;

import java.io.IOException;

public class SpeechControl {

    private SpeechManager speechManager;
    private SpeakOption speakOption;
    private String cadenaReconocida;
    private ModuloOpenAISpeechVoice moduloOpenAISpeechVoice = new ModuloOpenAISpeechVoice();

    public SpeechControl(SpeechManager speechManager, SpeakOption speakOption){
        this.speechManager = speechManager;
        this.speakOption = speakOption;
    }

    protected boolean reconocerRespuesta(){
        Log.d("prueba", "reconociendo respuesta...");

        while(getRespuesta()==null || getRespuesta()==""){
            Log.d("esperando", "esperando...... " + getRespuesta());
        }
        return true;
    }

    protected boolean robotHablando(){
        OperationResult or = speechManager.isSpeaking();
        if (or.getResult().equals("1")) {
            return true;
        }
        else{
            return false;
        }
    }

    protected void hablar(String respuesta){
        speechManager.startSpeak(respuesta, speakOption);
    }

    protected void pararHabla(){
        speechManager.stopSpeak();
    }

    protected void modoEscucha(){
        speechManager.doWakeUp();
        speechManager.setOnSpeechListener(new RecognizeListener() {
            @Override
            public boolean onRecognizeResult(Grammar grammar) {
                Log.d("prueba", "The text recognized by the robot is "+
                    grammar.getText());
                return true;
            }

            @Override
            public void onRecognizeVolume(int i) {

            }
        });
    }

    protected String getRespuesta(){
        return cadenaReconocida;
    }

    protected void gestionHabla(String voz, String respuesta) throws IOException {
        Log.d("hablar", respuesta);
        if(voz.equals("sanbot")){
            hablar(respuesta);
        }
        else{
            moduloOpenAISpeechVoice.peticionVozOpenAI(respuesta, voz.toLowerCase());
        }
        speechManager.setOnSpeechListener(new SpeakListener(){
            // Acción que se ejecuta cuando el robot termina de hablar
            @Override
            public void onSpeakFinish() {
                // Si está en modo conversación automática
                Log.d("fin", "termine de hablar");
            }

            @Override
            public void onSpeakProgress(int i) {
                // ...
            }
        });
    }

}
