package org.portalmc.core.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ErrorFrame extends JFrame {

    private final JLabel lblError;
    private final JButton btnOk;

    private Optional<Runnable> onOKClick = Optional.empty();

    public ErrorFrame() {
        super("Error");

        JPanel center = new JPanel();
        {
            center.setLayout(new BorderLayout());
            JLabel lblLabel = new JLabel("An error has occurred:");
            lblError = new JLabel("");
            center.add(lblLabel, BorderLayout.NORTH);
            center.add(lblError, BorderLayout.CENTER);
        }


        JPanel bottom = new JPanel();
        {
            bottom.setLayout(new FlowLayout());
            bottom.setBorder(new EmptyBorder(0, 100, 0, 100));
            btnOk = new JButton("OK");
            btnOk.addActionListener(e -> onOKClick.ifPresent(Runnable::run));
            bottom.add(btnOk);
        }

        JPanel root = new JPanel();
        {
            root.setLayout(new BorderLayout());
            root.setBorder(new EmptyBorder(10, 10, 10, 10));
            root.add(center, BorderLayout.CENTER);
            root.add(bottom, BorderLayout.SOUTH);
        }

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().add(root, BorderLayout.CENTER);
        setResizable(false);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    public void setErrorMessage(String message) {
        lblError.setText(message);
        pack();
    }

    public void setOnOkClickListener(Runnable action) {
        onOKClick = Optional.of(action);
    }
}
