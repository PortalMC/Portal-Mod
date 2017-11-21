package org.portalmc.core.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DownloadFrame extends JFrame {

    public DownloadFrame() {
        super("Download");

        JPanel center = new JPanel();
        {
            center.setLayout(new BorderLayout());
            JLabel lblText = new JLabel("Downloading mod...");
            center.add(lblText, BorderLayout.CENTER);
        }

        JPanel root = new JPanel();
        {
            root.setLayout(new BorderLayout());
            root.setBorder(new EmptyBorder(10, 10, 10, 10));
            root.add(center, BorderLayout.CENTER);
        }

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().add(root, BorderLayout.CENTER);
        setResizable(false);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }
}
