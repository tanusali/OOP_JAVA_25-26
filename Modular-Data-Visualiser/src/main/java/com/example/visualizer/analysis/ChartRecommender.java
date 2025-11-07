package com.example.visualizer.analysis;

import com.example.visualizer.data.DataSet;
import com.example.visualizer.data.DataSeries;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

public class ChartRecommender {

    public static class Recommendation {
        public final String chartType; // "Bar","Line","Pie"
        public final List<String> reasons; // explainability reasons, human readable
        public Recommendation(String chartType, List<String> reasons) {
            this.chartType = chartType; this.reasons = reasons;
        }
        public String getExplanation() {
            return String.join(" ", reasons);
        }
    }

    // thresholds you can change
    private static final int PIE_MAX_CATEGORIES = 6;
    private static final int BAR_MAX_CATEGORIES = 30;

    public Recommendation recommend(DataSet ds, int xIndex, int yIndex) {
        List<String> reasons = new ArrayList<>();
        if (ds == null) return new Recommendation("Bar", Collections.singletonList("No data loaded"));

        // Basic checks
        boolean yNumeric = isColumnNumeric(ds, yIndex);
        boolean xIsTime = isProbablyDateColumn(ds, xIndex);
        int uniqueX = countUnique(ds, xIndex);
        int rowCount = ds.getRowCount();

        // Heuristics
        if (xIsTime && yNumeric) {
            reasons.add("X looks like a time series and Y is numeric → Line chart recommended to show trends over time.");
            if (uniqueX <= 5) reasons.add("Only " + uniqueX + " time points — line still okay but consider bar for clarity.");
            return new Recommendation("Line", reasons);
        }

        if (!yNumeric) {
            reasons.add("Y is not numeric — Pie/Bar of counts recommended.");
            if (uniqueX <= PIE_MAX_CATEGORIES) {
                reasons.add("X has " + uniqueX + " categories (≤ " + PIE_MAX_CATEGORIES + ") → Pie chart is useful for share/parts.");
                return new Recommendation("Pie", reasons);
            } else {
                reasons.add("X has " + uniqueX + " categories (> " + PIE_MAX_CATEGORIES + ") → Bar chart recommended for readability.");
                return new Recommendation("Bar", reasons);
            }
        }

        // Y is numeric
        if (uniqueX <= PIE_MAX_CATEGORIES) {
            reasons.add("X has " + uniqueX + " categories and Y is numeric → Bar chart is clear; Pie possible for share view.");
            return new Recommendation("Bar", reasons);
        }

        if (rowCount <= 50 && uniqueX <= BAR_MAX_CATEGORIES) {
            reasons.add("Moderate number of points (" + rowCount + "). Bar or Line could work — Line shows trend, Bar shows discrete comparisons.");
            // prefer line for numeric sequences if X is ordinal/can be sorted
            return new Recommendation("Line", reasons);
        }

        // default
        reasons.add("Dataset is large or categorical mix — Bar chart is safest default for comparisons.");
        return new Recommendation("Bar", reasons);
    }

    private boolean isColumnNumeric(DataSet ds, int colIdx) {
        if (ds == null) return false;
        int n = Math.min(ds.getRowCount(), 200); // sample first 200 rows
        int numericCount = 0, total = 0;
        for (int i = 0; i < n; i++) {
            List<String> row = ds.getRows().get(i);
            if (colIdx >= row.size()) continue;
            String s = row.get(colIdx);
            if (s == null) continue;
            s = s.trim().replaceAll(",", "").replaceAll("₹|\\$|€", "");
            if (s.isEmpty()) continue;
            total++;
            try {
                Double.parseDouble(s);
                numericCount++;
            } catch (NumberFormatException ex) {
                // not numeric
            }
        }
        if (total == 0) return false;
        return numericCount >= (0.8 * total); // numeric if >=80% values numeric
    }

    private boolean isProbablyDateColumn(DataSet ds, int colIdx) {
        if (ds == null) return false;
        List<String> samples = new ArrayList<>();
        int n = Math.min(ds.getRowCount(), 40);
        for (int i=0;i<n;i++){
            List<String> row = ds.getRows().get(i);
            if (colIdx < row.size()) samples.add(row.get(colIdx));
        }
        if (samples.isEmpty()) return false;
        int success = 0;
        for (String s : samples) {
            if (s == null) continue;
            s = s.trim();
            if (s.isEmpty()) continue;
            if (looksLikeDate(s)) success++;
        }
        return success >= Math.max(3, samples.size() * 0.6); // 60% chance
    }

    private boolean looksLikeDate(String s) {
        // simple patterns: yyyy-mm-dd, dd/mm/yyyy, dd-mm-yyyy, month names, timestamps
        String[] patterns = {
                "\\d{4}-\\d{1,2}-\\d{1,2}",
                "\\d{1,2}/\\d{1,2}/\\d{4}",
                "\\d{1,2}-\\d{1,2}-\\d{4}",
                "\\d{4}/\\d{1,2}/\\d{1,2}",
                "\\d{1,2} [A-Za-z]{3,} \\d{4}", // 12 Mar 2023
        };
        for (String p : patterns) if (Pattern.matches(p, s)) return true;
        // attempt ISO parse
        try {
            DateTimeFormatter.ISO_LOCAL_DATE.parse(s);
            return true;
        } catch (DateTimeParseException ignored) {}
        return false;
    }

    private int countUnique(DataSet ds, int colIdx) {
        if (ds == null) return 0;
        Set<String> set = new HashSet<>();
        for (List<String> row : ds.getRows()) {
            if (colIdx < row.size()) set.add(row.get(colIdx));
        }
        return set.size();
    }
}
