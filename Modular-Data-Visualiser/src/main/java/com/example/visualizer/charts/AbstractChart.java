package com.example.visualizer.charts;

import com.example.visualizer.data.DataSeries;
import java.awt.*;

public abstract class AbstractChart implements Chart {
    protected String title = "";

    public void setTitle(String t) { this.title = t; }

    protected void drawTitle(Graphics2D g, Rectangle bounds) {
        if (title == null || title.isEmpty()) return;
        Font orig = g.getFont();
        g.setFont(orig.deriveFont(Font.BOLD, 16f));
        FontMetrics fm = g.getFontMetrics();
        int x = bounds.x + (bounds.width - fm.stringWidth(title)) / 2;
        g.drawString(title, x, bounds.y + fm.getAscent() + 4);
        g.setFont(orig);
    }

    protected void enableQuality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    public abstract void draw(Graphics2D g, Rectangle bounds, DataSeries series);
}
