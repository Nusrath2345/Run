import javax.swing.*;
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

    public LoginFrame() {
        setTitle("Run - Login");
        setSize(350, 220);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> attemptLogin());
        add(loginButton, gbc);

        gbc.gridy = 3;
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        add(messageLabel, gbc);
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
                messageLabel.setText("Login failed: " + (line != null ? line : responseCode));
            }

        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}