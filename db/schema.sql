-- ==========================================================
-- Pet Adoption Monitoring System Database Schema
-- Database: pet_adoption_db
-- ==========================================================

CREATE DATABASE IF NOT EXISTS pet_adoption_db;
USE pet_adoption_db;

-- ----------------------------------------------------------
-- 1. Table: users
-- ----------------------------------------------------------
DROP TABLE IF EXISTS adoption_history;
DROP TABLE IF EXISTS adoption_requests;
DROP TABLE IF EXISTS pets;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'ADOPTER') NOT NULL DEFAULT 'ADOPTER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 2. Table: pets
-- ----------------------------------------------------------
CREATE TABLE pets (
    pet_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    species VARCHAR(50) NOT NULL,
    breed VARCHAR(50) NOT NULL,
    age INT NOT NULL COMMENT 'Age in years',
    gender ENUM('Male', 'Female') NOT NULL,
    size ENUM('Small', 'Medium', 'Large') NOT NULL,
    status ENUM('Available', 'Pending', 'Adopted') NOT NULL DEFAULT 'Available',
    health_status VARCHAR(255) NOT NULL DEFAULT 'Healthy & Vaccinated',
    description TEXT,
    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 3. Table: adoption_requests
-- ----------------------------------------------------------
CREATE TABLE adoption_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    pet_id INT NOT NULL,
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    living_situation VARCHAR(255) NOT NULL COMMENT 'e.g., Own House with Yard, Apartment, etc.',
    has_other_pets BOOLEAN NOT NULL DEFAULT FALSE,
    notes TEXT,
    admin_remarks TEXT,
    review_date TIMESTAMP NULL,
    CONSTRAINT fk_req_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_req_pet FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 4. Table: adoption_history
-- ----------------------------------------------------------
CREATE TABLE adoption_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    request_id INT NULL,
    user_id INT NOT NULL,
    pet_id INT NOT NULL,
    adoption_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    handover_status VARCHAR(50) NOT NULL DEFAULT 'Completed',
    remarks TEXT,
    CONSTRAINT fk_hist_request FOREIGN KEY (request_id) REFERENCES adoption_requests(request_id) ON DELETE SET NULL,
    CONSTRAINT fk_hist_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_hist_pet FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================================
-- Sample Seed Data
-- ==========================================================

-- Insert Default Admin and Sample Adopters (Plaintext passwords for beginner project compatibility)
INSERT INTO users (username, password, full_name, email, phone, address, role) VALUES
('admin', 'admin123', 'System Administrator', 'admin@petadoption.com', '555-0100', '100 Rescue Shelter Way, Central City', 'ADMIN'),
('john_doe', 'user123', 'John Doe', 'john.doe@example.com', '555-0199', '42 Maple Street, Green Valley', 'ADOPTER'),
('sarah_m', 'user123', 'Sarah Miller', 'sarah.m@example.com', '555-0144', '77 Willow Ave, Oakridge', 'ADOPTER'),
('david_k', 'user123', 'David Kim', 'david.kim@example.com', '555-0188', '15 Pine Crest Road, Sunnyvale', 'ADOPTER');

-- Insert Sample Pets
INSERT INTO pets (name, species, breed, age, gender, size, status, health_status, description) VALUES
('Bella', 'Dog', 'Golden Retriever', 2, 'Female', 'Large', 'Available', 'Fully Vaccinated & Spayed', 'Friendly, energetic, and loves playing fetch. Great with kids.'),
('Milo', 'Cat', 'Domestic Short Hair', 1, 'Male', 'Small', 'Available', 'Vaccinated & Neutered', 'Affectionate lap cat who enjoys sunbathing and laser pointers.'),
('Rocky', 'Dog', 'German Shepherd', 3, 'Male', 'Large', 'Available', 'Healthy, Microchipped', 'Loyal, obedient, and protective. Completed basic obedience training.'),
('Luna', 'Cat', 'Siamese', 2, 'Female', 'Small', 'Pending', 'Vaccinated & De-wormed', 'Curious and vocal feline seeking a quiet and loving home.'),
('Charlie', 'Dog', 'Beagle', 4, 'Male', 'Medium', 'Adopted', 'Healthy & Neutered', 'Gentle hound with an extraordinary sense of smell and love for walks.'),
('Coco', 'Rabbit', 'Holland Lop', 1, 'Female', 'Small', 'Available', 'Healthy & Vet-Checked', 'Gentle bunny who loves fresh greens and gentle head scratches.'),
('Max', 'Dog', 'Labrador Retriever', 2, 'Male', 'Large', 'Available', 'Fully Vaccinated', 'Active swimmer and loyal companion looking for an active family.'),
('Daisy', 'Dog', 'Poodle (Toy)', 3, 'Female', 'Small', 'Available', 'Hypoallergenic, Groomed', 'Smart, easy to train, and perfect for apartment living.'),
('Simba', 'Cat', 'Maine Coon', 2, 'Male', 'Medium', 'Adopted', 'Healthy & Vaccinated', 'Majestic gentle giant with a plush coat and calm temperament.');

-- Insert Sample Requests
INSERT INTO adoption_requests (user_id, pet_id, request_date, status, living_situation, has_other_pets, notes, admin_remarks, review_date) VALUES
(2, 5, '2026-09-10 10:30:00', 'APPROVED', 'Own House with Fenced Backyard', TRUE, 'Experienced dog owner with one senior dog.', 'Application verified and approved after home check.', '2026-09-12 14:00:00'),
(3, 9, '2026-09-15 11:15:00', 'APPROVED', 'Spacious 2-Bedroom Condo', FALSE, 'Looking for an indoor furry companion.', 'Approved. Great home environment for a cat.', '2026-09-16 09:30:00'),
(2, 4, '2026-10-01 16:45:00', 'PENDING', 'Own House with Fenced Backyard', TRUE, 'We would love to welcome Luna to our family!', NULL, NULL),
(4, 1, '2026-09-20 13:00:00', 'REJECTED', 'Small Studio Apartment', FALSE, 'First time pet owner.', 'Large energetic dog is unsuitable for studio without yard.', '2026-09-22 11:00:00');

-- Insert Sample Adoption History
INSERT INTO adoption_history (request_id, user_id, pet_id, adoption_date, handover_status, remarks) VALUES
(1, 2, 5, '2026-09-13 15:00:00', 'Completed', 'Charlie safely handed over to John Doe with medical records.'),
(2, 3, 9, '2026-09-17 12:00:00', 'Completed', 'Simba welcomed into Sarah Miller home successfully.');
