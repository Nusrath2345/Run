package com.run.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    private static final String EMAIL_PLACEHOLDER = "user@example.com";

    private static final Color BG = new Color(13, 13, 13);
    private static final Color CARD_BG = new Color(22, 22, 22);
    private static final Color ICON_BG = new Color(32, 32, 32);
    private static final Color BORDER = new Color(48, 48, 48);
    private static final Color INPUT_BG = new Color(18, 18, 18);
    private static final Color BUTTON_BG = new Color(30, 30, 30);
    private static final Color BUTTON_BDR = new Color(65, 65, 65);
    private static final Color TEXT_PRIMARY = new Color(235, 235, 235);
    private static final Color TEXT_MUTED = new Color(120, 120, 120);
    private static final Color TEXT_HINT = new Color(65, 65, 65);
    private static final Color ERROR = new Color(210, 70, 70);

    private static final Font SANS = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font SANS_TITLE = new Font("Segoe UI", Font.PLAIN, 20);
    private static final Font SANS_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font SANS_LABEL = new Font("Segoe UI", Font.PLAIN, 10);

    public RegisterFrame() {
        setTitle("Rún - Create Account");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setBackground(BG);

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(40, 40, 40, 40));

        LoginFrame.RoundedPanel card = new LoginFrame.RoundedPanel(14, CARD_BG, BORDER);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(32, 36, 28, 36));
        card.setPreferredSize(new Dimension(420, 400));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(SANS_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Register a new account", SwingConstants.CENTER);
        subtitleLabel.setFont(SANS_SMALL);
        subtitleLabel.setForeground(TEXT_MUTED);

        emailField = new JTextField();
        styleInput(emailField);
        emailField.setForeground(TEXT_HINT);
        emailField.setText(EMAIL_PLACEHOLDER);
        emailField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (emailField.getText().equals(EMAIL_PLACEHOLDER)) {
                    emailField.setText("");
                    emailField.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (emailField.getText().isEmpty()) {
                    emailField.setForeground(TEXT_HINT);
                    emailField.setText(EMAIL_PLACEHOLDER);
                }
            }
        });

        passwordField = new JPasswordField();
        styleInput(passwordField);

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(SANS_SMALL);
        messageLabel.setForeground(ERROR);

        JButton registerButton = styledButton("Create Account");
        registerButton.addActionListener(e -> attemptRegister());

        JLabel backLabel = new JLabel("Back to Sign In", SwingConstants.CENTER);
        backLabel.setFont(SANS_SMALL);
        backLabel.setForeground(TEXT_MUTED);
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
            }
        });

        int row = 0;

        c.gridy = row++;
        c.insets = new Insets(0, 0, 4, 0);
        card.add(titleLabel, c);

        c.gridy = row++;
        c.insets = new Insets(0, 0, 24, 0);
        card.add(subtitleLabel, c);

        c.gridy = row++;
        c.insets = new Insets(0, 0, 2, 0);
        card.add(fieldLabel("Email"), c);

        c.gridy = row++;
        c.insets = new Insets(0, 0, 12, 0);
        emailField.setPreferredSize(new Dimension(0, 36));
        card.add(emailField, c);

        c.gridy = row++;
        c.insets = new Insets(0, 0, 2, 0);
        card.add(fieldLabel("Password"), c);

        c.gridy = row++;
        c.insets = new Insets(0, 0, 10, 0);
        passwordField.setPreferredSize(new Dimension(0, 36));
        card.add(passwordField, c);

        c.gridy = row++;
        c.insets = new Insets(0, 0, 6, 0);
        card.add(messageLabel, c);

        c.gridy = row++;
        c.insets = new Insets(0, 0, 12, 0);
        registerButton.setPreferredSize(new Dimension(0, 38));
        card.add(registerButton, c);

        c.gridy = row++;
        c.insets = new Insets(0, 0, 0, 0);
        card.add(backLabel, c);

        outer.add(card);
        setContentPane(outer);
        pack();
        setLocationRelativeTo(null);
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(SANS_LABEL);
        label.setForeground(TEXT_MUTED);
        return label;
    }

    private void styleInput(JTextField field) {
        field.setFont(SANS);
        field.setBackground(INPUT_BG);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(7, 10, 7, 10)));
    }

    private JButton styledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(42, 42, 42) : BUTTON_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(BUTTON_BDR);
                g2.setStroke(new BasicStroke(0.5f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(SANS);
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(BUTTON_BG);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(9, 20, 9, 20));
        return button;
    }

    private void attemptRegister() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || email.equals(EMAIL_PLACEHOLDER) || password.isEmpty()) {
            messageLabel.setText("Please enter email and password.");
            return;
        }

        try {
            URL url = new URL("http://localhost:8080/api/auth/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                messageLabel.setForeground(new Color(70, 180, 70));
                messageLabel.setText("Account created! You can now sign in.");
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String line = reader.readLine();
                reader.close();
                messageLabel.setForeground(ERROR);
                messageLabel.setText(line != null ? line : "Registration failed.");
            }

        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }
}