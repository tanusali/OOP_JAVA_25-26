package com.example.visualizer.charts;

import com.example.visualizer.data.DataPoint;
import com.example.visualizer.data.DataSeries;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class PieChart extends AbstractChart {
    private static final Color[] DEFAULT_PALETTE = {
            Color.BLUE, Color.RED, new Color(34, 139, 34), Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK, Color.GRAY
    };

    @Override
    public void draw(Graphics2D g, Rectangle bounds, DataSeries series) {
        enableQuality(g);
        drawTitle(g, bounds);

        List<DataPoint> pts = series.getPoints();
        if (pts == null || pts.isEmpty()) {
            g.setColor(Color.DARK_GRAY);
            g.drawString("No data", bounds.x + 10, bounds.y + 20);
            return;
        }

        int cx = bounds.x + bounds.width / 2;
        int cy = bounds.y + bounds.height / 2 + 10;
        int radius = Math.max(10, Math.min(bounds.width, bounds.height) / 4);

        // compute total (use only non-negative values)
        double total = 0;
        for (DataPoint p : pts) {
            double v = p.getValue();
            if (Double.isNaN(v) || Double.isInfinite(v)) v = 0;
            total += Math.max(0, v);
        }
        if (total <= 0) total = 1; // avoid divide-by-zero

        DecimalFormat df = new DecimalFormat("#.##");

        // draw slices; ensure final slice uses remaining angle to sum to 360
        int startAngle = 0;
        int i = 0;
        for (; i < pts.size(); i++) {
            DataPoint p = pts.get(i);
            double v = p.getValue();
            if (Double.isNaN(v) || Double.isInfinite(v)) v = 0;
            v = Math.max(0, v);

            int angle;
            if (i == pts.size() - 1) {
                // last slice: take remaining angle
                angle = 360 - startAngle;
            } else {
                angle = (int) Math.round((v / total) * 360.0);
                // clamp
                if (angle < 0) angle = 0;
                if (angle > 360 - startAngle) angle = Math.max(0, 360 - startAngle);
            }

            g.setColor(DEFAULT_PALETTE[i % DEFAULT_PALETTE.length]);
            g.fillArc(cx - radius, cy - radius, radius * 2, radius * 2, startAngle, angle);
            startAngle += angle;
            if (startAngle >= 360) break;
        }

        // legend on right
        int lx = bounds.x + bounds.width - 140;
        int ly = bounds.y + 40;
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();

        i = 0;
        for (DataPoint p : pts) {
            double v = p.getValue();
            if (Double.isNaN(v) || Double.isInfinite(v)) v = 0;
            String lbl = (p.getLabel() == null ? "" : p.getLabel());
            String valStr = df.format(v);

            // color box + label
            g.setColor(DEFAULT_PALETTE[i % DEFAULT_PALETTE.length]);
            g.fillRect(lx, ly + i * 20, 12, 12);
            g.setColor(Color.BLACK);
            g.drawString(lbl + " (" + valStr + ")", lx + 18, ly + i * 20 + fm.getAscent());
            i++;
        }
    }
}
