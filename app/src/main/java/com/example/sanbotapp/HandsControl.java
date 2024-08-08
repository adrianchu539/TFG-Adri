package com.example.sanbotapp;

import android.util.Log;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;

import java.util.ArrayList;
import java.util.List;

public class HandsControl {
    private HandMotionManager handMotionManager;

    public HandsControl(HandMotionManager handMotionManager){
        this.handMotionManager = handMotionManager;
    }

    protected boolean controlBasicoBrazos(AccionesBrazos accion, TipoBrazo brazo) throws InterruptedException {
        byte[] absolutePart = new byte[]{AbsoluteAngleHandMotion.PART_LEFT, AbsoluteAngleHandMotion.PART_RIGHT, AbsoluteAngleHandMotion.PART_BOTH};
        AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], 10, 0);
        switch(accion) {
            case LEVANTAR_BRAZO:
                switch (brazo) {
                    case IZQUIERDO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], 7, 10);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case DERECHO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[1], 7, 10);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case AMBOS:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], 7, 10);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                }
                break;
            case BAJAR_BRAZO:
                switch (brazo) {
                    case IZQUIERDO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], 7, 170);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case DERECHO:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[1], 7, 170);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                    case AMBOS:
                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], 7, 170);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        break;
                }
                break;
        }

        return true;
    }

    protected boolean reiniciar(){
        byte[] absolutePart = new byte[]{AbsoluteAngleHandMotion.PART_LEFT, AbsoluteAngleHandMotion.PART_RIGHT, AbsoluteAngleHandMotion.PART_BOTH};
        AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[0], 10, 0);
        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(absolutePart[2], 7, 170);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
        return true;
    }

    protected enum AccionesBrazos {
        LEVANTAR_BRAZO,
        BAJAR_BRAZO,
    }

    protected enum TipoBrazo {
        DERECHO,
        IZQUIERDO,
        AMBOS;
    }
}
