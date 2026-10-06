package com.petadoption.model;

import java.sql.Timestamp;

/**
 * Model class representing a Pet available or listed in the adoption system.
 */
public class Pet {
    private int petId;
    private String name;
    private String species;      // e.g. "Dog", "Cat", "Rabbit", "Bird", "Other"
    private String breed;
    private int age;             // in years
    private String gender;       // "Male", "Female"
    private String size;         // "Small", "Medium", "Large"
    private String status;       // "Available", "Pending", "Adopted"
    private String healthStatus;
    private String description;
    private Timestamp addedDate;

    public Pet() {
    }

    public Pet(int petId, String name, String species, String breed, int age, String gender, String size, String status, String healthStatus, String description, Timestamp addedDate) {
        this.petId = petId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.size = size;
        this.status = status;
        this.healthStatus = healthStatus;
        this.description = description;
        this.addedDate = addedDate;
    }

    public Pet(String name, String species, String breed, int age, String gender, String size, String status, String healthStatus, String description) {
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.size = size;
        this.status = status;
        this.healthStatus = healthStatus;
        this.description = description;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Timestamp addedDate) {
        this.addedDate = addedDate;
    }

    @Override
    public String toString() {
        return name + " (" + species + " - " + breed + ", " + age + " yrs)";
    }
}
