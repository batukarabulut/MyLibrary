import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Blue
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);    // Light Blue
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);       // Green
    private static final Color DANGER_COLOR = new Color(231, 76, 60);        // Red
    private static final Color DARK_COLOR = new Color(44, 62, 80);           // Dark Blue
    private static final Color LIGHT_COLOR = new Color(236, 240, 241);       // Light Gray
    private static final Color WHITE_COLOR = Color.WHITE;

    public LoginFrame() {
        initializeComponents();
        setupLayout();
        addEventListeners();
        setupFrame();
    }

    private void initializeComponents() {
        // Create modern text fields
        usernameField = createStyledTextField();
        passwordField = createStyledPasswordField();

        // Create modern buttons
        loginButton = createPrimaryButton("Login");
        exitButton = createSecondaryButton("Exit");
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setBackground(WHITE_COLOR);
        field.setForeground(DARK_COLOR);

        // Add focus effects
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        BorderFactory.createEmptyBorder(11, 14, 11, 14)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
        });

        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setBackground(WHITE_COLOR);
        field.setForeground(DARK_COLOR);

        // Add focus effects
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        BorderFactory.createEmptyBorder(11, 14, 11, 14)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
        });

        return field;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(WHITE_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effects
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(149, 165, 166));
        button.setForeground(WHITE_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effects
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(127, 140, 141));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(149, 165, 166));
            }
        });

        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Main container with padding - centered and cleaner
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(WHITE_COLOR);
        mainContainer.setBorder(new EmptyBorder(60, 80, 60, 80));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();

        // Content Panel (Login Form)
        JPanel contentPanel = createContentPanel();

        // Footer Panel (Buttons)
        JPanel footerPanel = createFooterPanel();

        // Add components to main container
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        mainContainer.add(footerPanel, BorderLayout.SOUTH);

        // Add main container directly (no split pane)
        add(mainContainer);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Main title
        JLabel titleLabel = new JLabel("MyLibrary");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Personal Library Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalStrut(50));

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome back! Please sign in to your account.");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setForeground(new Color(127, 140, 141));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(welcomeLabel);
        panel.add(Box.createVerticalStrut(40));

        // Username field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameLabel.setForeground(DARK_COLOR);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setMaximumSize(new Dimension(350, 50));
        usernameField.setPreferredSize(new Dimension(350, 50));

        panel.add(usernameLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(25));

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordLabel.setForeground(DARK_COLOR);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(350, 50));
        passwordField.setPreferredSize(new Dimension(350, 50));

        panel.add(passwordLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(50));

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setBackground(WHITE_COLOR);

        loginButton.setPreferredSize(new Dimension(140, 50));
        exitButton.setPreferredSize(new Dimension(140, 50));

        panel.add(loginButton);
        panel.add(exitButton);

        return panel;
    }

    private void setupFrame() {
        setTitle("MyLibrary - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700);  // Smaller, cleaner size
        setLocationRelativeTo(null);
        setResizable(false);

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default if system L&F fails
        }
    }

    private void addEventListeners() {
        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        // Exit button action
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Allow Enter key to trigger login
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Please enter both username and password.");
            return;
        }

        // Show loading state
        loginButton.setText("Signing in...");
        loginButton.setEnabled(false);

        // Use SwingWorker for smooth UI
        SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return DatabaseHelper.validateLogin(username, password);
            }

            @Override
            protected void done() {
                try {
                    int userType = get();

                    if (userType != -1) {
                        // Login successful
                        showSuccessMessage("Welcome back, " + username + "!");

                        // Small delay for better UX
                        Timer timer = new Timer(500, e -> {
                            dispose(); // Close login window

                            // Open appropriate main window
                            if (userType == 1) {
                                new Type1MainFrame().setVisible(true);
                            } else if (userType == 2) {
                                new Type2MainFrame().setVisible(true);
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();

                    } else {
                        // Login failed
                        showErrorMessage("Invalid username or password.");
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }

                } catch (Exception ex) {
                    showErrorMessage("Login error: " + ex.getMessage());
                } finally {
                    // Reset button state
                    loginButton.setText("Login");
                    loginButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}