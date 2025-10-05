import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.FontUIResource;

/**
 * Cleaned HomeMenuPanel: single constructor, no duplicated code.
 */
public class HomeMenuPanel extends JPanel {

    private final Image bgImage;
    private final String playerName;

        // Backward-compatible constructor (for older calls)
    public HomeMenuPanel(JFrame frame, int playerId, String username) {
        this(frame, playerId, username, "Player"); // default name if not provided
    }

    public HomeMenuPanel(JFrame frame, int playerId, String username, String name) {
        // Use provided name if present, otherwise fall back to username
        this.playerName = (name != null && !name.isEmpty()) ? name : username;

        // Load background image
        bgImage = new ImageIcon("E:/JAVA-PROJECT/DevilLevelGame/assets/HOME_2.jpg").getImage();

        // --- Load a custom font (try preferred files, fallback to system font) ---
        Font customFont = new Font("Serif", Font.BOLD, 40);
        try {
            File fontFile = new File("E:/JAVA-PROJECT/DevilLevelGame/assets/fonts/Orbitron-Regular.ttf");
            if (!fontFile.exists()) {
                fontFile = new File("E:/JAVA-PROJECT/DevilLevelGame/assets/fonts/Orbitron-Black.ttf");
            }
            if (fontFile.exists()) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
            }
        } catch (FontFormatException | IOException ex) {
            System.out.println("Custom font could not be loaded; using default.");
        }

        // Apply a derived UI font size to UI defaults (optional, keeps look consistent)
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, new FontUIResource(customFont.deriveFont(22f)));
            }
        }

        setLayout(null);
        setOpaque(false);

        // Screen dimensions (used for centering)
        int panelWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int panelHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        // Title label with colored HTML parts
        JLabel title = new JLabel(
                "<html><span style='color: #007BFF;'>EQUILIBRIUM</span> - "
                        + "<span style='color: #007BFF;'>Welcome " + escapeHtml(playerName) + "</span></html>",
                SwingConstants.CENTER);
        title.setFont(customFont.deriveFont(Font.BOLD, 60f));
        title.setBounds(0, 80, panelWidth, 100);
        add(title);

        // Metallic colors and button font
        Color metallicBase = new Color(180, 180, 200);
        Color metallicHighlight = new Color(230, 230, 255);
        Color metallicShadow = new Color(100, 100, 120);
        Font buttonFont = customFont.deriveFont(Font.BOLD, 24f);

        // Buttons configuration
        int buttonWidth = 150;
        int buttonHeight = 50;
        int spacingX = 20;
        int spacingY = 20;
        int bottomMargin = 200;
        int rows = 2;
        int cols = 4;
        int totalWidth = cols * buttonWidth + (cols - 1) * spacingX;
        int startX = (panelWidth - totalWidth) / 2;
        int startY = panelHeight - bottomMargin - (rows * buttonHeight + (rows - 1) * spacingY);

        // --- Level buttons (1..8) ---
        for (int i = 1; i <= 8; i++) {
            int row = (i - 1) / cols;
            int col = (i - 1) % cols;
            int x = startX + col * (buttonWidth + spacingX);
            int y = startY + row * (buttonHeight + spacingY);

            JButton levelButton = createMetallicButton("Level " + i, metallicBase, metallicHighlight, metallicShadow,
                    buttonFont);
            final int levelIndex = i - 1;
            levelButton.setBounds(x, y, buttonWidth, buttonHeight);
            levelButton.addActionListener(ev -> {
                GamePanel gamePanel = new GamePanel(playerId, username);
                gamePanel.setLevelIndex(levelIndex);
                frame.getContentPane().removeAll();
                frame.add(gamePanel);
                frame.revalidate();
                frame.repaint();
            });
            add(levelButton);
        }

        // --- Leaderboard button centered below levels ---
        JButton leaderboardBtn = createMetallicButton("Leaderboard", metallicBase, metallicHighlight, metallicShadow,
                buttonFont);
        int leaderboardWidth = 300;
        int leaderboardHeight = 60;
        int leaderboardX = (panelWidth - leaderboardWidth) / 2;
        int leaderboardY = startY + rows * (buttonHeight + spacingY);
        leaderboardBtn.setBounds(leaderboardX, leaderboardY, leaderboardWidth, leaderboardHeight);
        leaderboardBtn.addActionListener(ev -> new LeaderboardFrame());
        add(leaderboardBtn);
    }

    // Factory method for metallic-style buttons
    private JButton createMetallicButton(String text, Color base, Color highlight, Color shadow, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, highlight, shadow));
        button.setBackground(base);
        button.setOpaque(true);

        // Change background on rollover/press
        ChangeListener cl = e -> {
            if (button.getModel().isPressed()) {
                button.setBackground(shadow);
            } else if (button.getModel().isRollover()) {
                button.setBackground(highlight);
            } else {
                button.setBackground(base);
            }
        };
        button.getModel().addChangeListener(cl);

        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fill background where image doesn't cover
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, getWidth(), getHeight());

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

    // Simple html-escaping for the small username insertion into the title (prevents broken HTML for unusual names)
    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
