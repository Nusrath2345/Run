package com.run.ui.panels;

import javax.swing.*;
import javax.swing.border.*;
import com.run.ui.MainFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class BreachCheckerPanel extends JPanel {

    private JTextField emailField;
    private JButton checkButton;
    private JPanel resultPanel;

    private MainFrame mainFrame;

    public BreachCheckerPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout()); // centers everything
        setBackground(new Color(18, 18, 18));

        add(createCard());
    }

    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(28, 28, 28));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(50, 50, 50), 1, true),
                new EmptyBorder(25, 25, 25, 25)
        ));
        card.setPreferredSize(new Dimension(500, 380));

        // Title
        JLabel title = new JLabel("Data breach checker");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel subtitle = new JLabel("Check if your email has appeared in a known data breach");
        subtitle.setForeground(new Color(150, 150, 150));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Label
        JLabel emailLabel = new JLabel("EMAIL ADDRESS");
        emailLabel.setForeground(new Color(120, 120, 120));
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));

        // Input
        emailField = new JTextField("user@example.com");
        styleTextField(emailField);

        // Button
        checkButton = new JButton("Check for breaches");
        styleButton(checkButton);
        checkButton.addActionListener(this::checkEmail);

        // Result panel (dynamic)
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setOpaque(false);

        // Layout spacing
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(emailField);
        card.add(Box.createVerticalStrut(12));
        card.add(checkButton);
        card.add(Box.createVerticalStrut(20));
        card.add(resultPanel);

        return card;
    }

    // ======================
    // Styling helpers
    // ======================

    private void styleTextField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBackground(new Color(20, 20, 20));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);

        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(60, 60, 60), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(new Color(45, 45, 45));
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(12, 12, 12, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JPanel createResultBox(String text, Color bg, Color fg) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(bg);

        box.setBorder(new CompoundBorder(
                new LineBorder(bg.brighter(), 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel label = new JLabel(text);
        label.setForeground(fg);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        box.add(label, BorderLayout.CENTER);
        return box;
    }

    // ======================
    // Logic (unchanged, cleaner UI output)
    // ======================

    private void checkEmail(ActionEvent e) {
        String email = emailField.getText();

        checkButton.setEnabled(false);
        checkButton.setText("Checking...");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            String result;

            @Override
            protected Void doInBackground() {
                try {
                    URL url = new URL("http://localhost:8080/api/breach-checker/check");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    String jsonInput = "{\"email\":\"" + email + "\"}";

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
                    }

                    Scanner scanner = new Scanner(
                            conn.getResponseCode() < 300
                                    ? conn.getInputStream()
                                    : conn.getErrorStream()
                    );

                    result = scanner.useDelimiter("\\A").hasNext()
                            ? scanner.next()
                            : "";

                    scanner.close();

                } catch (Exception ex) {
                    result = "ERROR";
                }
                return null;
            }

            @Override
            protected void done() {
                updateUIResult(result);
                checkButton.setEnabled(true);
                checkButton.setText("Check for breaches");
            }
        };

        worker.execute();
    }

    private void updateUIResult(String response) {
        resultPanel.removeAll();

        if (response.contains("\"status\":\"CLEAR\"")) {
            resultPanel.add(createResultBox(
                    "✔ No breaches found — This email is safe",
                    new Color(20, 60, 30),
                    new Color(80, 220, 120)
            ));

        } else if (response.contains("\"status\":\"BREACHED\"")) {
            resultPanel.add(createResultBox(
                    "⚠ Breach detected — This email was found in known breaches",
                    new Color(60, 20, 20),
                    new Color(255, 90, 90)
            ));

        } else {
            resultPanel.add(createResultBox(
                    "Error checking email. Please try again.",
                    new Color(60, 40, 0),
                    new Color(255, 180, 60)
            ));
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }
}
