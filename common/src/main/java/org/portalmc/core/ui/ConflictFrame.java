package org.portalmc.core.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ConflictFrame extends JFrame {

    private Optional<Runnable> onKeepClick = Optional.empty();
    private Optional<Runnable> onDeleteClick = Optional.empty();
    private Optional<Runnable> onCancelClick = Optional.empty();

    public ConflictFrame(List<File> conflicts) {
        super("Conflict");

        JPanel top = new JPanel();
        {
            top.setLayout(new BorderLayout());
            top.setBorder(new EmptyBorder(0, 0, 10, 0));
            JLabel lblText = new JLabel("Following mod(s) that already be in mods directory are may be conflict.\n");
            top.add(lblText, BorderLayout.CENTER);
        }

        JPanel center = new JPanel();
        {
            center.setLayout(new BorderLayout(2, 2));
            center.setBorder(new EmptyBorder(0, 15, 0, 15));
            JTextPane lblList = new JTextPane();
            JScrollPane sp = new JScrollPane(lblList);
            sp.setPreferredSize(new Dimension(500, 250));
            lblList.setText(conflicts.stream().map(File::getName).collect(Collectors.joining("\n")));
            lblList.setEditable(false);
            lblList.setFocusable(false);
            center.add(sp, BorderLayout.CENTER);
        }

        JPanel bottom = new JPanel();
        {
            bottom.setLayout(new FlowLayout(FlowLayout.RIGHT));
            JButton btnKeep = new JButton("Keep");
            JButton btnDelete = new JButton("Delete");
            JButton btnCancel = new JButton("Cancel");
            btnKeep.addActionListener(e -> onKeepClick.ifPresent(Runnable::run));
            btnDelete.addActionListener(e -> onDeleteClick.ifPresent(Runnable::run));
            btnCancel.addActionListener(e -> onCancelClick.ifPresent(Runnable::run));
            bottom.add(btnKeep);
            bottom.add(btnDelete);
            bottom.add(btnCancel);
        }

        JPanel root = new JPanel();
        {
            root.setLayout(new BorderLayout());
            root.setBorder(new EmptyBorder(10, 10, 10, 10));
            root.add(top, BorderLayout.NORTH);
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

    public void setOnKeepClickListener(Runnable action) {
        onKeepClick = Optional.of(action);
    }

    public void setOnDeleteClickListener(Runnable action) {
        onDeleteClick = Optional.of(action);
    }

    public void setOnCancelClickListener(Runnable action) {
        onCancelClick = Optional.of(action);
    }
}
