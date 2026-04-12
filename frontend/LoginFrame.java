import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    // Colours
    private static final Color BG_DARK = new Color(10, 10, 10);
    private static final Color PANEL_BG = new Color(18, 18, 18);
    private static final Color ACCENT_GREEN = new Color(0, 255, 70);
    private static final Color FIELD_BG = new Color(28, 28, 28);
    private static final Color FIELD_BORDER = new Color(0, 180, 50);
    private static final Color TEXT_COLOR = new Color(200, 255, 200);
    private static final Color LABEL_COLOR = new Color(0, 200, 60);

    public LoginFrame() {
        setTitle("Rún - Login");
        setSize(420, 340);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(false);
        setBackground(BG_DARK);

                JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createLineBorder(ACCENT_GREEN, 1));


        JLabel header = new JLabel("[ RÚN ]", SwingConstants.CENTER);
        header.setFont(new Font("Monospaced", Font.BOLD, 28));
        header.setForeground(ACCENT_GREEN);
        header.setBorder(new EmptyBorder(24, 0, 4, 0));
                root.add(header, BorderLayout.NORTH);

        JLabel sub = new JLabel("CYBER SECURITY UTILITY // LOGIN", SwingConstants.CENTER);
        sub.setFont(new Font("Monospaced", Font.PLAIN, 11));
        sub.setForeground(new Color(0, 120, 40));

                JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(PANEL_BG);
        form.setBorder(new EmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        form.add(sub, gbc);

   gbc.gridwidth = 1;
         gbc.gridx = 0; gbc.gridy = 1;
        form.add(styledLabel("USERNAME:"), gbc);

                gbc.gridx = 1;
        usernameField = styledTextField();
        form.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(styledLabel("PASSWORD:"), gbc);


        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
            styleField(passwordField);
        form.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton loginButton = styledButton(">> AUTHENTICATE");
        loginButton.addActionListener(e -> attemptLogin());
        form.add(loginButton, gbc);

        gbc.gridy = 4;
        messageLabel = new JLabel("", SwingConstants.CENTER);
         messageLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
                 messageLabel.setForeground(Color.RED);
        form.add(messageLabel, gbc);

             root.add(form, BorderLayout.CENTER);
        setContentPane(root);
    }

        private JLabel styledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Monospaced", Font.BOLD, 12));
        label.setForeground(LABEL_COLOR);
        return label;
    }

    private JTextField styledTextField() {
        JTextField field = new JTextField(15);
        styleField(field);
        return field;
    }

    private void styleField(JTextField field) {
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(ACCENT_GREEN);
        field.setFont(new Font("Monospaced", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
    }

    private JButton styledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(0, 180, 50) : FIELD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(ACCENT_GREEN);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Monospaced", Font.BOLD, 13));
        button.setForeground(ACCENT_GREEN);
        button.setBackground(FIELD_BG);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password.");
            return;
        }

        try {
            URL url = new URL("http://localhost:8080/api/auth/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

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