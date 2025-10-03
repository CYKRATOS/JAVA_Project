import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class GameLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Equilibrium");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // full screen

            // --- Login Panel UI ---
            JPanel loginPanel = new JPanel();
            loginPanel.setLayout(new GridBagLayout());
            loginPanel.setBackground(Color.DARK_GRAY);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            JLabel userLabel = new JLabel("Username:");
            userLabel.setForeground(Color.WHITE);
            gbc.gridx = 0; gbc.gridy = 0;
            loginPanel.add(userLabel, gbc);

            JTextField usernameField = new JTextField(15);
            gbc.gridx = 1; gbc.gridy = 0;
            loginPanel.add(usernameField, gbc);

            JLabel passLabel = new JLabel("Password:");
            passLabel.setForeground(Color.WHITE);
            gbc.gridx = 0; gbc.gridy = 1;
            loginPanel.add(passLabel, gbc);

            JPasswordField passwordField = new JPasswordField(15);
            gbc.gridx = 1; gbc.gridy = 1;
            loginPanel.add(passwordField, gbc);

            JButton loginButton = new JButton("Login");
            JButton signupButton = new JButton("Signup");
            gbc.gridx = 0; gbc.gridy = 2;
            loginPanel.add(loginButton, gbc);
            gbc.gridx = 1; gbc.gridy = 2;
            loginPanel.add(signupButton, gbc);

            frame.add(loginPanel);
            frame.setVisible(true);

            // --- Login Action ---
            loginButton.addActionListener(e -> {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                int playerId = PlayerDAO.login(username, password);
                if (playerId != -1) {
                    JOptionPane.showMessageDialog(frame, "Login Successful!");

                    // Switch to HomeMenuPanel
                    frame.getContentPane().removeAll();
                    frame.add(new HomeMenuPanel(frame, playerId, username));
                    frame.revalidate();
                    frame.repaint();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid credentials!");
                }
            });

            // --- Signup Action ---
            signupButton.addActionListener(e -> {
                boolean ok = PlayerDAO.signup(usernameField.getText(), new String(passwordField.getPassword()));
                JOptionPane.showMessageDialog(frame, ok ? "Signup Successful!" : "Signup Failed!");
            });
        });
    }
}
