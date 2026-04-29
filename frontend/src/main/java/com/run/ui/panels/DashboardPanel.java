package com.run.ui.panels;
import com.run.ui.NavigationListener;
import com.run.ui.MainFrame;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
 
// dashboard panel - landing screen showing all tool cards and recent scans
// wires into MainFrame via NavigationListener
public class DashboardPanel extends JPanel {
 
    // colours pulled directly from MainFrame constants
    private static final Color BG_DARK       = new Color(30, 30, 30);
    private static final Color CARD_BG       = new Color(45, 45, 45);
    private static final Color CARD_HOVER    = new Color(55, 55, 55);
    private static final Color CARD_BORDER   = new Color(60, 60, 60);
    private static final Color TEXT_PRIMARY  = new Color(230, 230, 230);
    private static final Color TEXT_MUTED    = new Color(150, 150, 150);
    private static final Color ACCENT        = new Color(99, 102, 241);
 
    private final NavigationListener nav;
 
    public DashboardPanel(NavigationListener nav) {
        this.nav = nav;
        setBackground(BG_DARK);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(32, 36, 32, 36));
 
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
    }
 
    // ── Header: "Dashboard" title + subtitle ─────────────────────────────────
 
    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(0, 0, 24, 0));
 
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        JLabel subtitle = new JLabel("Select a tool below to get started");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(4, 0, 0, 0));
 
        header.add(title);
        header.add(subtitle);
        return header;
    }
 
    // ── Body: tool cards grid + recent scans ─────────────────────────────────
 
    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
 
        body.add(buildToolGrid());
        body.add(Box.createVerticalStrut(28));
        body.add(buildRecentScans());
 
        return body;
    }
 
    // ── Tool cards: 3-column grid, two rows ──────────────────────────────────
 
    private JPanel buildToolGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 14, 14));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
 
        grid.add(buildToolCard(
            "✉",
            "Email scanner",
            "Detect phishing & scam emails",
            MainFrame.EMAIL_SCANNER
        ));
        grid.add(buildToolCard(
            "🔗",
            "Link scanner",
            "Check URLs before visiting",
            MainFrame.LINK_CHECKER
        ));
        grid.add(buildToolCard(
            "📄",
            "File scanner",
            "Check files for malware",
            MainFrame.FILE_SCANNER
        ));
        grid.add(buildToolCard(
            "🔒",
            "Password checker",
            "Test password strength",
            MainFrame.PASSWORD_CHECKER
        ));
        grid.add(buildToolCard(
            "🔍",
            "Breach checker",
            "Check for data breaches",
            MainFrame.BREACH_CHECKER
        ));
 
        // empty cell to keep grid balanced (3 cols, 2 rows = 6 slots, 5 tools)
        JPanel empty = new JPanel();
        empty.setOpaque(false);
        grid.add(empty);
 
        return grid;
    }
 
    private JPanel buildToolCard(String icon, String title, String description, String panelName) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(CARD_BORDER, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
 
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        iconLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
 
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        JLabel descLabel = new JLabel("<html><p style='width:140px'>" + description + "</p></html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_MUTED);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
 
        card.add(iconLabel);
        card.add(titleLabel);
        card.add(descLabel);
 
        // hover + click behaviour
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(CARD_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BG);
            }
            @Override public void mouseClicked(MouseEvent e) {
                nav.navigateTo(panelName);
            }
        });
 
        // propagate mouse events from child labels so the whole card is clickable
        MouseAdapter propagate = new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(CARD_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BG);
            }
            @Override public void mouseClicked(MouseEvent e) {
                nav.navigateTo(panelName);
            }
        };
        iconLabel.addMouseListener(propagate);
        titleLabel.addMouseListener(propagate);
        descLabel.addMouseListener(propagate);
 
        return card;
    }
 
    // ── Recent scans section ──────────────────────────────────────────────────
 
    private JPanel buildRecentScans() {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        JLabel sectionLabel = new JLabel("RECENT SCANS");
        sectionLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        sectionLabel.setForeground(TEXT_MUTED);
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
 
        // placeholder box matching wireframe
        JPanel placeholder = new JPanel(new GridBagLayout());
        placeholder.setBackground(CARD_BG);
        placeholder.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(CARD_BORDER, 1, true),
            new EmptyBorder(24, 24, 24, 24)
        ));
        placeholder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        placeholder.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        JLabel placeholderText = new JLabel("Recent scan history will appear here");
        placeholderText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        placeholderText.setForeground(TEXT_MUTED);
        placeholder.add(placeholderText);
 
        section.add(sectionLabel);
        section.add(placeholder);
        return section;
    }
}
