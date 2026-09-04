package com.ritajshakeel.rrs.view.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.WindowConstants;

import com.ritajshakeel.rrs.controller.RRSController;
import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.domain.User;
import com.ritajshakeel.rrs.view.RRSView;

public class RRSSwingView extends JFrame implements RRSView {

    private static final long serialVersionUID = 1L;

    private transient RRSController controller;
    private JTextField nameTextField;
    private JButton registerButton;
    private JLabel errorLabel;
    private JComboBox<User> actingAsComboBox;

    public RRSSwingView() {
    	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        setResizable(false);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        JPanel actingAsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actingAsPanel.add(new JLabel("Acting as:"));
        actingAsComboBox = new JComboBox<>();
        actingAsComboBox.setName("actingAsComboBox");
        actingAsPanel.add(actingAsComboBox);
        contentPane.add(actingAsPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setName("tabbedPane");
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addTab("Register", buildRegisterPanel());
        tabbedPane.addTab("Resources", new JPanel());
        tabbedPane.addTab("Book", new JPanel());
        tabbedPane.addTab("My Reservations", new JPanel());
    }

    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        nameTextField = new JTextField(15);
        nameTextField.setName("nameTextField");
        panel.add(nameTextField);

        registerButton = new JButton("Register");
        registerButton.setEnabled(false);
        registerButton.addActionListener(e -> controller.registerUser(nameTextField.getText()));
        panel.add(registerButton);

        errorLabel = new JLabel(" ");
        errorLabel.setName("errorLabel");
        panel.add(errorLabel);

        nameTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                registerButton.setEnabled(!nameTextField.getText().trim().isEmpty());
            }
        });

        return panel;
    }

    public void setController(RRSController controller) {
        this.controller = controller;
    }

    @Override
    public void userRegistered(User user) {
        nameTextField.setText("");
        errorLabel.setText(" ");
        actingAsComboBox.addItem(user);
    }

    @Override
    public void showError(String message) {
        errorLabel.setText(message);
    }

    @Override
    public void usersListed(List<User> users) {
        actingAsComboBox.removeAllItems();
        for (User user : users) {
            actingAsComboBox.addItem(user);
        }
    }

    @Override
    public void resourceRegistered(Resource resource) {
        // to implement
    }

    @Override
    public void resourcesListed(List<Resource> resources) {
        // to implement
    }

    @Override
    public void reservationBooked(Reservation reservation) {
        // to implement
    }

    @Override
    public void reservationsListed(List<Reservation> reservations) {
        // to implement
    }

    @Override
    public void reservationConfirmed(Reservation reservation) {
        // to implement
    }

    @Override
    public void reservationCancelled(Reservation reservation) {
        // to implement
    }
}