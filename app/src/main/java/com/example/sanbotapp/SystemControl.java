package com.example.sanbotapp;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.SystemManager;

public class SystemControl {
    private SystemManager systemManager;
    private EmotionsType currentEmotion;

    public SystemControl(SystemManager systemManager){
        this.systemManager = systemManager;
    }

    // Función utilizada para cambiar la expresión facial del robot
    // por alguna de las emociones definidas en el sistema
    protected void cambiarEmocion(EmotionsType emotion) {
        currentEmotion = emotion;
        systemManager.showEmotion(currentEmotion);
    }

}
