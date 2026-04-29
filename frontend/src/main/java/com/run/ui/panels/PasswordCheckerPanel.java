package com.run.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.run.ui.MainFrame;

public class PasswordCheckerPanel extends JPanel  {

    private JTextField passwordField;
    private JLabel strengthLabel;
    private JProgressBar strengthBar;
    private JTextArea suggestionsArea;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private MainFrame mainFrame;
    public PasswordCheckerPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        // ================= TOP =================
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("Password Strength Checker");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Test your password strength and get suggestions");
        subtitle.setForeground(Color.LIGHT_GRAY);

        passwordField = new JTextField();
        passwordField.setToolTipText("Enter password");

        topPanel.add(title);
        topPanel.add(subtitle);
        topPanel.add(passwordField);

        add(topPanel, BorderLayout.NORTH);

        // ================= CENTER =================
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(30, 30, 30));

        strengthLabel = new JLabel("Strength: -");
        strengthLabel.setForeground(Color.WHITE);

        strengthBar = new JProgressBar(0, 100);
        strengthBar.setStringPainted(false);

        suggestionsArea = new JTextArea(6, 30);
        suggestionsArea.setEditable(false);
        suggestionsArea.setBackground(new Color(40, 40, 40));
        suggestionsArea.setForeground(Color.WHITE);

        centerPanel.add(strengthLabel);
        centerPanel.add(strengthBar);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(new JLabel("Suggestions:"));
        centerPanel.add(suggestionsArea);

        add(centerPanel, BorderLayout.CENTER);

        // ================= EVENT =================
        passwordField.addActionListener(e -> checkPassword());
    }

    private void checkPassword() {
        String password = passwordField.getText();

        if (password.isEmpty()) {
            strengthLabel.setText("Strength: -");
            strengthBar.setValue(0);
            suggestionsArea.setText("");
            return;
        }

        try {
            String jsonRequest = "{\"password\":\"" + password + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/password-checker/check"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(response.body());

            String strength = root.get("strength").asText();
            JsonNode details = root.get("details");

            // ================= UI UPDATE =================
            strengthLabel.setText("Strength: " + strength);

            switch (strength.toLowerCase()) {
                case "weak":
                    strengthBar.setValue(33);
                    strengthBar.setForeground(Color.RED);
                    break;
                case "medium":
                    strengthBar.setValue(66);
                    strengthBar.setForeground(Color.ORANGE);
                    break;
                case "strong":
                    strengthBar.setValue(100);
                    strengthBar.setForeground(Color.GREEN);
                    break;
                default:
                    strengthBar.setValue(0);
            }

            StringBuilder sb = new StringBuilder();
            if (details.isArray()) {
                for (JsonNode node : details) {
                    sb.append("• ").append(node.asText()).append("\n");
                }
            }

            suggestionsArea.setText(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            suggestionsArea.setText("Error checking password");
        }
    }
}