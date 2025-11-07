package com.example.visualizer.data;

import java.util.ArrayList;
import java.util.List;

/**
 * DataSet - holds column names and raw rows (each row is a list of strings).
 * Provides helpers to convert CSV columns into DataSeries for charts.
 */
public class DataSet {
    private final List<String> columnNames = new ArrayList<>();
    private final List<List<String>> rows = new ArrayList<>();

    public void setColumnNames(List<String> names) {
        columnNames.clear();
        if (names != null) columnNames.addAll(names);
    }

    public List<String> getColumnNames() { return columnNames; }

    public void addRow(List<String> row) {
        if (row == null) return;
        rows.add(new ArrayList<>(row));
    }

    public List<List<String>> getRows() { return rows; }

    public int getRowCount() { return rows.size(); }

    public int getColCount() { return columnNames.size(); }

    /**
     * Backwards-compatible: create a series using column 0 as labels and colIndex as values.
     */
    public DataSeries toSeries(int colIndex) {
        return toSeries(0, colIndex);
    }

    /**
     * Create a DataSeries using explicit label column and value column indices.
     *
     * @param labelColIndex index of the column to use as label (e.g., 0)
     * @param valueColIndex index of the numeric value column (e.g., 1)
     * @return DataSeries with label/value pairs for each row
     */
    public DataSeries toSeries(int labelColIndex, int valueColIndex) {
        String seriesName = (valueColIndex >= 0 && valueColIndex < columnNames.size())
                ? columnNames.get(valueColIndex)
                : ("col" + valueColIndex);

        DataSeries series = new DataSeries(seriesName);

        for (int i = 0; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            // Label selection: prefer label column if valid, otherwise use row index or first column if available
            String label = Integer.toString(i);
            if (labelColIndex >= 0 && labelColIndex < row.size()) {
                label = row.get(labelColIndex);
            } else if (!row.isEmpty()) {
                label = row.get(0);
            }

            Double val = Double.NaN;
            if (valueColIndex >= 0 && valueColIndex < row.size()) {
                String s = row.get(valueColIndex);
                if (s != null) s = s.trim();
                if (s != null && !s.isEmpty()) {
                    // Try parsing as double; handle common formatted numbers like "1,234"
                    try {
                        val = Double.parseDouble(s);
                    } catch (NumberFormatException ex) {
                        try {
                            val = Double.parseDouble(s.replaceAll(",", ""));
                        } catch (Exception e) {
                            // leave as NaN
                            val = Double.NaN;
                        }
                    }
                }
            }
            series.add(label, val);
        }

        return series;
    }
}
