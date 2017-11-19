package org.portalmc.core.ui;

import org.portalmc.core.model.Project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ProjectListFrame extends JFrame {

    private final JLabel lblStatusLeft;
    private final JLabel lblStatusRight;
    private final JLabel lblError;
    private final JButton btnConfirm;
    private final JTable table;
    private final DefaultTableModel tableModel;

    private Optional<Runnable> onConfirmClick = Optional.empty();
    private Optional<Runnable> onCancelClick = Optional.empty();

    private Optional<String> selectedProjectId = Optional.empty();

    public ProjectListFrame() {
        super("Project List");

        JPanel top = new JPanel();
        {
            top.setLayout(new BorderLayout());
            top.setBorder(new EmptyBorder(0, 0, 10, 0));
            lblStatusLeft = new JLabel("");
            lblStatusRight = new JLabel("");
            lblError = new JLabel("");
            lblError.setForeground(Color.red);
            top.add(lblStatusLeft, BorderLayout.WEST);
            top.add(lblStatusRight, BorderLayout.EAST);
            top.add(lblError, BorderLayout.SOUTH);
        }

        JPanel bottom = new JPanel();
        {
            bottom.setLayout(new FlowLayout(FlowLayout.RIGHT));
            btnConfirm = new JButton("Confirm");
            JButton btnCancel = new JButton("Cancel");
            btnConfirm.setEnabled(false);
            btnConfirm.addActionListener(e -> onConfirmClick.ifPresent(Runnable::run));
            btnCancel.addActionListener(e -> onCancelClick.ifPresent(Runnable::run));
            bottom.add(btnConfirm);
            bottom.add(btnCancel);
        }

        JPanel center = new JPanel();
        {
            center.setLayout(new BorderLayout());
            tableModel = new DefaultTableModel(new String[][]{}, new String[]{"Name", "Version", "Forge", "Id"});
            table = new JTable(tableModel);
            JScrollPane sp = new JScrollPane(table);
            sp.setPreferredSize(new Dimension(600, 250));
            table.getSelectionModel().addListSelectionListener(e -> {
                int selectedRow = table.getSelectedRow();
                String selectedData = (String) table.getValueAt(selectedRow, 3);
                selectedProjectId = Optional.of(selectedData);
                btnConfirm.setEnabled(true);
            });
            table.setDefaultEditor(Object.class, null);
            center.add(sp, BorderLayout.CENTER);
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

    public void setStatusLeft(String status) {
        lblStatusLeft.setText(status);
    }

    public void setStatusRight(String status) {
        lblStatusRight.setText(status);
    }

    public void setErrorMessage(String message) {
        lblError.setText(message);
        pack();
    }

    public void setOnConfirmClickListener(Runnable action) {
        onConfirmClick = Optional.of(action);
    }

    public void setOnCancelClickListener(Runnable action) {
        onCancelClick = Optional.of(action);
    }

    public void addProjects(List<Project> projects) {
        for (Project project : projects) {
            tableModel.addRow(new String[]{project.name, project.minecraftVersion, project.forgeVersion, project.id});
        }
    }

    public String getSelectedProjectId() {
        return selectedProjectId.orElse("");
    }
}
