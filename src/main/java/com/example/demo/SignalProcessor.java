package com.example.demo;

import java.util.ArrayList;
import java.util.List;

public class SignalProcessor {
    private List<Measurement> measurements;

    public SignalProcessor() {
        this.measurements = new ArrayList<>();
    }

    public void loadData(List<Measurement> data) {
        this.measurements = data;
    }

    public double calculateAveragePressure() {
        double sum = 0;
        for (Measurement measurement : measurements) {
            sum += measurement.getPressure();
        }
        return sum / measurements.size();
    }
}
