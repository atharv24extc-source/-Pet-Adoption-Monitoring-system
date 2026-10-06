package com.petadoption.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Utility class for styling Swing components with a clean, cohesive, modern design.
 */
public class UIUtils {
    // Primary Color Palette
    public static final Color COLOR_PRIMARY = new Color(41, 128, 185);       // Elegant Blue
    public static final Color COLOR_PRIMARY_DARK = new Color(31, 97, 141);
    public static final Color COLOR_ACCENT = new Color(39, 174, 96);         // Emerald Green
    public static final Color COLOR_ACCENT_DARK = new Color(30, 132, 73);
    public static final Color COLOR_DANGER = new Color(231, 76, 60);          // Coral Red
    public static final Color COLOR_WARNING = new Color(243, 156, 18);        // Amber Orange
    public static final Color COLOR_DARK_SIDEBAR = new Color(34, 47, 62);     // Deep Navy Slate
    public static final Color COLOR_BG_LIGHT = new Color(245, 247, 250);     // Clean Soft Off-White
    public static final Color COLOR_CARD_BG = Color.WHITE;
    public static final Color COLOR_TEXT_MAIN = new Color(44, 62, 80);
    public static final Color COLOR_TEXT_MUTED = new Color(127, 140, 141);
    public static final Color COLOR_BORDER = new Color(220, 224, 230);

    // Standard Fonts
    public static final Font FONT_HEADER_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    /**
     * Creates a styled primary action button.
     */
    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(new LineBorder(bg.darker(), 1, true), new EmptyBorder(8, 16, 8, 16)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    /**
     * Creates a styled sidebar navigation button.
     */
    public static JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(COLOR_DARK_SIDEBAR);
        btn.setForeground(new Color(220, 225, 230));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 20, 12, 20));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 73, 94));
                btn.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_DARK_SIDEBAR);
                btn.setForeground(new Color(220, 225, 230));
            }
        });
        return btn;
    }

    /**
     * Creates a styled card container panel.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_CARD_BG);
        panel.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    /**
     * Styles a JTable with modern headers, row height, and alternating row colors.
     */
    public static void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(FONT_BODY);
        table.setSelectionBackground(new Color(214, 234, 248));
        table.setSelectionForeground(COLOR_TEXT_MAIN);
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(new Color(236, 240, 241));
        header.setForeground(COLOR_TEXT_MAIN);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setReorderingAllowed(false);

        // Center or format specific cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    }

    /**
     * Table Cell Renderer for Status Badges (Available, Pending, Adopted, Approved, Rejected).
     */
    public static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(FONT_BODY_BOLD);

            if (value != null) {
                String val = value.toString().toUpperCase();
                if (val.contains("AVAILABLE") || val.contains("APPROVED")) {
                    label.setForeground(new Color(39, 174, 96));
                } else if (val.contains("PENDING")) {
                    label.setForeground(new Color(230, 126, 34));
                } else if (val.contains("ADOPTED")) {
                    label.setForeground(new Color(41, 128, 185));
                } else if (val.contains("REJECTED")) {
                    label.setForeground(new Color(231, 76, 60));
                } else {
                    label.setForeground(COLOR_TEXT_MAIN);
                }
            }
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
            } else {
                label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 252, 255));
            }
            return label;
        }
    }

    /**
     * Styles standard text inputs.
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(FONT_BODY);
        textField.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    /**
     * Shows a styled information dialog.
     */
    public static void showInfo(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a styled error dialog.
     */
    public static void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a confirmation dialog.
     */
    public static boolean showConfirm(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}
