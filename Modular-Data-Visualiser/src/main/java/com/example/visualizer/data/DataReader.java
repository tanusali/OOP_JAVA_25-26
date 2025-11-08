package com.example.visualizer.data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataReader {

    public DataSet readCSV(File file) throws IOException {
        try (InputStream is = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             CSVReader csv = new CSVReader(isr)) {

            DataSet ds = new DataSet();
            String[] header = csv.readNext();
            if (header == null) return ds;
            ds.setColumnNames(Arrays.stream(header).collect(Collectors.toList()));

            String[] row;
            while ((row = csv.readNext()) != null) {
                ds.addRow(Arrays.stream(row).collect(Collectors.toList()));
            }
            return ds;
        } catch (CsvValidationException e) {
            throw new IOException("CSV validation error: " + e.getMessage(), e);
        }
    }
}
