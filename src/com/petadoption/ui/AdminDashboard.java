package com.petadoption.ui;

import com.petadoption.dao.AdoptionHistoryDAO;
import com.petadoption.dao.AdoptionRequestDAO;
import com.petadoption.dao.PetDAO;
import com.petadoption.dao.ReportDAO;
import com.petadoption.dao.UserDAO;
import com.petadoption.model.AdoptionHistory;
import com.petadoption.model.AdoptionRequest;
import com.petadoption.model.Pet;
import com.petadoption.model.ReportSummary;
import com.petadoption.model.User;
import com.petadoption.util.UIUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Main Administrator Dashboard window.
 */
public class AdminDashboard extends JFrame {
    private static final long serialVersionUID = 1L;
    private final User currentUser;

    // DAOs
    private final PetDAO petDAO = new PetDAO();
    private final AdoptionRequestDAO requestDAO = new AdoptionRequestDAO();
    private final AdoptionHistoryDAO historyDAO = new AdoptionHistoryDAO();
    private final ReportDAO reportDAO = new ReportDAO();
    private final UserDAO userDAO = new UserDAO();

    // UI Navigation & Views
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    // Pet Management UI
    private JTable petTable;
    private DefaultTableModel petTableModel;
    private JTextField txtPetSearch;
    private JComboBox<String> cbPetSpeciesFilter;
    private JComboBox<String> cbPetStatusFilter;

    // Requests UI
    private JTable requestTable;
    private DefaultTableModel requestTableModel;
    private JComboBox<String> cbRequestStatusFilter;

    // History UI
    private JTable historyTable;
    private DefaultTableModel historyTableModel;

    // Users UI
    private JTable userTable;
    private DefaultTableModel userTableModel;

    // Reports UI Components
    private JLabel lblStatTotalPets;
    private JLabel lblStatAvailable;
    private JLabel lblStatAdopted;
    private JLabel lblStatPendingReq;
    private JLabel lblStatUsers;
    private DefaultTableModel speciesReportModel;

    public AdminDashboard(User user) {
        this.currentUser = user;
        setTitle("Pet Adoption Monitoring System - Administrator Dashboard (" + user.getFullName() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        initComponents();
        loadAllData();
    }

    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout());

        // 1. Top Header Bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIUtils.COLOR_PRIMARY_DARK);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("🐾 Pet Adoption Monitoring System | Admin Console");
        lblTitle.setFont(UIUtils.FONT_HEADER_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel lblUserInfo = new JLabel("Logged in as: " + currentUser.getFullName() + " [ADMIN]");
        lblUserInfo.setFont(UIUtils.FONT_BODY_BOLD);
        lblUserInfo.setForeground(new Color(230, 240, 250));

        JButton btnLogout = UIUtils.createButton("Logout", UIUtils.COLOR_DANGER, Color.WHITE);
        btnLogout.addActionListener(e -> logout());

        userPanel.add(lblUserInfo);
        userPanel.add(btnLogout);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Left Navigation Sidebar
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(UIUtils.COLOR_DARK_SIDEBAR);
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setBorder(new EmptyBorder(15, 10, 15, 10));

        JButton btnNavPets = UIUtils.createNavButton("🐾 Pet Management");
        JButton btnNavRequests = UIUtils.createNavButton("📋 Adoption Requests");
        JButton btnNavHistory = UIUtils.createNavButton("📜 Adoption History");
        JButton btnNavReports = UIUtils.createNavButton("📊 Reports & Stats");
        JButton btnNavUsers = UIUtils.createNavButton("👥 User Accounts");

        btnNavPets.addActionListener(e -> showCard("PETS"));
        btnNavRequests.addActionListener(e -> showCard("REQUESTS"));
        btnNavHistory.addActionListener(e -> showCard("HISTORY"));
        btnNavReports.addActionListener(e -> showCard("REPORTS"));
        btnNavUsers.addActionListener(e -> showCard("USERS"));

        sidebarPanel.add(btnNavPets);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(btnNavRequests);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(btnNavHistory);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(btnNavReports);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(btnNavUsers);
        sidebarPanel.add(Box.createVerticalGlue());

        rootPanel.add(sidebarPanel, BorderLayout.WEST);

        // 3. Main Center Content (CardLayout)
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(UIUtils.COLOR_BG_LIGHT);

        mainContentPanel.add(createPetManagementPanel(), "PETS");
        mainContentPanel.add(createAdoptionRequestsPanel(), "REQUESTS");
        mainContentPanel.add(createAdoptionHistoryPanel(), "HISTORY");
        mainContentPanel.add(createReportsPanel(), "REPORTS");
        mainContentPanel.add(createUserAccountsPanel(), "USERS");

        rootPanel.add(mainContentPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);
    }

    private void showCard(String name) {
        cardLayout.show(mainContentPanel, name);
        if ("PETS".equals(name)) refreshPets();
        else if ("REQUESTS".equals(name)) refreshRequests();
        else if ("HISTORY".equals(name)) refreshHistory();
        else if ("REPORTS".equals(name)) refreshReports();
        else if ("USERS".equals(name)) refreshUsers();
    }

    // ==========================================
    // VIEW 1: Pet Management Panel
    // ==========================================
    private JPanel createPetManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIUtils.COLOR_BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Filter / Search Bar
        JPanel topBar = UIUtils.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(UIUtils.FONT_BODY_BOLD);
        txtPetSearch = new JTextField(12);
        UIUtils.styleTextField(txtPetSearch);

        JLabel lblSpecies = new JLabel("Species:");
        lblSpecies.setFont(UIUtils.FONT_BODY_BOLD);
        cbPetSpeciesFilter = new JComboBox<>(new String[]{"ALL", "Dog", "Cat", "Rabbit", "Bird", "Other"});

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(UIUtils.FONT_BODY_BOLD);
        cbPetStatusFilter = new JComboBox<>(new String[]{"ALL", "Available", "Pending", "Adopted"});

        JButton btnFilter = UIUtils.createButton("Filter", UIUtils.COLOR_PRIMARY, Color.WHITE);
        JButton btnReset = UIUtils.createButton("Reset", UIUtils.COLOR_BORDER, UIUtils.COLOR_TEXT_MAIN);

        btnFilter.addActionListener(e -> searchPets());
        btnReset.addActionListener(e -> {
            txtPetSearch.setText("");
            cbPetSpeciesFilter.setSelectedIndex(0);
            cbPetStatusFilter.setSelectedIndex(0);
            refreshPets();
        });

        topBar.add(lblSearch);
        topBar.add(txtPetSearch);
        topBar.add(lblSpecies);
        topBar.add(cbPetSpeciesFilter);
        topBar.add(lblStatus);
        topBar.add(cbPetStatusFilter);
        topBar.add(btnFilter);
        topBar.add(btnReset);

        panel.add(topBar, BorderLayout.NORTH);

        // Center Table
        String[] columns = {"ID", "Name", "Species", "Breed", "Age (yrs)", "Gender", "Size", "Status", "Health Status", "Description"};
        petTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        petTable = new JTable(petTableModel);
        UIUtils.styleTable(petTable);
        petTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        petTable.getColumnModel().getColumn(7).setCellRenderer(new UIUtils.StatusBadgeRenderer());

        JScrollPane scrollPane = new JScrollPane(petTable);
        scrollPane.setBorder(new LineBorder(UIUtils.COLOR_BORDER, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomBar.setBackground(UIUtils.COLOR_BG_LIGHT);

        JButton btnAdd = UIUtils.createButton("+ Add Pet", UIUtils.COLOR_ACCENT, Color.WHITE);
        JButton btnEdit = UIUtils.createButton("✏ Edit Pet", UIUtils.COLOR_PRIMARY, Color.WHITE);
        JButton btnDelete = UIUtils.createButton("🗑 Delete Pet", UIUtils.COLOR_DANGER, Color.WHITE);
        JButton btnRefresh = UIUtils.createButton("🔄 Refresh", UIUtils.COLOR_BORDER, UIUtils.COLOR_TEXT_MAIN);

        btnAdd.addActionListener(e -> openAddPetDialog());
        btnEdit.addActionListener(e -> openEditPetDialog());
        btnDelete.addActionListener(e -> performDeletePet());
        btnRefresh.addActionListener(e -> refreshPets());

        bottomBar.add(btnRefresh);
        bottomBar.add(btnDelete);
        bottomBar.add(btnEdit);
        bottomBar.add(btnAdd);

        panel.add(bottomBar, BorderLayout.SOUTH);
        return panel;
    }

    private void searchPets() {
        String kw = txtPetSearch.getText().trim();
        String species = (String) cbPetSpeciesFilter.getSelectedItem();
        String status = (String) cbPetStatusFilter.getSelectedItem();

        try {
            List<Pet> list = petDAO.searchPets(kw, species, status, null, null);
            populatePetTable(list);
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error searching pets: " + ex.getMessage(), "Error");
        }
    }

    private void refreshPets() {
        try {
            List<Pet> list = petDAO.getAllPets();
            populatePetTable(list);
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error loading pets: " + ex.getMessage(), "Error");
        }
    }

    private void populatePetTable(List<Pet> list) {
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

    private void openAddPetDialog() {
        PetDialog dialog = new PetDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            try {
                petDAO.addPet(dialog.getPet());
                UIUtils.showInfo(this, "Pet added successfully!", "Success");
                refreshPets();
            } catch (SQLException ex) {
                UIUtils.showError(this, "Failed to add pet: " + ex.getMessage(), "Error");
            }
        }
    }

    private void openEditPetDialog() {
        int row = petTable.getSelectedRow();
        if (row < 0) {
            UIUtils.showError(this, "Please select a pet from the table to edit.", "Selection Required");
            return;
        }
        int petId = (Integer) petTableModel.getValueAt(row, 0);
        try {
            Pet pet = petDAO.getPetById(petId);
            if (pet != null) {
                PetDialog dialog = new PetDialog(this, pet);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    petDAO.updatePet(dialog.getPet());
                    UIUtils.showInfo(this, "Pet details updated successfully!", "Success");
                    refreshPets();
                }
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error: " + ex.getMessage(), "Database Error");
        }
    }

    private void performDeletePet() {
        int row = petTable.getSelectedRow();
        if (row < 0) {
            UIUtils.showError(this, "Please select a pet from the table to delete.", "Selection Required");
            return;
        }
        int petId = (Integer) petTableModel.getValueAt(row, 0);
        String name = (String) petTableModel.getValueAt(row, 1);

        if (UIUtils.showConfirm(this, "Are you sure you want to permanently delete '" + name + "'?", "Confirm Delete")) {
            try {
                petDAO.deletePet(petId);
                UIUtils.showInfo(this, "Pet removed successfully.", "Deleted");
                refreshPets();
            } catch (SQLException ex) {
                UIUtils.showError(this, "Could not delete pet: " + ex.getMessage(), "Error");
            }
        }
    }

    // ==========================================
    // VIEW 2: Adoption Requests Panel
    // ==========================================
    private JPanel createAdoptionRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIUtils.COLOR_BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top filter bar
        JPanel topBar = UIUtils.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel lblFilter = new JLabel("Filter Request Status:");
        lblFilter.setFont(UIUtils.FONT_BODY_BOLD);
        cbRequestStatusFilter = new JComboBox<>(new String[]{"ALL", "PENDING", "APPROVED", "REJECTED"});
        cbRequestStatusFilter.addActionListener(e -> refreshRequests());

        JButton btnRefresh = UIUtils.createButton("🔄 Refresh", UIUtils.COLOR_BORDER, UIUtils.COLOR_TEXT_MAIN);
        btnRefresh.addActionListener(e -> refreshRequests());

        topBar.add(lblFilter);
        topBar.add(cbRequestStatusFilter);
        topBar.add(btnRefresh);

        panel.add(topBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Req ID", "Date", "Status", "Pet Name", "Species / Breed", "Applicant Name", "Contact", "Living Situation", "Other Pets?", "Admin Remarks"};
        requestTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        requestTable = new JTable(requestTableModel);
        UIUtils.styleTable(requestTable);
        requestTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        requestTable.getColumnModel().getColumn(2).setCellRenderer(new UIUtils.StatusBadgeRenderer());

        panel.add(new JScrollPane(requestTable), BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomBar.setBackground(UIUtils.COLOR_BG_LIGHT);

        JButton btnReview = UIUtils.createButton("📋 Review & Process Request", UIUtils.COLOR_PRIMARY, Color.WHITE);
        btnReview.addActionListener(e -> openReviewDialog());

        bottomBar.add(btnReview);
        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshRequests() {
        String filter = (String) cbRequestStatusFilter.getSelectedItem();
        try {
            List<AdoptionRequest> list = requestDAO.getAllRequests(filter);
            requestTableModel.setRowCount(0);
            for (AdoptionRequest r : list) {
                requestTableModel.addRow(new Object[]{
                        r.getRequestId(),
                        r.getRequestDate(),
                        r.getStatus(),
                        r.getPetName(),
                        r.getPetSpecies() + " (" + r.getPetBreed() + ")",
                        r.getUserName(),
                        r.getUserPhone() + " | " + r.getUserEmail(),
                        r.getLivingSituation(),
                        r.isHasOtherPets() ? "Yes" : "No",
                        r.getAdminRemarks() == null ? "-" : r.getAdminRemarks()
                });
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error loading requests: " + ex.getMessage(), "Error");
        }
    }

    private void openReviewDialog() {
        int row = requestTable.getSelectedRow();
        if (row < 0) {
            UIUtils.showError(this, "Please select an adoption request from the table to review.", "Selection Required");
            return;
        }

        int reqId = (Integer) requestTableModel.getValueAt(row, 0);
        try {
            List<AdoptionRequest> list = requestDAO.getAllRequests("ALL");
            AdoptionRequest targetReq = null;
            for (AdoptionRequest r : list) {
                if (r.getRequestId() == reqId) {
                    targetReq = r;
                    break;
                }
            }

            if (targetReq != null) {
                RequestReviewDialog dialog = new RequestReviewDialog(this, targetReq);
                dialog.setVisible(true);

                if (dialog.isApproved()) {
                    requestDAO.approveRequest(targetReq.getRequestId(), targetReq.getUserId(), targetReq.getPetId(), dialog.getAdminRemarks());
                    UIUtils.showInfo(this, "Adoption request APPROVED! Pet status updated to Adopted.", "Success");
                    refreshRequests();
                    refreshPets();
                } else if (dialog.isRejected()) {
                    requestDAO.rejectRequest(targetReq.getRequestId(), targetReq.getPetId(), dialog.getAdminRemarks());
                    UIUtils.showInfo(this, "Adoption request REJECTED.", "Updated");
                    refreshRequests();
                    refreshPets();
                }
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error processing review: " + ex.getMessage(), "Error");
        }
    }

    // ==========================================
    // VIEW 3: Adoption History Panel
    // ==========================================
    private JPanel createAdoptionHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIUtils.COLOR_BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topBar = UIUtils.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel lblTitle = new JLabel("Completed Adoption History Log");
        lblTitle.setFont(UIUtils.FONT_SUBHEADER);
        topBar.add(lblTitle);

        JButton btnRefresh = UIUtils.createButton("🔄 Refresh", UIUtils.COLOR_BORDER, UIUtils.COLOR_TEXT_MAIN);
        btnRefresh.addActionListener(e -> refreshHistory());
        topBar.add(btnRefresh);

        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"History ID", "Adoption Date", "Pet Name", "Species / Breed", "Adopter Name", "Adopter Email", "Adopter Phone", "Handover Status", "Remarks"};
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

    private void refreshHistory() {
        try {
            List<AdoptionHistory> list = historyDAO.getAllHistory();
            historyTableModel.setRowCount(0);
            for (AdoptionHistory h : list) {
                historyTableModel.addRow(new Object[]{
                        h.getHistoryId(),
                        h.getAdoptionDate(),
                        h.getPetName(),
                        h.getPetSpecies() + " (" + h.getPetBreed() + ")",
                        h.getUserName(),
                        h.getUserEmail(),
                        h.getUserPhone(),
                        h.getHandoverStatus(),
                        h.getRemarks()
                });
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error loading history: " + ex.getMessage(), "Error");
        }
    }

    // ==========================================
    // VIEW 4: Reports & Analytics Panel
    // ==========================================
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIUtils.COLOR_BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Metrics KPI Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 12, 12));
        statsPanel.setOpaque(false);

        lblStatTotalPets = new JLabel("0", SwingConstants.CENTER);
        lblStatAvailable = new JLabel("0", SwingConstants.CENTER);
        lblStatAdopted = new JLabel("0", SwingConstants.CENTER);
        lblStatPendingReq = new JLabel("0", SwingConstants.CENTER);
        lblStatUsers = new JLabel("0", SwingConstants.CENTER);

        statsPanel.add(createMetricCard("Total Pets Listed", lblStatTotalPets, UIUtils.COLOR_PRIMARY));
        statsPanel.add(createMetricCard("Available Pets", lblStatAvailable, UIUtils.COLOR_ACCENT));
        statsPanel.add(createMetricCard("Adopted Pets", lblStatAdopted, new Color(142, 68, 173)));
        statsPanel.add(createMetricCard("Pending Requests", lblStatPendingReq, UIUtils.COLOR_WARNING));
        statsPanel.add(createMetricCard("Registered Adopters", lblStatUsers, new Color(52, 73, 94)));

        panel.add(statsPanel, BorderLayout.NORTH);

        // Center Breakdown table
        JPanel centerPanel = UIUtils.createCardPanel();
        centerPanel.setLayout(new BorderLayout(10, 10));

        JLabel lblSpeciesTitle = new JLabel("🐾 Inventory Breakdown by Pet Species");
        lblSpeciesTitle.setFont(UIUtils.FONT_SUBHEADER);
        centerPanel.add(lblSpeciesTitle, BorderLayout.NORTH);

        String[] cols = {"Species", "Total Registered", "Percentage of Total"};
        speciesReportModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable speciesTable = new JTable(speciesReportModel);
        UIUtils.styleTable(speciesTable);
        centerPanel.add(new JScrollPane(speciesTable), BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Refresh Button
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setBackground(UIUtils.COLOR_BG_LIGHT);
        JButton btnRefresh = UIUtils.createButton("🔄 Refresh Reports", UIUtils.COLOR_PRIMARY, Color.WHITE);
        btnRefresh.addActionListener(e -> refreshReports());
        bottomBar.add(btnRefresh);
        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(new CompoundBorder(
                new LineBorder(accentColor, 2, true),
                new EmptyBorder(12, 10, 12, 10)
        ));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(UIUtils.FONT_BODY_BOLD);
        lblTitle.setForeground(UIUtils.COLOR_TEXT_MAIN);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(accentColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void refreshReports() {
        try {
            ReportSummary summary = reportDAO.getReportSummary();
            lblStatTotalPets.setText(String.valueOf(summary.getTotalPets()));
            lblStatAvailable.setText(String.valueOf(summary.getAvailablePets()));
            lblStatAdopted.setText(String.valueOf(summary.getAdoptedPets()));
            lblStatPendingReq.setText(String.valueOf(summary.getPendingRequests()));
            lblStatUsers.setText(String.valueOf(summary.getTotalUsers()));

            speciesReportModel.setRowCount(0);
            int total = summary.getTotalPets();
            for (Map.Entry<String, Integer> entry : summary.getPetsBySpecies().entrySet()) {
                double pct = total > 0 ? (entry.getValue() * 100.0 / total) : 0.0;
                speciesReportModel.addRow(new Object[]{
                        entry.getKey(),
                        entry.getValue(),
                        String.format("%.1f %%", pct)
                });
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error calculating reports: " + ex.getMessage(), "Error");
        }
    }

    // ==========================================
    // VIEW 5: User Accounts Panel
    // ==========================================
    private JPanel createUserAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIUtils.COLOR_BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topBar = UIUtils.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel lblTitle = new JLabel("System User Directory");
        lblTitle.setFont(UIUtils.FONT_SUBHEADER);
        topBar.add(lblTitle);

        JButton btnRefresh = UIUtils.createButton("🔄 Refresh", UIUtils.COLOR_BORDER, UIUtils.COLOR_TEXT_MAIN);
        btnRefresh.addActionListener(e -> refreshUsers());
        topBar.add(btnRefresh);

        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"User ID", "Username", "Role", "Full Name", "Email", "Phone", "Living Address", "Member Since"};
        userTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        userTable = new JTable(userTableModel);
        UIUtils.styleTable(userTable);
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        return panel;
    }

    private void refreshUsers() {
        try {
            List<User> list = userDAO.getAllUsers();
            userTableModel.setRowCount(0);
            for (User u : list) {
                userTableModel.addRow(new Object[]{
                        u.getUserId(),
                        u.getUsername(),
                        u.getRole(),
                        u.getFullName(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getAddress(),
                        u.getCreatedAt()
                });
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error loading users: " + ex.getMessage(), "Error");
        }
    }

    private void loadAllData() {
        refreshPets();
        refreshRequests();
        refreshHistory();
        refreshReports();
        refreshUsers();
    }

    private void logout() {
        if (UIUtils.showConfirm(this, "Are you sure you want to log out?", "Confirm Logout")) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
