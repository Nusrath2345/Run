package com.run.ui.panels;

import com.run.ui.NavigationListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmailScannerPanel extends JPanel {

    private static final Color BG_DARK      = new Color(30, 30, 30);
    private static final Color CARD_BG      = new Color(45, 45, 45);
    private static final Color CARD_BORDER  = new Color(60, 60, 60);
    private static final Color TEXT_PRIMARY = new Color(230, 230, 230);
    private static final Color TEXT_MUTED   = new Color(150, 150, 150);
    private static final Color ACCENT       = new Color(99, 102, 241);
    private static final Color SAFE_BG      = new Color(20, 80, 20);
    private static final Color SUSPICIOUS_BG = new Color(80, 60, 10);
    private static final Color SCAM_BG      = new Color(80, 20, 20);
    private static final Color SAFE_BORDER  = new Color(40, 160, 40);
    private static final Color SUSPICIOUS_BORDER = new Color(180, 140, 20);
    private static final Color SCAM_BORDER  = new Color(160, 40, 40);

    private JTextArea emailInput;
    private JPanel resultPanel;

    public EmailScannerPanel(NavigationListener nav) {
        setBackground(BG_DARK);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(32, 36, 32, 36));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel title = new JLabel("Email scanner");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Paste an email to check for phishing or scam content");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(4, 0, 0, 0));

        header.add(title);
        header.add(subtitle);
        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JLabel inputLabel = new JLabel("EMAIL CONTENT");
        inputLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        inputLabel.setForeground(TEXT_MUTED);
        inputLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailInput = new JTextArea(6, 0);
        emailInput.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailInput.setBackground(CARD_BG);
        emailInput.setForeground(TEXT_MUTED);
        emailInput.setCaretColor(TEXT_PRIMARY);
        emailInput.setLineWrap(true);
        emailInput.setWrapStyleWord(true);
        emailInput.setText("Paste the full email text here...");
        emailInput.setBorder(new EmptyBorder(12, 12, 12, 12));
        emailInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (emailInput.getText().equals("Paste the full email text here...")) {
                    emailInput.setText("");
                    emailInput.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (emailInput.getText().isEmpty()) {
                    emailInput.setForeground(TEXT_MUTED);
                    emailInput.setText("Paste the full email text here...");
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(emailInput);
        scrollPane.setBorder(new LineBorder(CARD_BORDER, 1));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JLabel hint = new JLabel("Accepts raw email body text");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        hint.setForeground(TEXT_MUTED);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        hint.setBorder(new EmptyBorder(6, 0, 12, 0));

        JButton scanButton = new JButton("Scan email");
        scanButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        scanButton.setBackground(ACCENT);
        scanButton.setForeground(Color.WHITE);
        scanButton.setBorderPainted(false);
        scanButton.setFocusPainted(false);
        scanButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        scanButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        scanButton.addActionListener(e -> performScan());

        resultPanel = new JPanel();
        resultPanel.setOpaque(false);
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        body.add(inputLabel);
        body.add(Box.createVerticalStrut(6));
        body.add(scrollPane);
        body.add(hint);
        body.add(scanButton);
        body.add(resultPanel);

        return body;
    }

    private void performScan() {
        String content = emailInput.getText().trim();
        if (content.isEmpty() || content.equals("Paste the full email text here...")) {
            showResult("Please paste email content first.", "", new Color(80, 80, 80), new Color(120, 120, 120));
            return;
        }

        try {
            URL url = new URL("http://localhost:8080/api/email-scanner/scan");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String escaped = content.replace("\\", "\\\\").replace("\"", "\\\"")
                    .replace("\n", "\\n").replace("\r", "");
            String json = "{\"content\":\"" + escaped + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result = reader.readLine();
            reader.close();

            switch (result) {
                case "SAFE":
                    showResult("Clean — No threats found in this email", "No phishing indicators detected. This email appears safe.", SAFE_BG, SAFE_BORDER);
                    break;
                case "SUSPICIOUS":
                    showResult("Suspicious — Proceed with caution", "Some phishing indicators detected. Do not click any links or provide personal information.", SUSPICIOUS_BG, SUSPICIOUS_BORDER);
                    break;
                case "SCAM":
                    showResult("Scam — High-risk phishing email detected", "Multiple phishing indicators found. Do not interact with this email.", SCAM_BG, SCAM_BORDER);
                    break;
                default:
                    showResult("Unknown result", result, new Color(80, 80, 80), new Color(120, 120, 120));
            }

        } catch (Exception ex) {
            showResult("Error: " + ex.getMessage(), "", new Color(80, 80, 80), new Color(120, 120, 120));
        }
    }

private void showResult(String title, String details, Color bg, Color border) {
    resultPanel.removeAll();

    JPanel titleCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
    titleCard.setBackground(bg);
    titleCard.setBorder(new LineBorder(border, 1, true));
    titleCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
    titleCard.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
    titleLabel.setForeground(border);
    titleCard.add(titleLabel);

    JPanel detailsCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
    detailsCard.setBackground(new Color(50, 50, 50));
    detailsCard.setBorder(new LineBorder(new Color(70, 70, 70), 1, true));
    detailsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
    detailsCard.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel detailsLabel = new JLabel(details);
    detailsLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
    detailsLabel.setForeground(TEXT_MUTED);
    detailsCard.add(detailsLabel);

    resultPanel.add(titleCard);
    resultPanel.add(Box.createVerticalStrut(8));
    resultPanel.add(detailsCard);
    resultPanel.revalidate();
    resultPanel.repaint();
}
}