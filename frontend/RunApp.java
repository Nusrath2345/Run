import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// simple swing app to test the backend connection
public class RunApp extends JFrame {

    private JLabel resultLabel;

    public RunApp() {
        setTitle("Run - Cyber Security Utility");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        resultLabel = new JLabel("Click the button to test", SwingConstants.CENTER);
        add(resultLabel, BorderLayout.CENTER);

        JButton testButton = new JButton("Test Backend Connection");
        testButton.addActionListener(e -> callBackend());
        add(testButton, BorderLayout.SOUTH);
    }

    private void callBackend() {
        try {
            URL url = new URL("http://localhost:8080/api/test");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
            );
            String response = reader.readLine();
            reader.close();

            resultLabel.setText(response);
        } catch (Exception ex) {
            resultLabel.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RunApp().setVisible(true);
        });
    }
}