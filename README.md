# 🎓 Automated Seating Arrangement System

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Swing](https://img.shields.io/badge/GUI-Java%20Swing-blue?style=for-the-badge)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![Database](https://img.shields.io/badge/Database-MySQL%20%7C%20SQLite-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Diagrams](https://img.shields.io/badge/Diagrams-Draw.io-F08705?style=for-the-badge&logo=diagramsdotnet&logoColor=white)](https://app.diagrams.net/)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

An intelligent, automated desktop application built with **Java (Swing GUI)** and a **Relational Database (JDBC with MySQL / SQLite)** designed to automate conflict-free exam hall seat distribution, enforce anti-cheating student interleaving, render real-time interactive hall matrices, and enable instant student seat lookups.

---
🚀 Live Application

👉 Click here to open the Automated Seating Arrangement System
Link:https://automated-seating-arrangement-system-w5hfrcj7swgpvvgh4ndufp.streamlit.app/
Link:https://automated-seating-api.onrender.com

---

## 📌 Table of Contents
1. [Problem Statement](#-problem-statement)
2. [Key Features & Highlights](#-key-features--highlights)
3. [System Architecture & Tech Stack](#-system-architecture--tech-stack)
4. [Anti-Cheating Seating Algorithm](#-anti-cheating-seating-algorithm)
5. [Database Schema (Relational Design)](#-database-schema-relational-design)
6. [System Diagrams (Draw.io)](#-system-diagrams-drawio)
7. [Installation & How to Run](#-installation--how-to-run)
8. [End-Sem Lab Exam Quick Summary](#-end-sem-lab-exam-quick-summary)
9. [Team Members & Contributions](#-team-members--contributions)
10. [Submission Checklist](#-submission-checklist)

---

## 🚨 Problem Statement
Manual examination seating arrangement in universities is:
* **Time-consuming and tedious:** Requires manual calculations of hall dimensions and bench capacities.
* **Malpractice-prone:** Neighboring students often belong to the same department/subject, facilitating cheating.
* **Prone to human error:** Leading to room overcrowding, seat overlaps, and duplicate seat numbers.
* **Chaotic on exam mornings:** Heavy congestion around physical notice boards as students search for their halls.

**Solution:** The **Automated Seating Arrangement System** automates this workflow by dynamically interleaving students from differing branches across available halls in seconds, guaranteeing 0% neighbor overlap, and providing an interactive visual room matrix with instant roll-number search.

---

## ✨ Key Features & Highlights
* 🛡️ **Anti-Cheating Alternate Interleaving Engine:** Dynamically interleaves students from different departments (e.g., CSE $\leftrightarrow$ ECE $\leftrightarrow$ MECH) across adjacent benches.
* 🖥️ **Interactive Visual Hall Matrix:** Displays physical room grids ($Rows \times Columns$) with color-coded badges indicating student department.
* 🔍 **Instant Student Seat Search:** Students can input their roll number to immediately find their allocated room, row, column, and bench seat.
* 🏫 **Hall & Capacity Management:** Configure halls with custom rows, columns, and seats per bench with real-time capacity validation.
* 📋 **Master Data Management:** Add, update, and categorize students by branch, semester, and registered exams.
* 🗄️ **Zero-Setup Database Architecture:** Ships with modular JDBC connectivity supporting both **MySQL** and a zero-configuration embedded **SQLite** fallback.
* 📄 **Exportable Seating Charts:** Export allocation results to clean printable CSV sheets for notice boards.

---

## 🛠️ System Architecture & Tech Stack

```text
+---------------------------------------------------------+
|                  Java Swing GUI Layer                   |
|     (Dashboard, Student Form, Hall Matrix, Search)      |
+---------------------------------------------------------+
                            │
                            ▼
+---------------------------------------------------------+
|                Business Logic & Engine                  |
|   (Interleaving Algorithm, Capacity Validator, Models)  |
+---------------------------------------------------------+
                            │
                            ▼
+---------------------------------------------------------+
|             Data Access Object (DAO) Layer              |
|          (DatabaseManager.java with JDBC Driver)        |
+---------------------------------------------------------+
                            │
                            ▼
+---------------------------------------------------------+
|                Relational Database (3NF)                |
|        (MySQL / SQLite: Rooms, Students, Allocations)   |
+---------------------------------------------------------+
```

* **Programming Language:** Java (JDK 17 or 21)
* **GUI Toolkit:** Java Swing & AWT (`JFrame`, `JPanel`, `JTable`, `GridLayout`, `BorderLayout`)
* **Database Engine:** MySQL / SQLite
* **Database Driver:** JDBC (`PreparedStatement`, ACID Transactions)
* **Design Pattern:** Model-View-Controller (MVC) + Data Access Object (DAO)
* **Diagram Tool:** Draw.io (`draw_io_diagrams.xml`)

---

## 🧮 Anti-Cheating Seating Algorithm

```text
[ Department A Queue (CSE) ]  \
[ Department B Queue (ECE) ]   ===>  [ Round-Robin Interleaver ] ===> Bench 1: [A1] [B1]
[ Department C Queue (MECH)]  /                                        Bench 2: [C1] [A2]
                                                                       Bench 3: [B2] [C2]
```

1. **Partitioning:** Registered students are partitioned into department queues (`Map<String, Queue<Student>>`).
2. **Room Traversal:** Available examination halls are iterated row-by-row and bench-by-bench.
3. **Alternate Dequeue:** For each bench coordinate, the algorithm dequeues a student from alternating department queues, ensuring adjacent horizontal and vertical seats are populated by students taking different papers.
4. **Collision Prevention:** If a branch queue is exhausted, the algorithm pulls from the next distinct queue with available students.

---

## 🗄️ Database Schema (Relational Design)

The database consists of 5 normalized relational tables:

1. **`admin_users`:** Authentication credentials (`user_id`, `username`, `password_hash`, `full_name`).
2. **`rooms`:** Hall dimensions (`room_id`, `room_code`, `total_rows`, `total_columns`, `seats_per_bench`, `total_capacity`).
3. **`students`:** Student directory (`student_id`, `roll_number`, `student_name`, `department`, `semester`).
4. **`exams`:** Examination sessions (`exam_id`, `exam_name`, `exam_date`, `time_slot`).
5. **`seating_allocations`:** Assigned seats junction table (`allocation_id`, `exam_id`, `student_id`, `room_id`, `bench_row`, `bench_col`, `seat_position`).
   * *Integrity Rule:* Unique composite constraint on `(exam_id, room_id, bench_row, bench_col, seat_position)` prevents duplicate seat allocations.

---

## 📊 System Diagrams (Draw.io)

* **Public Draw.io Link:** [Click here to view Diagrams on Draw.io](https://app.diagrams.net/#G1YourPublicSharedFileDriveIdHere) *(Replace with your shared link)*
* **Included Diagrams in `draw_io_diagrams.xml`:**
  1. **Use Case Diagram:** Interacting roles (Admin, Proctor, Student).
  2. **Data Flow Diagram (Level 0 - Context):** High-level system data boundary.
  3. **Data Flow Diagram (Level 1 - Detailed):** Modular data stores and process flows.
  4. **Entity Relationship Diagram (ERD):** Tables and foreign keys.

> **How to Import & View:** Go to [Draw.io](https://app.diagrams.net/) $\rightarrow$ Click **File > Open From > Device** $\rightarrow$ Select `draw_io_diagrams.xml`.
---

## 🚀 Installation & How to Run

### Prerequisites
* **Java Development Kit (JDK):** Version 17 or higher (`javac -version`).
* **Git:** For cloning the repository.

### Quick Run (Command Line)
```bash
# 1. Clone this repository
git clone https://github.com/your-username/smart-exam-seating-system.git
cd smart-exam-seating-system

# 2. Compile Java source files
javac -d bin src/com/examseating/**/*.java src/com/examseating/*.java

# 3. Run the application
java -cp "bin;lib/*" com.examseating.Main
```
*(On Linux/macOS, use `java -cp "bin:lib/*" com.examseating.Main`)*

### Run in IDE (IntelliJ IDEA / Eclipse / VS Code)
1. Open your IDE and select **Open Project** $\rightarrow$ select the repository folder.
2. Navigate to `src/com/examseating/Main.java`.
3. Right-click and choose **Run 'Main.main()'**.
4. The application boots with a seeded database and pre-populated sample halls and students for instant evaluation!

---

## 🎓 End-Sem Lab Exam Quick Summary
*(Prepare these points for your End-Sem Lab Viva & Practical exam)*

* **Q1: Which algorithm is used for seating allocation?**  
  * **Answer:** An **Alternate Round-Robin Interleaving Algorithm** utilizing Java Collections (`HashMap` and `Queue`). It groups students by department and assigns seats sequentially across room grids so that adjacent seats never share the same department.
* **Q2: How does the system handle database connectivity?**  
  * **Answer:** Uses the **JDBC (Java Database Connectivity)** API with a Data Access Object (DAO) pattern. Prepared statements (`PreparedStatement`) are utilized for SQL injection prevention and batch transactions ensure ACID compliance.
* **Q3: What happens if student count exceeds hall capacity?**  
  * **Answer:** The system performs a pre-allocation check: $\sum \text{Students} \le \sum \text{Room Capacity}$. If violated, a warning dialog prompts the admin with the exact deficit and cancels allocation safely before any data is written.
* **Q4: How is duplicate seating prevented?**  
  * **Answer:** At the database level, a `UNIQUE` composite constraint on `(exam_id, room_id, bench_row, bench_col, seat_position)` ensures physical concurrency integrity.

---

## 👥 Team Members & Contributions

| Name | Roll Number | Role | Key Contributions |
|---|---|---|---|
| **Raghavi M** | [2025503520] | Team Lead & Architecture | SRS, System Architecture, DFD & Use Case Diagrams |
| **Naina Bawanthade** | [2025503070] | Algorithm & Logic | Interleaving Algorithm, Java Collections, Edge Cases |
| **Ramya K** | [2025503042] | GUI & Visual Layout | Java Swing Panels, Interactive Hall Matrix Grid |
| **Sandhiya S** | [2025503304] | Database & Reports | Relational Schema, JDBC DAO, Export Engine, README |
| **Lavanya A** | [2025503554] | Database & Reports | Relational Schema, JDBC DAO, Export Engine, README |

---

## 📋 Submission Checklist
- [x] **Team Presentation PPT:** Formatted slide deck guide (`PRESENTATION_SLIDES.md`).
- [x] **Individual Video Presentation:** 1–2 min script with face + voice setup (`VIDEO_PRESENTATION_SCRIPT.md`).
- [x] **Complete Project Report:** Full report with Title, SRS, Highlights, DFDs, Use Case, and DB description (`PROJECT_REPORT.md`).
- [x] **Draw.io File:** Ready-to-import diagram XML (`draw_io_diagrams.xml`).
- [x] **Source Code:** Fully functional Java Swing + JDBC desktop application.
- [x] **GitHub README.md:** Professional repository documentation.
