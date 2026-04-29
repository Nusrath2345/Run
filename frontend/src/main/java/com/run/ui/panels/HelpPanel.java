package com.run.ui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HelpPanel extends JPanel {

    public HelpPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- Title ---
        JLabel title = new JLabel("Help & FAQ");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(20, 20, 10, 20));
        add(title, BorderLayout.NORTH);

        // --- FAQ content panel ---
        JPanel faqPanel = new JPanel();
        faqPanel.setLayout(new BoxLayout(faqPanel, BoxLayout.Y_AXIS));
        faqPanel.setBackground(Color.WHITE);
        faqPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        String[][] faqs = {
            {
                "What does the link checker do?",
                "The link checker scans URLs you provide and checks them against known databases of " +
                "malicious, phishing, and suspicious websites. It analyses the URL structure, domain " +
                "reputation, and redirect chains to warn you before you visit a potentially harmful site."
            },
            {
                "How does the password checker work?",
                "The password checker evaluates your password's strength based on its length, use of " +
                "uppercase and lowercase letters, numbers, and special characters. It also checks whether " +
                "your password has appeared in known data breach databases — without ever sending your " +
                "actual password over the network. Only a partial hash is used for the lookup."
            },
            {
                "Is my data stored securely?",
                "Yes. This application does not transmit or store your personal data on any external server " +
                "unless explicitly required for a specific check (e.g. breach lookups use anonymised hashes). " +
                "Scan history is saved locally on your device and is never shared with third parties."
            },
            {
                "What file types can I scan?",
                "The file scanner supports common document and executable formats, including PDF, DOCX, TXT, " +
                "EXE, DLL, ZIP, and JAR files. Each file is checked for known malware signatures and " +
                "suspicious metadata. Note: encrypted or password-protected archives cannot be fully scanned."
            },
            {
                "What is a data breach?",
                "A data breach occurs when unauthorised individuals gain access to private or confidential " +
                "information — such as usernames, passwords, email addresses, or financial details — typically " +
                "from a hacked company or service. If your email appears in a known breach, it means that data " +
                "was exposed and you should change any affected passwords immediately."
            },
            {
                "How do I read my scan history?",
                "The Scan History panel shows a log of all scans you have performed, including the type of " +
                "scan, the item checked, the result, and the date and time. You can use this to track " +
                "previously flagged links, files, or passwords and revisit any that need attention."
            }
        };

        for (int i = 0; i < faqs.length; i++) {
            faqPanel.add(buildFaqEntry(faqs[i][0], faqs[i][1]));
            if (i < faqs.length - 1) {
                faqPanel.add(Box.createVerticalStrut(12));
                faqPanel.add(buildDivider());
                faqPanel.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane scrollPane = new JScrollPane(faqPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel buildFaqEntry(String question, String answer) {
        JPanel entry = new JPanel();
        entry.setLayout(new BoxLayout(entry, BoxLayout.Y_AXIS));
        entry.setBackground(Color.WHITE);
        entry.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel qLabel = new JLabel("<html><b>Q: " + question + "</b></html>");
        qLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        qLabel.setForeground(new Color(30, 100, 180));
        qLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel aLabel = new JLabel("<html><p style='width:520px'>" + answer + "</p></html>");
        aLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        aLabel.setForeground(new Color(50, 50, 50));
        aLabel.setBorder(new EmptyBorder(6, 0, 0, 0));
        aLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        entry.add(qLabel);
        entry.add(aLabel);
        return entry;
    }

    private JSeparator buildDivider() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(220, 220, 220));
        return sep;
    }
}