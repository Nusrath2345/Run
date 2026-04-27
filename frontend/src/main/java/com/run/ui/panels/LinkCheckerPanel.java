package com.run.ui.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.run.ui.ApiClient;
import com.run.ui.NavigationListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

// link checker ui matching wireframe
public class LinkCheckerPanel extends JPanel {

    private static final Color BG_DARK      = new Color(30, 30, 30);
    private static final Color CARD_BG      = new Color(50, 50, 50);
    private static final Color TEXT_PRIMARY  = new Color(230, 230, 230);
    private static final Color TEXT_MUTED    = new Color(150, 150, 150);
    private static final Color INPUT_BG     = new Color(45, 45, 45);
    private static final Color INPUT_BORDER = new Color(70, 70, 70);
    private static final Color BTN_BG       = new Color(55, 55, 55);
    private static final Color SAFE_GREEN   = new Color(74, 222, 128);
    private static final Color WARN_YELLOW  = new Color(250, 204, 21);
    private static final Color DANGER_RED   = new Color(248, 113, 113);
    private static final Color SAFE_BG      = new Color(34, 70, 34);
    private static final Color WARN_BG      = new Color(70, 60, 20);
    private static final Color DANGER_BG    = new Color(70, 30, 30);
    private static final Color ERROR_BG     = new Color(80, 30, 30);

    private final ObjectMapper mapper = new ObjectMapper();
    private final JTextField urlField;
    private final JButton scanButton;
    private final JPanel resultPanel;
    private final JLabel errorLabel;

    public LinkCheckerPanel(NavigationListener nav) {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_DARK);

        // title
        JLabel title = new JLabel("Link scanner");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);

        JLabel subtitle = new JLabel("Check a URL before opening it");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(20));

        // url label
        JLabel urlLabel = new JLabel("URL");
        urlLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        urlLabel.setForeground(TEXT_MUTED);
        urlLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(urlLabel);
        content.add(Box.createVerticalStrut(6));

        // url input
        urlField = new JTextField();
        urlField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        urlField.setBackground(INPUT_BG);
        urlField.setForeground(TEXT_PRIMARY);
        urlField.setCaretColor(TEXT_PRIMARY);
        urlField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BORDER),
                new EmptyBorder(10, 12, 10, 12)));
        urlField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        urlField.setAlignmentX(Component.LEFT_ALIGNMENT);
        urlField.addActionListener(e -> doScan());
        content.add(urlField);

        // hint text
        JLabel hint = new JLabel("Paste the full URL including https://");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(TEXT_MUTED);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(hint);
        content.add(Box.createVerticalStrut(6));

        // error label (hidden by default)
        errorLabel = new JLabel("");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setForeground(DANGER_RED);
        errorLabel.setBackground(ERROR_BG);
        errorLabel.setOpaque(true);
        errorLabel.setBorder(new EmptyBorder(8, 12, 8, 12));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        errorLabel.setVisible(false);
        content.add(errorLabel);
        content.add(Box.createVerticalStrut(10));

        // scan button
        scanButton = new JButton("Check link");
        scanButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        scanButton.setForeground(TEXT_PRIMARY);
        scanButton.setBackground(BTN_BG);
        scanButton.setBorderPainted(false);
        scanButton.setFocusPainted(false);
        scanButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        scanButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        scanButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        scanButton.addActionListener(e -> doScan());
        content.add(scanButton);
        content.add(Box.createVerticalStrut(24));

        // result states section
        JLabel resultTitle = new JLabel("RESULT STATES");
        resultTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        resultTitle.setForeground(TEXT_MUTED);
        resultTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(resultTitle);
        content.add(Box.createVerticalStrut(8));

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(BG_DARK);
        resultPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(resultPanel);

        add(content, BorderLayout.NORTH);
    }

    private void doScan() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            showError("Please enter a URL to scan.");
            return;
        }

        errorLabel.setVisible(false);
        scanButton.setEnabled(false);
        scanButton.setText("Scanning...");
        resultPanel.removeAll();
        resultPanel.revalidate();
        resultPanel.repaint();

        new SwingWorker<Void, Void>() {
            private String result;
            private String details;
            private String error;

            @Override
            protected Void doInBackground() {
                try {
                    String body = mapper.writeValueAsString(Map.of("url", url));
                    String response = ApiClient.post("/link-checker/scan", body);
                    JsonNode root = mapper.readTree(response);

                    if (root.path("success").asBoolean()) {
                        JsonNode data = root.path("data");
                        result  = data.path("result").asText("UNKNOWN");
                        details = data.path("details").asText("");
                    } else {
                        error = root.path("message").asText("Scan failed.");
                    }
                } catch (Exception e) {
                    error = "Could not reach the backend. Is it running on port 8080?";
                }
                return null;
            }

            @Override
            protected void done() {
                scanButton.setEnabled(true);
                scanButton.setText("Check link");
                if (error != null) {
                    showError(error);
                } else {
                    showResult(result, details);
                }
            }
        }.execute();
    }

    private void showResult(String result, String details) {
        resultPanel.removeAll();

        Color bg;
        Color fg;
        String label;

        switch (result) {
            case "SAFE" -> {
                bg = SAFE_BG; fg = SAFE_GREEN;
                label = "Safe - No threats detected on this domain";
            }
            case "SUSPICIOUS" -> {
                bg = WARN_BG; fg = WARN_YELLOW;
                label = "Suspicious - Domain has limited history, use caution";
            }
            default -> {
                bg = DANGER_BG; fg = DANGER_RED;
                label = "High risk - Known malicious or phishing domain";
            }
        }

        JPanel resultBox = new JPanel(new BorderLayout());
        resultBox.setBackground(bg);
        resultBox.setBorder(new EmptyBorder(14, 16, 14, 16));
        resultBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        resultBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel resultLabel = new JLabel(label);
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        resultLabel.setForeground(fg);
        resultBox.add(resultLabel, BorderLayout.CENTER);

        resultPanel.add(resultBox);
        resultPanel.add(Box.createVerticalStrut(10));

        if (details != null && !details.isEmpty()) {
            JTextArea detailsArea = new JTextArea(details);
            detailsArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
            detailsArea.setForeground(TEXT_MUTED);
            detailsArea.setBackground(CARD_BG);
            detailsArea.setLineWrap(true);
            detailsArea.setWrapStyleWord(true);
            detailsArea.setEditable(false);
            detailsArea.setBorder(new EmptyBorder(10, 12, 10, 12));
            detailsArea.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultPanel.add(detailsArea);
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
