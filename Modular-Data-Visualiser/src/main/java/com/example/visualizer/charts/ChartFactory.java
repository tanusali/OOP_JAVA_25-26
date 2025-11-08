package com.example.visualizer.charts;

public class ChartFactory {
    public static Chart create(String type) {
        switch (type.toLowerCase()) {
            case "bar": return new BarChart();
            case "line": return new LineChart();
            case "pie": return new PieChart();
            case "box": return new BoxChart();
            default: return new BarChart();
        }
    }
}
