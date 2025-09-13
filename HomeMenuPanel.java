import javax.swing.*;
import java.awt.*;

public class HomeMenuPanel extends JPanel {
    public HomeMenuPanel(JFrame frame, int playerId, String username) {
        setLayout(null);
        setBackground(Color.BLACK);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int panelWidth = screenSize.width;

        JLabel title = new JLabel("EQUILIBRIUM - Welcome " + username);
        title.setFont(new Font("Arial", Font.BOLD, 60));
        title.setForeground(Color.BLUE);
        title.setBounds(0, 50, panelWidth, 100);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        int buttonWidth = 150;
        int buttonHeight = 50;
        int spacing = 20;
        int startX = (panelWidth - (4 * buttonWidth + 3 * spacing)) / 2;
        int y = 250;

        GamePanel gamePanel = new GamePanel(playerId);

        for (int i = 1; i <= 4; i++) {
            JButton levelButton = new JButton("Level " + i);
            int level = i;
            levelButton.setBounds(startX + (i - 1) * (buttonWidth + spacing), y, buttonWidth, buttonHeight);
            levelButton.setFont(new Font("Arial", Font.BOLD, 20));
            levelButton.addActionListener(e -> {
                gamePanel.setLevelIndex(level - 1);
                frame.getContentPane().removeAll();
                frame.add(gamePanel);
                frame.revalidate();
                frame.repaint();
            });
            add(levelButton);
        }

        JButton leaderboardBtn = new JButton("Leaderboard");
        leaderboardBtn.setBounds(startX, y + 100, buttonWidth, buttonHeight);
        leaderboardBtn.addActionListener(e -> new LeaderboardFrame());
        add(leaderboardBtn);
    }
}
