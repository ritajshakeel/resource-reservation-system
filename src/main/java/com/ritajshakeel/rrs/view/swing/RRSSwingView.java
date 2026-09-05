package com.ritajshakeel.rrs.view.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.time.LocalDateTime;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.WindowConstants;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.SpinnerDateModel;

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
    private JComboBox<Resource> resourceComboBox;
    
    private JTextField resourceNameTextField;
    private JButton registerResourceButton;
    private JLabel resourceErrorLabel;
    private DefaultListModel<Resource> resourcesListModel;
    private JList<Resource> resourcesList;
    
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JButton bookButton;
    private JLabel bookErrorLabel;

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
        actingAsComboBox.addActionListener(e -> updateBookButtonState());
        actingAsPanel.add(actingAsComboBox);
        contentPane.add(actingAsPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setName("tabbedPane");
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addTab("Register", buildRegisterPanel());
        tabbedPane.addTab("Resources", buildResourcePanel());
        tabbedPane.addTab("Book", buildBookPanel());
        tabbedPane.addTab("My Reservations", new JPanel());
    }

    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        nameTextField = new JTextField(15);
        nameTextField.setName("nameTextField");
        panel.add(nameTextField);

        registerButton = new JButton("Register");
        registerButton.setName("registerUserButton");
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
    
    private JPanel buildResourcePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new FlowLayout());

        resourceNameTextField = new JTextField(15);
        resourceNameTextField.setName("resourceNameTextField");
        formPanel.add(resourceNameTextField);

        registerResourceButton = new JButton("Register");
        registerResourceButton.setName("registerResourceButton");
        registerResourceButton.setEnabled(false);
        registerResourceButton.addActionListener(e -> controller.registerResource(resourceNameTextField.getText()));
        formPanel.add(registerResourceButton);

        resourceErrorLabel = new JLabel(" ");
        resourceErrorLabel.setName("resourceErrorLabel");
        formPanel.add(resourceErrorLabel);

        resourceNameTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                registerResourceButton.setEnabled(!resourceNameTextField.getText().trim().isEmpty());
            }
        });

        resourcesListModel = new DefaultListModel<>();
        resourcesList = new JList<>(resourcesListModel);
        resourcesList.setName("resourcesList");

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resourcesList), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel buildBookPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        panel.add(new JLabel("Resource:"));
        resourceComboBox = new JComboBox<>();
        resourceComboBox.setName("resourceComboBox");
        resourceComboBox.addActionListener(e -> updateBookButtonState());
        panel.add(resourceComboBox);

        panel.add(new JLabel("Start:"));
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setName("startDateSpinner");
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd HH:mm"));
        panel.add(startDateSpinner);

        panel.add(new JLabel("End:"));
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setName("endDateSpinner");
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd HH:mm"));
        panel.add(endDateSpinner);

        bookButton = new JButton("Book");
        bookButton.setName("bookButton");
        bookButton.setEnabled(false);
        bookButton.addActionListener(e -> {
            LocalDateTime start = toLocalDateTime((Date) startDateSpinner.getValue());
            LocalDateTime end = toLocalDateTime((Date) endDateSpinner.getValue());
            User user = (User) actingAsComboBox.getSelectedItem();
            Resource resource = (Resource) resourceComboBox.getSelectedItem();
            controller.bookReservation(user, resource, start, end);
        });
        panel.add(bookButton);

        bookErrorLabel = new JLabel(" ");
        bookErrorLabel.setName("bookErrorLabel");
        panel.add(bookErrorLabel);

        return panel;
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private void updateBookButtonState() {
        boolean hasResource = resourceComboBox.getSelectedItem() != null;
        boolean hasUser = actingAsComboBox.getSelectedItem() != null;
        bookButton.setEnabled(hasResource && hasUser);
    }

    public void setController(RRSController controller) {
        this.controller = controller;
    }
    
    public RRSController getController() {
        return controller;
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
        resourcesListModel.addElement(resource);
        resourceComboBox.addItem(resource);
        resourceErrorLabel.setText(" ");
    }

    @Override
    public void resourcesListed(List<Resource> resources) {
        resourcesListModel.clear();
        resourceComboBox.removeAllItems();
        for (Resource resource : resources) {
            resourcesListModel.addElement(resource);
            resourceComboBox.addItem(resource);
        }
    }

    @Override
    public void reservationBooked(Reservation reservation) {
        startDateSpinner.setValue(new Date());
        endDateSpinner.setValue(new Date());
        bookErrorLabel.setText(" ");
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