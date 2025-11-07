package com.example.visualizer.analysis;

import java.util.*;

/**
 * Basic statistics utilities: quartiles, IQR, fences, and outliers.
 */
public class StatsUtil {

    public static class BoxStats {
        public double min, q1, median, q3, max;
        public List<Double> outliers = new ArrayList<>();
    }

    /** Compute box stats (Tukey IQR) from a list (non-null doubles). */
    public static BoxStats boxStats(List<Double> values) {
        BoxStats s = new BoxStats();
        if (values == null || values.isEmpty()) {
            s.min = s.q1 = s.median = s.q3 = s.max = Double.NaN;
            return s;
        }
        List<Double> v = new ArrayList<>(values);
        Collections.sort(v);
        int n = v.size();
        s.min = v.get(0);
        s.max = v.get(n-1);
        s.median = median(v);
        s.q1 = median(v.subList(0, n/2));
        s.q3 = median(v.subList((n+1)/2, n)); // works for both odd/even
        double iqr = s.q3 - s.q1;
        double lowerFence = s.q1 - 1.5 * iqr;
        double upperFence = s.q3 + 1.5 * iqr;
        for (double val : v) {
            if (val < lowerFence || val > upperFence) s.outliers.add(val);
        }
        return s;
    }

    private static double median(List<Double> v) {
        int n = v.size();
        if (n == 0) return Double.NaN;
        if (n % 2 == 1) return v.get(n/2);
        return (v.get(n/2 - 1) + v.get(n/2)) / 2.0;
    }
}
