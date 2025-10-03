import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
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

            // --- Login Panel UI with background image ---
Image bgImage = new ImageIcon("E:/JAVA-PROJECT/DevilLevelGame/assets/LOGIN.png").getImage(); // change path as needed

JPanel loginPanel = new JPanel(new GridBagLayout()) {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this); // scale to full panel size
    }
};

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(15, 15, 15, 15);

            // Username Label
            JLabel userLabel = new JLabel("Username:");
            userLabel.setForeground(Color.WHITE);
            userLabel.setFont(new Font("Verdana", Font.BOLD, 22));
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;
            loginPanel.add(userLabel, gbc);

            // Username Field
            JTextField usernameField = new JTextField(18);
            usernameField.setFont(new Font("Arial", Font.PLAIN, 20));
            usernameField.setBackground(Color.WHITE);
            usernameField.setForeground(Color.BLACK);
            usernameField.setCaretColor(Color.BLUE);
            gbc.gridx = 1; gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            loginPanel.add(usernameField, gbc);

            // Password Label
            JLabel passLabel = new JLabel("Password:");
            passLabel.setForeground(Color.WHITE);
            passLabel.setFont(new Font("Verdana", Font.BOLD, 22));
            gbc.gridx = 0; gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.EAST;
            loginPanel.add(passLabel, gbc);

            // Password Field
            JPasswordField passwordField = new JPasswordField(18);
            passwordField.setFont(new Font("Arial", Font.PLAIN, 20));
            passwordField.setBackground(Color.WHITE);
            passwordField.setForeground(Color.BLACK);
            passwordField.setCaretColor(Color.BLUE);
            gbc.gridx = 1; gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.WEST;
            loginPanel.add(passwordField, gbc);

            // Buttons Panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setOpaque(false); // transparent panel
            JButton loginButton = new JButton("Login");
            loginButton.setFont(new Font("Arial", Font.BOLD, 18));
            loginButton.setBackground(Color.WHITE);
            loginButton.setForeground(Color.BLUE);
            loginButton.setFocusPainted(false);

            JButton signupButton = new JButton("Signup");
            signupButton.setFont(new Font("Arial", Font.BOLD, 18));
            signupButton.setBackground(Color.WHITE);
            signupButton.setForeground(Color.BLUE);
            signupButton.setFocusPainted(false);

            buttonPanel.add(loginButton);
            buttonPanel.add(signupButton);

            gbc.gridx = 0; gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            loginPanel.add(buttonPanel, gbc);

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
