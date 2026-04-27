package com.run.ui.panels;

import com.run.ui.NavigationListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// basic dashboard - just shows the app name
// teammates can style this later
public class DashboardPanel extends JPanel {

    private static final Color BG_DARK     = new Color(30, 30, 30);
    private static final Color TEXT_PRIMARY = new Color(230, 230, 230);
    private static final Color TEXT_MUTED   = new Color(150, 150, 150);

    public DashboardPanel(NavigationListener nav) {
        setLayout(new GridBagLayout());
        setBackground(BG_DARK);

        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBackground(BG_DARK);

        JLabel title = new JLabel("Run");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(title);

        centre.add(Box.createVerticalStrut(8));

        JLabel subtitle = new JLabel("Select a tool from the sidebar to get started");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(subtitle);

        add(centre);
    }
}
