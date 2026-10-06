package com.petadoption.dao;

import com.petadoption.model.ReportSummary;
import com.petadoption.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data Access Object for aggregated summary reports and metrics.
 */
public class ReportDAO {

    public ReportSummary getReportSummary() throws SQLException {
        ReportSummary summary = new ReportSummary();

        try (Connection conn = DBConnection.getConnection()) {
            // 1. Pets Counts
            String petSql = "SELECT " +
                    "COUNT(*) AS total, " +
                    "SUM(CASE WHEN status = 'Available' THEN 1 ELSE 0 END) AS available, " +
                    "SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END) AS pending, " +
                    "SUM(CASE WHEN status = 'Adopted' THEN 1 ELSE 0 END) AS adopted " +
                    "FROM pets";
            try (PreparedStatement ps = conn.prepareStatement(petSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    summary.setTotalPets(rs.getInt("total"));
                    summary.setAvailablePets(rs.getInt("available"));
                    summary.setPendingPets(rs.getInt("pending"));
                    summary.setAdoptedPets(rs.getInt("adopted"));
                }
            }

            // 2. Users Count
            String userSql = "SELECT COUNT(*) FROM users WHERE role = 'ADOPTER'";
            try (PreparedStatement ps = conn.prepareStatement(userSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    summary.setTotalUsers(rs.getInt(1));
                }
            }

            // 3. Requests Counts
            String reqSql = "SELECT " +
                    "COUNT(*) AS total, " +
                    "SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS pending, " +
                    "SUM(CASE WHEN status = 'APPROVED' THEN 1 ELSE 0 END) AS approved, " +
                    "SUM(CASE WHEN status = 'REJECTED' THEN 1 ELSE 0 END) AS rejected " +
                    "FROM adoption_requests";
            try (PreparedStatement ps = conn.prepareStatement(reqSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    summary.setTotalRequests(rs.getInt("total"));
                    summary.setPendingRequests(rs.getInt("pending"));
                    summary.setApprovedRequests(rs.getInt("approved"));
                    summary.setRejectedRequests(rs.getInt("rejected"));
                }
            }

            // 4. Species Breakdown
            String speciesSql = "SELECT species, COUNT(*) AS count FROM pets GROUP BY species ORDER BY count DESC";
            Map<String, Integer> speciesMap = new LinkedHashMap<>();
            try (PreparedStatement ps = conn.prepareStatement(speciesSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    speciesMap.put(rs.getString("species"), rs.getInt("count"));
                }
            }
            summary.setPetsBySpecies(speciesMap);
        }

        return summary;
    }
}
