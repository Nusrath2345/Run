import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    private static final String EMAIL_PLACEHOLDER = "user@example.com";

    // Dark mode colours matching wireframe
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

    public LoginFrame() {
        setTitle("Rún - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(false);
        setResizable(false);
        setBackground(BG);

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(40, 40, 40, 40));

        RoundedPanel card = new RoundedPanel(14, CARD_BG, BORDER);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(32, 36, 28, 36));
        card.setPreferredSize(new Dimension(420, 420));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;

        // Lock icon panel
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ICON_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(0.5f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                g2.setColor(TEXT_MUTED);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(cx - 7, cy - 13, 14, 13, 0, 180);
                g2.setColor(new Color(50, 50, 50));
                g2.fillRoundRect(cx - 9, cy - 2, 18, 13, 3, 3);
                g2.setColor(TEXT_MUTED);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(cx - 9, cy - 2, 18, 13, 3, 3);
                g2.dispose();
            }
        };

        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(48, 48));
        iconPanel.setMaximumSize(new Dimension(48, 48));

        JPanel iconWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconWrap.setOpaque(false);
        iconWrap.add(iconPanel);

        JLabel titleLabel = new JLabel("Rún", SwingConstants.CENTER);
        titleLabel.setFont(SANS_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subtitleLabel.setFont(SANS_SMALL);
        subtitleLabel.setForeground(TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

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
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton signInButton = styledButton("Sign in");
        signInButton.addActionListener(e -> attemptLogin());

        JLabel forgotLabel = new JLabel("Forgot password?", SwingConstants.CENTER);
        forgotLabel.setFont(SANS_SMALL);
        forgotLabel.setForeground(TEXT_MUTED);
        forgotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add rows to card using GridBagLayout — each row fills full width
        int row = 0;

        c.gridy = row++;
        c.insets = new Insets(0, 0, 14, 0);
        card.add(iconWrap, c);

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
        signInButton.setPreferredSize(new Dimension(0, 38));
        card.add(signInButton, c);

        c.gridy = row++;
        c.insets = new Insets(0, 0, 0, 0);
        card.add(forgotLabel, c);

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
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;
        private final Color border;

        RoundedPanel(int radius, Color bg, Color border) {
            this.radius = radius;
            this.bg = bg;
            this.border = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius * 2, radius * 2));
            g2.setColor(border);
            g2.setStroke(new BasicStroke(0.5f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, radius * 2, radius * 2));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private void attemptLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || email.equals(EMAIL_PLACEHOLDER) || password.isEmpty()) {
            messageLabel.setText("Please enter email and password.");
            return;
        }

        try {
            URL url = new URL("http://localhost:8080/api/auth/login");
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
                dispose();
                new RunApp().setVisible(true);
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String line = reader.readLine();
                reader.close();
                messageLabel.setText("Access Denied: " + (line != null ? line : responseCode));
            }

        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}