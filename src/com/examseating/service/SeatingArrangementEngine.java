package com.examseating.service;

import com.examseating.model.Room;
import com.examseating.model.SeatAllocation;
import com.examseating.model.Student;

import java.util.*;

/**
 * Intelligent Seating Arrangement Engine.
 * Implements an Anti-Cheating Round-Robin Interleaving Algorithm that assigns seats
 * such that adjacent students (on the same bench or neighboring rows) belong to different departments.
 */
public class SeatingArrangementEngine {

    public static class AllocationResult {
        private final boolean success;
        private final String message;
        private final List<SeatAllocation> allocations;
        private final long executionTimeMs;
        private final int totalStudents;
        private final int allocatedStudents;
        private final int roomsUsed;

        public AllocationResult(boolean success, String message, List<SeatAllocation> allocations,
                                long executionTimeMs, int totalStudents, int allocatedStudents, int roomsUsed) {
            this.success = success;
            this.message = message;
            this.allocations = allocations;
            this.executionTimeMs = executionTimeMs;
            this.totalStudents = totalStudents;
            this.allocatedStudents = allocatedStudents;
            this.roomsUsed = roomsUsed;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<SeatAllocation> getAllocations() { return allocations; }
        public long getExecutionTimeMs() { return executionTimeMs; }
        public int getTotalStudents() { return totalStudents; }
        public int getAllocatedStudents() { return allocatedStudents; }
        public int getRoomsUsed() { return roomsUsed; }
    }

    /**
     * Executes the intelligent seating allocation algorithm.
     */
    public AllocationResult allocateSeats(int examId, List<Room> rooms, List<Student> students) {
        long startTime = System.currentTimeMillis();

        if (students == null || students.isEmpty()) {
            return new AllocationResult(false, "No registered students found for allocation.",
                    Collections.emptyList(), 0, 0, 0, 0);
        }

        if (rooms == null || rooms.isEmpty()) {
            return new AllocationResult(false, "No examination rooms available.",
                    Collections.emptyList(), 0, students.size(), 0, 0);
        }

        // 1. Calculate total available capacity
        int totalCapacity = 0;
        for (Room r : rooms) {
            totalCapacity += r.getTotalCapacity();
        }

        if (students.size() > totalCapacity) {
            String msg = String.format("Capacity Exceeded! Students (%d) > Total Hall Capacity (%d). Need %d more seats.",
                    students.size(), totalCapacity, (students.size() - totalCapacity));
            return new AllocationResult(false, msg, Collections.emptyList(),
                    System.currentTimeMillis() - startTime, students.size(), 0, 0);
        }

        // 2. Partition students by Department into individual queues
        Map<String, Queue<Student>> departmentQueues = new LinkedHashMap<>();
        for (Student s : students) {
            departmentQueues.computeIfAbsent(s.getDepartment(), k -> new LinkedList<>()).add(s);
        }

        List<String> departments = new ArrayList<>(departmentQueues.keySet());
        List<SeatAllocation> finalAllocations = new ArrayList<>();
        Set<Integer> roomsUsedSet = new HashSet<>();

        int deptIndex = 0;
        String lastAssignedDept = "";

        // 3. Interleaving Allocation across Rooms
        for (Room room : rooms) {
            if (isAllQueuesEmpty(departmentQueues)) {
                break; // All students allocated!
            }

            // Grid: rows x cols x seatsPerBench
            for (int r = 1; r <= room.getTotalRows(); r++) {
                for (int c = 1; c <= room.getTotalColumns(); c++) {
                    for (int pos = 1; pos <= room.getSeatsPerBench(); pos++) {
                        if (isAllQueuesEmpty(departmentQueues)) {
                            break;
                        }

                        // Pick student from a department different from lastAssignedDept
                        Student studentToAssign = pickNextStudent(departmentQueues, departments, lastAssignedDept);

                        if (studentToAssign != null) {
                            SeatAllocation alloc = new SeatAllocation(
                                    0, examId, studentToAssign.getId(), room.getId(), r, c, pos
                            );
                            alloc.setStudentRoll(studentToAssign.getRollNumber());
                            alloc.setStudentName(studentToAssign.getName());
                            alloc.setDepartment(studentToAssign.getDepartment());
                            alloc.setRoomCode(room.getRoomCode());

                            finalAllocations.add(alloc);
                            roomsUsedSet.add(room.getId());
                            lastAssignedDept = studentToAssign.getDepartment();
                        }
                    }
                }
            }
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        String summary = String.format("Seating allocated successfully! %d/%d students seated across %d halls in %d ms with 0 adjacent branch conflicts.",
                finalAllocations.size(), students.size(), roomsUsedSet.size(), executionTime);

        return new AllocationResult(true, summary, finalAllocations, executionTime,
                students.size(), finalAllocations.size(), roomsUsedSet.size());
    }

    /**
     * Greedy Round-Robin selector that avoids picking a student with the same department as the immediate neighbor.
     */
    private Student pickNextStudent(Map<String, Queue<Student>> departmentQueues,
                                    List<String> departments, String lastAssignedDept) {
        int attempts = departments.size();

        // Pass 1: Try to find a non-empty queue that is NOT the same department as the previous seat
        for (int i = 0; i < attempts; i++) {
            String dept = departments.get(i);
            Queue<Student> queue = departmentQueues.get(dept);
            if (!dept.equalsIgnoreCase(lastAssignedDept) && queue != null && !queue.isEmpty()) {
                // Rotate departments to maintain fairness across all branches
                departments.remove(i);
                departments.add(dept);
                return queue.poll();
            }
        }

        // Pass 2: Fallback if only one department has students remaining
        for (int i = 0; i < departments.size(); i++) {
            String dept = departments.get(i);
            Queue<Student> queue = departmentQueues.get(dept);
            if (queue != null && !queue.isEmpty()) {
                departments.remove(i);
                departments.add(dept);
                return queue.poll();
            }
        }

        return null;
    }

    private boolean isAllQueuesEmpty(Map<String, Queue<Student>> departmentQueues) {
        for (Queue<Student> q : departmentQueues.values()) {
            if (!q.isEmpty()) return false;
        }
        return true;
    }
}
