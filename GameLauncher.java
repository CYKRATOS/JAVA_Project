import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.plaf.FontUIResource;

public class GameLauncher {

    private static final Map<JTextField, String> placeholders = new HashMap<>();

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
                    // Start background music immediately
            SoundManager.getInstance().playMusic("E:/JAVA-PROJECT/DevilLevelGame/assets/game_bg.wav");
            // Load custom font
            Font customFont;
            try {
                customFont = Font.createFont(Font.TRUETYPE_FONT,
                        new File("E:/JAVA-PROJECT/DevilLevelGame/assets/fonts/Orbitron-SemiBold.ttf"));
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
                Enumeration<Object> keys = UIManager.getDefaults().keys();
                while (keys.hasMoreElements()) {
                    Object key = keys.nextElement();
                    if (UIManager.get(key) instanceof FontUIResource) {
                        UIManager.put(key, new FontUIResource(customFont.deriveFont(15f)));
                    }
                }
            } catch (FontFormatException | IOException e) {
                customFont = new Font("SansSerif", Font.BOLD, 15);
            }

            JFrame frame = new JFrame("ENIGMA");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            // Background panel
            JPanel loginPanel = new JPanel() {
                private final Image bgImage = new ImageIcon(
                        "E:/JAVA-PROJECT/DevilLevelGame/assets/LOGIN.png").getImage();

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(new Color(30, 30, 30));
                    g.fillRect(0, 0, getWidth(), getHeight());

                    int iw = bgImage.getWidth(this);
                    int ih = bgImage.getHeight(this);
                    if (iw > 0 && ih > 0) {
                        double pr = (double) getWidth() / getHeight();
                        double ir = (double) iw / ih;
                        int dw, dh;
                        if (pr > ir) { dh = getHeight(); dw = (int) (dh * ir); }
                        else          { dw = getWidth(); dh = (int) (dw / ir); }
                        int x = (getWidth() - dw) / 2;
                        int y = (getHeight() - dh) / 2;
                        g.drawImage(bgImage, x, y, dw, dh, this);
                    }
                }
            };

            loginPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10,10,10,10);

            // Buttons dimensions
            int buttonWidth = 150;
            int buttonHeight = 45;

            int fieldWidth = buttonWidth;
            int fieldHeight = buttonHeight;

            // Fields
            JTextField nameField = new JTextField(9);
            JTextField usernameField = new JTextField(9);
            JPasswordField passwordField = new JPasswordField(9);
            JPasswordField rePasswordField = new JPasswordField(9);

            JTextField[] allFields = { nameField, usernameField, passwordField, rePasswordField };
            
            for (JTextField f : allFields) {
                f.setFont(customFont.deriveFont(Font.PLAIN, 16f));
                f.setOpaque(false);
                f.setBorder(new LineBorder(Color.WHITE, 2, true));
                f.setPreferredSize(new java.awt.Dimension(fieldWidth, fieldHeight));
                f.setVisible(false); // initially hidden
            }

            // Placeholders
            addPlaceholder(nameField, "Name");
            addPlaceholder(usernameField, "Username");
            addPlaceholder(passwordField, "Password");
            addPlaceholder(rePasswordField, "Re-enter again");

            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            loginPanel.add(nameField, gbc); gbc.gridy++;
            loginPanel.add(usernameField, gbc); gbc.gridy++;
            loginPanel.add(passwordField, gbc); gbc.gridy++;
            loginPanel.add(rePasswordField, gbc);

            JLabel messageLabel = new JLabel("");
            messageLabel.setForeground(Color.RED);
            messageLabel.setFont(customFont.deriveFont(Font.PLAIN, 14f));
            messageLabel.setVisible(false);
            gbc.gridy++;
            loginPanel.add(messageLabel, gbc);

            JButton signInButton = new JButton("Sign In");
            JButton signUpButton = new JButton("Sign Up");
            JButton submitButton = new JButton("Submit");
            JButton cancelButton = new JButton("Cancel");

            submitButton.setVisible(false);
            cancelButton.setVisible(false);

            styleButton(signInButton, customFont);
            styleButton(signUpButton, customFont);
            styleButton(submitButton, customFont);
            styleButton(cancelButton, customFont);

            // Make SignIn and SignUp buttons same size
            signInButton.setPreferredSize(new java.awt.Dimension(buttonWidth, buttonHeight));
            signUpButton.setPreferredSize(new java.awt.Dimension(buttonWidth, buttonHeight));
            submitButton.setPreferredSize(new java.awt.Dimension(buttonWidth, buttonHeight)); // added
            cancelButton.setPreferredSize(new java.awt.Dimension(buttonWidth, buttonHeight)); // added

            gbc.gridy++; gbc.gridwidth = 2; gbc.gridx = 0;
            loginPanel.add(signInButton, gbc);
            gbc.gridy++;    
            loginPanel.add(signUpButton, gbc);
            gbc.gridy++;
            loginPanel.add(submitButton, gbc);
            gbc.gridy++;
            loginPanel.add(cancelButton, gbc);

            frame.add(loginPanel);
            frame.setVisible(true);

            SwingUtilities.invokeLater(() -> {
                frame.setFocusable(true);
                frame.requestFocusInWindow();
            });

            // Clear messages on typing
            KeyAdapter hideMsg = new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    messageLabel.setText("");
                    messageLabel.setVisible(false);
                }
            };
            for (JTextField f : allFields) f.addKeyListener(hideMsg);

            // --- SignIn ---
            signInButton.addActionListener(e -> {
    if (!usernameField.isVisible()) {
        // First click → just show the fields
        usernameField.setVisible(true);
        passwordField.setVisible(true);
        loginPanel.revalidate();
        loginPanel.repaint();
    } else {
        // Second click → fields visible, perform login
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (isEmptyOrPlaceholder(usernameField) || isEmptyOrPlaceholder(passwordField)) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Username and Password cannot be empty!");
            messageLabel.setVisible(true);
            return;
        }

        Player player = PlayerDAO.login(username, password);
if (player != null) {
    frame.getContentPane().removeAll();
    frame.add(new HomeMenuPanel(frame, player)); // ✅ pass the Player object
    frame.revalidate();
    frame.repaint();
}else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Invalid credentials!");
            messageLabel.setVisible(true);
        }
    }
});
            signUpButton.addActionListener(e -> {
    messageLabel.setText(""); 
    messageLabel.setVisible(false);

    // Show all 4 fields for Sign Up
    nameField.setVisible(true);
    usernameField.setVisible(true);
    passwordField.setVisible(true);
    rePasswordField.setVisible(true);

    submitButton.setVisible(true);
    cancelButton.setVisible(true);
    signInButton.setVisible(false);
    signUpButton.setVisible(false);

    // Reset all fields to placeholder
    for (JTextField f : allFields) resetField(f);

    loginPanel.revalidate();
    loginPanel.repaint();
});

            // --- Submit SignUp ---
            submitButton.addActionListener(e -> {
                messageLabel.setText(""); messageLabel.setVisible(false);

                if (isEmptyOrPlaceholder(nameField) || isEmptyOrPlaceholder(usernameField) ||
                        isEmptyOrPlaceholder(passwordField) || isEmptyOrPlaceholder(rePasswordField)) {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("All fields are required!");
                    messageLabel.setVisible(true);
                    return;
                }

                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String rePassword = new String(rePasswordField.getPassword()).trim();
                String name = nameField.getText().trim();

                if (!Pattern.matches("[a-zA-Z0-9_]{3,15}", username)) {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Username must be 3-15 characters (letters, numbers, underscore).");
                    messageLabel.setVisible(true);
                    return;
                }
                if (password.length() < 6) {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Password must be at least 6 characters!");
                    messageLabel.setVisible(true);
                    return;
                }
                if (!password.equals(rePassword)) {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Passwords do not match!");
                    messageLabel.setVisible(true);
                    return;
                }

                Player player = PlayerDAO.signup(username, password, name);
                if (player != null) {
                    messageLabel.setForeground(Color.GREEN);
                    //messageLabel.setText("Signup successful! Redirecting to Sign In...");
                    messageLabel.setVisible(true);

                    resetToLogin(nameField, usernameField, passwordField, rePasswordField,
                            messageLabel, signInButton, signUpButton, submitButton, cancelButton);
                } else {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Username already exists or signup failed!");
                    messageLabel.setVisible(true);
                }
            });

            // --- Cancel SignUp ---
            cancelButton.addActionListener(e -> {
                messageLabel.setText(""); messageLabel.setVisible(false);
                resetToLogin(nameField, usernameField, passwordField, rePasswordField,
                        messageLabel, signInButton, signUpButton, submitButton, cancelButton);
            });

        });
    }

    private static void addPlaceholder(JTextField field, String placeholder) {
        placeholders.put(field, placeholder);
        resetField(field);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholders.get(field))) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                    if (field instanceof JPasswordField jPasswordField) jPasswordField.setEchoChar('\u2022');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    resetField(field);
                }
            }
        });
    }

    private static void resetField(JTextField field) {
        String placeholder = placeholders.getOrDefault(field, "");
        field.setText(placeholder);
        field.setForeground(new Color(0, 191, 255));
        if (field instanceof JPasswordField jPasswordField) jPasswordField.setEchoChar((char) 0);
    }

    private static boolean isEmptyOrPlaceholder(JTextField field) {
        if (field instanceof JPasswordField pf) {
            String val = new String(pf.getPassword()).trim();
            return val.isEmpty() || pf.getEchoChar() == 0;
        } else {
            String val = field.getText().trim();
            return val.isEmpty() || val.equals(placeholders.getOrDefault(field, ""));
        }
    }

    private static void resetToLogin(JTextField nameField, JTextField usernameField,
                                     JPasswordField passwordField, JPasswordField rePasswordField,
                                     JLabel messageLabel, JButton signInButton, JButton signUpButton,
                                     JButton submitButton, JButton cancelButton) {
        for (JTextField f : new JTextField[]{ nameField, usernameField, passwordField, rePasswordField }) {
            resetField(f);
            f.setVisible(false); // hide fields
        }

        nameField.setVisible(false);
        rePasswordField.setVisible(false);
        submitButton.setVisible(false);
        cancelButton.setVisible(false);
        signInButton.setVisible(true);
        signUpButton.setVisible(true);
        messageLabel.setText(""); messageLabel.setVisible(false);

        nameField.getParent().revalidate();
        nameField.getParent().repaint();
    }

    private static void styleButton(JButton button, Font font) {
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setFont(font.deriveFont(Font.BOLD, 18f));
    }
}
