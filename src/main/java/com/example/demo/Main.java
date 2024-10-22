package com.example.demo;

import org.jtransforms.fft.DoubleFFT_1D;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Main extends JFrame {
    private static final int SAMPLE_RATE = 200; // Частота дискретизации
    private static final double DURATION = 2.0; // Продолжительность сигнала (в секундах)

    private DefaultCategoryDataset dataset;
    private boolean showOriginal = true; // Флаг для отображения оригинального сигнала
    private double scaleY = 4.0; // Масштаб для оси Y диаграммы
    private double barWidth = 5.0+; // Ширина столбиков

    private double[] originalSignal; // Оригинальный сигнал
    private double[] restoredSignal; // Восстановленный сигнал

    public Main() {
        createFrequencyChart();
    }

    private void createFrequencyChart() {
        double[] time = new double[(int) (SAMPLE_RATE * DURATION)];
        originalSignal = new double[time.length];
        restoredSignal = new double[time.length];

        // Генерация синусоидальных сигналов
        for (int i = 0; i < time.length; i++) {
            time[i] = i / (double) SAMPLE_RATE;

            // Генерация синусоид с частотами от 20 до 22 Гц
            double frequency = 20 + (i % (int) (SAMPLE_RATE * DURATION)) / (double) (SAMPLE_RATE * DURATION) * (22 - 20);
            // Генерация амплитуд от 20 до 25
            double amplitude = 20 + Math.random() * (25 - 20);
            originalSignal[i] = amplitude * Math.sin(2 * Math.PI * frequency * time[i]);
        }

        // Выполнение FFT
        DoubleFFT_1D fft = new DoubleFFT_1D(originalSignal.length);
        double[] complexSignal = new double[originalSignal.length * 2];

        // Заполнение массива комплексными числами (действительная часть)
        System.arraycopy(originalSignal, 0, complexSignal, 0, originalSignal.length);

        // Выполнение FFT
        fft.realForwardFull(complexSignal);

        // Обратное FFT
        fft.realInverseFull(complexSignal, true);

        // Восстановленный сигнал
        for (int i = 0; i < restoredSignal.length; i++) {
            restoredSignal[i] = complexSignal[i]; // Только действительная часть
        }

        // Определение и вывод значений вершин
        printPeakValues(originalSignal, "Оригинальный сигнал");
        printPeakValues(restoredSignal, "Восстановленный сигнал");

        // Создание набора данных
        dataset = new DefaultCategoryDataset();

        // Добавление данных в набор
        updateDataset();

        // Создание диаграммы
        JFreeChart chart = ChartFactory.createBarChart(
                "Сравнение оригинального и восстановленного сигналов",
                "Индекс образца",
                "Амплитуда",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Настройка рендерера для увеличения расстояния между столбцами и установки цвета
        BarRenderer renderer = new BarRenderer();
        renderer.setDrawBarOutline(true); // Рисовать контуры столбиков
        renderer.setItemMargin(5.0); // Увеличиваем расстояние между столбиками (значение от 0 до 1)

        // Установка цветов для столбиков
        renderer.setSeriesPaint(0, Color.BLUE); // Цвет для оригинального сигнала
        renderer.setSeriesPaint(1, Color.RED);  // Цвет для восстановленного сигнала

        chart.getCategoryPlot().setRenderer(renderer);

        // Создание и настройка панели диаграммы
        ChartPanel chartPanel = new ChartPanel(chart) {
            {
                // Добавление функциональности масштабирования по оси Y
                addMouseWheelListener(new MouseWheelListener() {
                    @Override
                    public void mouseWheelMoved(MouseWheelEvent e) {
                        double scrollAmount = e.getPreciseWheelRotation();
                        scaleY *= (scrollAmount < 0) ? 1.1 : 0.9; // Регулировка масштаба
                        scaleY = Math.max(scaleY, 0.1); // Ограничение минимального масштаба
                        updateChart(chart);
                    }
                });

                // Добавление функциональности для изменения ширины столбиков
                addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        int x = e.getX();
                        // Изменение ширины столбиков в зависимости от положения мыши
                        barWidth = (x / (double) getWidth()) * 0.5; // Максимальная ширина 0.5
                        updateChart(chart);
                    }
                });
            }
        };
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);

        // Добавление кнопок для переключения между оригинальными и восстановленными сигналами
        JPanel buttonPanel = new JPanel();
        JButton toggleButton = new JButton("Переключить оригинальный/восстановленный");
        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOriginal = !showOriginal; // Переключение флага
                updateDataset(); // Обновление набора данных в зависимости от флага
            }
        });
        buttonPanel.add(toggleButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void printPeakValues(double[] signal, String signalType) {
        double maxValue = Double.NEGATIVE_INFINITY;
        double minValue = Double.POSITIVE_INFINITY;

        for (double v : signal) {
            if (v > maxValue) maxValue = v;
            if (v < minValue) minValue = v;
        }

        System.out.println(signalType + ":");
        System.out.println("  Максимальная вершина: " + maxValue);
        System.out.println("  Минимальная вершина: " + minValue);
        System.out.println("  Преобладающая частота: " + (maxValue >= 0 ? "Правильная" : "Неправильная")); // Пример определения
    }

    private void updateDataset() {
        dataset.clear(); // Очистка текущего набора данных

        // Добавление данных в набор в зависимости от выбранного сигнала
        for (int i = 0; i < originalSignal.length; i++) {
            if (showOriginal) {
                dataset.addValue(originalSignal[i], "Оригинальный сигнал", String.valueOf(i));
            } else {
                dataset.addValue(restoredSignal[i], "Восстановленный сигнал", String.valueOf(i));
            }
        }

        repaint(); // Перерисовка графика
    }

    private void updateChart(JFreeChart chart) {
        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRangeAxis().setLowerBound(0); // Установка нижней границы на 0
        plot.getRangeAxis().setUpperBound(25 * scaleY); // Установка верхней границы
        // Установка ширины столбиков
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(barWidth); // Установка ширины столбиков
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main example = new Main();
            example.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            example.pack();
            example.setLocationRelativeTo(null);
            example.setVisible(true);
        });
    }
}
