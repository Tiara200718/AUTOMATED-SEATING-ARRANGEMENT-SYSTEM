package com.examseating.util;

import com.examseating.dao.DatabaseManager;
import com.examseating.ui.MainDashboardFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Headless-capable / Swing screenshot generator that renders pixel-perfect PNG previews
 * of all primary application tabs for project reports, PPT slides, and GitHub documentation.
 */
public class PreviewGenerator {
    private static final String WORKSPACE_DIR = "previews";
    private static final String ARTIFACT_DIR = "C:\\Users\\bhask\\.gemini\\antigravity-ide\\brain\\e9ef9784-3021-427d-92b4-03cdfea48693";

    public static void main(String[] args) {
        System.out.println("[PreviewGenerator] Generating high-resolution UI previews...");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        DatabaseManager.getInstance().seedSampleDataIfEmpty();

        File wsDir = new File(WORKSPACE_DIR);
        if (!wsDir.exists()) wsDir.mkdirs();

        File artDir = new File(ARTIFACT_DIR);
        if (!artDir.exists()) artDir.mkdirs();

        SwingUtilities.invokeLater(() -> {
            try {
                MainDashboardFrame frame = new MainDashboardFrame();
                frame.setSize(1180, 780);
                frame.addNotify();
                frame.validate();

                // 1. Dashboard Tab (Tab 0) with completed allocation
                frame.runAllocationQuietly();
                frame.selectTab(0);
                frame.getContentPane().validate();
                frame.getContentPane().repaint();
                saveComponentImage(frame.getContentPane(), "preview_dashboard.png");

                // 2. Visual Hall Matrix Tab (Tab 3)
                frame.selectTab(3);
                frame.selectMatrixRoom(0);
                frame.getContentPane().validate();
                frame.getContentPane().repaint();
                saveComponentImage(frame.getContentPane(), "preview_visual_matrix.png");

                // 3. Student Search Tab (Tab 4) with query
                frame.selectTab(4);
                frame.searchRollProgrammatically("22CSE001");
                frame.getContentPane().validate();
                frame.getContentPane().repaint();
                saveComponentImage(frame.getContentPane(), "preview_search.png");

                // 4. Room Management Tab (Tab 1)
                frame.selectTab(1);
                frame.getContentPane().validate();
                frame.getContentPane().repaint();
                saveComponentImage(frame.getContentPane(), "preview_rooms.png");

                // 5. Seating Chart Export Tab (Tab 5)
                frame.selectTab(5);
                frame.getContentPane().validate();
                frame.getContentPane().repaint();
                saveComponentImage(frame.getContentPane(), "preview_export.png");

                System.out.println("[PreviewGenerator] All 5 high-resolution previews generated successfully!");
                frame.dispose();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static void saveComponentImage(Container container, String fileName) throws Exception {
        int width = Math.max(container.getWidth(), 1180);
        int height = Math.max(container.getHeight(), 780);
        container.setSize(width, height);
        container.doLayout();
        container.validate();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        container.printAll(g2);
        g2.dispose();

        // Save to workspace directory
        File wsFile = new File(WORKSPACE_DIR, fileName);
        ImageIO.write(image, "png", wsFile);
        System.out.println("  -> Saved: " + wsFile.getAbsolutePath());

        // Save to artifact directory for markdown embedding
        File artFile = new File(ARTIFACT_DIR, fileName);
        ImageIO.write(image, "png", artFile);
        System.out.println("  -> Saved: " + artFile.getAbsolutePath());
    }
}
