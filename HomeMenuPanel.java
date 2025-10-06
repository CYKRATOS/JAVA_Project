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
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class HomeMenuPanel extends JPanel {

    private Image bgImage;
    private final String playerName;

    // Static flag to play splash only once
    private static boolean splashPlayed = false;

    // Backward-compatible constructor
    public HomeMenuPanel(JFrame frame, int playerId, String username) {
        this(frame, playerId, username, "Player");
    }

    public HomeMenuPanel(JFrame frame, int playerId, String username, String name) {
        this.playerName = (name != null && !name.isEmpty()) ? name : username;

        setLayout(null);
        setOpaque(false);

         if (!splashPlayed) { 
            splashPlayed = true; 

        // --- Splash Screen Panel ---
        JPanel splashPanel = new JPanel(null);
        splashPanel.setBackground(Color.BLACK);

        JLabel gifLabel = new JLabel(new ImageIcon(
                "E:/JAVA-PROJECT/DevilLevelGame/assets/videos/Intro1.1.gif"));
        gifLabel.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height);
        splashPanel.add(gifLabel);

        splashPanel.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height);
        add(splashPanel);
        revalidate();
        repaint();

        // --- Timer to remove splash after 3 seconds ---
        Timer splashTimer = new Timer(1000, e -> {
            remove(splashPanel);
            ((Timer) e.getSource()).stop();
            initHomeMenu(frame, playerId, username);
            revalidate();
            repaint();
        });
        splashTimer.setRepeats(false);
        splashTimer.start();
    }else {
            // If splash already played, go straight to home menu
            initHomeMenu(frame, playerId, username);
        }
    }

    private void initHomeMenu(JFrame frame, int playerId, String username) {

        // Load background image
        bgImage = new ImageIcon("E:/JAVA-PROJECT/DevilLevelGame/assets/HOME_2.jpg").getImage();

        // --- Load a custom font ---
        Font customFont = new Font("Serif", Font.BOLD, 40);
        try {
            File fontFile = new File("E:/JAVA-PROJECT/DevilLevelGame/assets/fonts/Orbitron-Regular.ttf");
            if (!fontFile.exists())
                fontFile = new File("E:/JAVA-PROJECT/DevilLevelGame/assets/fonts/Orbitron-Black.ttf");
            if (fontFile.exists()) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
            }
        } catch (FontFormatException | IOException ex) {
            System.out.println("Custom font could not be loaded; using default.");
        }

        // Apply font to UI defaults
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, new FontUIResource(customFont.deriveFont(22f)));
            }
        }

        int panelWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int panelHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        // Title label
        JLabel title = new JLabel(
                "<html><span style='color: #007BFF;'>ENIGMA</span> - "
                        + "<span style='color: #007BFF;'>Welcome " + escapeHtml(playerName) + "</span></html>",
                SwingConstants.CENTER);
        title.setFont(customFont.deriveFont(Font.BOLD, 60f));
        title.setBounds(0, 80, panelWidth, 100);
        add(title);

        // Button font and layout
        Font buttonFont = customFont.deriveFont(Font.BOLD, 24f);
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

        // --- Level buttons ---
        for (int i = 1; i <= 8; i++) {
            int row = (i - 1) / cols;
            int col = (i - 1) % cols;
            int x = startX + col * (buttonWidth + spacingX);
            int y = startY + row * (buttonHeight + spacingY);

            JButton levelButton = createGlassButton("Level " + i, buttonFont);
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

        // --- Leaderboard button ---
        JButton leaderboardBtn = createGlassButton("Leaderboard", buttonFont);
        int leaderboardWidth = 300;
        int leaderboardHeight = 60;
        int leaderboardX = (panelWidth - leaderboardWidth) / 2;
        int leaderboardY = startY + rows * (buttonHeight + spacingY);
        leaderboardBtn.setBounds(leaderboardX, leaderboardY, leaderboardWidth, leaderboardHeight);
        leaderboardBtn.addActionListener(ev -> new LeaderboardApp());
        add(leaderboardBtn);
    }

    private JButton createGlassButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font.deriveFont(Font.BOLD, 24f));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        Color glassColor = new Color(255, 255, 255, 30);
        Color hoverBorderColor = new Color(0, 123, 255);
        Color defaultBorder = Color.WHITE;

        button.addChangeListener(e -> {
            boolean hover = button.getModel().isRollover();
            if (hover) {
                button.setBorder(BorderFactory.createLineBorder(hoverBorderColor, 2));
                button.setFont(font.deriveFont(Font.BOLD, 26f));
            } else {
                button.setBorder(BorderFactory.createLineBorder(defaultBorder, 2));
                button.setFont(font.deriveFont(Font.BOLD, 24f));
            }
            button.repaint();
        });

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, javax.swing.JComponent c) {
                Graphics g2 = g.create();
                g2.setColor(button.getModel().isRollover() ? new Color(0, 123, 255, 200) : glassColor); //change the 200vlaue to adjust hover transparency
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                super.paint(g2, c);
                g2.dispose();
            }
        });

        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, getWidth(), getHeight());

        if (bgImage != null) {
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

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
