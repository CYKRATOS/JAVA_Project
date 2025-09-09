import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {
    // Constants
    private static final int PLAYER_SIZE = 80;
    private static final int PLAYER_SPEED = 5;
    private static final int JUMP_SPEED = -20;
    private static final int GRAVITY = 1;

    private final Timer timer;
    private int playerX = 100, playerY = 100;
    private int velX, velY;
    private boolean inAir;

    private int levelIndex = 0;
    private int score = 0;
    private boolean gameCompleted = false;

    private List<Spike> spikes;
    private Rectangle door;
    private final List<Level> levels;

    private final int panelWidth;
    private final int panelHeight;
    private final int groundHeight;

    // Level 4 dynamic variables
    private boolean level4Triggered = false;
    private Spike level4Obstacle;

    // Pause/Resume/Quit
    private boolean isPaused = false;
    private JButton pauseResumeButton;
    private final JButton quitButton;

    // Background image
    private BufferedImage backgroundImage;
    private BufferedImage playerImage;

    public GamePanel() {
        setLayout(null); // absolute positioning

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        panelWidth = screenSize.width;
        panelHeight = screenSize.height;
        groundHeight = panelHeight / 5;

        setPreferredSize(screenSize);

        levels = Levels.createLevels(panelWidth, panelHeight, groundHeight);

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("E:\\JAVA-PROJECT\\DevilLevelGame\\assets\\1wallpaper.jpg"));
            playerImage = ImageIO.read(new File("E:\\JAVA-PROJECT\\DevilLevelGame\\assets\\char.png"));
        } catch (IOException e) {
            System.out.println("Background image not found, using default color.");
        }

        // Quit button
        quitButton = new JButton("Quit");
        quitButton.setBounds(panelWidth - 120, 60, 100, 30); // under Pause button
        quitButton.setFocusable(false);
        quitButton.addActionListener(e -> System.exit(0));
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

        im.put(KeyStroke.getKeyStroke("LEFT"), "left");
        am.put("left", new AbstractAction() {@Override
        public void actionPerformed(ActionEvent e) { velX = -PLAYER_SPEED; } });

        im.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        am.put("right", new AbstractAction() {@Override
        public void actionPerformed(ActionEvent e) { velX = PLAYER_SPEED; } });

        im.put(KeyStroke.getKeyStroke("released LEFT"), "stopLeft");
        am.put("stopLeft", new AbstractAction() {@Override
        public void actionPerformed(ActionEvent e) { if (velX < 0) velX = 0; } });

        im.put(KeyStroke.getKeyStroke("released RIGHT"), "stopRight");
        am.put("stopRight", new AbstractAction() {@Override
        public void actionPerformed(ActionEvent e) { if (velX > 0) velX = 0; } });

        im.put(KeyStroke.getKeyStroke("UP"), "jump");
        am.put("jump", new AbstractAction() {@Override
        public void actionPerformed(ActionEvent e) { if (!inAir) { velY = JUMP_SPEED; inAir = true; } } });

        im.put(KeyStroke.getKeyStroke("R"), "restart");
        am.put("restart", new AbstractAction() {@Override
        public void actionPerformed(ActionEvent e) { 
            resetLevel(); 
            gameCompleted = false;
            level4Triggered = false;
        } });
    }

    // Allow GameLauncher to select a level
    public void setLevelIndex(int index) {
        this.levelIndex = index;
        resetLevel();
        gameCompleted = false;
        level4Triggered = false;
    }

    private void resetLevel() {
        Level lvl = levels.get(levelIndex);
        playerX = panelWidth / 20;
        playerY = panelHeight - groundHeight - PLAYER_SIZE;
        velX = 0;
        velY = 0;
        inAir = false;

        spikes = new ArrayList<>(lvl.getSpikes()); // mutable copy
        if (levelIndex == 0) {
            spikes.clear(); // no spikes at start in Level 1
        }
        spikes.forEach(Spike::reset);

        door = new Rectangle(lvl.getDoor());

        // Level 4 obstacle
        if (levelIndex == 3) {
            level4Triggered = false;
            level4Obstacle = new Spike(panelWidth + 30, panelHeight - groundHeight - 30, 30, 30, -5, true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isPaused || gameCompleted) return;

        velY += GRAVITY;
        playerX += velX;
        playerY += velY;

        if (playerX < 0) playerX = 0;
        if (playerX + PLAYER_SIZE > panelWidth) playerX = panelWidth - PLAYER_SIZE;

        int groundY = panelHeight - groundHeight - PLAYER_SIZE;
        if (playerY >= groundY) { playerY = groundY; velY = 0; inAir = false; }

        Rectangle playerRect = new Rectangle(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);

        // Level 1 spike appears only when player approaches door
        if (levelIndex == 0 && spikes.isEmpty()) {
            int triggerDistance = 100;
            if (playerX + PLAYER_SIZE >= door.x - triggerDistance) {
                Spike newSpike = new Spike(door.x - 40, panelHeight - groundHeight - 30, 30, 30, 0);
                spikes.add(newSpike);
            }
        }

        // Level 4 dynamic door + obstacle
        if (levelIndex == 3 && !level4Triggered) {
            int triggerDistance = (int)(0.1 * (door.x - playerX));
            if (playerX + PLAYER_SIZE >= door.x - triggerDistance) {
                door.x = 100; // teleport door
                level4Triggered = true;
                spikes.add(level4Obstacle); // moving obstacle
            }
        }

        // Spikes update + collision
        for (Spike spike : spikes) {
            spike.update(panelWidth);
            if (playerRect.intersects(spike.getRect())) {
                resetLevel();
                return;
            }
        }

        // Door collision
        if (playerRect.intersects(door)) {
            score += 100;
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
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            setBackground(Color.BLACK);
        }

        Graphics2D g2 = (Graphics2D) g;

        // Ground as thick line
        g2.setColor(Color.GREEN);
        g2.setStroke(new BasicStroke(10));
        g2.drawLine(0, getHeight() - 144, getWidth(), getHeight() - 144);

        // Spikes
        g2.setColor(Color.RED);
        for (Spike spike : spikes)
            spike.draw(g2);

        // Door
        g2.setColor(Color.ORANGE);
        g2.fillRect(door.x, door.y, door.width, door.height);


        //Player Design
        if (playerImage != null) {
            g2.drawImage(playerImage, playerX, playerY, PLAYER_SIZE, PLAYER_SIZE, this);
        } else {
            // Fallback (if image not loaded)
            g2.fillRect(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);
        }

        // HUD
        g2.setColor(Color.WHITE);
        if (gameCompleted) {
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            g2.drawString("Level Passed!", panelWidth / 2 - 150, panelHeight / 2);
        } else {
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("Level: " + (levelIndex + 1), 50, 50);
            g2.drawString("Score: " + score, 50, 80);
        }
    }
}
