package com.example.visualizer.charts;

import com.example.visualizer.data.DataPoint;
import com.example.visualizer.data.DataSeries;

import java.awt.*;
import java.util.List;

public class LineChart extends AbstractChart {
    @Override
    public void draw(Graphics2D g, Rectangle bounds, DataSeries series) {
        enableQuality(g);
        drawTitle(g, bounds);

        int top = bounds.y + 40;
        int bottom = bounds.y + bounds.height - 40;
        int left = bounds.x + 40;
        int right = bounds.x + bounds.width - 20;

        List<DataPoint> pts = series.getPoints();
        int n = pts.size();
        if (n == 0) {
            g.setColor(Color.DARK_GRAY);
            g.drawString("No data to display", left + 10, top + 20);
            return;
        }

        // Find min and max
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        for (DataPoint p : pts) {
            double v = p.getValue();
            if (Double.isNaN(v) || Double.isInfinite(v)) v = 0;
            if (v > max) max = v;
            if (v < min) min = v;
        }
        if (max == min) max = min + 1; // prevent divide-by-zero

        // X scaling
        int xStep = Math.max(1, (right - left) / Math.max(1, n - 1));

        // Prepare first point
        int prevX = left;
        double firstVal = pts.get(0).getValue();
        if (Double.isNaN(firstVal) || Double.isInfinite(firstVal)) firstVal = 0;
        int prevY = bottom - (int) (((firstVal - min) / (max - min)) * (bottom - top));

        g.setColor(Color.RED);
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(2f));

        // Draw lines
        for (int i = 1; i < n; i++) {
            DataPoint p = pts.get(i);
            double v = p.getValue();
            if (Double.isNaN(v) || Double.isInfinite(v)) v = 0;

            int xi = left + i * xStep;
            int yi = bottom - (int) (((v - min) / (max - min)) * (bottom - top));

            g.drawLine(prevX, prevY, xi, yi);
            g.fillOval(xi - 3, yi - 3, 6, 6);

            prevX = xi;
            prevY = yi;
        }

        g.setStroke(oldStroke);

        // Draw points & labels
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        for (int i = 0; i < n; i++) {
            DataPoint p = pts.get(i);
            int xi = left + i * xStep;
            String lbl = p.getLabel();
            if (lbl == null) lbl = "";
            g.drawString(lbl, xi - fm.stringWidth(lbl) / 2, bottom + fm.getAscent() + 2);
        }
    }
}
