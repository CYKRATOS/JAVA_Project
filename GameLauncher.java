import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class GameLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
           JFrame frame = new JFrame("Equilibrium - Login");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // ✅ Start with login screen ONLY
            frame.add(new LoginPanel(frame));

            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // full screen
            frame.setVisible(true);
            // --- Home Panel ---
            //JPanel homePanel = new JPanel();
            //homePanel.setLayout(null);
            //homePanel.setBackground(Color.BLACK);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int panelWidth = screenSize.width;

            // Title
            JLabel title = new JLabel("EQUILIBRIUM");
            title.setFont(new Font("Arial", Font.BOLD, 60));
            title.setForeground(Color.BLUE);
            title.setBounds(0, 50, panelWidth, 100);
            title.setHorizontalAlignment(SwingConstants.CENTER);

            // Level buttons
            int buttonWidth = 150;
            int buttonHeight = 50;
            int spacing = 20;
            int startX = (panelWidth - (4 * buttonWidth + 3 * spacing)) / 2;
            int y = 250;
            int playerId = 0;

            GamePanel gamePanel = new GamePanel(playerId); // create the game panel

            for (int i = 1; i <= 4; i++) {
                JButton levelButton = new JButton("Level " + i);
                int level = i; 
                levelButton.setBounds(startX + (i - 1) * (buttonWidth + spacing), y, buttonWidth, buttonHeight);
                levelButton.setFont(new Font("Arial", Font.BOLD, 20));
                levelButton.addActionListener((ActionEvent e) -> {
                    gamePanel.setLevelIndex(level - 1); // start selected level
                    frame.getContentPane().removeAll(); // remove home screen
                    frame.add(gamePanel); // show game panel
                    frame.revalidate();
                    frame.repaint();
                });
            }

            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximized window but keep title bar
            frame.setVisible(true);
        });
    }
}
