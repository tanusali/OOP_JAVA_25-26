package com.example.visualizer.ui;

import com.example.visualizer.analysis.BinAggregator;
import com.example.visualizer.analysis.ChartRecommender;
import com.example.visualizer.charts.Chart;
import com.example.visualizer.charts.ChartFactory;
import com.example.visualizer.data.DataReader;
import com.example.visualizer.data.DataSeries;
import com.example.visualizer.data.DataSet;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * MainWindow - UI for Modular Data Visualizer
 * Supports: Recommender, Auto-Aggregation (binning), Box plot
 */
public class MainWindow extends JFrame {

    // === Colors and Fonts ===
    private static final Color BG = new Color(250, 250, 250);
    private static final Color SIDEBAR_BG = new Color(245, 247, 250);
    private static final Color PRIMARY = new Color(33, 150, 243);
    private static final Color ACCENT = new Color(76, 175, 80);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    // === Constants ===
    private static final int SIDEBAR_WIDTH = 340;
    private static final int MAX_POINTS_BEFORE_BIN = 200;

    // === Components ===
    private final ChartPanel chartPanel = new ChartPanel();
    private final JComboBox<String> xColumnBox = new JComboBox<>();
    private final JComboBox<String> yColumnBox = new JComboBox<>();
    private final JComboBox<String> chartTypeBox = new JComboBox<>(new String[]{"Bar", "Line", "Pie", "Box"});
    private final JLabel suggestionLabel = new JLabel();
    private final JTextArea suggestionArea = new JTextArea();

    // Aggregation controls
    private final JSpinner binsSpinner = new JSpinner(new SpinnerNumberModel(10, 2, 200, 1));
    private final JCheckBox autoAggCheck = new JCheckBox("Auto-aggregate large X", true);

    // === Data ===
    private DataSet currentDataSet;

    public MainWindow() {
        setTitle("Modular Data Visualizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 760);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(10, 10));

        add(createTopToolbar(), BorderLayout.NORTH);
        add(createSidebar(), BorderLayout.WEST);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setBackground(BG);
        centerWrap.setBorder(new EmptyBorder(12, 12, 12, 12));
        chartPanel.setPreferredSize(new Dimension(860, 680));
        centerWrap.add(chartPanel, BorderLayout.CENTER);
        add(centerWrap, BorderLayout.CENTER);

        updateRecommendation();
    }

    // ===== Toolbar =====
    private JToolBar createTopToolbar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(BG);
        tb.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(6, 8, 6, 8)
        ));

        // Title (left)
        JLabel title = new JLabel("Modular Data Visualizer");
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY.darker());
        tb.add(title);

        tb.add(Box.createHorizontalGlue()); // push buttons to right

        JButton help = makeToolbarButton("?", "Help");
        help.addActionListener(e -> showHelp());
        tb.add(help);

        JButton about = makeToolbarButton("i", "About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Modular Data Visualizer\nBuilt with Java Swing\nAniket, Atharv and Ashutosh", "About", JOptionPane.INFORMATION_MESSAGE));
        tb.add(about);

        return tb;
    }

    private JButton makeToolbarButton(String text, String tooltip) {
        JButton b = new JButton(text);
        b.setToolTipText(tooltip);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusable(false);
        b.setBorder(new LineBorder(new Color(220,220,220), 1, true));
        b.setBackground(Color.WHITE);
        b.setPreferredSize(new Dimension(36, 28));
        return b;
    }

    // ===== Sidebar =====
    private JPanel createSidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(SIDEBAR_WIDTH, getHeight()));
        side.setBackground(SIDEBAR_BG);
        side.setLayout(new BorderLayout());

        // container with vertical layout
        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setBorder(new EmptyBorder(12, 12, 12, 12)); // <-- positive padding
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        // HEADER: Data label + centered Open CSV button
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel dataLabel = makeSectionLabel("Data");
        dataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(dataLabel);
        header.add(Box.createRigidArea(new Dimension(0,8)));

        JButton openBtn = new JButton("Open CSV");
        openBtn.setAlignmentX(Component.CENTER_ALIGNMENT); // centered
        openBtn.setBackground(PRIMARY);
        openBtn.setForeground(Color.WHITE);
        openBtn.setFocusPainted(false);
        openBtn.setFont(LABEL_FONT.deriveFont(Font.BOLD));
        openBtn.setMaximumSize(new Dimension(SIDEBAR_WIDTH - 40, 36));
        openBtn.setPreferredSize(new Dimension(SIDEBAR_WIDTH - 40, 36));
        openBtn.addActionListener(e -> openCsv());
        header.add(openBtn);
        header.add(Box.createRigidArea(new Dimension(0,12)));
        controls.add(header);

        // COLUMNS section
        controls.add(makeSectionLabel("Columns"));
        // set consistent size so combobox arrow isn't clipped
        Dimension comboSize = new Dimension(SIDEBAR_WIDTH - 40, 30);
        xColumnBox.setMaximumSize(comboSize);
        xColumnBox.setPreferredSize(comboSize);
        xColumnBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        yColumnBox.setMaximumSize(comboSize);
        yColumnBox.setPreferredSize(comboSize);
        yColumnBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        controls.add(labelledComponent("X Column (labels)", xColumnBox));
        controls.add(Box.createRigidArea(new Dimension(0,8)));
        controls.add(labelledComponent("Y Column (values)", yColumnBox));
        controls.add(Box.createRigidArea(new Dimension(0,12)));

        // CHART section
        controls.add(makeSectionLabel("Chart"));
        chartTypeBox.setMaximumSize(comboSize);
        chartTypeBox.setPreferredSize(comboSize);
        chartTypeBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        controls.add(labelledComponent("Chart type", chartTypeBox));
        controls.add(Box.createRigidArea(new Dimension(0,8)));

        // Aggregation row
        JPanel aggRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        aggRow.setOpaque(false);
        JLabel bLab = new JLabel("Bins:");
        bLab.setFont(LABEL_FONT.deriveFont(12f));
        binsSpinner.setPreferredSize(new Dimension(70, 24));
        autoAggCheck.setOpaque(false);
        aggRow.add(bLab);
        aggRow.add(binsSpinner);
        aggRow.add(autoAggCheck);
        aggRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        controls.add(aggRow);
        controls.add(Box.createRigidArea(new Dimension(0,8)));

        // Recommendation
        suggestionLabel.setFont(LABEL_FONT.deriveFont(Font.BOLD, 12f));
        suggestionLabel.setForeground(PRIMARY.darker());
        suggestionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controls.add(suggestionLabel);
        controls.add(Box.createRigidArea(new Dimension(0,6)));

        suggestionArea.setEditable(false);
        suggestionArea.setLineWrap(true);
        suggestionArea.setWrapStyleWord(true);
        suggestionArea.setFont(LABEL_FONT.deriveFont(11f));
        suggestionArea.setOpaque(false);
        suggestionArea.setBorder(new EmptyBorder(0, 0, 8, 0));
        suggestionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        suggestionArea.setMaximumSize(new Dimension(SIDEBAR_WIDTH - 40, 80));
        controls.add(suggestionArea);
        controls.add(Box.createRigidArea(new Dimension(0,8)));

        // Buttons (draw + export)
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        JButton drawBtn = new JButton("Draw Chart");
        drawBtn.setBackground(ACCENT);
        drawBtn.setForeground(Color.WHITE);
        drawBtn.setFocusPainted(false);
        drawBtn.setFont(LABEL_FONT.deriveFont(Font.BOLD));
        drawBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        drawBtn.setMaximumSize(new Dimension(SIDEBAR_WIDTH - 40, 40));
        drawBtn.addActionListener(e -> drawChart());
        actions.add(drawBtn);
        actions.add(Box.createRigidArea(new Dimension(0,8)));

        JButton exportBtn = new JButton("Export PNG");
        exportBtn.setBackground(Color.WHITE);
        exportBtn.setBorder(new LineBorder(new Color(220,220,220), 1, true));
        exportBtn.setFocusPainted(false);
        exportBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        exportBtn.setMaximumSize(new Dimension(SIDEBAR_WIDTH - 40, 36));
        exportBtn.addActionListener(e -> exportPng());
        actions.add(exportBtn);
        controls.add(actions);

        // listeners to update recommendation
        xColumnBox.addActionListener(e -> updateRecommendation());
        yColumnBox.addActionListener(e -> updateRecommendation());
        chartTypeBox.addActionListener(e -> updateRecommendation());
        binsSpinner.addChangeListener(e -> updateRecommendation());
        autoAggCheck.addActionListener(e -> updateRecommendation());

        controls.add(Box.createVerticalGlue());

        side.add(controls, BorderLayout.NORTH);

        JLabel foot = new JLabel("Tip: Use short labels for X axis");
        foot.setFont(LABEL_FONT.deriveFont(11f));
        foot.setBorder(new EmptyBorder(8,12,8,12));
        side.add(foot, BorderLayout.SOUTH);

        return side;
    }

    // ===== Utility UI Methods =====
    private JComponent labelledComponent(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(6,6));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(LABEL_FONT);
        l.setForeground(Color.DARK_GRAY);
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private JLabel makeSectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(LABEL_FONT.deriveFont(Font.BOLD, 13f));
        l.setBorder(new EmptyBorder(6,0,6,0));
        l.setForeground(PRIMARY.darker());
        return l;
    }

    private void showHelp() {
        String msg = "Load CSV → select X and Y columns → choose chart type → Draw Chart.\n" +
                "Auto-aggregation reduces too many X labels into bins (avg per bin).";
        JOptionPane.showMessageDialog(this, msg, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== Core Functions =====
    private void openCsv() {
        JFileChooser fc = new JFileChooser();
        int r = fc.showOpenDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) return;

        File f = fc.getSelectedFile();
        DataReader dr = new DataReader();
        try {
            DataSet ds = dr.readCSV(f);
            currentDataSet = ds;

            List<String> cols = ds.getColumnNames();
            xColumnBox.removeAllItems();
            yColumnBox.removeAllItems();
            for (String c : cols) { xColumnBox.addItem(c); yColumnBox.addItem(c); }

            if (cols.size() > 0) xColumnBox.setSelectedIndex(0);
            if (cols.size() > 1) yColumnBox.setSelectedIndex(1);

            updateRecommendation();
            JOptionPane.showMessageDialog(this, "Loaded: " + f.getName() + " (" + ds.getRowCount() + " rows)");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to open CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void drawChart() {
        if (currentDataSet == null) {
            JOptionPane.showMessageDialog(this, "Open a CSV first.");
            return;
        }

        int xIndex = xColumnBox.getSelectedIndex();
        int yIndex = yColumnBox.getSelectedIndex();
        if (xIndex < 0 || yIndex < 0) {
            JOptionPane.showMessageDialog(this, "Select X and Y columns.");
            return;
        }

        DataSeries orig = currentDataSet.toSeries(xIndex, yIndex);
        String chartType = (String) chartTypeBox.getSelectedItem();
        if (chartType == null) chartType = "Bar";

        int bins = (Integer) binsSpinner.getValue();
        boolean autoAggregate = autoAggCheck.isSelected();

        int points = orig.getLabels().size();
        DataSeries toDraw = orig;

        if (autoAggregate && points > MAX_POINTS_BEFORE_BIN && !chartType.equalsIgnoreCase("Box")) {
            toDraw = BinAggregator.aggregateForBarLine(orig, bins, BinAggregator.Agg.MEAN);
        }

        Chart chart = ChartFactory.create(chartType);
        try {
            chart.getClass().getMethod("setTitle", String.class)
                    .invoke(chart, chartType + " - " + orig.getName());
        } catch (Exception ignored) {}

        chartPanel.setChart(chart);
        chartPanel.setSeries(toDraw);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void exportPng() {
        JFileChooser fc = new JFileChooser();
        int r = fc.showSaveDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) return;

        File f = fc.getSelectedFile();
        BufferedImage img = new BufferedImage(chartPanel.getWidth(), chartPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        chartPanel.paint(g2);
        g2.dispose();

        try {
            ImageIO.write(img, "png", f);
            JOptionPane.showMessageDialog(this, "Exported PNG: " + f.getName());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save PNG: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRecommendation() {
        if (currentDataSet == null) {
            suggestionLabel.setText("Recommended: —");
            suggestionArea.setText("Load a CSV to get chart suggestions.");
            return;
        }

        int xIdx = xColumnBox.getSelectedIndex();
        int yIdx = yColumnBox.getSelectedIndex();
        if (xIdx < 0 || yIdx < 0) return;

        ChartRecommender rec = new ChartRecommender();
        ChartRecommender.Recommendation r = rec.recommend(currentDataSet, xIdx, yIdx);

        suggestionLabel.setText("Recommended: " + r.chartType);
        suggestionArea.setText(r.getExplanation());
    }
}
