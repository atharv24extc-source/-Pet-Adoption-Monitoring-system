package com.petadoption.model;

import java.util.Map;

/**
 * Model class holding high-level dashboard metrics and reporting data.
 */
public class ReportSummary {
    private int totalPets;
    private int availablePets;
    private int pendingPets;
    private int adoptedPets;
    private int totalUsers;
    private int totalRequests;
    private int pendingRequests;
    private int approvedRequests;
    private int rejectedRequests;
    private Map<String, Integer> petsBySpecies;

    public ReportSummary() {
    }

    public int getTotalPets() {
        return totalPets;
    }

    public void setTotalPets(int totalPets) {
        this.totalPets = totalPets;
    }

    public int getAvailablePets() {
        return availablePets;
    }

    public void setAvailablePets(int availablePets) {
        this.availablePets = availablePets;
    }

    public int getPendingPets() {
        return pendingPets;
    }

    public void setPendingPets(int pendingPets) {
        this.pendingPets = pendingPets;
    }

    public int getAdoptedPets() {
        return adoptedPets;
    }

    public void setAdoptedPets(int adoptedPets) {
        this.adoptedPets = adoptedPets;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    public int getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(int pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public int getApprovedRequests() {
        return approvedRequests;
    }

    public void setApprovedRequests(int approvedRequests) {
        this.approvedRequests = approvedRequests;
    }

    public int getRejectedRequests() {
        return rejectedRequests;
    }

    public void setRejectedRequests(int rejectedRequests) {
        this.rejectedRequests = rejectedRequests;
    }

    public Map<String, Integer> getPetsBySpecies() {
        return petsBySpecies;
    }

    public void setPetsBySpecies(Map<String, Integer> petsBySpecies) {
        this.petsBySpecies = petsBySpecies;
    }
}
