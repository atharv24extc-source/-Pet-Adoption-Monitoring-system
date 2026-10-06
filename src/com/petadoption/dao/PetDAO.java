package com.petadoption.dao;

import com.petadoption.model.Pet;
import com.petadoption.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Pets (Create, Read, Update, Delete, Search & Filters).
 */
public class PetDAO {

    public boolean addPet(Pet pet) throws SQLException {
        String sql = "INSERT INTO pets (name, species, breed, age, gender, size, status, health_status, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pet.getName());
            ps.setString(2, pet.getSpecies());
            ps.setString(3, pet.getBreed());
            ps.setInt(4, pet.getAge());
            ps.setString(5, pet.getGender());
            ps.setString(6, pet.getSize());
            ps.setString(7, pet.getStatus() == null ? "Available" : pet.getStatus());
            ps.setString(8, pet.getHealthStatus());
            ps.setString(9, pet.getDescription());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        pet.setPetId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updatePet(Pet pet) throws SQLException {
        String sql = "UPDATE pets SET name = ?, species = ?, breed = ?, age = ?, gender = ?, size = ?, status = ?, health_status = ?, description = ? WHERE pet_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pet.getName());
            ps.setString(2, pet.getSpecies());
            ps.setString(3, pet.getBreed());
            ps.setInt(4, pet.getAge());
            ps.setString(5, pet.getGender());
            ps.setString(6, pet.getSize());
            ps.setString(7, pet.getStatus());
            ps.setString(8, pet.getHealthStatus());
            ps.setString(9, pet.getDescription());
            ps.setInt(10, pet.getPetId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deletePet(int petId) throws SQLException {
        String sql = "DELETE FROM pets WHERE pet_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, petId);
            return ps.executeUpdate() > 0;
        }
    }

    public Pet getPetById(int petId) throws SQLException {
        String sql = "SELECT * FROM pets WHERE pet_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, petId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPet(rs);
                }
            }
        }
        return null;
    }

    public List<Pet> getAllPets() throws SQLException {
        List<Pet> list = new ArrayList<>();
        String sql = "SELECT * FROM pets ORDER BY pet_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPet(rs));
            }
        }
        return list;
    }

    public List<Pet> searchPets(String keyword, String species, String status, String size, Integer maxAge) throws SQLException {
        List<Pet> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM pets WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (name LIKE ? OR breed LIKE ? OR description LIKE ?) ");
            String term = "%" + keyword.trim() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }

        if (species != null && !species.equalsIgnoreCase("ALL") && !species.trim().isEmpty()) {
            sql.append("AND species = ? ");
            params.add(species.trim());
        }

        if (status != null && !status.equalsIgnoreCase("ALL") && !status.trim().isEmpty()) {
            sql.append("AND status = ? ");
            params.add(status.trim());
        }

        if (size != null && !size.equalsIgnoreCase("ALL") && !size.trim().isEmpty()) {
            sql.append("AND size = ? ");
            params.add(size.trim());
        }

        if (maxAge != null && maxAge > 0) {
            sql.append("AND age <= ? ");
            params.add(maxAge);
        }

        sql.append("ORDER BY pet_id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPet(rs));
                }
            }
        }
        return list;
    }

    public boolean updatePetStatus(int petId, String status) throws SQLException {
        String sql = "UPDATE pets SET status = ? WHERE pet_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, petId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<String> getDistinctSpecies() throws SQLException {
        List<String> speciesList = new ArrayList<>();
        String sql = "SELECT DISTINCT species FROM pets ORDER BY species ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String sp = rs.getString("species");
                if (sp != null && !sp.isEmpty()) {
                    speciesList.add(sp);
                }
            }
        }
        return speciesList;
    }

    private Pet mapResultSetToPet(ResultSet rs) throws SQLException {
        Pet p = new Pet();
        p.setPetId(rs.getInt("pet_id"));
        p.setName(rs.getString("name"));
        p.setSpecies(rs.getString("species"));
        p.setBreed(rs.getString("breed"));
        p.setAge(rs.getInt("age"));
        p.setGender(rs.getString("gender"));
        p.setSize(rs.getString("size"));
        p.setStatus(rs.getString("status"));
        p.setHealthStatus(rs.getString("health_status"));
        p.setDescription(rs.getString("description"));
        p.setAddedDate(rs.getTimestamp("added_date"));
        return p;
    }
}
