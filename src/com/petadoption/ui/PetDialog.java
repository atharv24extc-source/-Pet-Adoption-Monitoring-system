package com.petadoption.ui;

import com.petadoption.model.Pet;
import com.petadoption.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog for adding a new pet or editing an existing pet's details.
 */
public class PetDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private JTextField txtName;
    private JComboBox<String> cbSpecies;
    private JTextField txtBreed;
    private JSpinner spAge;
    private JComboBox<String> cbGender;
    private JComboBox<String> cbSize;
    private JComboBox<String> cbStatus;
    private JTextField txtHealth;
    private JTextArea txtDescription;

    private Pet pet;
    private boolean saved = false;

    public PetDialog(Frame parent, Pet petToEdit) {
        super(parent, petToEdit == null ? "Add New Pet" : "Edit Pet Details", true);
        this.pet = petToEdit;
        setSize(500, 620);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
        if (pet != null) {
            populateFields();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIUtils.COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel(pet == null ? "🐾 Add New Pet Profile" : "✏ Edit Pet: " + pet.getName());
        lblTitle.setFont(UIUtils.FONT_SUBHEADER);
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtName = new JTextField();
        cbSpecies = new JComboBox<>(new String[]{"Dog", "Cat", "Rabbit", "Bird", "Other"});
        txtBreed = new JTextField();
        spAge = new JSpinner(new SpinnerNumberModel(1, 0, 30, 1));
        cbGender = new JComboBox<>(new String[]{"Male", "Female"});
        cbSize = new JComboBox<>(new String[]{"Small", "Medium", "Large"});
        cbStatus = new JComboBox<>(new String[]{"Available", "Pending", "Adopted"});
        txtHealth = new JTextField("Healthy & Vaccinated");
        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setFont(UIUtils.FONT_BODY);
        JScrollPane descScroll = new JScrollPane(txtDescription);

        UIUtils.styleTextField(txtName);
        UIUtils.styleTextField(txtBreed);
        UIUtils.styleTextField(txtHealth);

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Pet Name *", txtName);
        addFormRow(formPanel, gbc, row++, "Species *", cbSpecies);
        addFormRow(formPanel, gbc, row++, "Breed *", txtBreed);
        addFormRow(formPanel, gbc, row++, "Age (Years) *", spAge);
        addFormRow(formPanel, gbc, row++, "Gender *", cbGender);
        addFormRow(formPanel, gbc, row++, "Size *", cbSize);
        addFormRow(formPanel, gbc, row++, "Status *", cbStatus);
        addFormRow(formPanel, gbc, row++, "Health & Vaccines", txtHealth);

        // Description area
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lblDesc = new JLabel("Description / Notes");
        lblDesc.setFont(UIUtils.FONT_BODY_BOLD);
        lblDesc.setForeground(UIUtils.COLOR_TEXT_MAIN);
        formPanel.add(lblDesc, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(descScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBorder(new EmptyBorder(0, 15, 10, 15));

        JButton btnCancel = UIUtils.createButton("Cancel", UIUtils.COLOR_BORDER, UIUtils.COLOR_TEXT_MAIN);
        JButton btnSave = UIUtils.createButton(pet == null ? "Add Pet" : "Save Changes", UIUtils.COLOR_ACCENT, Color.WHITE);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> savePet());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UIUtils.FONT_BODY_BOLD);
        lbl.setForeground(UIUtils.COLOR_TEXT_MAIN);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void populateFields() {
        txtName.setText(pet.getName());
        cbSpecies.setSelectedItem(pet.getSpecies());
        txtBreed.setText(pet.getBreed());
        spAge.setValue(pet.getAge());
        cbGender.setSelectedItem(pet.getGender());
        cbSize.setSelectedItem(pet.getSize());
        cbStatus.setSelectedItem(pet.getStatus());
        txtHealth.setText(pet.getHealthStatus());
        txtDescription.setText(pet.getDescription());
    }

    private void savePet() {
        String name = txtName.getText().trim();
        String species = (String) cbSpecies.getSelectedItem();
        String breed = txtBreed.getText().trim();
        int age = (Integer) spAge.getValue();
        String gender = (String) cbGender.getSelectedItem();
        String size = (String) cbSize.getSelectedItem();
        String status = (String) cbStatus.getSelectedItem();
        String health = txtHealth.getText().trim();
        String desc = txtDescription.getText().trim();

        if (name.isEmpty() || breed.isEmpty()) {
            UIUtils.showError(this, "Pet Name and Breed are required.", "Validation Error");
            return;
        }

        if (pet == null) {
            pet = new Pet(name, species, breed, age, gender, size, status, health, desc);
        } else {
            pet.setName(name);
            pet.setSpecies(species);
            pet.setBreed(breed);
            pet.setAge(age);
            pet.setGender(gender);
            pet.setSize(size);
            pet.setStatus(status);
            pet.setHealthStatus(health);
            pet.setDescription(desc);
        }

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public Pet getPet() {
        return pet;
    }
}
