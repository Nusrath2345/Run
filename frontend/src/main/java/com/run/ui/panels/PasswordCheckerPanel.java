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

public class PasswordCheckerPanel extends JPanel {

    private JTextField passwordField;
    private JLabel strengthLabel;
    private JProgressBar strengthBar;
    private JPanel suggestionsPanel;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PasswordCheckerPanel(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(new Color(24, 24, 24));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(24, 24, 24));

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(24, 24, 24));
        container.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // ================= TITLE =================
        JLabel title = new JLabel("Password strength checker");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Test how strong your password is and get improvement suggestions");
        subtitle.setForeground(new Color(160, 160, 160));
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        container.add(title);
        container.add(Box.createVerticalStrut(20)); 
        container.add(subtitle);
        container.add(Box.createVerticalStrut(20)); 

        // ================= INPUT =================
        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setForeground(new Color(130, 130, 130));
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 11));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JTextField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setBackground(new Color(40, 40, 40));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel helperText = new JLabel("Password is not stored or transmitted");
        helperText.setForeground(new Color(110, 110, 110));
        helperText.setFont(new Font("Arial", Font.PLAIN, 11));
        helperText.setAlignmentX(Component.LEFT_ALIGNMENT);

        container.add(passwordLabel);
        container.add(Box.createVerticalStrut(5));
        container.add(passwordField);
        container.add(Box.createVerticalStrut(5));
        container.add(helperText);
        container.add(Box.createVerticalStrut(80)); 

        // ================= STRENGTH =================
        JPanel strengthRow = new JPanel(new BorderLayout());
        strengthRow.setBackground(new Color(24, 24, 24));
        strengthRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel strengthTitle = new JLabel("STRENGTH");
        strengthTitle.setForeground(new Color(130, 130, 130));
        strengthTitle.setFont(new Font("Arial", Font.BOLD, 11));

        strengthLabel = new JLabel("-");
        strengthLabel.setForeground(Color.ORANGE);

        strengthRow.add(strengthTitle, BorderLayout.WEST);
        strengthRow.add(strengthLabel, BorderLayout.EAST);

        strengthBar = new JProgressBar(0, 100);
        strengthBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        strengthBar.setBorderPainted(false);
        strengthBar.setBackground(new Color(60, 60, 60));
        strengthBar.setForeground(Color.ORANGE);
        strengthBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        container.add(strengthRow);
        container.add(Box.createVerticalStrut(8));
        container.add(strengthBar);
        container.add(Box.createVerticalStrut(50)); 

        // ================= SUGGESTIONS =================
        JLabel suggestionsTitle = new JLabel("SUGGESTIONS");
        suggestionsTitle.setForeground(new Color(130, 130, 130));
        suggestionsTitle.setFont(new Font("Arial", Font.BOLD, 11));
        suggestionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        suggestionsPanel = new JPanel();
        suggestionsPanel.setLayout(new BoxLayout(suggestionsPanel, BoxLayout.Y_AXIS));
        suggestionsPanel.setBackground(new Color(35, 35, 35));
        suggestionsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        suggestionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        container.add(suggestionsTitle);
        container.add(Box.createVerticalStrut(50));
        container.add(suggestionsPanel);

        wrapper.add(container, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);

        passwordField.addActionListener(e -> checkPassword());
    }

    private void checkPassword() {
        String password = passwordField.getText();

        if (password.isEmpty()) {
            strengthLabel.setText("-");
            strengthBar.setValue(0);
            suggestionsPanel.removeAll();
            suggestionsPanel.revalidate();
            suggestionsPanel.repaint();
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

            strengthLabel.setText(capitalize(strength));

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

            suggestionsPanel.removeAll();

            if (details.isArray()) {
                for (JsonNode node : details) {
                    JLabel item = new JLabel("– " + node.asText());
                    item.setForeground(new Color(200, 200, 200));
                    item.setAlignmentX(Component.LEFT_ALIGNMENT);
                    suggestionsPanel.add(item);
                }
            }

            suggestionsPanel.revalidate();
            suggestionsPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}