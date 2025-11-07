package com.example.visualizer.data;

import java.util.Objects;

/**
 * Simple container representing one (label, value) pair.
 * Used by charts to render points.
 */
public class DataPoint implements Comparable<DataPoint> {
    private final String label;
    private final double value;

    public DataPoint(String label, double value) {
        this.label = label == null ? "" : label;
        this.value = value;
    }

    /** Convenience constructor accepting Double (may be null) */
    public DataPoint(String label, Double value) {
        this.label = label == null ? "" : label;
        this.value = value == null ? 0.0 : value.doubleValue();
    }

    // --- Getters commonly used by chart code ---
    public String getLabel() {
        return label;
    }

    public double getValue() {
        return value;
    }

    // Some code may expect getX/getY — provide aliases
    public String getX() {
        return label;
    }

    public double getY() {
        return value;
    }

    @Override
    public String toString() {
        return "DataPoint{" + "label='" + label + '\'' + ", value=" + value + '}';
    }

    @Override
    public int compareTo(DataPoint o) {
        return Double.compare(this.value, o.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataPoint)) return false;
        DataPoint dataPoint = (DataPoint) o;
        return Double.compare(dataPoint.value, value) == 0 &&
                Objects.equals(label, dataPoint.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, value);
    }
}
