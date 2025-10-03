import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {
    // Constants
    private static final int PLAYER_SIZE = 50;
    private static final int PLAYER_SPEED = 5;
    private static final int JUMP_SPEED = -20;
    private static final int GRAVITY = 1;

    private final Timer timer;
    private final SoundManager soundManager = new SoundManager();

    private int playerX = 100, playerY = 100;
    private int velX, velY;
    private boolean inAir;

    private int levelIndex = 0;
    private int score = 0;
    private boolean gameCompleted = false;

    private List<Spike> spikes;
    private Rectangle door;
    private final List<Level> levels;

    // Pause/Resume/Quit
    private boolean isPaused = false;
    private JButton pauseResumeButton;
    private final JButton quitButton;

    // Background image
    private BufferedImage backgroundImage;

    private final int panelWidth;
    private final int panelHeight;
    private final int groundHeight;

    private final int playerId;

    public GamePanel(int playerId, String username) {
        this.playerId = playerId;

        setLayout(null);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        panelWidth = screenSize.width;
        panelHeight = screenSize.height;
        groundHeight = panelHeight / 5;

        setPreferredSize(screenSize);

        levels = Levels.createLevels(panelWidth, panelHeight, groundHeight);

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("E:/JAVA-PROJECT/DevilLevelGame/assets/BG_IMAGE.png"));
        } catch (IOException e) {}

        // Quit button
        quitButton = new JButton("Quit");
        quitButton.setBounds(panelWidth - 120, 60, 100, 30);
        quitButton.setFocusable(false);
        quitButton.addActionListener(e -> {
            // Save score on quit
            saveTotalScore();
            // Back to home menu
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.getContentPane().removeAll();
            topFrame.add(new HomeMenuPanel(topFrame, playerId, username));
            topFrame.revalidate();
            topFrame.repaint();
        });
        add(quitButton);

        // Pause/Resume button
        pauseResumeButton = new JButton("Pause");
        pauseResumeButton.setBounds(panelWidth - 120, 20, 100, 30);
        pauseResumeButton.addActionListener(e -> {
            isPaused = !isPaused;
            pauseResumeButton.setText(isPaused ? "Resume" : "Pause");
        });
        add(pauseResumeButton);

        setupKeyBindings();
        resetLevel();

        timer = new Timer(16, this);
        timer.start();
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("UP"), "jump");
        am.put("jump", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!inAir) {
                    velY = JUMP_SPEED;
                    inAir = true;
                    soundManager.playSound("assets/jump.wav");
                }
            }
        });

        im.put(KeyStroke.getKeyStroke("LEFT"), "left");
        am.put("left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { velX = -PLAYER_SPEED; }
        });

        im.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        am.put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { velX = PLAYER_SPEED; }
        });

        im.put(KeyStroke.getKeyStroke("released LEFT"), "stopLeft");
        am.put("stopLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { if (velX < 0) velX = 0; }
        });

        im.put(KeyStroke.getKeyStroke("released RIGHT"), "stopRight");
        am.put("stopRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { if (velX > 0) velX = 0; }
        });

        im.put(KeyStroke.getKeyStroke("R"), "restart");
        am.put("restart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetLevel();
                gameCompleted = false;
            }
        });
    }

    public void setLevelIndex(int index) {
        this.levelIndex = index;
        resetLevel();
        gameCompleted = false;
    }

    private final int LIFT_HEIGHT = 30;

    private void resetLevel() {
        Level lvl = levels.get(levelIndex);

        playerX = panelWidth / 20;
        playerY = panelHeight - groundHeight - PLAYER_SIZE - LIFT_HEIGHT;

        velX = 0;
        velY = 0;
        inAir = false;

        spikes = new ArrayList<>(lvl.getSpikes());
        spikes.forEach(Spike::reset);

        door = new Rectangle(lvl.getDoor());

        soundManager.playMusic("assets/BG_MUSIC.wav");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isPaused || gameCompleted) return;

        velY += GRAVITY;
        playerX += velX;
        playerY += velY;

        if (playerX < 0) playerX = 0;
        if (playerX + PLAYER_SIZE > panelWidth) playerX = panelWidth - PLAYER_SIZE;

        int groundY = panelHeight - groundHeight - PLAYER_SIZE - LIFT_HEIGHT;
        if (playerY >= groundY) {
            playerY = groundY;
            velY = 0;
            inAir = false;
        }

        Rectangle playerRect = new Rectangle(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);

        for (Spike spike : spikes) {
            spike.update();
            if (playerRect.intersects(spike.getRect())) {
                soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/death.wav");
                resetLevel();
                return;
            }
        }

        // Check door collision
        if (playerRect.intersects(door)) {
            score += 100;
            soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/game-level-complete.wav");
            loadNextLevel();
        }

        repaint();
    }

    private void loadNextLevel() {
        if (levelIndex < levels.size() - 1) {
            levelIndex++;
            resetLevel();
        } else {
            gameCompleted = true;
            soundManager.stopMusic();

            // Save total score when game completed
            saveTotalScore();
        }
    }

    private void saveTotalScore() {
        int totalScore = score; // total points accumulated
        GameDAO.saveOrUpdateScore(playerId, totalScore); // method will insert or update
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        for (Spike spike : spikes) {
            spike.draw(g2);
        }

        g2.setColor(new Color(139, 69, 19));
        g2.fillRect(door.x, door.y, door.width, door.height);

        g2.setColor(new Color(160, 82, 45));
        int panelMargin = 5;
        int topPanelHeight = (door.height - 3 * panelMargin) / 2;
        g2.fillRect(door.x + panelMargin, door.y + panelMargin, door.width - 2 * panelMargin, topPanelHeight);
        g2.fillRect(door.x + panelMargin, door.y + 2 * panelMargin + topPanelHeight, door.width - 2 * panelMargin, topPanelHeight);

        g2.setColor(Color.YELLOW);
        int knobSize = 8;
        g2.fillOval(door.x + door.width - 20, door.y + door.height / 2, knobSize, knobSize);

        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(door.x, door.y, door.width, door.height);

        // Player
        int bodyWidth = 30, bodyHeight = 50, headSize = 20, legWidth = 10, legHeight = 15;
        g2.setColor(Color.WHITE);
        g2.fillRect(playerX + (PLAYER_SIZE - headSize)/2, playerY, headSize, headSize);
        g2.fillRect(playerX + (PLAYER_SIZE - bodyWidth)/2, playerY + headSize, bodyWidth, bodyHeight);

        int legOffset = 0;
        if (velX != 0) legOffset = (int)((System.currentTimeMillis() / 150 % 2) == 0 ? 5 : -5);
        g2.fillRect(playerX + (PLAYER_SIZE - bodyWidth)/2, playerY + headSize + bodyHeight, legWidth, legHeight);
        g2.fillRect(playerX + (PLAYER_SIZE - bodyWidth)/2 + bodyWidth - legWidth + legOffset,
                    playerY + headSize + bodyHeight, legWidth, legHeight);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Score: " + score, 20, 30);

        if (gameCompleted) {
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.setColor(Color.YELLOW);
            g2.drawString("Congratulations! Game Completed!", panelWidth / 2 - 300, panelHeight / 2);
        }
    }
}
