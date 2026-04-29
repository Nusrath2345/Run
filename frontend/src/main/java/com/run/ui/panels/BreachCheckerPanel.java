package com.run.ui.panels;

import javax.swing.*;

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
    private JLabel resultLabel;


    private MainFrame mainFrame;
    public BreachCheckerPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.setBackground(new Color(30, 30, 30));

        emailField = new JTextField(20);
        emailField.setText("user@example.com");

        checkButton = new JButton("Check for breaches");
        resultLabel = new JLabel(" ");
        resultLabel.setForeground(Color.WHITE);

        checkButton.addActionListener(this::checkEmail);

        inputPanel.add(emailField);
        inputPanel.add(checkButton);

        add(inputPanel, BorderLayout.NORTH);
        add(resultLabel, BorderLayout.CENTER);
    }

    private void checkEmail(ActionEvent e) {
        String email = emailField.getText();

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

                    int code = conn.getResponseCode();

                    Scanner scanner;
                    if (code >= 200 && code < 300) {
                        scanner = new Scanner(conn.getInputStream());
                    } else {
                        scanner = new Scanner(conn.getErrorStream());
                    }

                    String response = scanner.useDelimiter("\\A").hasNext()
                            ? scanner.useDelimiter("\\A").next()
                            : "";
                    scanner.close();

                    result = response;

                } catch (Exception ex) {
                    result = "ERROR";
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                updateUIResult(result);
            }
        };

        worker.execute();
    }

    private void updateUIResult(String response) {
        if (response.contains("\"status\":\"CLEAR\"")) {
            resultLabel.setText("✅ No breaches found");
            resultLabel.setForeground(Color.GREEN);

        } else if (response.contains("\"status\":\"BREACHED\"")) {
            resultLabel.setText("⚠️ Breach detected!");
            resultLabel.setForeground(Color.RED);

        } else {
            resultLabel.setText("❌ Error checking email");
            resultLabel.setForeground(Color.ORANGE);
        }
    }
}
