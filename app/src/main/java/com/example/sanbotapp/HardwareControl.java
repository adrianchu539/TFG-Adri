package com.example.sanbotapp;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;

public class HardwareControl {
    private HardWareManager hardWareManager;

    public HardwareControl(HardWareManager hardWareManager){
        this.hardWareManager = hardWareManager;
    }

    protected boolean encenderLED(byte parte, byte modo) {
        hardWareManager.setLED(new LED(parte, modo));
        return true;
    }

    protected boolean apagarLED(byte parte) {
        hardWareManager.setLED(new LED(parte, LED.MODE_CLOSE));
        return true;
    }
}
