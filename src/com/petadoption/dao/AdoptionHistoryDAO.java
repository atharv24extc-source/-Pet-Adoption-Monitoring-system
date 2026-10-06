package com.petadoption.dao;

import com.petadoption.model.AdoptionHistory;
import com.petadoption.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Adoption History log.
 */
public class AdoptionHistoryDAO {

    public List<AdoptionHistory> getAllHistory() throws SQLException {
        List<AdoptionHistory> list = new ArrayList<>();
        String sql = "SELECT h.*, u.full_name AS user_name, u.email AS user_email, u.phone AS user_phone, " +
                "p.name AS pet_name, p.species AS pet_species, p.breed AS pet_breed " +
                "FROM adoption_history h " +
                "JOIN users u ON h.user_id = u.user_id " +
                "JOIN pets p ON h.pet_id = p.pet_id " +
                "ORDER BY h.adoption_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToHistory(rs));
            }
        }
        return list;
    }

    public List<AdoptionHistory> getHistoryByUser(int userId) throws SQLException {
        List<AdoptionHistory> list = new ArrayList<>();
        String sql = "SELECT h.*, u.full_name AS user_name, u.email AS user_email, u.phone AS user_phone, " +
                "p.name AS pet_name, p.species AS pet_species, p.breed AS pet_breed " +
                "FROM adoption_history h " +
                "JOIN users u ON h.user_id = u.user_id " +
                "JOIN pets p ON h.pet_id = p.pet_id " +
                "WHERE h.user_id = ? " +
                "ORDER BY h.adoption_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToHistory(rs));
                }
            }
        }
        return list;
    }

    private AdoptionHistory mapResultSetToHistory(ResultSet rs) throws SQLException {
        AdoptionHistory h = new AdoptionHistory();
        h.setHistoryId(rs.getInt("history_id"));
        int reqId = rs.getInt("request_id");
        h.setRequestId(rs.wasNull() ? null : reqId);
        h.setUserId(rs.getInt("user_id"));
        h.setPetId(rs.getInt("pet_id"));
        h.setAdoptionDate(rs.getTimestamp("adoption_date"));
        h.setHandoverStatus(rs.getString("handover_status"));
        h.setRemarks(rs.getString("remarks"));

        h.setUserName(rs.getString("user_name"));
        h.setUserEmail(rs.getString("user_email"));
        h.setUserPhone(rs.getString("user_phone"));
        h.setPetName(rs.getString("pet_name"));
        h.setPetSpecies(rs.getString("pet_species"));
        h.setPetBreed(rs.getString("pet_breed"));
        return h;
    }
}
