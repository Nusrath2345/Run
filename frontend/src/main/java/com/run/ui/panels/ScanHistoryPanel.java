package com.run.ui.panels;
 
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.run.ui.ApiClient;
import com.run.ui.NavigationListener;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
 
// scan history panel - shows all past scans in a table with filters and CSV export
public class ScanHistoryPanel extends JPanel {
 
    private static final Color BG_DARK      = new Color(30, 30, 30);
    private static final Color CARD_BG      = new Color(45, 45, 45);
    private static final Color TABLE_BG     = new Color(40, 40, 40);
    private static final Color TABLE_GRID   = new Color(60, 60, 60);
    private static final Color HEADER_BG    = new Color(50, 50, 50);
    private static final Color TEXT_PRIMARY = new Color(230, 230, 230);
    private static final Color TEXT_MUTED   = new Color(150, 150, 150);
    private static final Color ACCENT       = new Color(99, 102, 241);
    private static final Color BTN_BG       = new Color(55, 55, 55);
    private static final Color SAFE_GREEN   = new Color(74, 222, 128);
    private static final Color WARN_YELLOW  = new Color(250, 204, 21);
    private static final Color DANGER_RED   = new Color(248, 113, 113);
    private static final Color INPUT_BG     = new Color(50, 50, 50);
    private static final Color INPUT_BORDER = new Color(70, 70, 70);
 
    private final ObjectMapper mapper = new ObjectMapper();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JComboBox<String> typeFilter;
    private final JComboBox<String> resultFilter;
    private final JLabel statusLabel;
 
    // column order: Type, Target, Result, Details, Date
    private static final String[] COLUMNS = {"Type", "Target", "Result", "Details", "Date"};
 
    public ScanHistoryPanel(NavigationListener nav) {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(28, 36, 28, 36));
 
        // ── Header ───────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
 
        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
 
        JLabel title = new JLabel("Scan History");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
 
        JLabel subtitle = new JLabel("All past scan results");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);
 
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitle);
        header.add(titleBlock, BorderLayout.WEST);
 
        // export button
        JButton exportBtn = new JButton("Export CSV");
        exportBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        exportBtn.setForeground(TEXT_PRIMARY);
        exportBtn.setBackground(ACCENT);
        exportBtn.setBorderPainted(false);
        exportBtn.setFocusPainted(false);
        exportBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exportBtn.setBorder(new EmptyBorder(8, 16, 8, 16));
        exportBtn.addActionListener(e -> exportToCSV());
        header.add(exportBtn, BorderLayout.EAST);
 
        add(header, BorderLayout.NORTH);
 
        // ── Filters ───────────────────────────────────────────────────────────
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterBar.setOpaque(false);
        filterBar.setBorder(new EmptyBorder(0, 0, 12, 0));
 
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setForeground(TEXT_MUTED);
        typeLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
 
        typeFilter = buildComboBox(new String[]{"ALL", "LINK", "FILE", "EMAIL", "PASSWORD", "BREACH"});
        typeFilter.addActionListener(e -> loadData());
 
        JLabel resultLabel = new JLabel("Result:");
        resultLabel.setForeground(TEXT_MUTED);
        resultLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
 
        resultFilter = buildComboBox(new String[]{"ALL", "SAFE", "SUSPICIOUS", "UNSAFE", "SCAM", "WEAK", "MEDIUM", "STRONG", "CLEAR", "BREACHED"});
        resultFilter.addActionListener(e -> loadData());
 
        JButton refreshBtn = new JButton("↻ Refresh");
        refreshBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        refreshBtn.setForeground(TEXT_MUTED);
        refreshBtn.setBackground(BTN_BG);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadData());
 
        filterBar.add(typeLabel);
        filterBar.add(typeFilter);
        filterBar.add(resultLabel);
        filterBar.add(resultFilter);
        filterBar.add(refreshBtn);
 
        // ── Table ─────────────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
 
        table = new JTable(tableModel);
        table.setBackground(TABLE_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setGridColor(TABLE_GRID);
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(99, 102, 241, 80));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
 
        // column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);   // Type
        table.getColumnModel().getColumn(1).setPreferredWidth(220);  // Target
        table.getColumnModel().getColumn(2).setPreferredWidth(90);   // Result
        table.getColumnModel().getColumn(3).setPreferredWidth(280);  // Details
        table.getColumnModel().getColumn(4).setPreferredWidth(140);  // Date
 
        // header styling
        table.getTableHeader().setBackground(HEADER_BG);
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, TABLE_GRID));
 
        // colour-code the Result column
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(true);
                String val = value == null ? "" : value.toString();
                switch (val) {
                    case "SAFE", "CLEAR", "STRONG" ->
                        setForeground(SAFE_GREEN);
                    case "SUSPICIOUS", "MEDIUM" ->
                        setForeground(WARN_YELLOW);
                    case "UNSAFE", "SCAM", "BREACHED", "WEAK" ->
                        setForeground(DANGER_RED);
                    default ->
                        setForeground(TEXT_MUTED);
                }
                setBackground(isSelected ? new Color(99, 102, 241, 80) : TABLE_BG);
                return this;
            }
        });
 
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(TABLE_BG);
        scrollPane.getViewport().setBackground(TABLE_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(TABLE_GRID));
 
        // ── Status bar ────────────────────────────────────────────────────────
        statusLabel = new JLabel("Loading...");
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        statusLabel.setForeground(TEXT_MUTED);
        statusLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
 
        // ── Centre panel (filters + table + status) ───────────────────────────
        JPanel centre = new JPanel(new BorderLayout());
        centre.setOpaque(false);
        centre.add(filterBar, BorderLayout.NORTH);
        centre.add(scrollPane, BorderLayout.CENTER);
        centre.add(statusLabel, BorderLayout.SOUTH);
 
        add(centre, BorderLayout.CENTER);
 
        // load data when panel is first shown
        loadData();
    }
 
    // ── Load data from backend ────────────────────────────────────────────────
 
    private void loadData() {
        statusLabel.setText("Loading...");
        String type   = (String) typeFilter.getSelectedItem();
        String result = (String) resultFilter.getSelectedItem();
 
        new SwingWorker<Void, Void>() {
            private JsonNode rows;
            private String error;
 
            @Override
            protected Void doInBackground() {
                try {
                    String path = "/scan-history?type=" + type + "&result=" + result;
                    String response = ApiClient.get(path);
                    JsonNode root = mapper.readTree(response);
                    if (root.path("success").asBoolean()) {
                        rows = root.path("data");
                    } else {
                        error = root.path("message").asText("Failed to load history.");
                    }
                } catch (Exception e) {
                    error = "Could not reach backend. Is it running on port 8080?";
                }
                return null;
            }
 
            @Override
            protected void done() {
                tableModel.setRowCount(0);
                if (error != null) {
                    statusLabel.setText("Error: " + error);
                    return;
                }
                if (rows != null && rows.isArray()) {
                    for (JsonNode row : rows) {
                        tableModel.addRow(new Object[]{
                            row.path("scanType").asText(""),
                            row.path("target").asText(""),
                            row.path("result").asText(""),
                            row.path("details").asText(""),
                            row.path("createdAt").asText("").replace("T", " ")
                        });
                    }
                    int count = tableModel.getRowCount();
                    statusLabel.setText(count + " record" + (count == 1 ? "" : "s") + " found");
                } else {
                    statusLabel.setText("No records found.");
                }
            }
        }.execute();
    }
 
    // ── CSV Export ────────────────────────────────────────────────────────────
 
    private void exportToCSV() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No data to export.", "Export CSV", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
 
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save CSV");
        chooser.setSelectedFile(new File("scan_history.csv"));
        int choice = chooser.showSaveDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) return;
 
        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }
 
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            // header row
            pw.println(String.join(",", COLUMNS));
            // data rows
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                StringBuilder row = new StringBuilder();
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    if (c > 0) row.append(",");
                    String cell = tableModel.getValueAt(r, c).toString();
                    // wrap in quotes if contains comma or newline
                    if (cell.contains(",") || cell.contains("\n") || cell.contains("\"")) {
                        cell = "\"" + cell.replace("\"", "\"\"") + "\"";
                    }
                    row.append(cell);
                }
                pw.println(row);
            }
            JOptionPane.showMessageDialog(this,
                "Exported to " + file.getName(), "Export CSV", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Export failed: " + e.getMessage(), "Export CSV", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    // ── Helpers ───────────────────────────────────────────────────────────────
 
    private JComboBox<String> buildComboBox(String[] options) {
        JComboBox<String> combo = new JComboBox<>(options);
        combo.setBackground(INPUT_BG);
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setBorder(BorderFactory.createLineBorder(INPUT_BORDER));
        combo.setFocusable(false);
        return combo;
    }
}