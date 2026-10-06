package com.petadoption.ui;

import com.petadoption.dao.UserDAO;
import com.petadoption.model.User;
import com.petadoption.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Registration Window for new Adopters.
 */
public class RegisterFrame extends JDialog {
    private static final long serialVersionUID = 1L;
    private JTextField txtFullName;
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;

    private final UserDAO userDAO = new UserDAO();

    public RegisterFrame(Frame parent) {
        super(parent, "Adopter Registration", true);
        setSize(480, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.COLOR_BG_LIGHT);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(UIUtils.COLOR_ACCENT);
        headerPanel.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel lblTitle = new JLabel("🐾 Create Adopter Account");
        lblTitle.setFont(UIUtils.FONT_HEADER_TITLE);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Join our community to adopt and provide forever homes");
        lblSub.setFont(UIUtils.FONT_SMALL);
        lblSub.setForeground(new Color(235, 250, 240));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(lblSub);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Card
        JPanel cardPanel = UIUtils.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtFullName = new JTextField();
        txtUsername = new JTextField();
        txtEmail = new JTextField();
        txtPhone = new JTextField();
        txtAddress = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirmPassword = new JPasswordField();

        UIUtils.styleTextField(txtFullName);
        UIUtils.styleTextField(txtUsername);
        UIUtils.styleTextField(txtEmail);
        UIUtils.styleTextField(txtPhone);
        UIUtils.styleTextField(txtAddress);
        txtPassword.setFont(UIUtils.FONT_BODY);
        txtConfirmPassword.setFont(UIUtils.FONT_BODY);

        int row = 0;
        addFormRow(cardPanel, gbc, row++, "Full Name *", txtFullName);
        addFormRow(cardPanel, gbc, row++, "Username *", txtUsername);
        addFormRow(cardPanel, gbc, row++, "Email Address *", txtEmail);
        addFormRow(cardPanel, gbc, row++, "Phone Number *", txtPhone);
        addFormRow(cardPanel, gbc, row++, "Living Address *", txtAddress);
        addFormRow(cardPanel, gbc, row++, "Password *", txtPassword);
        addFormRow(cardPanel, gbc, row++, "Confirm Password *", txtConfirmPassword);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        centerWrapper.setBackground(UIUtils.COLOR_BG_LIGHT);
        centerWrapper.add(cardPanel);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(UIUtils.COLOR_BG_LIGHT);

        JButton btnCancel = UIUtils.createButton("Cancel", UIUtils.COLOR_BORDER, UIUtils.COLOR_TEXT_MAIN);
        JButton btnRegister = UIUtils.createButton("Register Account", UIUtils.COLOR_ACCENT, Color.WHITE);

        btnCancel.addActionListener(e -> dispose());
        btnRegister.addActionListener(e -> performRegister());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnRegister);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UIUtils.FONT_BODY_BOLD);
        lbl.setForeground(UIUtils.COLOR_TEXT_MAIN);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        field.setPreferredSize(new Dimension(240, 30));
        panel.add(field, gbc);
    }

    private void performRegister() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();

        // Basic validations
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
            UIUtils.showError(this, "All fields marked with * are required.", "Validation Error");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            UIUtils.showError(this, "Please enter a valid email address.", "Validation Error");
            return;
        }

        if (!password.equals(confirmPassword)) {
            UIUtils.showError(this, "Passwords do not match!", "Validation Error");
            return;
        }

        if (password.length() < 4) {
            UIUtils.showError(this, "Password must be at least 4 characters long.", "Validation Error");
            return;
        }

        try {
            if (userDAO.isUsernameTaken(username)) {
                UIUtils.showError(this, "Username '" + username + "' is already taken. Please choose another.", "Registration Error");
                return;
            }

            if (userDAO.isEmailTaken(email)) {
                UIUtils.showError(this, "Email address is already registered.", "Registration Error");
                return;
            }

            User newUser = new User(username, password, fullName, email, phone, address, "ADOPTER");
            boolean success = userDAO.register(newUser);
            if (success) {
                UIUtils.showInfo(this, "Account registered successfully!\nYou can now log in with your credentials.", "Success");
                dispose();
            } else {
                UIUtils.showError(this, "Failed to register account. Please try again.", "Error");
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Database error: " + ex.getMessage(), "Database Error");
        }
    }
}
