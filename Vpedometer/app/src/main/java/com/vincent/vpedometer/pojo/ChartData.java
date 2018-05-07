package com.vincent.vpedometer.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/2/13 19:03
 */
public class ChartData implements Serializable {

    private ArrayList<String> timeData = new ArrayList<>();
    private ArrayList<String> stepData = new ArrayList<>();

    public ArrayList<String> getTimeData() {
        return timeData;
    }

    public void setTimeData(ArrayList<String> timeData) {
        this.timeData = timeData;
    }

    public ArrayList<String> getStepData() {
        return stepData;
    }

    public void setStepData(ArrayList<String> stepData) {
        this.stepData = stepData;
    }
}
