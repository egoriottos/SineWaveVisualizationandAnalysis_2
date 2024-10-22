package com.example.demo;

import org.apache.commons.math3.complex.Complex;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
import java.util.List;

public class GraphPlotter {
    public void plotTimeSeries(List<Measurement> measurements) {
        XYSeries series = new XYSeries("Давление от времени");

        for (Measurement measurement : measurements) {
            series.add(measurement.getTime(), measurement.getPressure());
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Pressure vs Time",
                "Время",
                "Давление",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        JFrame frame = new JFrame("Signal Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

    public void plotFrequencySpectrum(Complex[] fftResults) {
        XYSeries series = new XYSeries("Амплитудный спектр");

        for (int i = 0; i < fftResults.length / 2; i++) {
            series.add(i, fftResults[i].abs()); // Модуль комплексного числа
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Frequency Spectrum",
                "Frequency",
                "Amplitude",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        JFrame frame = new JFrame("Frequency Spectrum");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }
}
