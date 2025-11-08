package com.example.visualizer;

import com.example.visualizer.ui.MainWindow;

import javax.swing.*;

public class AppMain {
    public static void main(String[] args) {
        // Set Nimbus Look & Feel safely — don't change Nimbus UI defaults in ways that can cause cycles.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available or setting LAF fails, fall back to system LAF
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // ignore — worst case the default LAF will be used
            }
        }

        SwingUtilities.invokeLater(() -> {
            MainWindow mw = new MainWindow();
            mw.setVisible(true);
        });
    }
}
