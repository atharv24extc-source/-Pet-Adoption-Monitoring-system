package com.petadoption.ui;

import com.petadoption.dao.AdoptionHistoryDAO;
import com.petadoption.dao.AdoptionRequestDAO;
import com.petadoption.dao.PetDAO;
import com.petadoption.dao.UserDAO;
import com.petadoption.model.AdoptionHistory;
import com.petadoption.model.AdoptionRequest;
import com.petadoption.model.Pet;
import com.petadoption.model.User;
import com.petadoption.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Main Dashboard window for registered Adopter users.
 */
public class AdopterDashboard extends JFrame {
    private static final long serialVersionUID = 1L;
    private final User currentUser;

    // DAOs
    private final PetDAO petDAO = new PetDAO();
    private final AdoptionRequestDAO requestDAO = new AdoptionRequestDAO();
    private final AdoptionHistoryDAO historyDAO = new AdoptionHistoryDAO();
    private final UserDAO userDAO = new UserDAO();

    // UI Navigation & Layout
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    // Browse UI
    private JTable petTable;
    private DefaultTableModel petTableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbSpeciesFilter;
    private JComboBox<String> cbSizeFilter;

    // My Requests UI
    private JTable requestsTable;
    private DefaultTableModel requestsTableModel;

    // My Adopted Pets UI
    private JTable historyTable;
    private DefaultTableModel historyTableModel;

    // Profile UI
    private JTextField txtProfileName;
    private JTextField txtProfilePhone;
    private JTextField txtProfileAddress;
    private JPasswordField txtProfilePass;

    public AdopterDashboard(User user) {
        this.currentUser = user;
        setTitle("Pet Adoption Monitoring System - Adopter Portal (" + user.getFullName() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);
        initComponents();
        loadAllData();
    }

    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout());

        // 1. Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIUtils.COLOR_ACCENT_DARK);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("🐾 Pet Adoption Portal | Find Your Forever Companion");
        lblTitle.setFont(UIUtils.FONT_HEADER_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel lblUserInfo = new JLabel("Welcome, " + currentUser.getFullName());
        lblUserInfo.setFont(UIUtils.FONT_BODY_BOLD);
        lblUserInfo.setForeground(new Color(235, 250, 240));

        JButton btnLogout = UIUtils.createButton("Logout", UIUtils.COLOR_DANGER, Color.WHITE);
        btnLogout.addActionListener(e -> logout());

        userPanel.add(lblUserInfo);
        userPanel.add(btnLogout);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Sidebar
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(UIUtils.COLOR_DARK_SIDEBAR);
        sidebarPanel.setPreferredSize(new Dimension(210, 0));
        sidebarPanel.setBorder(new EmptyBorder(15, 10, 15, 10));

        JButton btnNavBrowse = UIUtils.createNavButton("🔍 Browse Pets");
        JButton btnNavMyRequests = UIUtils.createNavButton("📋 My Applications");
        JButton btnNavMyAdoptions = UIUtils.createNavButton("🏆 My Adopted Pets");
        JButton btnNavProfile = UIUtils.createNavButton("👤 My Profile");

        btnNavBrowse.addActionListener(e -> showCard("BROWSE"));
        btnNavMyRequests.addActionListener(e -> showCard("REQUESTS"));
        btnNavMyAdoptions.addActionListener(e -> showCard("ADOPTIONS"));
        btnNavProfile.addActionListener(e -> showCard("PROFILE"));

        sidebarPanel.add(btnNavBrowse);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(btnNavMyRequests);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(btnNavMyAdoptions);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(btnNavProfile);
        sidebarPanel.add(Box.createVerticalGlue());

        rootPanel.add(sidebarPanel, BorderLayout.WEST);

        // 3. Center Cards
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(UIUtils.COLOR_BG_LIGHT);

        mainContentPanel.add(createBrowsePetsPanel(), "BROWSE");
        mainContentPanel.add(createMyRequestsPanel(), "REQUESTS");
        mainContentPanel.add(createMyAdoptedPetsPanel(), "ADOPTIONS");
        mainContentPanel.add(createProfilePanel(), "PROFILE");

        rootPanel.add(mainContentPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);
    }

    private void showCard(String name) {
        cardLayout.show(mainContentPanel, name);
        if ("BROWSE".equals(name)) refreshBrowsePets();
        else if ("REQUESTS".equals(name)) refreshMyRequests();
        else if ("ADOPTIONS".equals(name)) refreshMyAdoptedPets();
        else if ("PROFILE".equals(name)) loadProfileData();
    }

    // ==========================================
    // VIEW 1: Browse Pets Panel
    // ==========================================
    private JPanel createBrowsePetsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIUtils.COLOR_BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Filter Bar
        JPanel topBar = UIUtils.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(UIUtils.FONT_BODY_BOLD);
        txtSearch = new JTextField(12);
        UIUtils.styleTextField(txtSearch);

        JLabel lblSpecies = new JLabel("Species:");
        lblSpecies.setFont(UIUtils.FONT_BODY_BOLD);
        cbSpeciesFilter = new JComboBox<>(new String[]{"ALL", "Dog", "Cat", "Rabbit", "Bird", "Other"});

        JLabel lblSize = new JLabel("Size:");
        lblSize.setFont(UIUtils.FONT_BODY_BOLD);
        cbSizeFilter = new JComboBox<>(new String[]{"ALL", "Small", "Medium", "Large"});

        JButton btnFilter = UIUtils.createButton("Search", UIUtils.COLOR_PRIMARY, Color.WHITE);
        JButton btnReset = UIUtils.createButton("Reset", UIUtils.COLOR_BORDER, UIUtils.COLOR_TEXT_MAIN);

        btnFilter.addActionListener(e -> searchBrowsePets());
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cbSpeciesFilter.setSelectedIndex(0);
            cbSizeFilter.setSelectedIndex(0);
            refreshBrowsePets();
        });

        topBar.add(lblSearch);
        topBar.add(txtSearch);
        topBar.add(lblSpecies);
        topBar.add(cbSpeciesFilter);
        topBar.add(lblSize);
        topBar.add(cbSizeFilter);
        topBar.add(btnFilter);
        topBar.add(btnReset);

        panel.add(topBar, BorderLayout.NORTH);

        // Pet Table
        String[] cols = {"Pet ID", "Name", "Species", "Breed", "Age (yrs)", "Gender", "Size", "Status", "Health & Vaccines", "Description"};
        petTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        petTable = new JTable(petTableModel);
        UIUtils.styleTable(petTable);
        petTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        petTable.getColumnModel().getColumn(7).setCellRenderer(new UIUtils.StatusBadgeRenderer());

        panel.add(new JScrollPane(petTable), BorderLayout.CENTER);

        // Bottom Action
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomBar.setBackground(UIUtils.COLOR_BG_LIGHT);

        JButton btnAdopt = UIUtils.createButton("🐾 Apply to Adopt Selected Pet", UIUtils.COLOR_ACCENT, Color.WHITE);
        btnAdopt.setPreferredSize(new Dimension(240, 36));
        btnAdopt.addActionListener(e -> openAdoptionRequestDialog());

        bottomBar.add(btnAdopt);
        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    private void searchBrowsePets() {
        String kw = txtSearch.getText().trim();
        String species = (String) cbSpeciesFilter.getSelectedItem();
        String size = (String) cbSizeFilter.getSelectedItem();

        try {
            List<Pet> list = petDAO.searchPets(kw, species, null, size, null);
            populateBrowseTable(list);
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error: " + ex.getMessage(), "Search Error");
        }
    }

    private void refreshBrowsePets() {
        try {
            List<Pet> list = petDAO.getAllPets();
            populateBrowseTable(list);
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error loading pets: " + ex.getMessage(), "Error");
        }
    }

    private void populateBrowseTable(List<Pet> list) {
        petTableModel.setRowCount(0);
        for (Pet p : list) {
            petTableModel.addRow(new Object[]{
                    p.getPetId(),
                    p.getName(),
                    p.getSpecies(),
                    p.getBreed(),
                    p.getAge(),
                    p.getGender(),
                    p.getSize(),
                    p.getStatus(),
                    p.getHealthStatus(),
                    p.getDescription()
            });
        }
    }

    private void openAdoptionRequestDialog() {
        int row = petTable.getSelectedRow();
        if (row < 0) {
            UIUtils.showError(this, "Please select a pet from the table first.", "Select a Pet");
            return;
        }

        int petId = (Integer) petTableModel.getValueAt(row, 0);
        String petName = (String) petTableModel.getValueAt(row, 1);
        String status = (String) petTableModel.getValueAt(row, 7);

        if ("Adopted".equalsIgnoreCase(status)) {
            UIUtils.showError(this, "'" + petName + "' has already been adopted and is no longer available.", "Unavailable");
            return;
        }

        try {
            if (requestDAO.hasActivePendingRequest(currentUser.getUserId(), petId)) {
                UIUtils.showError(this, "You already have an active pending application for " + petName + ".", "Duplicate Request");
                return;
            }

            Pet pet = petDAO.getPetById(petId);
            if (pet != null) {
                AdoptionRequestDialog dialog = new AdoptionRequestDialog(this, pet, currentUser);
                dialog.setVisible(true);

                if (dialog.isSubmitted()) {
                    requestDAO.submitRequest(dialog.getRequest());
                    UIUtils.showInfo(this, "Your adoption application for " + pet.getName() + " has been submitted!\nThe shelter admin will review your application soon.", "Application Submitted");
                    refreshBrowsePets();
                    refreshMyRequests();
                }
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Failed to submit application: " + ex.getMessage(), "Error");
        }
    }

    // ==========================================
    // VIEW 2: My Applications Panel
    // ==========================================
    private JPanel createMyRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIUtils.COLOR_BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topBar = UIUtils.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel lblTitle = new JLabel("My Adoption Applications & Status");
        lblTitle.setFont(UIUtils.FONT_SUBHEADER);
        topBar.add(lblTitle);

        JButton btnRefresh = UIUtils.createButton("🔄 Refresh", UIUtils.COLOR_BORDER, UIUtils.COLOR_TEXT_MAIN);
        btnRefresh.addActionListener(e -> refreshMyRequests());
        topBar.add(btnRefresh);

        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Req ID", "Submission Date", "Pet Name", "Species / Breed", "Status", "Living Situation", "Other Pets?", "Admin Remarks", "Reviewed Date"};
        requestsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        requestsTable = new JTable(requestsTableModel);
        UIUtils.styleTable(requestsTable);
        requestsTable.getColumnModel().getColumn(4).setCellRenderer(new UIUtils.StatusBadgeRenderer());

        panel.add(new JScrollPane(requestsTable), BorderLayout.CENTER);
        return panel;
    }

    private void refreshMyRequests() {
        try {
            List<AdoptionRequest> list = requestDAO.getRequestsByUser(currentUser.getUserId());
            requestsTableModel.setRowCount(0);
            for (AdoptionRequest r : list) {
                requestsTableModel.addRow(new Object[]{
                        r.getRequestId(),
                        r.getRequestDate(),
                        r.getPetName(),
                        r.getPetSpecies() + " (" + r.getPetBreed() + ")",
                        r.getStatus(),
                        r.getLivingSituation(),
                        r.isHasOtherPets() ? "Yes" : "No",
                        r.getAdminRemarks() == null ? "Pending Review" : r.getAdminRemarks(),
                        r.getReviewDate() == null ? "-" : r.getReviewDate().toString()
                });
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error loading requests: " + ex.getMessage(), "Error");
        }
    }

    // ==========================================
    // VIEW 3: My Adopted Pets Panel
    // ==========================================
    private JPanel createMyAdoptedPetsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIUtils.COLOR_BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topBar = UIUtils.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel lblTitle = new JLabel("🏆 My Adopted Forever Pets");
        lblTitle.setFont(UIUtils.FONT_SUBHEADER);
        topBar.add(lblTitle);

        JButton btnRefresh = UIUtils.createButton("🔄 Refresh", UIUtils.COLOR_BORDER, UIUtils.COLOR_TEXT_MAIN);
        btnRefresh.addActionListener(e -> refreshMyAdoptedPets());
        topBar.add(btnRefresh);

        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Record ID", "Adoption Date", "Pet Name", "Species", "Breed", "Handover Status", "Notes / Remarks"};
        historyTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        historyTable = new JTable(historyTableModel);
        UIUtils.styleTable(historyTable);

        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        return panel;
    }

    private void refreshMyAdoptedPets() {
        try {
            List<AdoptionHistory> list = historyDAO.getHistoryByUser(currentUser.getUserId());
            historyTableModel.setRowCount(0);
            for (AdoptionHistory h : list) {
                historyTableModel.addRow(new Object[]{
                        h.getHistoryId(),
                        h.getAdoptionDate(),
                        h.getPetName(),
                        h.getPetSpecies(),
                        h.getPetBreed(),
                        h.getHandoverStatus(),
                        h.getRemarks()
                });
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error loading adoptions: " + ex.getMessage(), "Error");
        }
    }

    // ==========================================
    // VIEW 4: Profile Panel
    // ==========================================
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        panel.setBackground(UIUtils.COLOR_BG_LIGHT);

        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(500, 420));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("👤 My Profile Information");
        lblTitle.setFont(UIUtils.FONT_SUBHEADER);
        lblTitle.setForeground(UIUtils.COLOR_PRIMARY_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(lblTitle, gbc);

        txtProfileName = new JTextField();
        txtProfilePhone = new JTextField();
        txtProfileAddress = new JTextField();
        txtProfilePass = new JPasswordField();

        UIUtils.styleTextField(txtProfileName);
        UIUtils.styleTextField(txtProfilePhone);
        UIUtils.styleTextField(txtProfileAddress);
        txtProfilePass.setFont(UIUtils.FONT_BODY);

        int row = 1;
        gbc.gridwidth = 1;
        addProfileRow(card, gbc, row++, "Full Name", txtProfileName);
        addProfileRow(card, gbc, row++, "Phone", txtProfilePhone);
        addProfileRow(card, gbc, row++, "Address", txtProfileAddress);
        addProfileRow(card, gbc, row++, "Password", txtProfilePass);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        JButton btnSave = UIUtils.createButton("Update Profile", UIUtils.COLOR_ACCENT, Color.WHITE);
        btnSave.setPreferredSize(new Dimension(200, 36));
        btnSave.addActionListener(e -> updateProfile());
        card.add(btnSave, gbc);

        panel.add(card);
        return panel;
    }

    private void addProfileRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtils.FONT_BODY_BOLD);
        lbl.setForeground(UIUtils.COLOR_TEXT_MAIN);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    private void loadProfileData() {
        txtProfileName.setText(currentUser.getFullName());
        txtProfilePhone.setText(currentUser.getPhone());
        txtProfileAddress.setText(currentUser.getAddress());
        txtProfilePass.setText(currentUser.getPassword());
    }

    private void updateProfile() {
        String name = txtProfileName.getText().trim();
        String phone = txtProfilePhone.getText().trim();
        String address = txtProfileAddress.getText().trim();
        String pass = new String(txtProfilePass.getPassword()).trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || pass.isEmpty()) {
            UIUtils.showError(this, "Please fill all fields.", "Validation Error");
            return;
        }

        currentUser.setFullName(name);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);
        currentUser.setPassword(pass);

        try {
            boolean ok = userDAO.updateProfile(currentUser);
            if (ok) {
                UIUtils.showInfo(this, "Profile updated successfully!", "Success");
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error updating profile: " + ex.getMessage(), "Error");
        }
    }

    private void loadAllData() {
        refreshBrowsePets();
        refreshMyRequests();
        refreshMyAdoptedPets();
        loadProfileData();
    }

    private void logout() {
        if (UIUtils.showConfirm(this, "Are you sure you want to log out?", "Confirm Logout")) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
