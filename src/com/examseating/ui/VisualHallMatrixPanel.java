package com.examseating.ui;

import com.examseating.model.Room;
import com.examseating.model.SeatAllocation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Interactive Graphical Matrix Component that visually renders the physical bench layout
 * of an examination hall, color-coded by branch.
 */
public class VisualHallMatrixPanel extends JPanel {
    private Room room;
    private final Map<String, SeatAllocation> seatMap = new HashMap<>(); // key: "r_c_pos"
    private final JPanel gridPanel;
    private final JLabel headerLabel;
    private final JPanel legendPanel;

    // Harmonious branch color palette
    private static final Map<String, Color> DEPT_COLORS = new HashMap<>();
    static {
        DEPT_COLORS.put("CSE", new Color(37, 99, 235));   // Blue
        DEPT_COLORS.put("ECE", new Color(22, 163, 74));   // Emerald Green
        DEPT_COLORS.put("MECH", new Color(234, 88, 12));  // Orange
        DEPT_COLORS.put("CIVIL", new Color(147, 51, 234));// Purple
        DEPT_COLORS.put("IT", new Color(13, 148, 136));   // Teal
        DEPT_COLORS.put("DEFAULT", new Color(71, 85, 105));// Slate
    }

    public VisualHallMatrixPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top info header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(248, 250, 252));

        headerLabel = new JLabel("Select an Exam Room to view visual seating grid", JLabel.LEFT);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(new Color(15, 23, 42));
        topPanel.add(headerLabel, BorderLayout.WEST);

        // Legend panel
        legendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        legendPanel.setBackground(new Color(248, 250, 252));
        buildLegend();
        topPanel.add(legendPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        gridPanel = new JPanel();
        gridPanel.setBackground(new Color(241, 245, 249));
        gridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void buildLegend() {
        legendPanel.removeAll();
        for (Map.Entry<String, Color> entry : DEPT_COLORS.entrySet()) {
            if (entry.getKey().equals("DEFAULT")) continue;
            JPanel tag = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
            tag.setBackground(new Color(248, 250, 252));
            JLabel colorBox = new JLabel("   ");
            colorBox.setOpaque(true);
            colorBox.setBackground(entry.getValue());
            colorBox.setBorder(new LineBorder(Color.GRAY, 1));
            JLabel label = new JLabel(entry.getKey());
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            tag.add(colorBox);
            tag.add(label);
            legendPanel.add(tag);
        }
    }

    public void displayRoomLayout(Room r, List<SeatAllocation> allocations) {
        this.room = r;
        this.seatMap.clear();

        if (allocations != null) {
            for (SeatAllocation sa : allocations) {
                if (sa.getRoomId() == r.getId()) {
                    String key = sa.getBenchRow() + "_" + sa.getBenchCol() + "_" + sa.getSeatPosition();
                    seatMap.put(key, sa);
                }
            }
        }

        int occupiedCount = seatMap.size();
        headerLabel.setText(String.format("Exam Hall: %s (%s) | Layout: %d Rows x %d Columns | Occupied: %d / %d Seats",
                r.getRoomCode(), r.getBuilding(), r.getTotalRows(), r.getTotalColumns(), occupiedCount, r.getTotalCapacity()));

        gridPanel.removeAll();
        gridPanel.setLayout(new GridLayout(r.getTotalRows(), r.getTotalColumns(), 12, 12));

        for (int row = 1; row <= r.getTotalRows(); row++) {
            for (int col = 1; col <= r.getTotalColumns(); col++) {
                gridPanel.add(createBenchCard(row, col, r.getSeatsPerBench()));
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel createBenchCard(int row, int col, int seatsPerBench) {
        JPanel bench = new JPanel(new BorderLayout(5, 5));
        bench.setBackground(Color.WHITE);
        bench.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel benchHeader = new JLabel("Bench R" + row + "-C" + col, JLabel.CENTER);
        benchHeader.setFont(new Font("Segoe UI", Font.BOLD, 11));
        benchHeader.setForeground(new Color(100, 116, 139));
        bench.add(benchHeader, BorderLayout.NORTH);

        JPanel seatsRow = new JPanel(new GridLayout(1, seatsPerBench, 6, 0));
        seatsRow.setOpaque(false);

        for (int pos = 1; pos <= seatsPerBench; pos++) {
            String key = row + "_" + col + "_" + pos;
            SeatAllocation sa = seatMap.get(key);
            seatsRow.add(createSeatBadge(sa, row, col, pos));
        }

        bench.add(seatsRow, BorderLayout.CENTER);
        return bench;
    }

    private JButton createSeatBadge(SeatAllocation sa, int row, int col, int pos) {
        JButton seatBtn = new JButton();
        seatBtn.setFocusPainted(false);
        seatBtn.setLayout(new BorderLayout());

        if (sa != null) {
            String dept = sa.getDepartment() != null ? sa.getDepartment().toUpperCase() : "DEFAULT";
            Color bgColor = DEPT_COLORS.getOrDefault(dept, DEPT_COLORS.get("DEFAULT"));
            seatBtn.setBackground(bgColor);
            seatBtn.setForeground(Color.WHITE);

            JLabel rollLbl = new JLabel(sa.getStudentRoll(), JLabel.CENTER);
            rollLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            rollLbl.setForeground(Color.WHITE);

            JLabel subLbl = new JLabel(dept + " (Pos " + (pos == 1 ? "L" : "R") + ")", JLabel.CENTER);
            subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            subLbl.setForeground(new Color(241, 245, 249));

            seatBtn.add(rollLbl, BorderLayout.CENTER);
            seatBtn.add(subLbl, BorderLayout.SOUTH);

            seatBtn.setToolTipText(String.format("<html><b>Student:</b> %s<br><b>Roll:</b> %s<br><b>Branch:</b> %s<br><b>Seat:</b> Row %d, Col %d (%s)</html>",
                    sa.getStudentName(), sa.getStudentRoll(), sa.getDepartment(), row, col, pos == 1 ? "Left" : "Right"));

            seatBtn.addActionListener(e -> {
                JOptionPane.showMessageDialog(this,
                        String.format("Roll Number: %s\nStudent Name: %s\nDepartment: %s\nHall: %s\nRow: %d, Column: %d\nBench Position: %s",
                                sa.getStudentRoll(), sa.getStudentName(), sa.getDepartment(),
                                sa.getRoomCode(), row, col, pos == 1 ? "Left" : "Right"),
                        "Allocated Seat Details",
                        JOptionPane.INFORMATION_MESSAGE);
            });
        } else {
            seatBtn.setBackground(new Color(241, 245, 249));
            seatBtn.setForeground(new Color(148, 163, 184));
            JLabel emptyLbl = new JLabel("Empty (" + (pos == 1 ? "L" : "R") + ")", JLabel.CENTER);
            emptyLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            emptyLbl.setForeground(new Color(148, 163, 184));
            seatBtn.add(emptyLbl, BorderLayout.CENTER);
            seatBtn.setEnabled(false);
        }

        return seatBtn;
    }
}
