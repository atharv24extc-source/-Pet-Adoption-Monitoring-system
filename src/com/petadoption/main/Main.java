package com.petadoption.main;

import com.petadoption.ui.LoginFrame;
import com.petadoption.util.DBConnection;

import javax.swing.*;
import java.sql.Connection;

/**
 * Main application entry point for Pet Adoption Monitoring System.
 */
public class Main {
    public static void main(String[] args) {
        // Set cross-platform look and feel or modern system look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        // Launch UI on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Optional: Background check of DB connection
            new Thread(() -> {
                try (Connection conn = DBConnection.getConnection()) {
                    if (conn != null) {
                        System.out.println("Successfully connected to MySQL database: " + DBConnection.getDbName());
                    }
                } catch (Exception ex) {
                    System.err.println("Note: Database not reachable at default settings. You can configure it via the UI '⚙ DB Connection' button.");
                }
            }).start();

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
