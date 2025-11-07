package com.example.visualizer.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single named data series — for example,
 * X = categories/labels, Y = numeric values.
 *
 * Provides both low-level access (getLabels/getValues) and a
 * compatibility method getPoints() which returns List<DataPoint>
 * (used by charts in this project).
 */
public class DataSeries {
    private final String name;
    private final List<String> labels; // X-axis values
    private final List<Double> values; // Y-axis values

    public DataSeries(String name) {
        this.name = name;
        this.labels = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public DataSeries(String name, List<String> labels, List<Double> values) {
        this.name = name;
        // defensive copies
        this.labels = (labels != null) ? new ArrayList<>(labels) : new ArrayList<>();
        this.values = (values != null) ? new ArrayList<>(values) : new ArrayList<>();
    }

    // --- Add a single data point ---
    public void add(String label, double value) {
        labels.add(label);
        values.add(value);
    }

    // --- Getters ---
    public String getName() {
        return name;
    }

    public List<String> getLabels() {
        return Collections.unmodifiableList(labels);
    }

    public List<Double> getValues() {
        return Collections.unmodifiableList(values);
    }

    // --- Compatibility method expected by charts: returns List<DataPoint> ---
    // This method builds a list of DataPoint objects from labels & values.
    public List<DataPoint> getPoints() {
        int n = Math.min(labels.size(), values.size());
        List<DataPoint> pts = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            pts.add(new DataPoint(labels.get(i), values.get(i)));
        }
        return pts;
    }

    // --- Derived information ---
    public int size() {
        return Math.min(labels.size(), values.size());
    }

    public double getMaxValue() {
        return values.stream().mapToDouble(Double::doubleValue).max().orElse(0);
    }

    public double getMinValue() {
        return values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
    }

    public double getMeanValue() {
        if (values.isEmpty()) return 0;
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    public double getSumValue() {
        return values.stream().mapToDouble(Double::doubleValue).sum();
    }

    // --- Safe element access ---
    public String getLabelAt(int index) {
        return (index >= 0 && index < labels.size()) ? labels.get(index) : null;
    }

    public Double getValueAt(int index) {
        return (index >= 0 && index < values.size()) ? values.get(index) : null;
    }

    // --- For debugging ---
    @Override
    public String toString() {
        return "DataSeries{name='" + name + "', points=" + size() + "}";
    }
}
