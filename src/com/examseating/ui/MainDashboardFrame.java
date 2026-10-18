package com.examseating.ui;

import com.examseating.dao.DatabaseManager;
import com.examseating.model.Exam;
import com.examseating.model.Room;
import com.examseating.model.SeatAllocation;
import com.examseating.model.Student;
import com.examseating.service.SeatingArrangementEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Main Application Dashboard Frame for Smart Exam Seating Arrangement System.
 * Built using modern Java Swing layout with full management tabs, visual matrix, and search portal.
 */
public class MainDashboardFrame extends JFrame {
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final SeatingArrangementEngine engine = new SeatingArrangementEngine();

    // UI Components
    private JLabel totalStudentsCardLbl;
    private JLabel totalRoomsCardLbl;
    private JLabel totalCapacityCardLbl;
    private JLabel seatingStatusCardLbl;

    private JTable roomTable;
    private DefaultTableModel roomTableModel;

    private JTable studentTable;
    private DefaultTableModel studentTableModel;

    private JComboBox<Room> matrixRoomCombo;
    private VisualHallMatrixPanel visualMatrixPanel;

    private JTextField searchRollField;
    private JPanel searchResultPanel;
    private JLabel searchResultTitle;
    private JLabel searchResultDetails;

    private JTextArea exportPreviewArea;
    private JTextArea logArea;

    private Exam currentExam;

    public MainDashboardFrame() {
        setTitle("Smart Exam Seating Arrangement System - Java & Database Project");
        setSize(1180, 780);
        setMinimumSize(new Dimension(1000, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Fetch current exam
        List<Exam> exams = db.getAllExams();
        currentExam = exams.isEmpty() ? new Exam(1, "End-Semester Examination", "2026-11-15", "09:30 AM") : exams.get(0);

        initComponents();
        refreshAllData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 1. Top Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42)); // Dark Slate
        headerPanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 2, 2));
        titleBox.setOpaque(false);

        JLabel titleLbl = new JLabel("SMART EXAM SEATING ARRANGEMENT SYSTEM");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLbl.setForeground(Color.WHITE);

        JLabel subTitleLbl = new JLabel("Automated Anti-Cheating Seating Allocation • Java Swing & JDBC Architecture");
        subTitleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subTitleLbl.setForeground(new Color(148, 163, 184));

        titleBox.add(titleLbl);
        titleBox.add(subTitleLbl);
        headerPanel.add(titleBox, BorderLayout.WEST);

        // DB Status Badge
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        badgePanel.setOpaque(false);
        JLabel dbBadge = new JLabel(db.isUsingJdbc() ? "● SQLite DB Connected" : "● Embedded Storage Ready");
        dbBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dbBadge.setForeground(new Color(52, 211, 153)); // Emerald Green
        badgePanel.add(dbBadge);
        headerPanel.add(badgePanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Tabbed Navigation
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("  📊 Dashboard  ", createDashboardTab());
        tabbedPane.addTab("  🏫 Rooms & Halls  ", createRoomsTab());
        tabbedPane.addTab("  🎓 Students  ", createStudentsTab());
        tabbedPane.addTab("  🪑 Visual Hall Matrix  ", createVisualMatrixTab());
        tabbedPane.addTab("  🔍 Student Search  ", createSearchTab());
        tabbedPane.addTab("  📄 Export & Report  ", createExportTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ================= TAB 1: DASHBOARD =================
    private JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Metrics Cards
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        metricsPanel.setOpaque(false);

        totalStudentsCardLbl = new JLabel("0", JLabel.CENTER);
        totalRoomsCardLbl = new JLabel("0", JLabel.CENTER);
        totalCapacityCardLbl = new JLabel("0", JLabel.CENTER);
        seatingStatusCardLbl = new JLabel("Pending", JLabel.CENTER);

        metricsPanel.add(createCard("Registered Students", totalStudentsCardLbl, new Color(59, 130, 246)));
        metricsPanel.add(createCard("Exam Halls", totalRoomsCardLbl, new Color(16, 185, 129)));
        metricsPanel.add(createCard("Total Seat Capacity", totalCapacityCardLbl, new Color(139, 92, 246)));
        metricsPanel.add(createCard("Seating Status", seatingStatusCardLbl, new Color(245, 158, 11)));

        panel.add(metricsPanel, BorderLayout.NORTH);

        // Center Action & Log Box
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        buttonBar.setOpaque(false);

        JButton runAllocBtn = new JButton("⚡ Run Smart Seating Allocation");
        runAllocBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        runAllocBtn.setBackground(new Color(37, 99, 235));
        runAllocBtn.setForeground(Color.WHITE);
        runAllocBtn.setFocusPainted(false);
        runAllocBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        runAllocBtn.addActionListener(e -> triggerSmartAllocation());

        JButton clearAllocBtn = new JButton("🧹 Reset Seating");
        clearAllocBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        clearAllocBtn.setBackground(new Color(239, 68, 68));
        clearAllocBtn.setForeground(Color.WHITE);
        clearAllocBtn.setFocusPainted(false);
        clearAllocBtn.addActionListener(e -> resetSeating());

        JButton seedBtn = new JButton("🔄 Re-Seed Sample Data");
        seedBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        seedBtn.setBackground(new Color(71, 85, 105));
        seedBtn.setForeground(Color.WHITE);
        seedBtn.setFocusPainted(false);
        seedBtn.addActionListener(e -> {
            db.seedSampleDataIfEmpty();
            refreshAllData();
            log("Sample database data successfully verified.");
        });

        buttonBar.add(runAllocBtn);
        buttonBar.add(clearAllocBtn);
        buttonBar.add(seedBtn);
        centerPanel.add(buttonBar, BorderLayout.NORTH);

        // System Log Box
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setBackground(new Color(15, 23, 42));
        logArea.setForeground(new Color(226, 232, 240));
        logArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("System Engine Execution Logs"));
        centerPanel.add(logScroll, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCard(String title, JLabel valueLbl, Color accent) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(new Color(100, 116, 139));

        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLbl.setForeground(accent);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        return card;
    }

    // ================= TAB 2: ROOMS =================
    private JPanel createRoomsTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add Room Form
        JPanel formCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JTextField codeField = new JTextField(7);
        JTextField bldgField = new JTextField(12);
        JSpinner rowsSpin = new JSpinner(new SpinnerNumberModel(5, 1, 20, 1));
        JSpinner colsSpin = new JSpinner(new SpinnerNumberModel(4, 1, 20, 1));
        JSpinner benchSpin = new JSpinner(new SpinnerNumberModel(2, 1, 3, 1));

        JButton addBtn = new JButton("➕ Add Exam Hall");
        addBtn.setBackground(new Color(16, 185, 129));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addBtn.addActionListener(e -> {
            String code = codeField.getText().trim();
            String bldg = bldgField.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Room Code cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int rows = (Integer) rowsSpin.getValue();
            int cols = (Integer) colsSpin.getValue();
            int seats = (Integer) benchSpin.getValue();

            db.addRoom(new Room(code, bldg.isEmpty() ? "Main Block" : bldg, rows, cols, seats));
            codeField.setText("");
            bldgField.setText("");
            refreshAllData();
            log("Added new Exam Hall: " + code + " (Capacity: " + (rows * cols * seats) + ")");
        });

        formCard.add(new JLabel("Room Code:"));
        formCard.add(codeField);
        formCard.add(new JLabel("Building:"));
        formCard.add(bldgField);
        formCard.add(new JLabel("Rows:"));
        formCard.add(rowsSpin);
        formCard.add(new JLabel("Cols:"));
        formCard.add(colsSpin);
        formCard.add(new JLabel("Seats/Bench:"));
        formCard.add(benchSpin);
        formCard.add(addBtn);

        panel.add(formCard, BorderLayout.NORTH);

        // Room Table
        String[] cols = {"Room Code", "Building", "Rows", "Columns", "Seats/Bench", "Total Capacity"};
        roomTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        roomTable = new JTable(roomTableModel);
        roomTable.setRowHeight(26);
        roomTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(roomTable);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ================= TAB 3: STUDENTS =================
    private JPanel createStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add Student Form
        JPanel formCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JTextField rollField = new JTextField(9);
        JTextField nameField = new JTextField(12);
        JComboBox<String> deptCombo = new JComboBox<>(new String[]{"CSE", "ECE", "MECH", "CIVIL", "IT"});
        JSpinner semSpin = new JSpinner(new SpinnerNumberModel(4, 1, 8, 1));

        JButton addBtn = new JButton("➕ Enroll Student");
        addBtn.setBackground(new Color(59, 130, 246));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addBtn.addActionListener(e -> {
            String roll = rollField.getText().trim();
            String name = nameField.getText().trim();
            if (roll.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Roll Number and Name are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String dept = (String) deptCombo.getSelectedItem();
            int sem = (Integer) semSpin.getValue();

            db.addStudent(new Student(roll, name, dept, sem));
            rollField.setText("");
            nameField.setText("");
            refreshAllData();
            log("Enrolled Student: " + roll + " - " + name + " (" + dept + ")");
        });

        formCard.add(new JLabel("Roll Number:"));
        formCard.add(rollField);
        formCard.add(new JLabel("Student Name:"));
        formCard.add(nameField);
        formCard.add(new JLabel("Department:"));
        formCard.add(deptCombo);
        formCard.add(new JLabel("Semester:"));
        formCard.add(semSpin);
        formCard.add(addBtn);

        panel.add(formCard, BorderLayout.NORTH);

        // Student Table
        String[] cols = {"Roll Number", "Student Name", "Department", "Semester"};
        studentTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setRowHeight(26);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(studentTable);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ================= TAB 4: VISUAL HALL MATRIX =================
    private JPanel createVisualMatrixTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        controlBar.setBackground(Color.WHITE);
        controlBar.setBorder(new EmptyBorder(8, 8, 8, 8));

        controlBar.add(new JLabel("Select Exam Hall to Visualize:"));
        matrixRoomCombo = new JComboBox<>();
        matrixRoomCombo.setPreferredSize(new Dimension(280, 30));
        matrixRoomCombo.addActionListener(e -> updateVisualMatrix());
        controlBar.add(matrixRoomCombo);

        JButton refreshMatrixBtn = new JButton("🔄 Refresh Grid");
        refreshMatrixBtn.addActionListener(e -> updateVisualMatrix());
        controlBar.add(refreshMatrixBtn);

        panel.add(controlBar, BorderLayout.NORTH);

        visualMatrixPanel = new VisualHallMatrixPanel();
        panel.add(visualMatrixPanel, BorderLayout.CENTER);
        return panel;
    }

    // ================= TAB 5: STUDENT SEAT SEARCH =================
    private JPanel createSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel searchBarCard = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        searchBarCard.setBackground(Color.WHITE);
        searchBarCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel prompt = new JLabel("Enter Student Roll Number:");
        prompt.setFont(new Font("Segoe UI", Font.BOLD, 15));
        searchRollField = new JTextField(15);
        searchRollField.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JButton searchBtn = new JButton("🔍 Find My Seat");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchBtn.setBackground(new Color(37, 99, 235));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> performSeatSearch());

        searchBarCard.add(prompt);
        searchBarCard.add(searchRollField);
        searchBarCard.add(searchBtn);

        panel.add(searchBarCard, BorderLayout.NORTH);

        // Result Card
        searchResultPanel = new JPanel(new BorderLayout(15, 15));
        searchResultPanel.setBackground(Color.WHITE);
        searchResultPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(25, 25, 25, 25)
        ));

        searchResultTitle = new JLabel("Enter a roll number above to search allocated exam seat.", JLabel.CENTER);
        searchResultTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        searchResultTitle.setForeground(new Color(100, 116, 139));

        searchResultDetails = new JLabel("", JLabel.CENTER);
        searchResultDetails.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        searchResultDetails.setForeground(new Color(15, 23, 42));

        searchResultPanel.add(searchResultTitle, BorderLayout.NORTH);
        searchResultPanel.add(searchResultDetails, BorderLayout.CENTER);

        panel.add(searchResultPanel, BorderLayout.CENTER);
        return panel;
    }

    private void performSeatSearch() {
        String roll = searchRollField.getText().trim();
        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a roll number to search.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SeatAllocation sa = db.searchAllocationByRoll(roll);
        if (sa != null) {
            searchResultTitle.setText("✅ ALLOCATION FOUND FOR: " + sa.getStudentRoll());
            searchResultTitle.setForeground(new Color(16, 185, 129));

            String html = String.format("<html><div style='text-align: center; line-height: 1.8;'>" +
                            "<h2 style='color: #1e3a8a; margin-bottom: 5px;'>Examination Hall: %s</h2>" +
                            "<p style='font-size: 14px;'><b>Student Name:</b> %s &nbsp;&nbsp;|&nbsp;&nbsp; <b>Branch:</b> %s</p>" +
                            "<p style='font-size: 16px; background-color: #eff6ff; padding: 10px; border-radius: 8px; display: inline-block;'>" +
                            "<b>Bench Coordinate:</b> Row %d &nbsp;•&nbsp; Column %d &nbsp;•&nbsp; <b>Position:</b> %s Seat" +
                            "</p></div></html>",
                    sa.getRoomCode(), sa.getStudentName(), sa.getDepartment(),
                    sa.getBenchRow(), sa.getBenchCol(), sa.getSeatPositionText());
            searchResultDetails.setText(html);
        } else {
            searchResultTitle.setText("❌ NO SEAT ALLOCATED YET FOR: " + roll.toUpperCase());
            searchResultTitle.setForeground(new Color(239, 68, 68));
            searchResultDetails.setText("<html><p style='color: #64748b;'>Either seating has not been generated, or this roll number is not registered.</p></html>");
        }
    }

    // ================= TAB 6: EXPORT =================
    private JPanel createExportTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        topBar.setOpaque(false);

        JButton exportCsvBtn = new JButton("💾 Save Seating Chart (CSV)");
        exportCsvBtn.setBackground(new Color(16, 185, 129));
        exportCsvBtn.setForeground(Color.WHITE);
        exportCsvBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        exportCsvBtn.addActionListener(e -> exportSeatingToCsv());

        JButton refreshPreviewBtn = new JButton("🔄 Refresh Report Preview");
        refreshPreviewBtn.addActionListener(e -> generateReportPreview());

        topBar.add(exportCsvBtn);
        topBar.add(refreshPreviewBtn);
        panel.add(topBar, BorderLayout.NORTH);

        exportPreviewArea = new JTextArea();
        exportPreviewArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        exportPreviewArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(exportPreviewArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Seating Chart Notice Board Sheet"));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void exportSeatingToCsv() {
        List<SeatAllocation> list = db.getAllocationsForExam(currentExam.getId());
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No allocations to export! Please run seating allocation first.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("exam_seating_chart.csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Exam Name,Hall Code,Row,Column,Seat Position,Roll Number,Student Name,Department\n");
                for (SeatAllocation sa : list) {
                    writer.write(String.format("\"%s\",\"%s\",%d,%d,\"%s\",\"%s\",\"%s\",\"%s\"\n",
                            sa.getExamName(), sa.getRoomCode(), sa.getBenchRow(), sa.getBenchCol(),
                            sa.getSeatPositionText(), sa.getStudentRoll(), sa.getStudentName(), sa.getDepartment()));
                }
                JOptionPane.showMessageDialog(this, "Seating chart exported successfully to:\n" + file.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to save file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void generateReportPreview() {
        List<SeatAllocation> list = db.getAllocationsForExam(currentExam.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append(String.format("                %s - SEATING CHART NOTICE SHEET\n", currentExam.getExamName().toUpperCase()));
        sb.append("========================================================================================\n");
        sb.append(String.format("%-10s | %-12s | %-20s | %-8s | %-12s | %-10s\n",
                "HALL", "ROLL NO", "STUDENT NAME", "BRANCH", "BENCH COORD", "POSITION"));
        sb.append("----------------------------------------------------------------------------------------\n");

        for (SeatAllocation sa : list) {
            sb.append(String.format("%-10s | %-12s | %-20s | %-8s | R%-2d-C%-8d | %-10s\n",
                    sa.getRoomCode(), sa.getStudentRoll(), sa.getStudentName(), sa.getDepartment(),
                    sa.getBenchRow(), sa.getBenchCol(), sa.getSeatPositionText()));
        }

        if (list.isEmpty()) {
            sb.append("\n  [!] No seating has been generated yet. Go to Dashboard and click 'Run Smart Seating Allocation'.\n");
        } else {
            sb.append("----------------------------------------------------------------------------------------\n");
            sb.append(String.format("Total Seated Students: %d\n", list.size()));
        }
        exportPreviewArea.setText(sb.toString());
    }

    // ================= CORE ACTIONS =================
    private void triggerSmartAllocation() {
        List<Room> rooms = db.getAllRooms();
        List<Student> students = db.getAllStudents();

        log("Starting anti-cheating allocation for " + students.size() + " students across " + rooms.size() + " rooms...");
        SeatingArrangementEngine.AllocationResult result = engine.allocateSeats(currentExam.getId(), rooms, students);

        if (result.isSuccess()) {
            db.saveAllocations(currentExam.getId(), result.getAllocations());
            log(result.getMessage());
            JOptionPane.showMessageDialog(this, result.getMessage(), "Allocation Completed", JOptionPane.INFORMATION_MESSAGE);
            refreshAllData();
        } else {
            log("ALLOCATION FAILED: " + result.getMessage());
            JOptionPane.showMessageDialog(this, result.getMessage(), "Allocation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetSeating() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to reset all seating allocations?", "Confirm Reset", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            db.clearAllocationsForExam(currentExam.getId());
            refreshAllData();
            log("Seating allocations cleared.");
        }
    }

    private void updateVisualMatrix() {
        Room selected = (Room) matrixRoomCombo.getSelectedItem();
        if (selected != null) {
            List<SeatAllocation> allocations = db.getAllocationsForExam(currentExam.getId());
            visualMatrixPanel.displayRoomLayout(selected, allocations);
        }
    }

    public void refreshAllData() {
        List<Room> rooms = db.getAllRooms();
        List<Student> students = db.getAllStudents();
        List<SeatAllocation> allocations = db.getAllocationsForExam(currentExam.getId());

        int totalCap = 0;
        for (Room r : rooms) totalCap += r.getTotalCapacity();

        // Update Dashboard cards
        totalStudentsCardLbl.setText(String.valueOf(students.size()));
        totalRoomsCardLbl.setText(String.valueOf(rooms.size()));
        totalCapacityCardLbl.setText(String.valueOf(totalCap));

        if (allocations.isEmpty()) {
            seatingStatusCardLbl.setText("Pending");
            seatingStatusCardLbl.setForeground(new Color(245, 158, 11));
        } else {
            seatingStatusCardLbl.setText("Allocated (" + allocations.size() + ")");
            seatingStatusCardLbl.setForeground(new Color(16, 185, 129));
        }

        // Refresh Rooms Table
        roomTableModel.setRowCount(0);
        for (Room r : rooms) {
            roomTableModel.addRow(new Object[]{
                    r.getRoomCode(), r.getBuilding(), r.getTotalRows(), r.getTotalColumns(),
                    r.getSeatsPerBench(), r.getTotalCapacity()
            });
        }

        // Refresh Students Table
        studentTableModel.setRowCount(0);
        for (Student s : students) {
            studentTableModel.addRow(new Object[]{
                    s.getRollNumber(), s.getName(), s.getDepartment(), s.getSemester()
            });
        }

        // Refresh Room combo in visual matrix tab
        Room currentSelection = (Room) matrixRoomCombo.getSelectedItem();
        matrixRoomCombo.removeAllItems();
        for (Room r : rooms) {
            matrixRoomCombo.addItem(r);
        }
        if (currentSelection != null) {
            for (int i = 0; i < matrixRoomCombo.getItemCount(); i++) {
                if (matrixRoomCombo.getItemAt(i).getId() == currentSelection.getId()) {
                    matrixRoomCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        updateVisualMatrix();
        generateReportPreview();
    }

    private void log(String message) {
        String ts = String.format("[%tT] ", System.currentTimeMillis());
        logArea.append(ts + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
