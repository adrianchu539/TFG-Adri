package com.example.sanbotapp;

import android.util.Log;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.OperationResult;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.speech.Grammar;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.interfaces.speech.RecognizeListener;

import java.io.IOException;

public class SpeechControl {

    private SpeechManager speechManager;
    private SpeakOption speakOption;
    private String cadenaReconocida;
    private ModuloOpenAISpeechVoice moduloOpenAISpeechVoice = new ModuloOpenAISpeechVoice();
    private boolean esperar;

    public SpeechControl(SpeechManager speechManager, SpeakOption speakOption){
        this.speechManager = speechManager;
        this.speakOption = speakOption;
    }

    protected boolean reconocerRespuesta(){
        speechManager.setOnSpeechListener(new RecognizeListener() {

            // Intercepta el diálogo hablado por el usuario
            @Override
            public boolean onRecognizeResult(Grammar grammar) {
                esperar = true;

                Log.d("prueba", "reconociendo consulta...");

                cadenaReconocida = grammar.getText();

                esperar=false;

                return true;
            }

            // Intercepta el volumen hablado por el usuario
            @Override
            public void onRecognizeVolume(int i) {
                // ...
            }

            public void onStartRecognize() {
                // ...
            }

            public void onStopRecognize() {
                // ...
            }

            public void onError(int i, int i1) {
                // ...
            }
        });
        while(esperar){

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
    }

}
