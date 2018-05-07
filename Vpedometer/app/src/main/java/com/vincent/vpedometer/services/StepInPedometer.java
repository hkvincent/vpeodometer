package com.vincent.vpedometer.services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import com.vincent.vpedometer.base.StepMode;

/**
 * Created by Administrator on 2018/2/8.
 */

public class StepInPedometer extends StepMode {
    private final String TAG = "StepInPedometer";
    private int lastStep = -1;
    private int liveStep = 0;
    private int increment = 0;
    //0-TYPE_STEP_DETECTOR 1-TYPE_STEP_COUNTER
    private int sensorMode = 0;
    private boolean hasRecord;
    private int hasStepCount;
    private int previousStepCount;

    public StepInPedometer(Context context, StepCallBack stepCallBack) {
        super(context, stepCallBack);

    }

    @Override
    protected void registerSensor() {
        addCountStepListener();
    }

    /**
     * this method will call when sensor has reaction
     *
     * @param event
     */
    @Override
    public  void onSensorChanged(SensorEvent event) {
        liveStep = (int) event.values[0];
        if (sensorMode == 0) {
            StepMode.CURRENT_STEP += liveStep;
        } else if (sensorMode == 1) {
            int tempStep = (int) event.values[0];
            if (!hasRecord) {
                hasRecord = true;
                hasStepCount = tempStep;
            } else {
                int thisStepCount = tempStep - hasStepCount;
                CURRENT_STEP += (thisStepCount - previousStepCount);
                previousStepCount = thisStepCount;
            }
        }
        stepCallBack.Step(StepMode.CURRENT_STEP);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void addCountStepListener() {
        //get two sensor which one can be used.
        Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        //first sensor will return the step counting is not cumulative
        if (detectorSensor != null) {
            sensorManager.registerListener(this, detectorSensor, SensorManager.SENSOR_DELAY_UI);
            isAvailable = true;
            sensorMode = 0;
            //second one will return the step counting is cumulative
        } else if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
            isAvailable = true;
            sensorMode = 1;
            //google step sensor can not be used
        } else {
            isAvailable = false;
            Log.v(TAG, "Count sensor not available!");
        }
    }
}
