package com.petadoption.ui;

import com.petadoption.dao.UserDAO;
import com.petadoption.model.User;
import com.petadoption.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Login Window for the Pet Adoption Monitoring System.
 */
public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("Pet Adoption Monitoring System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.COLOR_BG_LIGHT);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(UIUtils.COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel lblLogo = new JLabel("🐾 Pet Adoption System");
        lblLogo.setFont(UIUtils.FONT_HEADER_TITLE);
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Connecting Loving Families with Pets in Need");
        lblSubtitle.setFont(UIUtils.FONT_BODY);
        lblSubtitle.setForeground(new Color(235, 245, 255));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblLogo);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        headerPanel.add(lblSubtitle);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Card Panel
        JPanel cardPanel = UIUtils.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblSignIn = new JLabel("Sign In to Your Account");
        lblSignIn.setFont(UIUtils.FONT_SUBHEADER);
        lblSignIn.setForeground(UIUtils.COLOR_TEXT_MAIN);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        cardPanel.add(lblSignIn, gbc);

        // Username
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UIUtils.FONT_BODY_BOLD);
        lblUser.setForeground(UIUtils.COLOR_TEXT_MAIN);
        cardPanel.add(lblUser, gbc);

        gbc.gridy = 2;
        txtUsername = new JTextField("admin");
        UIUtils.styleTextField(txtUsername);
        txtUsername.setPreferredSize(new Dimension(300, 34));
        cardPanel.add(txtUsername, gbc);

        // Password
        gbc.gridy = 3;
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UIUtils.FONT_BODY_BOLD);
        lblPass.setForeground(UIUtils.COLOR_TEXT_MAIN);
        cardPanel.add(lblPass, gbc);

        gbc.gridy = 4;
        txtPassword = new JPasswordField("admin123");
        txtPassword.setFont(UIUtils.FONT_BODY);
        txtPassword.setPreferredSize(new Dimension(300, 34));
        cardPanel.add(txtPassword, gbc);

        // Action Buttons
        gbc.gridy = 5;
        gbc.insets = new Insets(16, 12, 6, 12);
        JButton btnLogin = UIUtils.createButton("Log In", UIUtils.COLOR_PRIMARY, Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(300, 38));
        btnLogin.addActionListener(e -> performLogin());
        cardPanel.add(btnLogin, gbc);

        // Register Link / Button
        gbc.gridy = 6;
        gbc.insets = new Insets(4, 12, 4, 12);
        JButton btnRegister = UIUtils.createButton("Create New Adopter Account", UIUtils.COLOR_ACCENT, Color.WHITE);
        btnRegister.setPreferredSize(new Dimension(300, 34));
        btnRegister.addActionListener(e -> openRegistration());
        cardPanel.add(btnRegister, gbc);

        // Demo credentials hint
        gbc.gridy = 7;
        gbc.insets = new Insets(8, 12, 4, 12);
        JLabel lblHint = new JLabel("<html><center style='color:#7f8c8d; font-size:10px;'>Demo: Admin (admin / admin123)<br>Adopter (john_doe / user123)</center></html>");
        lblHint.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(lblHint, gbc);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        centerWrapper.setBackground(UIUtils.COLOR_BG_LIGHT);
        centerWrapper.add(cardPanel);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // Bottom Bar (DB Config button)
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(UIUtils.COLOR_BG_LIGHT);
        bottomBar.setBorder(new EmptyBorder(5, 15, 10, 15));

        JButton btnDbSettings = new JButton("⚙ DB Connection");
        btnDbSettings.setFont(UIUtils.FONT_SMALL);
        btnDbSettings.setFocusPainted(false);
        btnDbSettings.setBorderPainted(false);
        btnDbSettings.setContentAreaFilled(false);
        btnDbSettings.setForeground(UIUtils.COLOR_TEXT_MUTED);
        btnDbSettings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDbSettings.addActionListener(e -> new DBConfigDialog(this).setVisible(true));

        bottomBar.add(btnDbSettings, BorderLayout.EAST);
        mainPanel.add(bottomBar, BorderLayout.SOUTH);

        // Enter key to login
        getRootPane().setDefaultButton(btnLogin);

        setContentPane(mainPanel);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            UIUtils.showError(this, "Please enter both username and password.", "Login Error");
            return;
        }

        try {
            User user = userDAO.authenticate(username, password);
            if (user != null) {
                dispose(); // Close login window
                if (user.isAdmin()) {
                    new AdminDashboard(user).setVisible(true);
                } else {
                    new AdopterDashboard(user).setVisible(true);
                }
            } else {
                UIUtils.showError(this, "Invalid username or password. Please try again.", "Authentication Failed");
            }
        } catch (SQLException ex) {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Database connection failed!\n" + ex.getMessage() + "\n\nWould you like to configure MySQL connection settings?",
                    "Database Error",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
            );
            if (option == JOptionPane.YES_OPTION) {
                new DBConfigDialog(this).setVisible(true);
            }
        }
    }

    private void openRegistration() {
        new RegisterFrame(this).setVisible(true);
    }
}
