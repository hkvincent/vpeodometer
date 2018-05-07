package com.vincent.vpedometer.base;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Administrator on 2018/2/8.
 * two mode ,first mode is using acceleration to calculate step
 * the second mode is using google step count sensor
 */

public abstract class StepMode implements SensorEventListener {

    public interface StepCallBack {
        void Step(int stepNum);
    }

    private Context context; // the object has necessary information we need
    public StepCallBack stepCallBack;
    public SensorManager sensorManager;
    public static int CURRENT_STEP = 0;
    public boolean isAvailable = false;

    public StepMode(Context context, StepCallBack stepCallBack) {
        this.context = context;
        this.stepCallBack = stepCallBack;
    }

    public boolean getStep() {
        prepareSensorManager();
        registerSensor();
        return isAvailable;
    }

    protected abstract void registerSensor();

    private void prepareSensorManager() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            sensorManager = null;
        }
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        // getLock(this);
//        int VERSION_CODES = android.os.Build.VERSION.SDK_INT;
//        if (VERSION_CODES >= 19) {
//            addCountStepListener();
//        } else {
//            addBasePedoListener();
//        }

    }

}
