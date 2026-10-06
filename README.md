# 🐾 Pet Adoption Monitoring System (Java Swing + JDBC + MySQL)

A complete, beginner-friendly desktop application for managing and monitoring pet rescue operations, pet adoptions, applications review, adoption logs, and analytics.

---

## 📁 Project Architecture & Package Structure

```
PetAdoptionSystem/
├── .classpath                          # Eclipse Classpath configuration
├── .project                            # Eclipse Project definition
├── db/
│   └── schema.sql                      # Complete MySQL database creation & seed data
├── lib/
│   └── mysql-connector-j-8.4.0.jar     # MySQL JDBC Driver Connector (Pre-included)
├── src/
│   └── com/
│       └── petadoption/
│           ├── main/
│           │   └── Main.java                  # Main application entry point
│           ├── model/
│           │   ├── User.java                  # User entity (Admin & Adopter)
│           │   ├── Pet.java                   # Pet profile entity
│           │   ├── AdoptionRequest.java       # Adoption application model
│           │   ├── AdoptionHistory.java       # Completed adoption history record
│           │   └── ReportSummary.java         # Aggregated system metrics model
│           ├── dao/
│           │   ├── UserDAO.java               # Authentication, registration & user DB ops
│           │   ├── PetDAO.java                # Pet CRUD, multi-criteria search & status ops
│           │   ├── AdoptionRequestDAO.java    # Request submission, approval & rejection logic
│           │   ├── AdoptionHistoryDAO.java    # History logs query & tracking
│           │   └── ReportDAO.java             # Dashboard metrics & species distribution
│           ├── ui/
│           │   ├── LoginFrame.java            # Sign In window with DB settings quick-access
│           │   ├── RegisterFrame.java         # Adopter account registration modal
│           │   ├── AdminDashboard.java        # Administrator management console
│           │   ├── AdopterDashboard.java      # Adopter browsing & tracking portal
│           │   ├── PetDialog.java             # Add/Edit Pet dialog
│           │   ├── AdoptionRequestDialog.java # Adoption submission dialog
│           │   ├── RequestReviewDialog.java   # Admin review & approve/reject modal
│           │   └── DBConfigDialog.java        # Runtime MySQL connection configuration
│           └── util/
│               ├── DBConnection.java          # JDBC Connection Manager with persistent prefs
│               └── UIUtils.java               # Modern UI styling, color palette & badges
└── README.md
```

---

## 🚀 Key Features

### 👨‍💼 1. Administrator Capabilities:
* **Pet Management (CRUD)**: Add, edit, delete, and filter pets by species, breed, age, and adoption status.
* **Adoption Request Processing**: View pending applications with applicant details, housing environment, existing pet information, and reason for adoption. Approve or Reject with remarks.
* **Automated Workflow**: Approving a request automatically transitions pet status to `Adopted`, logs the event in `adoption_history`, and closes other competing pending requests for that pet.
* **Adoption History Log**: View complete records of all finalized adoptions with adopter contact details and handover status.
* **Reports & Statistics**: Live dashboard cards for total pets, available pets, adopted pets, pending requests, and registered users, alongside a species distribution breakdown.
* **User Management**: View registered adopters and administrators.

### 🐶 2. Adopter Capabilities:
* **Account Registration & Login**: Easy sign-up with contact information and address.
* **Pet Search & Discovery**: Search pets by keyword, species, size, and status.
* **Adoption Application**: Submit adoption applications specifying living situation, other pets at home, and reason for adoption.
* **Real-time Status Tracking**: View application status badges (`PENDING`, `APPROVED`, `REJECTED`) and administrator feedback remarks.
* **My Adopted Pets**: View all pets successfully adopted by the user.
* **Profile Management**: Update profile details, contact numbers, address, and password.

---

## 🗄️ Database Setup (MySQL)

### Step 1: Execute Database Script
Open **MySQL Workbench**, **phpMyAdmin**, or the **MySQL Command Line Client**, and run the SQL script located at:
```
db/schema.sql
```

Alternatively, run from terminal/PowerShell:
```bash
mysql -u root -p < db/schema.sql
```

This creates the `pet_adoption_db` database and sets up all required tables (`users`, `pets`, `adoption_requests`, `adoption_history`) with sample data.

---

## 💻 Importing and Running in Eclipse

1. **Open Eclipse IDE** (Eclipse IDE for Java Developers).
2. Go to **File -> Import...**
3. Select **General -> Existing Projects into Workspace** and click **Next**.
4. Click **Browse...** next to *Select root directory* and select the `PetAdoptionSystem` folder.
5. Click **Finish**.
6. **Verify Build Path**:
   - Right-click the project `PetAdoptionSystem` -> **Build Path** -> **Configure Build Path...**
   - Under **Libraries**, verify that `mysql-connector-j-8.4.0.jar` (located in `lib/`) is present. If not, click **Add JARs...** and choose `PetAdoptionSystem/lib/mysql-connector-j-8.4.0.jar`.
7. **Run the Application**:
   - In Package Explorer, navigate to `src/com/petadoption/main/Main.java`.
   - Right-click `Main.java` -> **Run As** -> **Java Application**.

---

## 🔑 Default User Credentials

| Role | Username | Password | Notes |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | Full access to Pet CRUD, Request approval, Reports, Users |
| **Adopter** | `john_doe` | `user123` | Pre-loaded adopter with active and historical requests |
| **Adopter** | `sarah_m` | `user123` | Pre-loaded adopter with finalized adoption |
| **Adopter** | `david_k` | `user123` | Pre-loaded adopter |

---

## ⚙️ MySQL Connection Configuration

If your local MySQL root password is not `root`, you can configure the connection in either of two ways:
1. **Via the GUI**: Click the **⚙ DB Connection** button at the bottom-right of the Login window. Enter your MySQL Host, Port, Database name, Username, and Password, test the connection, and click **Save & Connect**.
2. **Via Code**: Modify the default values in `com/petadoption/util/DBConnection.java`.
