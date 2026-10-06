package com.petadoption.ui;

import com.petadoption.model.AdoptionRequest;
import com.petadoption.model.Pet;
import com.petadoption.model.User;
import com.petadoption.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog for submitting an adoption application for a selected pet.
 */
public class AdoptionRequestDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private final Pet pet;
    private final User user;

    private JComboBox<String> cbLivingSituation;
    private JCheckBox chkOtherPets;
    private JTextArea txtNotes;
    private boolean submitted = false;
    private AdoptionRequest request;

    public AdoptionRequestDialog(Frame parent, Pet pet, User user) {
        super(parent, "Submit Adoption Application", true);
        this.pet = pet;
        this.user = user;
        setSize(480, 520);
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
        JLabel lblTitle = new JLabel("🐾 Adopt " + pet.getName() + " (" + pet.getBreed() + ")");
        lblTitle.setFont(UIUtils.FONT_SUBHEADER);
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblApplicant = new JLabel("Applicant: " + user.getFullName() + " (" + user.getEmail() + ")");
        lblApplicant.setFont(UIUtils.FONT_BODY_BOLD);
        lblApplicant.setForeground(UIUtils.COLOR_PRIMARY_DARK);

        String[] livingOptions = {
                "Own Single-Family House (Fenced Yard)",
                "Own House (No Yard)",
                "Rented Apartment / Condo (Pet-Friendly)",
                "Townhouse / Duplex",
                "Farm / Rural Property"
        };
        cbLivingSituation = new JComboBox<>(livingOptions);
        chkOtherPets = new JCheckBox("I currently have other pets at home");
        chkOtherPets.setFont(UIUtils.FONT_BODY);

        txtNotes = new JTextArea(4, 20);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        txtNotes.setFont(UIUtils.FONT_BODY);
        JScrollPane notesScroll = new JScrollPane(txtNotes);

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formPanel.add(lblApplicant, gbc);

        gbc.gridwidth = 1;
        addFormRow(formPanel, gbc, row++, "Living Situation *", cbLivingSituation);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formPanel.add(chkOtherPets, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblNotes = new JLabel("Why do you want to adopt?");
        lblNotes.setFont(UIUtils.FONT_BODY_BOLD);
        formPanel.add(lblNotes, gbc);

        gbc.gridx = 1;
        formPanel.add(notesScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBorder(new EmptyBorder(0, 15, 10, 15));

        JButton btnCancel = UIUtils.createButton("Cancel", UIUtils.COLOR_BORDER, UIUtils.COLOR_TEXT_MAIN);
        JButton btnSubmit = UIUtils.createButton("Submit Application", UIUtils.COLOR_ACCENT, Color.WHITE);

        btnCancel.addActionListener(e -> dispose());
        btnSubmit.addActionListener(e -> handleSubmit());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSubmit);
        add(buttonPanel, BorderLayout.SOUTH);
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
        panel.add(field, gbc);
    }

    private void handleSubmit() {
        String living = (String) cbLivingSituation.getSelectedItem();
        boolean hasOther = chkOtherPets.isSelected();
        String notes = txtNotes.getText().trim();

        if (notes.isEmpty()) {
            UIUtils.showError(this, "Please provide a brief note on why you'd like to adopt " + pet.getName() + ".", "Required Field");
            return;
        }

        request = new AdoptionRequest();
        request.setUserId(user.getUserId());
        request.setPetId(pet.getPetId());
        request.setLivingSituation(living);
        request.setHasOtherPets(hasOther);
        request.setNotes(notes);
        request.setStatus("PENDING");

        submitted = true;
        dispose();
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public AdoptionRequest getRequest() {
        return request;
    }
}
