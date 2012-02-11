package com.hexhaus.icarrus.ui;

import com.hexhaus.icarrus.dao.CredentialDao;
import com.hexhaus.icarrus.dao.ImageDao;
import com.hexhaus.icarrus.dao.LoggingDao;
import com.hexhaus.icarrus.dao.LoginDao;
import com.hexhaus.icarrus.handler.MessageHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: 67726e
 * Date: 8/5/11
 * Time: 10:20 PM
 */
class LoginForm extends JFrame {
    private final int WIDTH = 300, HEIGHT = 200;
    private LoginForm form_pointer = this;                                                      // Pointer used to access this JFrame

    public LoginForm() {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = environment.getDefaultScreenDevice();
        DisplayMode displayMode = device.getDisplayMode();                                      // Get object containing the size of the available screen real estate
        int windowX = ((displayMode.getWidth() - WIDTH) / 2);                                   // Calculate X coordinate so the window is horizontally centered
        int windowY = ((displayMode.getHeight() - HEIGHT) / 2);                                 // Calculate Y coordinate so the window is vertically centered

        getContentPane().add(new LoginPanel());                                                 // Add in LoginPanel object containing layout for this dialog
        setBounds(windowX, windowY, WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);                                      // Only dispose of system resources used by this window upon close
        setTitle("Icarrus ~ Login");
        setResizable(false);
        setIconImage(ImageDao.getImage("wingIcon-32x32.png"));                                  // Attempt to set the window's icon image
    }

    private class LoginPanel extends JPanel {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JCheckBox rememberMe;

        public LoginPanel() {
            EnterListener enterListener = new EnterListener();                                  // Listener object for all login initiating events
            this.setLayout(null);

            JLabel username = new JLabel("Username:");
            username.setBounds(30, 20, 100, 20);
            this.add(username);

            usernameField = new JTextField();
            usernameField.setBounds(130, 20, 120, 20);
            usernameField.addKeyListener(enterListener);
            usernameField.addActionListener(enterListener);
            this.add(usernameField);

            JLabel password = new JLabel("Password:");
            password.setBounds(30, 60, 100, 20);
            this.add(password);

            passwordField = new JPasswordField();
            passwordField.setBounds(130, 60, 120, 20);
            passwordField.addKeyListener(enterListener);
            this.add(passwordField);

            rememberMe = new JCheckBox("Remember Me");
            rememberMe.setBounds(127, 90, 95, 20);
            this.add(rememberMe);

            JButton login = new JButton("Login");
            login.setBounds(50, 135, 80, 25);
            login.addActionListener(enterListener);
            this.add(login);

            JButton cancel = new JButton("Cancel");
            cancel.setBounds(150, 135, 80, 25);
            this.add(cancel);

            usernameField.requestFocusInWindow();                                               // Start off the first field with focus for one of those UX niceties
        }

        private void loginClicked() {
            LoginDao loginServerDAO = new LoginDao(
                        usernameField.getText(), passwordField.getPassword());                  // Create object to interface with the login server
            if (loginServerDAO.getLoginStatus()) {                                              // Check for a successful login
                if (rememberMe.isSelected()) CredentialDao.storeCredentials(
                        usernameField.getText(), passwordField.getPassword());                  // Store the username and password in a file for automatic login
                form_pointer.dispose();                                                         // Clear up resources used by this form
                ExtendedTrayIcon.setLoginStatus("Logout");
                MessageHandler.postMessage("Logged In", "You have been successfully logged in.", LoggingDao.Status.Info);
            }
        }

        private class EnterListener implements KeyListener, ActionListener {
            public void keyReleased(KeyEvent event) {}
            public void keyTyped(KeyEvent event) {}
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {                                  // Check if the key pressed was ENTER
                    loginClicked();                                                             // Initiate a login if ENTER was pressed
                }
            }
            public void actionPerformed(ActionEvent event) {
                loginClicked();                                                                 // Initiate a login action when the button is clicked
            }
        }

        private class CancelListener implements ActionListener {
            public void actionPerformed(ActionEvent event) {
                usernameField.setText("");
                passwordField.setText("");
                usernameField.requestFocusInWindow();
            }
        }
    }
}
