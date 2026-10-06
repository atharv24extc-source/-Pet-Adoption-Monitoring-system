package com.petadoption.ui;

import com.petadoption.util.DBConnection;
import com.petadoption.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog allowing users to configure and test their MySQL connection parameters.
 */
public class DBConfigDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private JTextField txtHost;
    private JTextField txtPort;
    private JTextField txtDbName;
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JLabel lblStatus;
    private boolean connected = false;

    public DBConfigDialog(Frame parent) {
        super(parent, "Database Connection Settings", true);
        initComponents();
    }

    private void initComponents() {
        setSize(450, 420);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIUtils.COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("MySQL Connection Configuration");
        lblTitle.setFont(UIUtils.FONT_SUBHEADER);
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtHost = new JTextField(DBConnection.getHost());
        txtPort = new JTextField(DBConnection.getPort());
        txtDbName = new JTextField(DBConnection.getDbName());
        txtUser = new JTextField(DBConnection.getUser());
        txtPass = new JPasswordField(DBConnection.getPassword());

        UIUtils.styleTextField(txtHost);
        UIUtils.styleTextField(txtPort);
        UIUtils.styleTextField(txtDbName);
        UIUtils.styleTextField(txtUser);
        txtPass.setFont(UIUtils.FONT_BODY);

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Host:", txtHost);
        addFormRow(formPanel, gbc, row++, "Port:", txtPort);
        addFormRow(formPanel, gbc, row++, "Database Name:", txtDbName);
        addFormRow(formPanel, gbc, row++, "Username:", txtUser);
        addFormRow(formPanel, gbc, row++, "Password:", txtPass);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(UIUtils.FONT_SMALL);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(lblStatus, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBorder(new EmptyBorder(0, 15, 10, 15));

        JButton btnTest = UIUtils.createButton("Test Connection", new Color(108, 117, 125), Color.WHITE);
        JButton btnSave = UIUtils.createButton("Save & Connect", UIUtils.COLOR_ACCENT, Color.WHITE);
        JButton btnCancel = UIUtils.createButton("Cancel", UIUtils.COLOR_BORDER, UIUtils.COLOR_TEXT_MAIN);

        btnTest.addActionListener(e -> testConnection());
        btnSave.addActionListener(e -> saveAndClose());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnTest);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        JLabel label = new JLabel(labelText);
        label.setFont(UIUtils.FONT_BODY_BOLD);
        label.setForeground(UIUtils.COLOR_TEXT_MAIN);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    private void testConnection() {
        String h = txtHost.getText().trim();
        String p = txtPort.getText().trim();
        String db = txtDbName.getText().trim();
        String u = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        boolean ok = DBConnection.testConnection(h, p, db, u, pass);
        if (ok) {
            lblStatus.setText("Connection successful!");
            lblStatus.setForeground(UIUtils.COLOR_ACCENT);
        } else {
            lblStatus.setText("Connection failed! Check credentials & DB server.");
            lblStatus.setForeground(UIUtils.COLOR_DANGER);
        }
    }

    private void saveAndClose() {
        String h = txtHost.getText().trim();
        String p = txtPort.getText().trim();
        String db = txtDbName.getText().trim();
        String u = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        if (h.isEmpty() || p.isEmpty() || db.isEmpty() || u.isEmpty()) {
            lblStatus.setText("Please fill all required fields.");
            lblStatus.setForeground(UIUtils.COLOR_DANGER);
            return;
        }

        DBConnection.updateConfig(h, p, db, u, pass);
        connected = true;
        dispose();
    }

    public boolean isConnected() {
        return connected;
    }
}
