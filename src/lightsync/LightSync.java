/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package lightsync;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
// === UI Utilities for Styling ===
class UIUtils {
    static void styleButton(JButton btn, Color fg, Color border) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(Color.WHITE);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border, 2),
            BorderFactory.createEmptyBorder(10, 18, 10, 18)));
    }

    static JPanel gradientPanel(Color c1, Color c2, int axis) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        panel.setLayout(axis == BoxLayout.X_AXIS ? new BoxLayout(panel, BoxLayout.X_AXIS) : new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }
}

public class LightSync extends JFrame {
    public static AdminDashboard adminDashboard;
    public static PublicDashboard publicDashboard;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LightSync().showLogin());
    }

    public void showLogin() {
        setTitle("Smart Traffic Control - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 300));
        setPreferredSize(new Dimension(520, 380));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel bgPanel = UIUtils.gradientPanel(new Color(58, 123, 213), new Color(0, 210, 255), BoxLayout.Y_AXIS);
        bgPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel welcomeLabel = new JLabel("Smart Traffic Control System", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bgPanel.add(welcomeLabel);
        bgPanel.add(Box.createVerticalStrut(18));

        JLabel subtitle = new JLabel("Login to continue", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(230, 245, 255));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        bgPanel.add(subtitle);
        bgPanel.add(Box.createVerticalStrut(30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(1, 2, 30, 0));
        JButton adminLoginBtn = new JButton("Admin");
        JButton publicAccessBtn = new JButton("Public");

        UIUtils.styleButton(adminLoginBtn, new Color(58, 123, 213), new Color(58, 123, 213));
        UIUtils.styleButton(publicAccessBtn, new Color(0, 210, 255), new Color(0, 210, 255));

        buttonPanel.add(adminLoginBtn);
        buttonPanel.add(publicAccessBtn);
        bgPanel.add(buttonPanel);
        bgPanel.add(Box.createVerticalStrut(30));

        JLabel copyright = new JLabel("© 2026 LightSync", JLabel.CENTER);
        copyright.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        copyright.setForeground(new Color(230, 245, 255));
        copyright.setAlignmentX(Component.CENTER_ALIGNMENT);
        bgPanel.add(Box.createVerticalGlue());
        bgPanel.add(copyright);

        add(bgPanel, BorderLayout.CENTER);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                int pad = Math.max(20, w / 24);
                bgPanel.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
            }
        });

        adminLoginBtn.addActionListener(e -> {
            dispose();
            showAdminLogin();
        });
        publicAccessBtn.addActionListener(e -> {
            dispose();
            if (publicDashboard == null) publicDashboard = new PublicDashboard();
            publicDashboard.setVisible(true);
        });

        setVisible(true);
    }

    public void showAdminLogin() {
        JFrame loginFrame = new JFrame("Admin Login");
        loginFrame.setSize(350, 200);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        JLabel statusLabel = new JLabel();

        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (username.equals("admin") && password.equals("1234")) {
                loginFrame.dispose();
                if (adminDashboard == null) {
                    adminDashboard = new AdminDashboard();
                    AdminDashboardHolder.adminInstance = adminDashboard;
                }
                if (!adminDashboard.isVisible()) {
                    adminDashboard.setVisible(true);
                }
            } else {
                statusLabel.setText("Invalid credentials!");
                statusLabel.setForeground(Color.RED);
            }
        });

        loginFrame.add(userLabel);
        loginFrame.add(userField);
        loginFrame.add(passLabel);
        loginFrame.add(passField);
        loginFrame.add(new JLabel());
        loginFrame.add(loginBtn);
        loginFrame.add(new JLabel());
        loginFrame.add(statusLabel);

        loginFrame.setVisible(true);
    }
}

// === Public Dashboard with Simulation ===
class PublicDashboard extends JFrame {
    private JLabel signalLabel;
    private JLabel statusLabel;
    private Timer pollTimer;

    PublicDashboard() {
        setTitle("Public Traffic Dashboard");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(400, 300));
        setPreferredSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Switch buttons panel (top-left)
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        switchPanel.setOpaque(false);
        JButton adminBtn = new JButton("Admin");
        adminBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        adminBtn.setMargin(new Insets(2, 8, 2, 8));
        adminBtn.addActionListener(e -> {
            dispose();
            new LightSync().showAdminLogin();
        });
        switchPanel.add(adminBtn);
        add(switchPanel, BorderLayout.NORTH);

        JPanel bgPanel = UIUtils.gradientPanel(new Color(0, 210, 255), new Color(58, 123, 213), BoxLayout.Y_AXIS);
        bgPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        bgPanel.add(Box.createVerticalStrut(18));

        signalLabel = new JLabel("Current Signal: -", JLabel.CENTER);
        signalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        signalLabel.setForeground(new Color(230, 245, 255));
        signalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bgPanel.add(signalLabel);
        bgPanel.add(Box.createVerticalStrut(30));

        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        bgPanel.add(statusLabel);

        bgPanel.add(Box.createVerticalGlue());

        add(bgPanel, BorderLayout.CENTER);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                int pad = Math.max(20, w / 24);
                bgPanel.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
            }
        });

        startPollingAdmin();
        setVisible(true);
    }

    private void startPollingAdmin() {
        pollTimer = new Timer(500, e -> updateFromAdmin());
        pollTimer.start();
    }

    private void updateFromAdmin() {
        AdminDashboard admin = AdminDashboardHolder.getInstance();
        if (admin != null) {
            String sig = admin.getCurrentSignal();
            int time = admin.getSignalTimeLeft();
            signalLabel.setText("Current Signal: " + sig);
            statusLabel.setText("Time Left: " + time + "s");
        } else {
            signalLabel.setText("Current Signal: -");
            statusLabel.setText("Waiting for admin dashboard...");
        }
    }
}
// === Admin Dashboard ===
class AdminDashboard extends JFrame {
    private JLabel timerLabel;
    private JPanel signalIndicatorPanel;
    private JButton emergencyBtn, criminalBtn;
    private String currentSignal = "GREEN";
    private Timer signalTimer;
    private int signalTimeLeft;
    private final int GREEN_TIME = 35;
    private final int YELLOW_TIME = 5;
    private final int RED_TIME = 15;
    private String previousSignal = "GREEN";
    private boolean emergencyOverride = false;
    private int resumeCycleTime = -1;
    private String resumeCycleSignal = null;
    private int emergencyStep = 0; // 0: none, 1: after yellow, 2: after green, 3: after post-emergency yellow
    private JLabel statusLabel; // For status messages at the bottom
    private List<String> criminalNumbers = new ArrayList<>();
    private final String CRIMINAL_FILE = FileUtils.getCriminalFilePath();

    AdminDashboard() {
        loadCriminalNumbers();
        setTitle("Admin Traffic Control Dashboard");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(600, 400));
        setPreferredSize(new Dimension(900, 520));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Switch buttons panel (top-left)
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        switchPanel.setOpaque(false);
        JButton publicBtn = new JButton("Public");
        publicBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        publicBtn.setMargin(new Insets(2, 8, 2, 8));
        publicBtn.addActionListener(e -> {
            dispose();
            if (LightSync.publicDashboard == null) LightSync.publicDashboard = new PublicDashboard();
            LightSync.publicDashboard.setVisible(true);
        });
        switchPanel.add(publicBtn);
        add(switchPanel, BorderLayout.NORTH);

        JPanel bgPanel = UIUtils.gradientPanel(new Color(58, 123, 213), new Color(0, 210, 255), BoxLayout.X_AXIS);
        bgPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Left: Timer and color indicator
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));

        JLabel dashTitle = new JLabel("Admin Dashboard", JLabel.CENTER);
        dashTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        dashTitle.setForeground(Color.WHITE);
        dashTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(dashTitle);
        leftPanel.add(Box.createVerticalStrut(18));

        timerLabel = new JLabel("Timer: 0s", JLabel.CENTER);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 38));
        timerLabel.setForeground(new Color(230, 245, 255));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(timerLabel);
        leftPanel.add(Box.createVerticalStrut(30));

        signalIndicatorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int size = 110;
                int x = 0;
                int y = 0;
                Color color = Color.GREEN;
                if (currentSignal.equals("RED")) color = Color.RED;
                else if (currentSignal.equals("YELLOW")) color = Color.YELLOW;
                g.setColor(color);
                g.fillOval(x, y, size, size);
                g.setColor(Color.BLACK);
                g.drawOval(x, y, size, size);
            }
        };
        signalIndicatorPanel.setPreferredSize(new Dimension(110, 110));
        signalIndicatorPanel.setMaximumSize(new Dimension(110, 110));
        signalIndicatorPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(signalIndicatorPanel);
        leftPanel.add(Box.createVerticalGlue());

        // Right: Buttons
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        emergencyBtn = new JButton("Emergency Vehicle Detected");
        UIUtils.styleButton(emergencyBtn, new Color(58, 123, 213), new Color(58, 123, 213));
        emergencyBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        emergencyBtn.setMaximumSize(new Dimension(340, 60));
        emergencyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        emergencyBtn.addActionListener(e -> handleEmergency());
        rightPanel.add(emergencyBtn);
        rightPanel.add(Box.createVerticalStrut(30));

        criminalBtn = new JButton("Criminal Detected");
        UIUtils.styleButton(criminalBtn, new Color(0, 210, 255), new Color(0, 210, 255));
        criminalBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        criminalBtn.setMaximumSize(new Dimension(340, 60));
        criminalBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        criminalBtn.addActionListener(e -> handleCriminal());
        rightPanel.add(criminalBtn);
        rightPanel.add(Box.createVerticalStrut(30));

        rightPanel.add(Box.createVerticalGlue());

        bgPanel.add(leftPanel);
        bgPanel.add(rightPanel);

        add(bgPanel, BorderLayout.CENTER);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                int pad = Math.max(30, w / 30);
                bgPanel.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
            }
        });

        // Status label at the bottom
        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        add(statusLabel, BorderLayout.SOUTH);

        startAutoSignalCycle();
        timerLabel.setText("Timer: " + signalTimeLeft + "s");
        setVisible(true);
    }

    // Pedestrian request check removed as per request

    // Pedestrian request handler removed as per request

    private void startAutoSignalCycle() {
        if (signalTimer != null) signalTimer.stop();
        setSignal("GREEN");
        signalTimeLeft = GREEN_TIME;
        previousSignal = "RED";
        signalTimer = new Timer(1000, e -> autoSignalTick());
        signalTimer.start();
    }

    private void autoSignalTick() {
        try {
            signalTimeLeft--;
            if (signalTimeLeft <= 0) {
                if (emergencyOverride) {
                    if (emergencyStep == 1) {
                        // After extra yellow, now do 10s green
                        setSignal("GREEN");
                        signalTimeLeft = 10;
                        emergencyStep = 2;
                    } else if (emergencyStep == 2) {
                        // After 10s green, always do 5s yellow before resuming
                        setSignal("YELLOW");
                        signalTimeLeft = 5;
                        emergencyStep = 3;
                    } else if (emergencyStep == 3) {
                        // After post-emergency yellow, resume normal cycle
                        setSignal(resumeCycleSignal);
                        signalTimeLeft = resumeCycleTime;
                        emergencyOverride = false;
                        resumeCycleSignal = null;
                        emergencyStep = 0;
                    } else if (currentSignal.equals("YELLOW") && resumeCycleSignal != null) {
                        // For RED case: after yellow, do 10s green
                        setSignal("GREEN");
                        signalTimeLeft = 10;
                        emergencyStep = 2;
                    } else if (currentSignal.equals("GREEN") && resumeCycleSignal != null) {
                        // After 10s green, always do 5s yellow before resuming
                        setSignal("YELLOW");
                        signalTimeLeft = 5;
                        emergencyStep = 3;
                    } else {
                        nextSignal();
                    }
                } else {
                    nextSignal();
                }
            }
            timerLabel.setText("Timer: " + signalTimeLeft + "s");
        } catch (Exception ex) {
            showBottomDialog("Timer error: " + ex.getMessage());
        }
    }

    private void nextSignal() {
        if (currentSignal.equals("GREEN")) {
            previousSignal = "GREEN";
            setSignal("YELLOW");
            signalTimeLeft = YELLOW_TIME;
        } else if (currentSignal.equals("YELLOW")) {
            if (previousSignal.equals("RED")) {
                setSignal("GREEN");
                signalTimeLeft = GREEN_TIME;
                previousSignal = "YELLOW";
            } else {
                setSignal("RED");
                signalTimeLeft = RED_TIME;
                previousSignal = "YELLOW";
            }
        } else {
            previousSignal = "RED";
            setSignal("GREEN");
            signalTimeLeft = GREEN_TIME;
        }
    }

    private void setSignal(String color) {
        currentSignal = color;
        signalIndicatorPanel.repaint();
    }

    private void showBottomDialog(String message) {
        new Thread(() -> {
            try {
                JDialog dialog = new JDialog(this, "Info", false);
                JLabel label = new JLabel(message, JLabel.CENTER);
                label.setFont(new Font("Arial", Font.PLAIN, 16));
                dialog.add(label);
                dialog.setSize(420, 80);
                dialog.setUndecorated(true);
                dialog.setLocation(
                    getX() + (getWidth() - dialog.getWidth()) / 2,
                    getY() + getHeight() - dialog.getHeight() - 40
                );
                SwingUtilities.invokeLater(() -> dialog.setVisible(true));
                // Auto-close after 2 seconds
                Thread.sleep(2000);
                SwingUtilities.invokeLater(dialog::dispose);
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JDialog errorDialog = new JDialog(this, "Error", false);
                    JLabel errorLabel = new JLabel("Error: " + ex.getMessage(), JLabel.CENTER);
                    errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
                    errorLabel.setForeground(Color.RED);
                    errorDialog.add(errorLabel);
                    errorDialog.setSize(420, 80);
                    errorDialog.setUndecorated(true);
                    errorDialog.setLocation(
                        getX() + (getWidth() - errorDialog.getWidth()) / 2,
                        getY() + getHeight() - errorDialog.getHeight() - 40
                    );
                    errorDialog.setVisible(true);
                    new Timer(2000, e -> errorDialog.dispose()).start();
                });
            }
        }).start();
    }

    private void handleEmergency() {
        try {
            if (currentSignal.equals("GREEN")) {
                if (signalTimeLeft > 10) {
                    showBottomDialog("Already GREEN with more than 10s left. No change.");
                } else {
                    signalTimeLeft += 10;
                    showBottomDialog("Added 10s to GREEN for emergency vehicle.");
                }
            } else if (currentSignal.equals("YELLOW")) {
                if (previousSignal.equals("RED")) {
                    showBottomDialog("Signal is transitioning from RED to GREEN. No change.");
                } else if (previousSignal.equals("GREEN")) {
                    // G->Y->R, so add 5s to yellow, then 10s green, then 5s yellow, then RED
                    emergencyOverride = true;
                    emergencyStep = 1;
                    resumeCycleSignal = "RED";
                    resumeCycleTime = RED_TIME;
                    signalTimeLeft += 5;
                    showBottomDialog("Added 5s to YELLOW, then will do 10s GREEN, then 5s YELLOW for emergency vehicle.");
                } else {
                    showBottomDialog("No change.");
                }
            } else if (currentSignal.equals("RED")) {
                emergencyOverride = true;
                emergencyStep = 0;
                resumeCycleSignal = "RED";
                resumeCycleTime = signalTimeLeft;
                setSignal("YELLOW");
                signalTimeLeft = 5;
                previousSignal = "RED";
                showBottomDialog("Switching to 5s YELLOW, then 10s GREEN, then 5s YELLOW for emergency vehicle.");
            }
        } catch (Exception ex) {
            showBottomDialog("Emergency error: " + ex.getMessage());
        }
    }

    private void handleCriminal() {
        try {
            SwingUtilities.invokeLater(() -> showCriminalDialog());
        } catch (Exception ex) {
            showBottomDialog("Criminal error: " + ex.getMessage());
        }
    }

    private void showCriminalDialog() {
        JDialog dialog = new JDialog(this, "Criminal Detected", true);
        dialog.setLayout(new FlowLayout());
        JButton inputBtn = new JButton("Input No");
        JButton updateBtn = new JButton("Update List");
        inputBtn.setFont(new Font("Arial", Font.BOLD, 16));
        updateBtn.setFont(new Font("Arial", Font.BOLD, 16));
        dialog.add(inputBtn);
        dialog.add(updateBtn);
        inputBtn.addActionListener(e -> {
            dialog.dispose();
            showCriminalInputDialog();
        });
        updateBtn.addActionListener(e -> {
            dialog.dispose();
            showCriminalListDialog();
        });
        dialog.setSize(320, 120);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showCriminalListDialog() {
        String[] columnNames = {"#", "Plate Number"};
        Object[][] data = new Object[criminalNumbers.size()][2];
        for (int i = 0; i < criminalNumbers.size(); i++) {
            data[i][0] = (i + 1);
            data[i][1] = criminalNumbers.get(i);
        }
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, columnNames) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(table);
        JDialog listDialog = new JDialog(this, "Criminal List", true);
        listDialog.setLayout(new BorderLayout());
        listDialog.add(scrollPane, BorderLayout.CENTER);

        JButton addBtn = new JButton("Add");
        addBtn.setFont(new Font("Arial", Font.BOLD, 16));
        addBtn.setToolTipText("Add a new criminal plate number");
        addBtn.addActionListener(evt -> {
            String input = JOptionPane.showInputDialog(listDialog, "Enter criminal plate (alphanumeric):");
            if (input != null && !input.trim().isEmpty()) {
                String plate = input.trim().toUpperCase();
                if (criminalNumbers.contains(plate)) {
                    JOptionPane.showMessageDialog(listDialog, "Plate already exists!", "Duplicate", JOptionPane.WARNING_MESSAGE);
                } else {
                    criminalNumbers.add(plate);
                    saveCriminalNumbers();
                    model.addRow(new Object[]{criminalNumbers.size(), plate});
                    JOptionPane.showMessageDialog(listDialog, "Plate added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        JButton removeBtn = new JButton("Remove");
        removeBtn.setFont(new Font("Arial", Font.BOLD, 16));
        removeBtn.setToolTipText("Remove selected criminal plate number");
        removeBtn.addActionListener(evt -> {
            int selectedIdx = table.getSelectedRow();
            if (selectedIdx != -1) {
                int confirm = JOptionPane.showConfirmDialog(listDialog, "Remove selected plate?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    criminalNumbers.remove(selectedIdx);
                    model.removeRow(selectedIdx);
                    saveCriminalNumbers();
                }
            } else {
                JOptionPane.showMessageDialog(listDialog, "Select a plate to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        listDialog.add(btnPanel, BorderLayout.SOUTH);
        listDialog.setSize(400, 350);
        listDialog.setLocationRelativeTo(this);
        listDialog.setVisible(true);
    }

    private void showCriminalInputDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter license plate (alphanumeric):");
        if (input != null && !input.trim().isEmpty()) {
            String plate = input.trim().toUpperCase();
            if (criminalNumbers.contains(plate)) {
                showBottomDialog("Match found!");
            } else {
                showBottomDialog("No match found.");
            }
        }
    }

    private void loadCriminalNumbers() {
        criminalNumbers.clear();
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(CRIMINAL_FILE);
            if (java.nio.file.Files.exists(path)) {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(path);
                for (String line : lines) {
                    line = line.trim().toUpperCase();
                    if (!line.isEmpty()) {
                        criminalNumbers.add(line);
                    }
                }
            }
        } catch (Exception ex) {
            showBottomDialog("Failed to load criminal plates: " + ex.getMessage());
            FileUtils.logError("Failed to load criminal plates", ex);
        }
    }

    private void saveCriminalNumbers() {
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(CRIMINAL_FILE), criminalNumbers);
        } catch (Exception ex) {
            showBottomDialog("Failed to save criminal plates: " + ex.getMessage());
            FileUtils.logError("Failed to save criminal plates", ex);
        }
    }

    // Simple test for file I/O
    public static void testFileIO() {
        try {
            String testFile = FileUtils.getCriminalFilePath();
            java.util.List<String> testList = new java.util.ArrayList<>();
            testList.add("TEST123");
            java.nio.file.Files.write(java.nio.file.Paths.get(testFile), testList);
            java.util.List<String> readList = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(testFile));
            assert readList.get(0).equals("TEST123");
            java.nio.file.Files.delete(java.nio.file.Paths.get(testFile));
        } catch (Exception ex) {
            FileUtils.logError("Test file I/O failed", ex);
        }
    }

    public String getCurrentSignal() {
        return currentSignal;
    }
    public int getSignalTimeLeft() {
        return signalTimeLeft;
    }
}

// === File Utilities for Persistence and Logging ===
class FileUtils {
    private static final String ERROR_LOG = System.getProperty("user.home") + "/LightSync_error.log";
    public static String getCriminalFilePath() {
        String userHome = System.getProperty("user.home");
        String sep = System.getProperty("file.separator");
        return userHome + sep + "criminal_plates.txt";
    }
    public static void logError(String msg, Exception ex) {
        try {
            java.nio.file.Files.write(
                java.nio.file.Paths.get(ERROR_LOG),
                (msg + ": " + ex + System.lineSeparator()).getBytes(),
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND
            );
        } catch (Exception ignored) {}
    }
}



// === Simulation Panel ===
class SimPanel extends JPanel implements ActionListener {
    private List<Vehicle> vehicles = new ArrayList<>();
    private Timer timer;
    private int vehicleCount = 0;
    private JLabel countLabel;

    public SimPanel(JLabel countLabel) {
        this.countLabel = countLabel;
        setPreferredSize(new Dimension(500, 300));
        setBackground(Color.WHITE);
        timer = new Timer(50, this); // 20 FPS
        timer.start();

        // Add vehicles periodically
        new Timer(2000, e -> addVehicle()).start();
    }

    private void addVehicle() {
        vehicles.add(new Vehicle(0, 120, 5));
        vehicleCount++;
        countLabel.setText("🚗 Vehicles on Road: " + vehicleCount);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawRoad((Graphics2D) g);
        drawVehicles((Graphics2D) g);
    }

    private void drawRoad(Graphics2D g2) {
        g2.setColor(Color.GRAY);
        g2.fillRect(0, 100, getWidth(), 60); // horizontal road
        g2.setColor(Color.YELLOW);
        g2.drawLine(0, 130, getWidth(), 130); // center line
    }

    private void drawVehicles(Graphics2D g2) {
        g2.setColor(Color.BLUE);
        for (Vehicle v : vehicles) {
            g2.fillRect(v.x, v.y, 40, 20);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Vehicle v : vehicles) {
            v.x += v.speed;
        }
        repaint();
    }
}

// === Vehicle Class ===
class Vehicle {
    int x, y, speed;

    public Vehicle(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }
}

class AdminDashboardHolder {
    public static AdminDashboard adminInstance;

    public static AdminDashboard getInstance() {
        return adminInstance;
    }
}
