import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
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

public class GamePanel extends JPanel implements java.awt.event.ActionListener {

    private static final int PLAYER_SIZE = 50;
    private static final int PLAYER_SPEED = 5;
    private static final int JUMP_SPEED = -20;
    private static final int GRAVITY = 1;
    private static final int LIFT_HEIGHT = 30;
    private static final int LEVEL3_SPIKE_SPEED = 8;

    private final Timer timer;
    private final Player player;
    private final SoundManager soundManager = new SoundManager();

    private int playerX = 100, playerY = 100;
    private int velX = 0, velY = 0;
    private boolean inAir;

    private int levelIndex = 0;
    private int score = 0;
    private boolean gameCompleted = false;

    private List<Spike> spikes;
    private Rectangle door;
    private final List<Level> levels;

    private List<Coin> coins;
    private boolean spikeActivated = false;

    private boolean isPaused = false;
    private final JButton pauseResumeButton;
    private final JButton quitButton;

    private BufferedImage backgroundImage;

    private final int panelWidth;
    private final int panelHeight;
    private final int groundHeight;

    // Level 3 logic
    private boolean doorEventTriggered = false;
    private boolean level3SpikeSpawned = false;
    private Spike level3Spike;

    // Level 4 sliding door
    private int level4DoorState = 0;
    private boolean level4DoorSliding = false;
    private int level4SlideTargetX = 0;
    private int level4SlideSpeed = 8;
    private int level4LastDoorXBeforeSlide = 0;
    private final int LEVEL4_SLIDE_DISTANCE = 200;
    private final int LEVEL4_PROXIMITY = 200;

    public GamePanel(Player player) { // now uses Player object
        this.player = player;

        setLayout(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        panelWidth = screenSize.width;
        panelHeight = screenSize.height;
        groundHeight = panelHeight / 5;
        setPreferredSize(screenSize);

        levels = Levels.createLevels(panelWidth, panelHeight, groundHeight);

        try {
            backgroundImage = ImageIO.read(new File("E:/JAVA-PROJECT/DevilLevelGame/assets/BG_IMAGE.png"));
        } catch (IOException e) {}

        // Quit button
        quitButton = new JButton("Quit");
        quitButton.setBounds(panelWidth - 120, 60, 100, 30);
        quitButton.setFocusable(false);
        quitButton.setFont(quitButton.getFont().deriveFont(14f));
        quitButton.addActionListener(e -> {
    // Save current score
    saveTotalScore();

    // Refresh the player's cleared level from the database
    int latestLevelCleared = PlayerDAO.getPlayerLevel(player.getId());
    player.setLevelCleared(latestLevelCleared); // update Player object

    // Switch back to Home Menu
    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
    frame.getContentPane().removeAll();
    frame.add(new HomeMenuPanel(frame, player)); // Pass updated player object
    frame.revalidate();
    frame.repaint();
});
        add(quitButton);

        // Pause button
        pauseResumeButton = new JButton("Pause");
        pauseResumeButton.setBounds(panelWidth - 120, 20, 100, 30);
        pauseResumeButton.setFont(pauseResumeButton.getFont().deriveFont(14f));
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
                    soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/jump.wav");
                }
            }
        });

        im.put(KeyStroke.getKeyStroke("LEFT"), "left");
        am.put("left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (levelIndex == 4 || levelIndex == 5) {
            // In Level 5: reverse controls → LEFT key moves right
            velX = PLAYER_SPEED;
        } else {
            velX = -PLAYER_SPEED;
        }
            }
        });

        im.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        am.put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (levelIndex == 4 || levelIndex == 5) {
            // In Level 5: reverse controls → RIGHT key moves left
            velX = -PLAYER_SPEED;
        } else {
            velX = PLAYER_SPEED;
        }
            }
        });

        im.put(KeyStroke.getKeyStroke("released LEFT"), "stopLeft");
        am.put("stopLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { velX = 0; }
        });

        im.put(KeyStroke.getKeyStroke("released RIGHT"), "stopRight");
        am.put("stopRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { velX = 0; }
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

    private void resetLevel() {
        Level lvl = levels.get(levelIndex);

        velX = velY = 0;
        inAir = false;

        spikes = new ArrayList<>(lvl.getSpikes());
        spikes.forEach(Spike::reset);

        door = new Rectangle(lvl.getDoor());

        coins = new ArrayList<>();
        spikeActivated = false;

        doorEventTriggered = false;
        level3SpikeSpawned = false;
        level3Spike = null;

        if (levelIndex == 3) {
            level4DoorState = 0;
            level4DoorSliding = false;
            level4LastDoorXBeforeSlide = 0;
            level4SlideTargetX = 0;
        }

        // Player starting position
        playerX = switch (levelIndex) {
            case 4 -> (panelWidth - PLAYER_SIZE) / 2;
            case 5 -> panelWidth - PLAYER_SIZE - 50;
            default -> panelWidth / 20;
        };

        playerY = panelHeight - groundHeight - PLAYER_SIZE - LIFT_HEIGHT;

        // Level 2 coins
        if (levelIndex == 1) {
            int numCoins = 11, coinSize = 40, spacing = 80;
            int startX = panelWidth / 2 - ((numCoins - 1) * spacing) / 2;
            int y = panelHeight - groundHeight - coinSize;
            for (int i = 0; i < numCoins; i++) {
                int x = startX + i * spacing;
                coins.add(new Coin(x, y, coinSize, coinSize));
            }
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

        int groundY = panelHeight - groundHeight - PLAYER_SIZE - LIFT_HEIGHT;
        if (playerY >= groundY) {
            playerY = groundY;
            velY = 0;
            inAir = false;
        }

        Rectangle playerRect = new Rectangle(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);

        // ---------- Level-specific logic ----------
        switch (levelIndex) {
            case 0 -> {
                // Level 1 spikes (unchanged)
                if (spikes.size() >= 3) {
                    spikes.get(1).setTriggerDistance(150);
                    spikes.get(2).setTriggerDistance(100);
                }
                for (Spike spike : spikes) {
                    if (spike.isReactive() && !spike.getTriggered()) {
                        int distance = Math.abs(spike.getRect().x - playerX);
                        if (distance <= spike.getTriggerDistance()) spike.trigger();
                    }
                    spike.update();
                    if (playerRect.intersects(spike.getRect())) {
                        soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/death.wav");
                        resetLevel();
                        return;
                    }
                }
                if (playerRect.intersects(door)) { // Level 1 door
                    score += 100;
                    soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/game-level-complete.wav");
                    loadNextLevel();
                    return;
                }
            }

            case 1 -> {
                // Level 2 coins (unchanged)
                long collected = coins.stream().filter(Coin::isCollected).count();
                for (Coin coin : coins) {
                    if (!coin.isCollected() && playerRect.intersects(coin.getRect())) {
                        coin.setCollected(true);
                        soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/coin.wav");
                    }
                }

                if (!spikeActivated && collected >= 8 && coins.size() > 8) {
                    Coin ninthCoin = coins.get(8);
                    spikes.add(new Spike(ninthCoin.getRect().x, panelHeight - groundHeight - 40, 60, 40));
                    spikeActivated = true;
                    // Remove 9th coin after spike spawn
                    coins.get(8).setCollected(true);
                }

                for (Spike spike : spikes) {
                    spike.update();
                    if (playerRect.intersects(spike.getRect())) {
                        soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/death.wav");
                        resetLevel();
                        return;
                    }
                }

                if (collected == coins.size() && playerRect.intersects(door)) {
                    score += 100;
                    soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/game-level-complete.wav");
                    loadNextLevel();
                    return;
                }
            }

            case 2 -> {
                // Level 3 door teleport & moving spike (unchanged)
                if (!doorEventTriggered && playerRect.intersects(door)) {
                    doorEventTriggered = true;
                    door.x = 50; // teleport left
                }

                if (doorEventTriggered && !level3SpikeSpawned) {
                    level3Spike = new Spike(panelWidth, panelHeight - groundHeight - 40, 60, 40);
                    level3Spike.setVelocity(-LEVEL3_SPIKE_SPEED, 0);
                    level3SpikeSpawned = true;
                }

                if (level3SpikeSpawned) {
                    level3Spike.update();
                    if (playerRect.intersects(level3Spike.getRect())) {
                        soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/death.wav");
                        resetLevel();
                        return;
                    }
                }

                if (playerRect.intersects(door)) {
                    score += 100;
                    soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/game-level-complete.wav");
                    loadNextLevel();
                    return;
                }
            }

case 3 -> {
    // ---------- LEVEL 4: Sliding Door Trap ----------

    // 1) Update existing spikes
    for (Spike spike : spikes) {
        spike.update();
        if (playerRect.intersects(spike.getRect())) {
            soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/death.wav");
            resetLevel();
            return;
        }
    }

    // 2) Handle door sliding
    if (level4DoorSliding) {
        // Move door towards target
        if (door.x < level4SlideTargetX) {
            door.x += level4SlideSpeed;
            if (door.x >= level4SlideTargetX) {
                door.x = level4SlideTargetX;
                level4DoorSliding = false; // reached target
                level4DoorState++; // increment state only after sliding finishes

                // spawn stationary spike at previous door location
                int spikeX = level4LastDoorXBeforeSlide;
                int spikeY = panelHeight - groundHeight - 40;
                spikes.add(new Spike(spikeX, spikeY, 60, 40));
                //soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/spawn.wav");
            }
        }
    } else {
        // 3) Not sliding: check player proximity triggers
        int dist = Math.abs(playerX - door.x);

        if (level4DoorState < 2 && dist <= LEVEL4_PROXIMITY) {
            // first two slides
            level4LastDoorXBeforeSlide = door.x;
            level4SlideTargetX = door.x + LEVEL4_SLIDE_DISTANCE;

            // clamp target to prevent offscreen
            int maxDoorX = panelWidth - door.width - 10;
            if (level4SlideTargetX > maxDoorX) level4SlideTargetX = maxDoorX;

            level4DoorSliding = true; // start sliding
        } else if (level4DoorState == 2 && dist <= LEVEL4_PROXIMITY) {
            // third approach: teleport left
            door.x = 10; // leftmost side
            level4DoorState = 3;
            //soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/teleport.wav");
        }
    }

    // 4) Check door collision (level complete)
    if (playerRect.intersects(door)) {
        score += 100;
        soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/game-level-complete.wav");
        loadNextLevel();
        return;
    }
}

case 4 -> {
    // ---------- LEVEL 5: Reverse Controls Challenge ----------

    // 1. Update all spikes
    for (Spike spike : spikes) {
        spike.update();
        if (playerRect.intersects(spike.getRect())) {
            soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/death.wav");
            resetLevel();
            return;
        }
    }

    // 2. Check door collision (level complete)
    if (playerRect.intersects(door)) {
        score += 100;
        soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/game-level-complete.wav");
        loadNextLevel();
        return;
    }
}

case 5 -> {
    // ---------- LEVEL 6: Right-to-Left + Sliding Spikes (4 spikes) ----------

    // Update all spikes and check collisions
    for (Spike spike : spikes) {
        spike.update();
        if (playerRect.intersects(spike.getRect())) {
            soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/death.wav");
            resetLevel();
            return;
        }
    }

    // Sliding triggers for first and last spikes
    int proximity = 150;       // trigger distance
    int slideDistance = 50;   // pixels to slide
    Spike firstSpike = spikes.get(0);
    Spike lastSpike = spikes.get(spikes.size() - 1);

    if (!firstSpike.isSliding() && Math.abs(playerX - firstSpike.getRect().x) <= proximity) {
        firstSpike.startSliding(slideDistance);
        firstSpike.setDirection(1); // slide to the right
    }

    if (!lastSpike.isSliding() && Math.abs(playerX - lastSpike.getRect().x) <= proximity) {
        lastSpike.startSliding(slideDistance);
        lastSpike.setDirection(1); // slide to the right
    }

    // Door collision: complete level
    if (playerRect.intersects(door)) {
        score += 100;
        soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/game-level-complete.wav");
        loadNextLevel();
        return;
    }
}
case 6 -> {
    if (playerRect.intersects(door)) {
        score += 100;
        soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/game-level-complete.wav");
        loadNextLevel();
        return;
    }
}

case 7 -> {
    if (playerRect.intersects(door)) {
        score += 100;
        soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/game-level-complete.wav");
        loadNextLevel();
        return;
    }
}
            default -> {
                // Generic fallback: update spikes and check collisions
                for (Spike spike : spikes) {
                    spike.update();
                    if (playerRect.intersects(spike.getRect())) {
                        soundManager.playSound("E:/JAVA-PROJECT/DevilLevelGame/assets/death.wav");
                        resetLevel();
                        return;
                    }
                }
            }
        }

        repaint();
    }

    private void loadNextLevel() {
        if (levelIndex < levels.size() - 1) {
            levelIndex++;
            resetLevel();
            if (levelIndex + 1 > player.getLevelCleared()) PlayerDAO.updatePlayerLevel(player.getId(), levelIndex + 1);
        } else {
            gameCompleted = true;
            velX = velY = 0; inAir = false; soundManager.stopMusic();
            PlayerDAO.updatePlayerLevel(player.getId(), levelIndex + 1);
            saveTotalScore();
            repaint();
        }
    }

    private void saveTotalScore() { GameDAO.saveOrUpdateScore(player.getId(), score); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // ---------------- Background ----------------
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // ---------------- Spikes ----------------
        for (Spike spike : spikes) {
            spike.draw(g2);
        }

        // ---------------- Coins (Level 2) ----------------
        if (levelIndex == 1) {
            for (Coin coin : coins) {
                if (!coin.isCollected()) coin.draw(g2);
            }
        }

        // ---------------- Door ----------------
        // Base door rectangle
        g2.setColor(new Color(139, 69, 19)); // brown wood
        g2.fillRect(door.x, door.y, door.width, door.height);

        // Add panels for wooden texture
        int panelMargin = 5;
        int doorpanelHeight = (door.height - 3 * panelMargin) / 2;  
        g2.setColor(new Color(160, 82, 45)); // lighter brown
        g2.fillRect(door.x + panelMargin, door.y + panelMargin, door.width - 2 * panelMargin, doorpanelHeight);
        g2.fillRect(door.x + panelMargin, door.y + 2 * panelMargin + panelHeight, door.width - 2 * panelMargin, panelHeight);

        // Add metal handle
        g2.setColor(Color.GRAY);
        g2.fillOval(door.x + door.width - 20, door.y + door.height / 2 - 5, 10, 10);

        // Add wood grain effect (simple lines)
        g2.setColor(new Color(100, 50, 20, 120)); // semi-transparent dark brown
        for (int i = door.x + 5; i < door.x + door.width - 5; i += 5) {
            g2.drawLine(i, door.y + 5, i, door.y + door.height - 5);
        }

        // Draw the door border
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new java.awt.BasicStroke(2));
        g2.drawRect(door.x, door.y, door.width, door.height);

        // ---------------- Level 3 Spike ----------------
        if (levelIndex == 2 && level3SpikeSpawned && level3Spike != null) {
            level3Spike.draw(g2);
        }

        // ---------------- Player ----------------
        int bodyWidth = 30, bodyHeight = 50, headSize = 20;
        int legWidth = 10, legHeight = 15;

        g2.setColor(Color.WHITE);
        // Head
        g2.fillRect(playerX + (PLAYER_SIZE - headSize) / 2, playerY, headSize, headSize);
        // Body
        g2.fillRect(playerX + (PLAYER_SIZE - bodyWidth) / 2, playerY + headSize, bodyWidth, bodyHeight);

        // Legs with walking offset
        int legOffset = 0;
        if (velX != 0) legOffset = (System.currentTimeMillis() / 150 % 2 == 0) ? 5 : -5;

        g2.fillRect(playerX + (PLAYER_SIZE - bodyWidth) / 2, playerY + headSize + bodyHeight, legWidth, legHeight);
        g2.fillRect(playerX + (PLAYER_SIZE - bodyWidth) / 2 + bodyWidth - legWidth + legOffset,
                    playerY + headSize + bodyHeight, legWidth, legHeight);

       // ---------------- Score ----------------
g2.setColor(Color.WHITE);
g2.drawString("Score: " + score, 20, 53);

// ---------------- Level Indicator ----------------
g2.setColor(Color.CYAN);
g2.drawString("Level: " + (levelIndex + 1), 20, 25);
        // ---------------- Game Completed ----------------
        if (gameCompleted && levelIndex >= 7) { // after level 8
    String msg1 = "Congratulations! Game Completed!";
    String msg2 = "Final Score: " + score;

    // Use your current font and derive a bigger version
    Font bigFont = g2.getFont().deriveFont(Font.BOLD, 48f);
    g2.setFont(bigFont);
    g2.setColor(Color.YELLOW);

    // Center horizontally
    int msg1Width = g2.getFontMetrics().stringWidth(msg1);
    int msg2Width = g2.getFontMetrics().stringWidth(msg2);

    // Draw msg1 slightly above center, msg2 below
    g2.drawString(msg1, (panelWidth - msg1Width) / 2, panelHeight / 2 - 20);
    g2.drawString(msg2, (panelWidth - msg2Width) / 2, panelHeight / 2 + 40);
}   
    }

    public void setLevelIndex(int levelIndex) {
    this.levelIndex = levelIndex;
    resetLevel();
}
}
