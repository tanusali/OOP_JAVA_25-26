package com.example.visualizer.charts;

import com.example.visualizer.data.DataSeries;
import java.awt.*;

public interface Chart {
    void draw(Graphics2D g, Rectangle bounds, DataSeries series);
}
