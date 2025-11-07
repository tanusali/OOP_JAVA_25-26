package com.example.visualizer.ui;

import com.example.visualizer.charts.Chart;
import com.example.visualizer.data.DataSeries;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ChartPanel extends JPanel {
    private Chart chart;
    private DataSeries series;

    public ChartPanel() {
        setBackground(Color.WHITE);
        setBorder(new CompoundBorder(new LineBorder(new Color(230,230,230)), new EmptyBorder(8,8,8,8)));
        setLayout(new BorderLayout());
    }

    public void setChart(Chart chart) {
        this.chart = chart;
        repaint();
    }

    public void setSeries(DataSeries series) {
        this.series = series;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (chart == null || series == null || series.getPoints().isEmpty()) {
            g.setColor(new Color(120, 120, 120));
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g.drawString("No chart to display. Load CSV and select chart type.", 18, 24);
            return;
        }

        Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());
        chart.draw(g, bounds, series);
    }
}
