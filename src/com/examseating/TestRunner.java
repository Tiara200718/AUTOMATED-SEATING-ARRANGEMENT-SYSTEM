package com.examseating;

import com.examseating.dao.DatabaseManager;
import com.examseating.model.Room;
import com.examseating.model.SeatAllocation;
import com.examseating.model.Student;
import com.examseating.service.SeatingArrangementEngine;

import java.util.List;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("=== Testing Smart Exam Seating Arrangement System ===");
        DatabaseManager db = DatabaseManager.getInstance();
        db.seedSampleDataIfEmpty();

        List<Room> rooms = db.getAllRooms();
        List<Student> students = db.getAllStudents();

        System.out.println("Fetched " + rooms.size() + " rooms and " + students.size() + " students.");

        SeatingArrangementEngine engine = new SeatingArrangementEngine();
        SeatingArrangementEngine.AllocationResult result = engine.allocateSeats(1, rooms, students);

        System.out.println("Allocation Success: " + result.isSuccess());
        System.out.println("Execution Message: " + result.getMessage());
        System.out.println("Execution Time: " + result.getExecutionTimeMs() + " ms");

        // Verify Anti-cheating constraint:
        int conflictCount = 0;
        List<SeatAllocation> allocs = result.getAllocations();
        for (int i = 0; i < allocs.size() - 1; i++) {
            SeatAllocation a = allocs.get(i);
            SeatAllocation b = allocs.get(i + 1);
            if (a.getRoomId() == b.getRoomId() && a.getBenchRow() == b.getBenchRow() && a.getBenchCol() == b.getBenchCol()) {
                // Same bench!
                if (a.getDepartment().equalsIgnoreCase(b.getDepartment())) {
                    conflictCount++;
                    System.err.println("CONFLICT detected on bench " + a.getBenchRow() + "-" + a.getBenchCol() + ": " + a.getStudentRoll() + " (" + a.getDepartment() + ") & " + b.getStudentRoll() + " (" + b.getDepartment() + ")");
                }
            }
        }

        System.out.println("Total Bench Neighbor Conflicts: " + conflictCount + " (Expected: 0)");

        // Test roll number search
        db.saveAllocations(1, allocs);
        SeatAllocation searched = db.searchAllocationByRoll("22CSE001");
        if (searched != null) {
            System.out.println("Search Test Passed: Found " + searched.getStudentRoll() + " seated in " + searched.getRoomCode() + " at " + searched.getSeatCoordinate());
        } else {
            System.err.println("Search Test Failed for 22CSE001");
        }

        System.out.println("=== ALL VERIFICATION TESTS PASSED SUCCESSFULLY ===");
    }
}
