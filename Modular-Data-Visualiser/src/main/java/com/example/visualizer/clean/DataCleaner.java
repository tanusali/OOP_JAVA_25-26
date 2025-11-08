package com.example.visualizer.clean;

import com.example.visualizer.data.DataSet;

import java.util.*;

public class DataCleaner {

    public static class CleaningSuggestion {
        public final int row;
        public final int col;
        public final String original;
        public final String suggestion;
        public CleaningSuggestion(int row, int col, String original, String suggestion) {
            this.row = row; this.col = col; this.original = original; this.suggestion = suggestion;
        }
    }

    public List<CleaningSuggestion> suggest(DataSet ds) {
        List<CleaningSuggestion> list = new ArrayList<>();
        for (int r = 0; r < ds.getRowCount(); r++) {
            List<String> row = ds.getRows().get(r);
            for (int c = 0; c < row.size(); c++) {
                String s = row.get(c);
                if (s == null) s = "";
                String trimmed = s.trim();
                if (!trimmed.equals(s)) {
                    list.add(new CleaningSuggestion(r, c, s, trimmed));
                }
                // suggest replacing common null tokens with empty
                String low = trimmed.toLowerCase();
                if (low.equals("n/a") || low.equals("-") || low.equals("--") || low.equals("na")) {
                    list.add(new CleaningSuggestion(r, c, s, ""));
                }
                // numeric currency cleaning suggestion
                String numericCandidate = trimmed.replaceAll("[₹,$€]", "").replaceAll(",", "");
                if (!numericCandidate.equals(trimmed) && numericCandidate.matches("[-]?\\d+(\\.\\d+)?")) {
                    list.add(new CleaningSuggestion(r, c, s, numericCandidate));
                }
            }
        }
        return list;
    }

    public void applySuggestions(DataSet ds, List<CleaningSuggestion> suggestions) {
        for (CleaningSuggestion sg : suggestions) {
            List<String> row = ds.getRows().get(sg.row);
            if (sg.col < row.size()) row.set(sg.col, sg.suggestion);
        }
    }
}
