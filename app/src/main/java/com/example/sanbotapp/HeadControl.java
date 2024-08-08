package com.example.sanbotapp;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;

public class HeadControl {
    private HeadMotionManager headMotionManager;

    public HeadControl(HeadMotionManager headMotionManager){
        this.headMotionManager = headMotionManager;
    }

    protected enum AccionesCabeza {
        DERECHA,
        IZQUIERDA,
        ARRIBA,
        ABAJO,
        CENTRO;
    }

    protected boolean controlBasicoCabeza(AccionesCabeza accion) {
        RelativeAngleHeadMotion relativeAngleHeadMotion;
        AbsoluteAngleHeadMotion absoluteAngleHeadMotion;
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

    protected boolean reiniciar(){
        AbsoluteAngleHeadMotion absoluteAngleHeadMotion;
        absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,90);
        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
        return true;
    }
}
