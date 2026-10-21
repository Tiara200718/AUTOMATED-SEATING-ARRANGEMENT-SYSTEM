-- ========================================================
-- Smart Exam Seating Arrangement System - Database Schema
-- Compatible with MySQL 8.0+ and MariaDB
-- ========================================================

CREATE DATABASE IF NOT EXISTS exam_seating_db;
USE exam_seating_db;

-- 1. Admin Users Table
CREATE TABLE IF NOT EXISTS admin_users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Examination Halls / Rooms Table
CREATE TABLE IF NOT EXISTS rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_code VARCHAR(20) NOT NULL UNIQUE,
    building VARCHAR(50) NOT NULL,
    total_rows INT NOT NULL,
    total_columns INT NOT NULL,
    seats_per_bench INT NOT NULL DEFAULT 2,
    total_capacity INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Students Table
CREATE TABLE IF NOT EXISTS students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    roll_number VARCHAR(30) NOT NULL UNIQUE,
    student_name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    semester INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Exams Table
CREATE TABLE IF NOT EXISTS exams (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_name VARCHAR(100) NOT NULL,
    exam_date DATE NOT NULL,
    time_slot VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Seating Allocations Table (Junction Table)
CREATE TABLE IF NOT EXISTS seating_allocations (
    allocation_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    student_id INT NOT NULL,
    room_id INT NOT NULL,
    bench_row INT NOT NULL,
    bench_col INT NOT NULL,
    seat_position INT NOT NULL, -- 1 = Left, 2 = Right
    allocated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE,
    -- Prevent duplicate seating in same exam session:
    CONSTRAINT uq_seat_coordinate UNIQUE (exam_id, room_id, bench_row, bench_col, seat_position),
    -- Prevent student from being seated in two places in same exam session:
    CONSTRAINT uq_student_exam UNIQUE (exam_id, student_id)
);

-- Indexes for lightning fast lookups
CREATE INDEX idx_student_roll ON students(roll_number);
CREATE INDEX idx_alloc_exam_room ON seating_allocations(exam_id, room_id);
CREATE INDEX idx_alloc_student ON seating_allocations(student_id);

-- Sample Data Seeding
INSERT IGNORE INTO admin_users (username, password_hash, full_name) 
VALUES ('admin', 'admin123', 'Chief Exam Superintendent');

INSERT IGNORE INTO rooms (room_code, building, total_rows, total_columns, seats_per_bench, total_capacity) VALUES
('LH-101', 'Academic Block A', 5, 4, 2, 40),
('LH-102', 'Academic Block A', 5, 3, 2, 30),
('CS-LAB-1', 'Computing Center', 4, 3, 2, 24);

INSERT IGNORE INTO exams (exam_name, exam_date, time_slot) VALUES
('End-Semester Examination 2026', '2026-11-15', '09:30 AM - 12:30 PM');
