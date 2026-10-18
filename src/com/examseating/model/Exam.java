package com.examseating.model;

/**
 * Model representing an Examination session.
 */
public class Exam {
    private int id;
    private String examName;
    private String examDate;
    private String timeSlot;

    public Exam() {}

    public Exam(int id, String examName, String examDate, String timeSlot) {
        this.id = id;
        this.examName = examName;
        this.examDate = examDate;
        this.timeSlot = timeSlot;
    }

    public Exam(String examName, String examDate, String timeSlot) {
        this(0, examName, examDate, timeSlot);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    @Override
    public String toString() {
        return examName + " (" + examDate + " " + timeSlot + ")";
    }
}
