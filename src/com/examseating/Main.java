package com.examseating;

import com.examseating.dao.DatabaseManager;
import com.examseating.ui.MainDashboardFrame;

import javax.swing.*;

/**
 * Main application entry point for the Smart Exam Seating Arrangement System.
 */
public class Main {
    public static void main(String[] args) {
        // Set modern native/system Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback to cross-platform standard
        }

        // Initialize Database DAO and seed default data
        DatabaseManager.getInstance().seedSampleDataIfEmpty();

        // Launch GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainDashboardFrame frame = new MainDashboardFrame();
            frame.setVisible(true);
        });
    }
}
