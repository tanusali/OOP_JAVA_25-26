package com.example.visualizer.charts;

import com.example.visualizer.data.DataPoint;
import com.example.visualizer.data.DataSeries;

import java.awt.*;
import java.util.List;

public class BarChart extends AbstractChart {
    @Override
    public void draw(Graphics2D g, Rectangle bounds, DataSeries series) {
        enableQuality(g);
        drawTitle(g, bounds);

        int top = bounds.y + 40;
        int bottom = bounds.y + bounds.height - 40;
        int left = bounds.x + 40;
        int right = bounds.x + bounds.width - 20;

        List<DataPoint> points = series.getPoints();
        if (points == null || points.isEmpty()) {
            // nothing to draw
            g.setColor(Color.DARK_GRAY);
            g.drawString("No data", left + 10, top + 20);
            return;
        }

        // compute max (defensive against NaN/Infinite)
        double max = 0;
        for (DataPoint p : points) {
            double v = p.getValue();
            if (Double.isNaN(v) || Double.isInfinite(v)) v = 0;
            if (v > max) max = v;
        }
        if (max == 0) max = 1;

        int n = points.size();
        int barAreaWidth = right - left;
        // ensure at least 1 column and non-negative
        int spacing = 6;
        int barWidth = Math.max(6, Math.max(1, barAreaWidth / Math.max(1, n)) - spacing);
        if (barWidth < 1) barWidth = 1;

        int x = left;
        g.setColor(Color.BLUE);
        FontMetrics fm = g.getFontMetrics();

        // draw bars
        for (DataPoint p : points) {
            double v = p.getValue();
            if (Double.isNaN(v) || Double.isInfinite(v)) v = 0;
            int h = (int) ((v / max) * (bottom - top));
            g.fillRect(x, bottom - h, barWidth, Math.max(1, h));
            x += barWidth + spacing;
        }

        // axis labels (x: labels)
        x = left;
        g.setColor(Color.BLACK);
        for (DataPoint p : points) {
            String lbl = p.getLabel();
            int tx = x + barWidth / 2 - fm.stringWidth(lbl) / 2;
            int ty = bottom + fm.getHeight();
            g.drawString(lbl, tx, ty);
            x += barWidth + spacing;
        }
    }
}
