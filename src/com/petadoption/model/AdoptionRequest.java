package com.petadoption.model;

import java.sql.Timestamp;

/**
 * Model class representing an adoption request submitted by an adopter.
 */
public class AdoptionRequest {
    private int requestId;
    private int userId;
    private int petId;
    private Timestamp requestDate;
    private String status;           // "PENDING", "APPROVED", "REJECTED"
    private String livingSituation;  // e.g. "Apartment", "House with yard"
    private boolean hasOtherPets;
    private String notes;
    private String adminRemarks;
    private Timestamp reviewDate;

    // Joined fields for display convenience
    private String userName;
    private String userEmail;
    private String userPhone;
    private String petName;
    private String petSpecies;
    private String petBreed;

    public AdoptionRequest() {
    }

    public AdoptionRequest(int requestId, int userId, int petId, Timestamp requestDate, String status, String livingSituation, boolean hasOtherPets, String notes, String adminRemarks, Timestamp reviewDate) {
        this.requestId = requestId;
        this.userId = userId;
        this.petId = petId;
        this.requestDate = requestDate;
        this.status = status;
        this.livingSituation = livingSituation;
        this.hasOtherPets = hasOtherPets;
        this.notes = notes;
        this.adminRemarks = adminRemarks;
        this.reviewDate = reviewDate;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLivingSituation() {
        return livingSituation;
    }

    public void setLivingSituation(String livingSituation) {
        this.livingSituation = livingSituation;
    }

    public boolean isHasOtherPets() {
        return hasOtherPets;
    }

    public void setHasOtherPets(boolean hasOtherPets) {
        this.hasOtherPets = hasOtherPets;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }

    public Timestamp getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Timestamp reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetSpecies() {
        return petSpecies;
    }

    public void setPetSpecies(String petSpecies) {
        this.petSpecies = petSpecies;
    }

    public String getPetBreed() {
        return petBreed;
    }

    public void setPetBreed(String petBreed) {
        this.petBreed = petBreed;
    }
}
