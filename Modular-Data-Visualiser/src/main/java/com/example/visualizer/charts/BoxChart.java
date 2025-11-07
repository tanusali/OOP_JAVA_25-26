package com.example.visualizer.charts;

import com.example.visualizer.analysis.BinAggregator;
import com.example.visualizer.analysis.StatsUtil;
import com.example.visualizer.data.DataSeries;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple BoxChart that draws a box for each bucket.
 * Note: adapt to your Chart interface — here we implement a draw(Graphics2D,g,area,seriesBuckets).
 */
public class BoxChart implements Chart {

    private String title = "Box Plot";

    public void setTitle(String t) { this.title = t; }

    @Override
    public void draw(Graphics2D g, Rectangle area, DataSeries series) {
        // We assume series here encodes many labels and values.
        // We'll bucketize into e.g., 10 bins for visuals (caller should have done this).
        int bins = Math.max(1, Math.min(20, Math.max(1, 10))); // default 10
        List<BinAggregator.Bucket> buckets = BinAggregator.bucketize(series, bins);

        // compute box stats for each bucket
        List<StatsUtil.BoxStats> stats = new ArrayList<>();
        for (BinAggregator.Bucket b : buckets) {
            stats.add(StatsUtil.boxStats(b.values));
        }

        // layout math
        int w = area.width;
        int h = area.height;
        int left = area.x + 40;
        int top = area.y + 30;
        int bottom = area.y + h - 40;
        int availW = w - 80;
        int per = Math.max(10, availW / Math.max(1, buckets.size()));
        int centerY = (top + bottom) / 2;

        // Determine global y-range from min/max across stats (excluding NaN)
        double globalMin = Double.POSITIVE_INFINITY, globalMax = Double.NEGATIVE_INFINITY;
        for (StatsUtil.BoxStats s : stats) {
            if (!Double.isNaN(s.min)) {
                globalMin = Math.min(globalMin, s.min);
                globalMax = Math.max(globalMax, s.max);
            }
        }
        if (globalMin == Double.POSITIVE_INFINITY || globalMax == Double.NEGATIVE_INFINITY) {
            // nothing to draw
            g.setColor(Color.DARK_GRAY);
            g.drawString("No numeric data to display for boxplot", left, top + 20);
            return;
        }

        // helper to map value -> pixel y
        final double min = globalMin;
        final double max = globalMax;
        java.util.function.DoubleFunction<Integer> mapY = v -> {
            double frac = (v - min) / (max - min);
            if (Double.isNaN(frac) || Double.isInfinite(frac)) return centerY;
            return (int) (bottom - frac * (bottom - top));
        };

        // draw each box
        int x = left + 10;
        for (int i = 0; i < buckets.size(); i++) {
            StatsUtil.BoxStats s = stats.get(i);
            if (Double.isNaN(s.min)) {
                x += per;
                continue;
            }
            int cx = x + per / 2;
            int yMin = mapY.apply(s.min);
            int yQ1 = mapY.apply(s.q1);
            int yMed = mapY.apply(s.median);
            int yQ3 = mapY.apply(s.q3);
            int yMax = mapY.apply(s.max);

            // whiskers
            g.setColor(Color.DARK_GRAY);
            g.drawLine(cx, yMax, cx, yQ3);
            g.drawLine(cx, yQ1, cx, yMin);
            // box
            int boxTop = Math.min(yQ1, yQ3);
            int boxHeight = Math.abs(yQ3 - yQ1);
            g.setColor(new Color(33, 150, 243, 160));
            g.fillRect(cx - per/4, boxTop, per/2, Math.max(2, boxHeight));
            g.setColor(Color.BLACK);
            g.drawRect(cx - per/4, boxTop, per/2, Math.max(2, boxHeight));
            // median
            g.setColor(Color.RED);
            g.drawLine(cx - per/4, yMed, cx + per/4, yMed);
            // outliers
            g.setColor(Color.MAGENTA);
            for (double out : s.outliers) {
                int oy = mapY.apply(out);
                g.fillOval(cx - 3, oy - 3, 6, 6);
            }

            // label (rotated if needed)
            String lab = buckets.get(i).label;
            g.setColor(Color.DARK_GRAY);
            g.drawString(lab, cx - (per/2) + 2, bottom + 14); // simple horizontal label

            x += per;
        }

        // title
        g.setColor(Color.DARK_GRAY);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
        g.drawString(title, area.x + 8, area.y + 16);
    }
}
