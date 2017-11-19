package org.portalmc.core.ui;

import org.portalmc.core.model.CoremodConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class LoginFrame extends JFrame {

    private final JLabel lblStatus;
    private final JLabel lblError;
    private final JTextField textPassword;
    private final JTextField textEmail;
    private final JButton btnLogin;
    private final JButton btnCancel;
    private Optional<Runnable> onLoginClick = Optional.empty();
    private Optional<Runnable> onCancelClick = Optional.empty();

    public LoginFrame(CoremodConfig config) {
        super("Login");

        JPanel top = new JPanel();
        {
            top.setLayout(new BorderLayout());
            top.setBorder(new EmptyBorder(0, 0, 10, 0));
            JLabel lblUrl = new JLabel("URL: " + config.hostname);
            lblStatus = new JLabel("");
            lblError = new JLabel("");
            lblError.setForeground(Color.red);
            top.add(lblUrl, BorderLayout.CENTER);
            top.add(lblStatus, BorderLayout.EAST);
            top.add(lblError, BorderLayout.SOUTH);
        }

        JPanel center = new JPanel();
        {
            center.setLayout(new GridLayout(2, 2));
            JLabel lblEmail = new JLabel("Email");
            JLabel lblPassword = new JLabel("Password");
            textEmail = new JTextField();
            textPassword = new JPasswordField();
            center.add(lblEmail);
            center.add(textEmail);
            center.add(lblPassword);
            center.add(textPassword);
        }

        JPanel bottom = new JPanel();
        {
            bottom.setLayout(new FlowLayout());
            bottom.setBorder(new EmptyBorder(0, 100, 0, 100));
            btnLogin = new JButton("Login");
            btnCancel = new JButton("Cancel");
            btnLogin.addActionListener(e -> onLoginClick.ifPresent(Runnable::run));
            btnCancel.addActionListener(e -> onCancelClick.ifPresent(Runnable::run));
            bottom.add(btnLogin);
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

    public void setFreeze(boolean isFreeze) {
        textEmail.setEnabled(!isFreeze);
        textPassword.setEnabled(!isFreeze);
        btnLogin.setEnabled(!isFreeze);
        btnCancel.setEnabled(!isFreeze);
    }

    public void setStatus(String status) {
        lblStatus.setText(status);
    }

    public void setErrorMessage(String message) {
        lblError.setText(message);
        pack();
    }

    public void setOnLoginClickListener(Runnable action) {
        onLoginClick = Optional.of(action);
    }

    public void setOnCancelClickListener(Runnable action) {
        onCancelClick = Optional.of(action);
    }

    public String getEmail() {
        return textEmail.getText();
    }

    public String getPassword() {
        return textPassword.getText();
    }

}
