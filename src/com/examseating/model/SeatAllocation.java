package com.examseating.model;

/**
 * Model representing an allocated seat in an exam hall.
 */
public class SeatAllocation {
    private int id;
    private int examId;
    private int studentId;
    private int roomId;
    private int benchRow;
    private int benchCol;
    private int seatPosition; // 1 for Left, 2 for Right

    // Joined fields for display
    private String studentRoll;
    private String studentName;
    private String department;
    private String roomCode;
    private String examName;

    public SeatAllocation() {}

    public SeatAllocation(int id, int examId, int studentId, int roomId, int benchRow, int benchCol, int seatPosition) {
        this.id = id;
        this.examId = examId;
        this.studentId = studentId;
        this.roomId = roomId;
        this.benchRow = benchRow;
        this.benchCol = benchCol;
        this.seatPosition = seatPosition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getBenchRow() {
        return benchRow;
    }

    public void setBenchRow(int benchRow) {
        this.benchRow = benchRow;
    }

    public int getBenchCol() {
        return benchCol;
    }

    public void setBenchCol(int benchCol) {
        this.benchCol = benchCol;
    }

    public int getSeatPosition() {
        return seatPosition;
    }

    public void setSeatPosition(int seatPosition) {
        this.seatPosition = seatPosition;
    }

    public String getStudentRoll() {
        return studentRoll;
    }

    public void setStudentRoll(String studentRoll) {
        this.studentRoll = studentRoll;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getSeatPositionText() {
        return seatPosition == 1 ? "Left" : "Right";
    }

    public String getSeatCoordinate() {
        return "R" + benchRow + "-C" + benchCol + " (" + getSeatPositionText() + ")";
    }

    @Override
    public String toString() {
        return studentRoll + " @ " + roomCode + " [" + getSeatCoordinate() + "]";
    }
}
