package com.run.ui;

import com.run.ui.panels.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// main window with sidebar navigation and content area
// matches the wireframe layout: dark sidebar on left, panels on right
public class MainFrame extends JFrame implements NavigationListener {

    public static final String DASHBOARD        = "DASHBOARD";
    public static final String LINK_CHECKER     = "LINK_CHECKER";
    public static final String FILE_SCANNER     = "FILE_SCANNER";
    public static final String EMAIL_SCANNER    = "EMAIL_SCANNER";
    public static final String PASSWORD_CHECKER = "PASSWORD_CHECKER";
    public static final String BREACH_CHECKER   = "BREACH_CHECKER";
    public static final String SCAN_HISTORY     = "SCAN_HISTORY";

    private static final Color BG_DARK      = new Color(30, 30, 30);
    private static final Color SIDEBAR_BG   = new Color(40, 40, 40);
    private static final Color SIDEBAR_HOVER = new Color(55, 55, 55);
    private static final Color TEXT_PRIMARY  = new Color(230, 230, 230);
    private static final Color TEXT_MUTED    = new Color(150, 150, 150);
    private static final Color ACCENT        = new Color(99, 102, 241);
    private static final Color NAVBAR_BG     = new Color(35, 35, 35);
    private static final Color NAVBAR_BORDER = new Color(55, 55, 55);
    private static final Color AVATAR_BG     = new Color(99, 102, 241);

    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private JButton activeButton;

        private String userEmail = "user@example.com";

    public MainFrame() {
        super("Run - Cybersecurity Fundamentals");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        setLayout(new BorderLayout());

        // top navbar
        add(buildNavbar(), BorderLayout.NORTH);
 

        // sidebar
        JPanel sidebar = buildSidebar();
        add(sidebar, BorderLayout.WEST);

        // content area
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(BG_DARK);

        cardPanel.add(new DashboardPanel(this),    DASHBOARD);
        cardPanel.add(new LinkCheckerPanel(this),   LINK_CHECKER);
        cardPanel.add(new FileScannerPanel(this),   FILE_SCANNER);
        cardPanel.add(makePlaceholder("Email Scanner - Coming Soon"),    EMAIL_SCANNER);
        cardPanel.add(makePlaceholder("Password Checker - Coming Soon"), PASSWORD_CHECKER);
        cardPanel.add(makePlaceholder("Breach Checker - Coming Soon"),   BREACH_CHECKER);
        cardPanel.add(makePlaceholder("Scan History - Coming Soon"),     SCAN_HISTORY);

        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, DASHBOARD);
    }

    private JPanel buildNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(NAVBAR_BG);
        navbar.setPreferredSize(new Dimension(0, 52));
        navbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, NAVBAR_BORDER),
            new EmptyBorder(0, 20, 0, 20)
        ));
 
        // left: app name
        JLabel appName = new JLabel("Rún");
        appName.setFont(new Font("SansSerif", Font.BOLD, 18));
        appName.setForeground(TEXT_PRIMARY);
        navbar.add(appName, BorderLayout.WEST);
 
        // right: email + avatar
        JPanel userSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        userSection.setOpaque(false);
 
        JLabel emailLabel = new JLabel(userEmail);
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        emailLabel.setForeground(TEXT_MUTED);
 
        JLabel avatar = new JLabel("U") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AVATAR_BG);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setPreferredSize(new Dimension(30, 30));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setVerticalAlignment(SwingConstants.CENTER);
        avatar.setFont(new Font("SansSerif", Font.BOLD, 13));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(false);
 
        userSection.add(emailLabel);
        userSection.add(avatar);
        navbar.add(userSection, BorderLayout.EAST);
 
        return navbar;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));
 
        // tools label
        JLabel toolsLabel = new JLabel("  TOOLS");
        toolsLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        toolsLabel.setForeground(TEXT_MUTED);
        toolsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolsLabel.setBorder(new EmptyBorder(0, 16, 8, 0));
        sidebar.add(toolsLabel);
 
        // nav buttons
        activeButton = addNavButton(sidebar, "Dashboard",        DASHBOARD);
        addNavButton(sidebar, "Email scanner",    EMAIL_SCANNER);
        addNavButton(sidebar, "Link scanner",     LINK_CHECKER);
        addNavButton(sidebar, "File scanner",     FILE_SCANNER);
        addNavButton(sidebar, "Password checker", PASSWORD_CHECKER);
        addNavButton(sidebar, "Breach checker",   BREACH_CHECKER);
 
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }
 

    private JButton addNavButton(JPanel sidebar, String label, String panelName) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 36));
        btn.setBorder(new EmptyBorder(8, 20, 8, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeButton) btn.setBackground(SIDEBAR_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeButton) btn.setBackground(SIDEBAR_BG);
            }
        });

        btn.addActionListener(e -> {
            if (activeButton != null) {
                activeButton.setBackground(SIDEBAR_BG);
                activeButton.setForeground(TEXT_PRIMARY);
            }
            btn.setBackground(ACCENT);
            btn.setForeground(Color.WHITE);
            activeButton = btn;
            navigateTo(panelName);
        });

        if (panelName.equals(DASHBOARD)) {
            btn.setBackground(ACCENT);
            btn.setForeground(Color.WHITE);
        }

        sidebar.add(btn);
        return btn;
    }

    private JPanel makePlaceholder(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_DARK);
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        label.setForeground(TEXT_MUTED);
        panel.add(label);
        return panel;
    }

    // called after login to update the navbar with the real user email
    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @Override
    public void navigateTo(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }
}
