package com.run.ui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;

// app entry point
public class RunApp {
    public static void main(String[] args) {
        // set dark look and feel defaults
        UIManager.put("Panel.background", new Color(30, 30, 30));
        UIManager.put("OptionPane.background", new Color(30, 30, 30));
        UIManager.put("OptionPane.messageForeground", new Color(230, 230, 230));

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
