package com.petadoption.model;

import java.sql.Timestamp;

/**
 * Model class representing historical log of finalized adoptions.
 */
public class AdoptionHistory {
    private int historyId;
    private Integer requestId;
    private int userId;
    private int petId;
    private Timestamp adoptionDate;
    private String handoverStatus;
    private String remarks;

    // Joined fields for reporting/viewing
    private String userName;
    private String userEmail;
    private String userPhone;
    private String petName;
    private String petSpecies;
    private String petBreed;

    public AdoptionHistory() {
    }

    public AdoptionHistory(int historyId, Integer requestId, int userId, int petId, Timestamp adoptionDate, String handoverStatus, String remarks) {
        this.historyId = historyId;
        this.requestId = requestId;
        this.userId = userId;
        this.petId = petId;
        this.adoptionDate = adoptionDate;
        this.handoverStatus = handoverStatus;
        this.remarks = remarks;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
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

    public Timestamp getAdoptionDate() {
        return adoptionDate;
    }

    public void setAdoptionDate(Timestamp adoptionDate) {
        this.adoptionDate = adoptionDate;
    }

    public String getHandoverStatus() {
        return handoverStatus;
    }

    public void setHandoverStatus(String handoverStatus) {
        this.handoverStatus = handoverStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
