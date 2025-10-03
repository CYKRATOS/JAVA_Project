import java.awt.*;
import javax.swing.*;

public class HomeMenuPanel extends JPanel {

    public HomeMenuPanel(JFrame frame, int playerId, String username) {
        setLayout(null);
        setBackground(Color.BLACK);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int panelWidth = screenSize.width;

        // Title
        JLabel title = new JLabel("EQUILIBRIUM - Welcome " + username);
        title.setFont(new Font("Arial", Font.BOLD, 60));
        title.setForeground(Color.BLUE);
        title.setBounds(0, 50, panelWidth, 100);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // Buttons configuration
        int buttonWidth = 150;
        int buttonHeight = 50;
        int spacingX = 20;
        int spacingY = 20;
        int startX = (panelWidth - (4 * buttonWidth + 3 * spacingX)) / 2;
        int startY = 250;

        // Level buttons (2 rows x 4 columns)
        for (int i = 1; i <= 8; i++) {
            JButton levelButton = new JButton("Level " + i);
            int level = i;
            int row = (i - 1) / 4; // 0 or 1
            int col = (i - 1) % 4; // 0..3
            int x = startX + col * (buttonWidth + spacingX);
            int y = startY + row * (buttonHeight + spacingY);

            levelButton.setBounds(x, y, buttonWidth, buttonHeight);
            levelButton.setFont(new Font("Arial", Font.BOLD, 20));

            levelButton.addActionListener(e -> {
                // Create GamePanel with both playerId and username
                GamePanel gamePanel = new GamePanel(playerId, username);
                gamePanel.setLevelIndex(level - 1);

                frame.getContentPane().removeAll();
                frame.add(gamePanel);
                frame.revalidate();
                frame.repaint();
            });

            add(levelButton);
        }

        // Leaderboard button
        JButton leaderboardBtn = new JButton("Leaderboard");
        leaderboardBtn.setBounds(startX, startY + 2 * (buttonHeight + spacingY), buttonWidth, buttonHeight);
        leaderboardBtn.setFont(new Font("Arial", Font.BOLD, 20));
        leaderboardBtn.addActionListener(e -> new LeaderboardFrame());
        add(leaderboardBtn);
    }
}
