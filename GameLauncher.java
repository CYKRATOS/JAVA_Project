import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class GameLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // --- LOAD GLOBAL FONT ---
            try {
                Font customFont = Font.createFont(Font.TRUETYPE_FONT,
                        new File("E:/JAVA-PROJECT/DevilLevelGame/assets/fonts/Eater-Regular.ttf"));

                java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(customFont);

                // Apply globally to all Swing components
                Enumeration<Object> keys = UIManager.getDefaults().keys();
                while (keys.hasMoreElements()) {
                    Object key = keys.nextElement();
                    Object value = UIManager.get(key);
                    if (value instanceof FontUIResource) {
                        UIManager.put(key, new FontUIResource(customFont.deriveFont(15f))); // default size
                    }
                }
            } catch (FontFormatException | IOException e) {
            }
            // --- END GLOBAL FONT ---

            JFrame frame = new JFrame("Equilibrium");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            // --- Modern Login Panel ---
            JPanel loginPanel = new JPanel() {
                private final Image bgImage = new ImageIcon("E:/JAVA-PROJECT/DevilLevelGame/assets/LOGIN.png").getImage();

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    int imgWidth = bgImage.getWidth(this);
                    int imgHeight = bgImage.getHeight(this);

                    if (imgWidth > 0 && imgHeight > 0) {
                        double panelRatio = (double) getWidth() / getHeight();
                        double imgRatio = (double) imgWidth / imgHeight;

                        int drawWidth, drawHeight;

                        if (panelRatio > imgRatio) {
                            drawHeight = getHeight();
                            drawWidth = (int) (drawHeight * imgRatio);
                        } else {
                            drawWidth = getWidth();
                            drawHeight = (int) (drawWidth / imgRatio);
                        }

                        int x = (getWidth() - drawWidth) / 2;
                        int y = (getHeight() - drawHeight) / 2;

                        g.drawImage(bgImage, x, y, drawWidth, drawHeight, this);
                    }
                }
            };

            loginPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(15, 15, 15, 15);

            JLabel userLabel = new JLabel("USERNAME:");
    userLabel.setForeground(Color.WHITE);
    gbc.gridx = 0; gbc.gridy = 0;
    loginPanel.add(userLabel, gbc);


            JTextField usernameField = new JTextField(10);
    usernameField.setPreferredSize(new Dimension(220, 40)); // enough height
    usernameField.setMargin(new Insets(5, 10, 5, 10));
    gbc.gridx = 1; gbc.gridy = 0;
    loginPanel.add(usernameField, gbc);


             JLabel passLabel = new JLabel("PASSWORD:");
    passLabel.setForeground(Color.WHITE);
    gbc.gridx = 0; gbc.gridy = 1;
    loginPanel.add(passLabel, gbc);


            JPasswordField passwordField = new JPasswordField(10);
    passwordField.setPreferredSize(new Dimension(220, 40));
    passwordField.setMargin(new Insets(5, 10, 5, 10));
    gbc.gridx = 1; gbc.gridy = 1;
    loginPanel.add(passwordField, gbc);

            JButton loginButton = new JButton("LOGIN");
            Color translucentBlue = new Color(0, 0, 0, 0); // alpha 120/255
    loginButton.setForeground(Color.WHITE);
    loginButton.setBackground(translucentBlue);
    loginButton.setOpaque(false);
    loginButton.setContentAreaFilled(false);
    gbc.gridx = 0; gbc.gridy = 2;
    loginPanel.add(loginButton, gbc);


            JButton signupButton = new JButton("SIGNUP");
            Color translucentBlue1 = new Color(0, 0, 0, 0); // alpha 120/255
            signupButton.setForeground(Color.WHITE);
    signupButton.setBackground(translucentBlue1);
    signupButton.setOpaque(false);
    signupButton.setContentAreaFilled(false);
    gbc.gridx = 1; gbc.gridy = 2;
    loginPanel.add(signupButton, gbc);

            frame.add(loginPanel);
            frame.setVisible(true);

            // --- Login Action ---
            loginButton.addActionListener(e -> {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Username and Password cannot be empty!");
                    return;
                }

                int playerId = PlayerDAO.login(username, password);
                if (playerId != -1) {
                    JOptionPane.showMessageDialog(frame, "Login Successful!");
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
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Username and Password cannot be empty!");
                    return;
                }

                if (!Pattern.matches("[a-zA-Z0-9_]{3,15}", username)) {
                    JOptionPane.showMessageDialog(frame, "Username must be 3-15 characters and contain only letters, numbers, or underscore!");
                    return;
                }

                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(frame, "Password must be at least 6 characters!");
                    return;
                }

                boolean ok = PlayerDAO.signup(username, password);
                if (!ok) {
                    JOptionPane.showMessageDialog(frame, "Username already exists or signup failed!");
                }
            });
        });
    }
}
