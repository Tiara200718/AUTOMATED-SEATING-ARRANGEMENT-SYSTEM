package com.examseating.model;

/**
 * Model representing an Examination Hall / Room entity.
 */
public class Room {
    private int id;
    private String roomCode;
    private String building;
    private int totalRows;
    private int totalColumns;
    private int seatsPerBench;
    private int totalCapacity;

    public Room() {
        this.seatsPerBench = 2;
    }

    public Room(int id, String roomCode, String building, int totalRows, int totalColumns, int seatsPerBench) {
        this.id = id;
        this.roomCode = roomCode;
        this.building = building;
        this.totalRows = totalRows;
        this.totalColumns = totalColumns;
        this.seatsPerBench = seatsPerBench > 0 ? seatsPerBench : 2;
        this.totalCapacity = this.totalRows * this.totalColumns * this.seatsPerBench;
    }

    public Room(String roomCode, String building, int totalRows, int totalColumns, int seatsPerBench) {
        this(0, roomCode, building, totalRows, totalColumns, seatsPerBench);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
        this.totalCapacity = this.totalRows * this.totalColumns * this.seatsPerBench;
    }

    public int getTotalColumns() {
        return totalColumns;
    }

    public void setTotalColumns(int totalColumns) {
        this.totalColumns = totalColumns;
        this.totalCapacity = this.totalRows * this.totalColumns * this.seatsPerBench;
    }

    public int getSeatsPerBench() {
        return seatsPerBench;
    }

    public void setSeatsPerBench(int seatsPerBench) {
        this.seatsPerBench = seatsPerBench;
        this.totalCapacity = this.totalRows * this.totalColumns * this.seatsPerBench;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    @Override
    public String toString() {
        return roomCode + " (" + building + ") - Cap: " + totalCapacity + " (" + totalRows + "x" + totalColumns + ")";
    }
}
