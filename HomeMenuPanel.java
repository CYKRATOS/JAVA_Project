import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.FontUIResource;

public class HomeMenuPanel extends JPanel {

    private final Image bgImage;

    public HomeMenuPanel(JFrame frame, int playerId, String username) {

        // Load background image
        bgImage = new ImageIcon("E:/JAVA-PROJECT/DevilLevelGame/assets/HOME_2.jpg").getImage();

        // --- LOAD GLOBAL FONT ---
        Font customFont = new Font("Serif", Font.BOLD, 40);
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT,
                    new File("E:/JAVA-PROJECT/DevilLevelGame/assets/fonts/Eater-Regular.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (FontFormatException | IOException e) {
            System.out.println("Custom font could not be loaded. Using default font.");
        }

        // Apply smaller font globally
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

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int panelWidth = screenSize.width;

        // Title label with colored EQUILIBRIUM
        JLabel title = new JLabel(
            "<html><span style='color: #007BFF;'>EQUILIBRIUM</span> - "
            + "<span style='color: #007BFF;'>Welcome " + username + "</span></html>",
            SwingConstants.CENTER
        );
        title.setFont(customFont.deriveFont(Font.BOLD, 60f));
        title.setBounds(0, 80, panelWidth, 100);
        add(title);

        // --- Metallic Button Colors ---
        Color metallicBase = new Color(180, 180, 200);
        Color metallicHighlight = new Color(230, 230, 255);
        Color metallicShadow = new Color(100, 100, 120);
        Font buttonFont = customFont.deriveFont(Font.BOLD, 24f);

        // Buttons configuration
        int buttonWidth = 150;
        int buttonHeight = 50;
        int spacingX = 20;
        int spacingY = 20;
        int bottomMargin = 200; // distance from bottom edge of the panel
        int startY = Toolkit.getDefaultToolkit().getScreenSize().height - bottomMargin - (2 * buttonHeight + spacingY); 
        int startX = (panelWidth - (4 * buttonWidth + 3 * spacingX)) / 2; // horizontally centered

        // --- Level Buttons (2 rows x 4 columns) ---
        for (int i = 1; i <= 8; i++) {
            JButton levelButton = createMetallicButton("Level " + i, metallicBase, metallicHighlight, metallicShadow, buttonFont);
            int level = i;
            int row = (i - 1) / 4;
            int col = (i - 1) % 4;
            int x = startX + col * (buttonWidth + spacingX);
            int y = startY + row * (buttonHeight + spacingY);
            levelButton.setBounds(x, y, buttonWidth, buttonHeight);

            levelButton.addActionListener(e -> {
                GamePanel gamePanel = new GamePanel(playerId, username);
                gamePanel.setLevelIndex(level - 1);
                frame.getContentPane().removeAll();
                frame.add(gamePanel);
                frame.revalidate();
                frame.repaint();
            });

            add(levelButton);
        }

        // --- Centered Leaderboard Button ---
        JButton leaderboardBtn = createMetallicButton("Leaderboard", metallicBase, metallicHighlight, metallicShadow, buttonFont);
        int leaderboardWidth = 300;
        int leaderboardHeight = 60;
        int leaderboardX = (panelWidth - leaderboardWidth) / 2;
        int leaderboardY = startY + 2 * (buttonHeight + spacingY);
        leaderboardBtn.setBounds(leaderboardX, leaderboardY, leaderboardWidth, leaderboardHeight);
        leaderboardBtn.addActionListener(e -> new LeaderboardFrame());
        add(leaderboardBtn);
    }

    // --- Metallic Button Factory ---
    private JButton createMetallicButton(String text, Color base, Color highlight, Color shadow, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createBevelBorder(1, highlight, shadow));
        button.setBackground(base);
        button.setOpaque(true);

        button.getModel().addChangeListener((ChangeEvent e) -> {
            if (button.getModel().isPressed()) {
                button.setBackground(shadow);
            } else if (button.getModel().isRollover()) {
                button.setBackground(highlight);
            } else {
                button.setBackground(base);
            }
        });

        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fill background with gray where image doesn't cover
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
}
