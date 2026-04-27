package com.run.ui.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.run.ui.ApiClient;
import com.run.ui.NavigationListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

// file scanner ui matching wireframe
public class FileScannerPanel extends JPanel {

    private static final Color BG_DARK      = new Color(30, 30, 30);
    private static final Color CARD_BG      = new Color(50, 50, 50);
    private static final Color TEXT_PRIMARY  = new Color(230, 230, 230);
    private static final Color TEXT_MUTED    = new Color(150, 150, 150);
    private static final Color BTN_BG       = new Color(55, 55, 55);
    private static final Color SAFE_GREEN   = new Color(74, 222, 128);
    private static final Color DANGER_RED   = new Color(248, 113, 113);
    private static final Color WARN_YELLOW  = new Color(250, 204, 21);
    private static final Color SAFE_BG      = new Color(34, 70, 34);
    private static final Color DANGER_BG    = new Color(70, 30, 30);
    private static final Color WARN_BG      = new Color(70, 60, 20);
    private static final Color ERROR_BG     = new Color(80, 30, 30);
    private static final Color DROP_BORDER  = new Color(80, 80, 80);

    private static final long MAX_FILE_BYTES = 10L * 1024 * 1024;

    private final ObjectMapper mapper = new ObjectMapper();
    private final JLabel fileNameLabel;
    private final JLabel fileSizeLabel;
    private final JButton scanButton;
    private final JPanel resultPanel;
    private final JLabel errorLabel;

    private File selectedFile;

    public FileScannerPanel(NavigationListener nav) {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_DARK);

        // title
        JLabel title = new JLabel("File scanner");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);

        JLabel subtitle = new JLabel("Upload a file to check for malware before opening it");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(20));

        // drop zone
        JPanel dropZone = new JPanel(new GridBagLayout());
        dropZone.setBackground(BG_DARK);
        dropZone.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createDashedBorder(DROP_BORDER, 4, 4),
                new EmptyBorder(30, 20, 30, 20)));
        dropZone.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        dropZone.setAlignmentX(Component.LEFT_ALIGNMENT);
        dropZone.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel dropContent = new JPanel();
        dropContent.setLayout(new BoxLayout(dropContent, BoxLayout.Y_AXIS));
        dropContent.setBackground(BG_DARK);

        JLabel clickLabel = new JLabel("Click to upload or drag and drop");
        clickLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        clickLabel.setForeground(TEXT_PRIMARY);
        clickLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dropContent.add(clickLabel);

        JLabel supportedLabel = new JLabel("Supported: .pdf, .docx, .xlsx, .zip, .exe, .txt");
        supportedLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        supportedLabel.setForeground(TEXT_MUTED);
        supportedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dropContent.add(supportedLabel);

        dropZone.add(dropContent);

        dropZone.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                chooseFile();
            }
        });

        content.add(dropZone);
        content.add(Box.createVerticalStrut(10));

        // error label
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
        content.add(Box.createVerticalStrut(6));

        // file info row
        JPanel fileInfoRow = new JPanel(new BorderLayout());
        fileInfoRow.setBackground(CARD_BG);
        fileInfoRow.setBorder(new EmptyBorder(10, 12, 10, 12));
        fileInfoRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        fileInfoRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        fileNameLabel = new JLabel("No file selected");
        fileNameLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fileNameLabel.setForeground(TEXT_PRIMARY);

        fileSizeLabel = new JLabel("");
        fileSizeLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        fileSizeLabel.setForeground(TEXT_MUTED);

        fileInfoRow.add(fileNameLabel, BorderLayout.WEST);
        fileInfoRow.add(fileSizeLabel, BorderLayout.EAST);
        content.add(fileInfoRow);
        content.add(Box.createVerticalStrut(10));

        // scan button
        scanButton = new JButton("Scan file");
        scanButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        scanButton.setForeground(TEXT_PRIMARY);
        scanButton.setBackground(BTN_BG);
        scanButton.setBorderPainted(false);
        scanButton.setFocusPainted(false);
        scanButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        scanButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        scanButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        scanButton.setEnabled(false);
        scanButton.addActionListener(e -> doScan());
        content.add(scanButton);
        content.add(Box.createVerticalStrut(24));

        // result section
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

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a file to scan");
        int choice = chooser.showOpenDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            fileNameLabel.setText(selectedFile.getName());
            fileSizeLabel.setText(formatSize(selectedFile.length()));
            scanButton.setEnabled(true);
            errorLabel.setVisible(false);
            resultPanel.removeAll();
            resultPanel.revalidate();
            resultPanel.repaint();
        }
    }

    private void doScan() {
        if (selectedFile == null) return;

        if (selectedFile.length() > MAX_FILE_BYTES) {
            showError("File exceeds 10 MB limit. Please choose a smaller file.");
            return;
        }

        errorLabel.setVisible(false);
        scanButton.setEnabled(false);
        scanButton.setText("Scanning...");
        resultPanel.removeAll();
        resultPanel.revalidate();
        resultPanel.repaint();

        final File fileToScan = selectedFile;

        new SwingWorker<Void, Void>() {
            private String result;
            private String details;
            private String error;

            @Override
            protected Void doInBackground() {
                try {
                    String response = ApiClient.postFile("/file-scanner/scan", fileToScan);
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
                scanButton.setText("Scan file");
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
                label = "Clean - No threats found in this file";
            }
            case "SUSPICIOUS" -> {
                bg = WARN_BG; fg = WARN_YELLOW;
                label = "Suspicious - This file may require caution";
            }
            default -> {
                bg = DANGER_BG; fg = DANGER_RED;
                label = "Threat detected - This file may contain malware, do not open";
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

    private String formatSize(long bytes) {
        if (bytes < 1024)              return bytes + " B";
        if (bytes < 1024 * 1024)       return (bytes / 1024) + " KB";
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }
}
