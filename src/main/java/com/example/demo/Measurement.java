package com.example.demo;

public class Measurement {
    private double time;   // Время
    private double pressure; // Давление

    public Measurement(double time, double pressure) {
        this.time = time;
        this.pressure = pressure;
    }

    public double getTime() {
        return time;
    }

    public double getPressure() {
        return pressure;
    }
}
