package com.run.ui.panels;
 
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.run.ui.ApiClient;
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
 
    private static final Color BG_DARK      = new Color(30, 30, 30);
    private static final Color CARD_BG      = new Color(45, 45, 45);
    private static final Color CARD_HOVER   = new Color(55, 55, 55);
    private static final Color CARD_BORDER  = new Color(60, 60, 60);
    private static final Color TEXT_PRIMARY = new Color(230, 230, 230);
    private static final Color TEXT_MUTED   = new Color(150, 150, 150);
    private static final Color ACCENT       = new Color(99, 102, 241);
    private static final Color SAFE_GREEN   = new Color(74, 222, 128);
    private static final Color WARN_YELLOW  = new Color(250, 204, 21);
    private static final Color DANGER_RED   = new Color(248, 113, 113);
 
    private final NavigationListener nav;
    private final ObjectMapper mapper = new ObjectMapper();
    private JPanel recentScansContent;
 
    public DashboardPanel(NavigationListener nav) {
        this.nav = nav;
        setBackground(BG_DARK);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(32, 36, 32, 36));
 
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
 
        // load recent scans when panel is created
        loadRecentScans();
    }
 
    // ── Header ────────────────────────────────────────────────────────────────
 
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
 
    // ── Body ──────────────────────────────────────────────────────────────────
 
    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
 
        body.add(buildToolGrid());
        body.add(Box.createVerticalStrut(28));
        body.add(buildRecentScansSection());
 
        return body;
    }
 
    // ── Tool grid: 3 cols, 2 rows — now all 6 slots filled ───────────────────
 
    private JPanel buildToolGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 14, 14));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
 
        grid.add(buildToolCard("✉",  "Email scanner",   "Detect phishing & scam emails", MainFrame.EMAIL_SCANNER));
        grid.add(buildToolCard("🔗", "Link scanner",    "Check URLs before visiting",    MainFrame.LINK_CHECKER));
        grid.add(buildToolCard("📄", "File scanner",    "Check files for malware",       MainFrame.FILE_SCANNER));
        grid.add(buildToolCard("🔒", "Password checker","Test password strength",         MainFrame.PASSWORD_CHECKER));
        grid.add(buildToolCard("🔍", "Breach checker",  "Check for data breaches",       MainFrame.BREACH_CHECKER));
        grid.add(buildToolCard("🕓", "Scan History",    "View all past scan results",    MainFrame.SCAN_HISTORY));
 
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
 
        MouseAdapter click = new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(CARD_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { card.setBackground(CARD_BG); }
            @Override public void mouseClicked(MouseEvent e) { nav.navigateTo(panelName); }
        };
        card.addMouseListener(click);
        iconLabel.addMouseListener(click);
        titleLabel.addMouseListener(click);
        descLabel.addMouseListener(click);
 
        return card;
    }
 
    // ── Recent scans section ──────────────────────────────────────────────────
 
    private JPanel buildRecentScansSection() {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        // header row: label + "View all" link
        JPanel sectionHeader = new JPanel(new BorderLayout());
        sectionHeader.setOpaque(false);
        sectionHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        sectionHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
 
        JLabel sectionLabel = new JLabel("RECENT SCANS");
        sectionLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        sectionLabel.setForeground(TEXT_MUTED);
 
        JLabel viewAll = new JLabel("View all →");
        viewAll.setFont(new Font("SansSerif", Font.PLAIN, 11));
        viewAll.setForeground(ACCENT);
        viewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAll.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                nav.navigateTo(MainFrame.SCAN_HISTORY);
            }
        });
 
        sectionHeader.add(sectionLabel, BorderLayout.WEST);
        sectionHeader.add(viewAll, BorderLayout.EAST);
 
        // content area — updated by loadRecentScans()
        recentScansContent = new JPanel();
        recentScansContent.setLayout(new BoxLayout(recentScansContent, BoxLayout.Y_AXIS));
        recentScansContent.setBackground(CARD_BG);
        recentScansContent.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(CARD_BORDER, 1, true),
            new EmptyBorder(16, 20, 16, 20)
        ));
        recentScansContent.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        // default loading state
        JLabel loading = new JLabel("Loading recent scans...");
        loading.setFont(new Font("SansSerif", Font.PLAIN, 13));
        loading.setForeground(TEXT_MUTED);
        loading.setAlignmentX(Component.LEFT_ALIGNMENT);
        recentScansContent.add(loading);
 
        section.add(sectionHeader);
        section.add(recentScansContent);
        return section;
    }
 
    // ── Load recent scans from backend ────────────────────────────────────────
 
    private void loadRecentScans() {
        new SwingWorker<Void, Void>() {
            private JsonNode rows;
            private String error;
 
            @Override
            protected Void doInBackground() {
                try {
                    String response = ApiClient.get("/scan-history?type=ALL&result=ALL");
                    JsonNode root = mapper.readTree(response);
                    if (root.path("success").asBoolean()) {
                        rows = root.path("data");
                    } else {
                        error = root.path("message").asText("Failed to load.");
                    }
                } catch (Exception e) {
                    error = "Backend not reachable.";
                }
                return null;
            }
 
            @Override
            protected void done() {
                recentScansContent.removeAll();
 
                if (error != null || rows == null || !rows.isArray() || rows.size() == 0) {
                    JLabel empty = new JLabel("Recent scan history will appear here");
                    empty.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    empty.setForeground(TEXT_MUTED);
                    empty.setAlignmentX(Component.LEFT_ALIGNMENT);
                    recentScansContent.add(empty);
                } else {
                    // show up to 5 most recent scans
                    int limit = Math.min(rows.size(), 5);
                    // iterate from end to get most recent first
                    for (int i = rows.size() - 1; i >= rows.size() - limit; i--) {
                        JsonNode row = rows.get(i);
                        recentScansContent.add(buildRecentScanRow(
                            row.path("scanType").asText(""),
                            row.path("target").asText(""),
                            row.path("result").asText(""),
                            row.path("createdAt").asText("").replace("T", " ")
                        ));
                        if (i > rows.size() - limit) {
                            JSeparator sep = new JSeparator();
                            sep.setForeground(CARD_BORDER);
                            sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                            recentScansContent.add(sep);
                        }
                    }
                }
 
                recentScansContent.revalidate();
                recentScansContent.repaint();
            }
        }.execute();
    }
 
    private JPanel buildRecentScanRow(String type, String target, String result, String date) {
        // use GridBagLayout so left side is constrained and can never push into the right
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.setBorder(new EmptyBorder(6, 0, 6, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.NONE;
 
        // col 0: type badge (fixed width)
        JLabel typeBadge = new JLabel(type);
        typeBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
        typeBadge.setForeground(ACCENT);
        typeBadge.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ACCENT, 1, true),
            new EmptyBorder(1, 6, 1, 6)
        ));
        gbc.gridx = 0; gbc.weightx = 0;
        row.add(typeBadge, gbc);
 
        // col 1: target — takes remaining space, clipped with html to prevent overflow
        String displayTarget = target.length() > 28 ? target.substring(0, 25) + "..." : target;
        JLabel targetLabel = new JLabel(displayTarget);
        targetLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        targetLabel.setForeground(TEXT_PRIMARY);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        row.add(targetLabel, gbc);
 
        // col 2: result (fixed width, right-aligned)
        JLabel resultLabel = new JLabel(result);
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        resultLabel.setForeground(getResultColour(result));
        resultLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 2; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 8, 0, 12);
        row.add(resultLabel, gbc);
 
        // col 3: date (fixed width, right-aligned)
        JLabel dateLabel = new JLabel(date.length() > 16 ? date.substring(0, 16) : date);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        dateLabel.setForeground(TEXT_MUTED);
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 3; gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        row.add(dateLabel, gbc);
 
        return row;
    }
 
    private Color getResultColour(String result) {
        return switch (result) {
            case "SAFE", "CLEAR", "STRONG" -> SAFE_GREEN;
            case "SUSPICIOUS", "MEDIUM"    -> WARN_YELLOW;
            default                         -> DANGER_RED;
        };
    }
}