package com.petadoption.dao;

import com.petadoption.model.AdoptionHistory;
import com.petadoption.model.AdoptionRequest;
import com.petadoption.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling Adoption Requests and review workflow.
 */
public class AdoptionRequestDAO {

    public boolean submitRequest(AdoptionRequest req) throws SQLException {
        String insertSql = "INSERT INTO adoption_requests (user_id, pet_id, living_situation, has_other_pets, notes, status) VALUES (?, ?, ?, ?, ?, 'PENDING')";
        String updatePetSql = "UPDATE pets SET status = 'Pending' WHERE pet_id = ? AND status = 'Available'";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Transaction

            // 1. Insert Request
            try (PreparedStatement psReq = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                psReq.setInt(1, req.getUserId());
                psReq.setInt(2, req.getPetId());
                psReq.setString(3, req.getLivingSituation());
                psReq.setBoolean(4, req.isHasOtherPets());
                psReq.setString(5, req.getNotes());

                int affected = psReq.executeUpdate();
                if (affected <= 0) {
                    conn.rollback();
                    return false;
                }
                try (ResultSet rs = psReq.getGeneratedKeys()) {
                    if (rs.next()) {
                        req.setRequestId(rs.getInt(1));
                    }
                }
            }

            // 2. Update Pet Status to Pending
            try (PreparedStatement psPet = conn.prepareStatement(updatePetSql)) {
                psPet.setInt(1, req.getPetId());
                psPet.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
            }
            throw ex;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public boolean hasActivePendingRequest(int userId, int petId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM adoption_requests WHERE user_id = ? AND pet_id = ? AND status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, petId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<AdoptionRequest> getRequestsByUser(int userId) throws SQLException {
        List<AdoptionRequest> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name AS user_name, u.email AS user_email, u.phone AS user_phone, " +
                "p.name AS pet_name, p.species AS pet_species, p.breed AS pet_breed " +
                "FROM adoption_requests r " +
                "JOIN users u ON r.user_id = u.user_id " +
                "JOIN pets p ON r.pet_id = p.pet_id " +
                "WHERE r.user_id = ? ORDER BY r.request_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToRequest(rs));
                }
            }
        }
        return list;
    }

    public List<AdoptionRequest> getAllRequests(String statusFilter) throws SQLException {
        List<AdoptionRequest> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, u.full_name AS user_name, u.email AS user_email, u.phone AS user_phone, " +
                        "p.name AS pet_name, p.species AS pet_species, p.breed AS pet_breed " +
                        "FROM adoption_requests r " +
                        "JOIN users u ON r.user_id = u.user_id " +
                        "JOIN pets p ON r.pet_id = p.pet_id ");

        if (statusFilter != null && !statusFilter.equalsIgnoreCase("ALL") && !statusFilter.trim().isEmpty()) {
            sql.append("WHERE r.status = ? ");
        }

        sql.append("ORDER BY r.request_id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (statusFilter != null && !statusFilter.equalsIgnoreCase("ALL") && !statusFilter.trim().isEmpty()) {
                ps.setString(1, statusFilter.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToRequest(rs));
                }
            }
        }
        return list;
    }

    /**
     * Approves an adoption request, marks pet as Adopted, records adoption history,
     * and rejects any other pending requests for that same pet.
     */
    public boolean approveRequest(int requestId, int userId, int petId, String remarks) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Update this request status to APPROVED
            String updateReqSql = "UPDATE adoption_requests SET status = 'APPROVED', admin_remarks = ?, review_date = NOW() WHERE request_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateReqSql)) {
                ps.setString(1, remarks);
                ps.setInt(2, requestId);
                ps.executeUpdate();
            }

            // 2. Mark pet as Adopted
            String updatePetSql = "UPDATE pets SET status = 'Adopted' WHERE pet_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updatePetSql)) {
                ps.setInt(1, petId);
                ps.executeUpdate();
            }

            // 3. Insert record into adoption_history
            String insertHistSql = "INSERT INTO adoption_history (request_id, user_id, pet_id, adoption_date, handover_status, remarks) VALUES (?, ?, ?, NOW(), 'Completed', ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertHistSql)) {
                ps.setInt(1, requestId);
                ps.setInt(2, userId);
                ps.setInt(3, petId);
                ps.setString(4, remarks != null ? remarks : "Adoption finalized.");
                ps.executeUpdate();
            }

            // 4. Automatically reject any other pending requests for this same pet
            String rejectOtherSql = "UPDATE adoption_requests SET status = 'REJECTED', admin_remarks = 'Pet adopted by another applicant.', review_date = NOW() WHERE pet_id = ? AND request_id != ? AND status = 'PENDING'";
            try (PreparedStatement ps = conn.prepareStatement(rejectOtherSql)) {
                ps.setInt(1, petId);
                ps.setInt(2, requestId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
            }
            throw ex;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Rejects an adoption request with admin remarks. If no other pending requests exist, sets pet back to Available.
     */
    public boolean rejectRequest(int requestId, int petId, String remarks) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Update request status to REJECTED
            String updateReqSql = "UPDATE adoption_requests SET status = 'REJECTED', admin_remarks = ?, review_date = NOW() WHERE request_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateReqSql)) {
                ps.setString(1, remarks);
                ps.setInt(2, requestId);
                ps.executeUpdate();
            }

            // 2. Check if there are other pending requests for this pet
            String countPendingSql = "SELECT COUNT(*) FROM adoption_requests WHERE pet_id = ? AND status = 'PENDING' AND request_id != ?";
            int otherPending = 0;
            try (PreparedStatement ps = conn.prepareStatement(countPendingSql)) {
                ps.setInt(1, petId);
                ps.setInt(2, requestId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        otherPending = rs.getInt(1);
                    }
                }
            }

            // If no other pending requests, mark pet Available again
            if (otherPending == 0) {
                String updatePetSql = "UPDATE pets SET status = 'Available' WHERE pet_id = ? AND status = 'Pending'";
                try (PreparedStatement ps = conn.prepareStatement(updatePetSql)) {
                    ps.setInt(1, petId);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
            }
            throw ex;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private AdoptionRequest mapResultSetToRequest(ResultSet rs) throws SQLException {
        AdoptionRequest r = new AdoptionRequest();
        r.setRequestId(rs.getInt("request_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setPetId(rs.getInt("pet_id"));
        r.setRequestDate(rs.getTimestamp("request_date"));
        r.setStatus(rs.getString("status"));
        r.setLivingSituation(rs.getString("living_situation"));
        r.setHasOtherPets(rs.getBoolean("has_other_pets"));
        r.setNotes(rs.getString("notes"));
        r.setAdminRemarks(rs.getString("admin_remarks"));
        r.setReviewDate(rs.getTimestamp("review_date"));

        r.setUserName(rs.getString("user_name"));
        r.setUserEmail(rs.getString("user_email"));
        r.setUserPhone(rs.getString("user_phone"));
        r.setPetName(rs.getString("pet_name"));
        r.setPetSpecies(rs.getString("pet_species"));
        r.setPetBreed(rs.getString("pet_breed"));
        return r;
    }
}
