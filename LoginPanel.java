import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton;

    public LoginPanel(JFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(Color.DARK_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0;
        add(userLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0;
        add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1;
        add(passLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        add(passwordField, gbc);

        loginButton = new JButton("Login");
        signupButton = new JButton("Signup");

        gbc.gridx = 0; gbc.gridy = 2;
        add(loginButton, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        add(signupButton, gbc);

        // ✅ Login Action
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            int playerId = PlayerDAO.login(username, password);

            if (playerId != -1) {
                JOptionPane.showMessageDialog(this, "Login Successful!");

                // 👉 Switch to Home/Game panel
                frame.getContentPane().removeAll();
                frame.add(new HomeMenuPanel(frame, playerId, username)); // go to home menu
                frame.revalidate();
                frame.repaint();

            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        });

        // ✅ Signup Action
        signupButton.addActionListener(e -> {
            boolean ok = PlayerDAO.signup(usernameField.getText(), new String(passwordField.getPassword()));
            JOptionPane.showMessageDialog(this, ok ? "Signup Successful!" : "Signup Failed!");
        });
    }
}
