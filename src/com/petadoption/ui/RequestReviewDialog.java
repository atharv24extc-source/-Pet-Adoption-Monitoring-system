package com.petadoption.ui;

import com.petadoption.model.AdoptionRequest;
import com.petadoption.util.UIUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Dialog for Admin to review applicant details, pet info, notes, and Approve or Reject the adoption.
 */
public class RequestReviewDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private final AdoptionRequest request;
    private JTextArea txtRemarks;
    private boolean approved = false;
    private boolean rejected = false;
    private String adminRemarks;

    public RequestReviewDialog(Frame parent, AdoptionRequest request) {
        super(parent, "Review Adoption Request #" + request.getRequestId(), true);
        this.request = request;
        setSize(550, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIUtils.COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("📋 Review Request #" + request.getRequestId() + " - " + request.getPetName());
        lblTitle.setFont(UIUtils.FONT_SUBHEADER);
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Details Panel
        JPanel detailsPanel = new JPanel(new GridLayout(6, 2, 8, 8));
        detailsPanel.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(UIUtils.COLOR_BORDER, 1), "Application Details", 0, 0, UIUtils.FONT_BODY_BOLD, UIUtils.COLOR_PRIMARY_DARK),
                new EmptyBorder(8, 12, 8, 12)
        ));

        addDetailRow(detailsPanel, "Applicant Name:", request.getUserName());
        addDetailRow(detailsPanel, "Applicant Contact:", request.getUserEmail() + " | " + request.getUserPhone());
        addDetailRow(detailsPanel, "Target Pet:", request.getPetName() + " (" + request.getPetSpecies() + " - " + request.getPetBreed() + ")");
        addDetailRow(detailsPanel, "Request Date:", request.getRequestDate() != null ? request.getRequestDate().toString() : "N/A");
        addDetailRow(detailsPanel, "Living Situation:", request.getLivingSituation());
        addDetailRow(detailsPanel, "Has Other Pets?", request.isHasOtherPets() ? "Yes" : "No");

        contentPanel.add(detailsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Applicant notes
        JPanel notesPanel = new JPanel(new BorderLayout(5, 5));
        notesPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(UIUtils.COLOR_BORDER, 1), "Applicant Note / Reason", 0, 0, UIUtils.FONT_BODY_BOLD, UIUtils.COLOR_PRIMARY_DARK));
        JTextArea txtApplicantNote = new JTextArea(request.getNotes() == null ? "None" : request.getNotes());
        txtApplicantNote.setEditable(false);
        txtApplicantNote.setLineWrap(true);
        txtApplicantNote.setWrapStyleWord(true);
        txtApplicantNote.setBackground(new Color(248, 249, 250));
        txtApplicantNote.setFont(UIUtils.FONT_BODY);
        notesPanel.add(new JScrollPane(txtApplicantNote), BorderLayout.CENTER);
        notesPanel.setPreferredSize(new Dimension(500, 80));
        contentPanel.add(notesPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Admin Remarks Input
        JPanel remarksPanel = new JPanel(new BorderLayout(5, 5));
        remarksPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(UIUtils.COLOR_BORDER, 1), "Admin Review Remarks", 0, 0, UIUtils.FONT_BODY_BOLD, UIUtils.COLOR_PRIMARY_DARK));
        txtRemarks = new JTextArea(3, 20);
        txtRemarks.setLineWrap(true);
        txtRemarks.setWrapStyleWord(true);
        txtRemarks.setFont(UIUtils.FONT_BODY);
        if (request.getAdminRemarks() != null) {
            txtRemarks.setText(request.getAdminRemarks());
        }
        remarksPanel.add(new JScrollPane(txtRemarks), BorderLayout.CENTER);
        contentPanel.add(remarksPanel);

        add(contentPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBorder(new EmptyBorder(0, 15, 10, 15));

        JButton btnCancel = UIUtils.createButton("Close", UIUtils.COLOR_BORDER, UIUtils.COLOR_TEXT_MAIN);
        JButton btnReject = UIUtils.createButton("Reject Request", UIUtils.COLOR_DANGER, Color.WHITE);
        JButton btnApprove = UIUtils.createButton("Approve & Finalize", UIUtils.COLOR_ACCENT, Color.WHITE);

        btnCancel.addActionListener(e -> dispose());
        btnReject.addActionListener(e -> handleReject());
        btnApprove.addActionListener(e -> handleApprove());

        buttonPanel.add(btnCancel);
        if ("PENDING".equalsIgnoreCase(request.getStatus())) {
            buttonPanel.add(btnReject);
            buttonPanel.add(btnApprove);
        }

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtils.FONT_BODY_BOLD);
        lbl.setForeground(UIUtils.COLOR_TEXT_MAIN);
        JLabel val = new JLabel(value != null ? value : "N/A");
        val.setFont(UIUtils.FONT_BODY);
        val.setForeground(UIUtils.COLOR_TEXT_MAIN);
        panel.add(lbl);
        panel.add(val);
    }

    private void handleApprove() {
        if (UIUtils.showConfirm(this, "Are you sure you want to APPROVE this adoption request?\nThis will mark the pet as Adopted and record into history.", "Confirm Approval")) {
            approved = true;
            adminRemarks = txtRemarks.getText().trim();
            if (adminRemarks.isEmpty()) {
                adminRemarks = "Approved by Administrator.";
            }
            dispose();
        }
    }

    private void handleReject() {
        if (UIUtils.showConfirm(this, "Are you sure you want to REJECT this adoption request?", "Confirm Rejection")) {
            rejected = true;
            adminRemarks = txtRemarks.getText().trim();
            if (adminRemarks.isEmpty()) {
                adminRemarks = "Application did not meet criteria at this time.";
            }
            dispose();
        }
    }

    public boolean isApproved() {
        return approved;
    }

    public boolean isRejected() {
        return rejected;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }
}
