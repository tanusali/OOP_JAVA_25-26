package com.example.visualizer.analysis;

import com.example.visualizer.data.DataSeries;

import java.util.*;

/**
 * BinAggregator - utilities to aggregate / bin a DataSeries into fewer points.
 * - For Bar/Line charts: create a new DataSeries with labels "bin 1", "bin 2", ... and aggregated numeric value (mean sum, count).
 * - For Box plot: return per-bin lists for computing quartiles.
 */
public class BinAggregator {

    public enum Agg { SUM, MEAN, MEDIAN, COUNT }

    /**
     * Aggregate numeric series into `bins`. Series labels are grouped sequentially into bins of roughly equal sizes.
     * Returns a new DataSeries (labels: "1..", values aggregated).
     */
    public static DataSeries aggregateForBarLine(DataSeries series, int bins, Agg agg) {
        List<String> labels = series.getLabels();
        List<Double> values = series.getValues();

        int n = labels.size();
        if (bins <= 0) bins = 1;
        if (bins >= n) return series; // nothing to do

        int base = n / bins;
        int rem = n % bins;

        List<String> outLabels = new ArrayList<>();
        List<Double> outValues = new ArrayList<>();

        int idx = 0;
        for (int b = 0; b < bins; b++) {
            int size = base + (b < rem ? 1 : 0);
            if (size == 0) continue;
            double aggVal;
            List<Double> bucket = new ArrayList<>();
            StringBuilder lab = new StringBuilder();

            for (int k = 0; k < size; k++) {
                if (idx >= n) break;
                lab.append(labels.get(idx));
                if (k < size - 1) lab.append(", ");
                Double v = values.get(idx);
                if (v != null && !v.isNaN()) bucket.add(v);
                idx++;
            }
            String label = lab.length() > 20 ? (lab.substring(0, 18) + "…") : lab.toString();
            if (bucket.isEmpty()) aggVal = Double.NaN;
            else {
                switch (agg) {
                    case SUM: aggVal = bucket.stream().mapToDouble(Double::doubleValue).sum(); break;
                    case MEDIAN: aggVal = median(bucket); break;
                    case COUNT: aggVal = bucket.size(); break;
                    default: aggVal = bucket.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
                }
            }
            outLabels.add(label);
            outValues.add(aggVal);
        }

        DataSeries ds = new DataSeries(series.getName() + " (binned " + bins + ")", outLabels, outValues);
        return ds;
    }

    /**
     * Build per-bin numeric lists for boxplot creation.
     * Returns list of buckets: each bucket is List<Double>
     */
    public static List<Bucket> bucketize(DataSeries series, int bins) {
        List<String> labels = series.getLabels();
        List<Double> values = series.getValues();
        int n = labels.size();
        if (bins <= 0) bins = 1;
        if (bins >= n) {
            // one value per label → each bucket single element
            List<Bucket> out = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                Bucket b = new Bucket(labels.get(i), new ArrayList<>());
                Double v = values.get(i);
                if (v != null && !v.isNaN()) b.values.add(v);
                out.add(b);
            }
            return out;
        }
        int base = n / bins;
        int rem = n % bins;

        List<Bucket> out = new ArrayList<>();
        int idx = 0;
        for (int b = 0; b < bins; b++) {
            int size = base + (b < rem ? 1 : 0);
            StringBuilder lab = new StringBuilder();
            List<Double> vals = new ArrayList<>();
            for (int k = 0; k < size && idx < n; k++) {
                String l = labels.get(idx);
                lab.append(l);
                if (k < size - 1) lab.append(", ");
                Double v = values.get(idx);
                if (v != null && !v.isNaN()) vals.add(v);
                idx++;
            }
            String label = lab.length() > 20 ? (lab.substring(0, 18) + "…") : lab.toString();
            out.add(new Bucket(label, vals));
        }
        return out;
    }

    public static class Bucket {
        public final String label;
        public final List<Double> values;
        public Bucket(String label, List<Double> values) {
            this.label = label; this.values = values;
        }
    }

    private static double median(List<Double> list) {
        if (list.isEmpty()) return Double.NaN;
        Collections.sort(list);
        int m = list.size() / 2;
        if (list.size() % 2 == 1) return list.get(m);
        return (list.get(m-1) + list.get(m)) / 2.0;
    }
}
