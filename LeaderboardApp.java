import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

// Final Clean Modern Top 10 Leaderboard (no refresh/close buttons)
public class LeaderboardApp extends JFrame {
    private final DefaultListModel<PlayerEntry> listModel = new DefaultListModel<>();
    private final JList<PlayerEntry> list = new JList<>(listModel);
    private int hoveredIndex = -1;
    private final Font appFont;

    public LeaderboardApp() {
        // Use system look & feel for consistent UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ignored) {}

        appFont = UIManager.getFont("Label.font").deriveFont(Font.PLAIN, 14f);

        setTitle("Top 10 Players");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Main panel with gradient background
        add(new GradientBackgroundPanel(), BorderLayout.CENTER);

        // Load leaderboard data
        loadTop10Scores();

        setVisible(true);
    }

    // Gradient background and leaderboard card
    private class GradientBackgroundPanel extends JPanel {
        GradientBackgroundPanel() {
            setLayout(new GridBagLayout());
            setOpaque(true);

            JPanel card = new RoundedCardPanel();
            card.setPreferredSize(new Dimension(360, 400));
            card.setLayout(new BorderLayout());
            card.setBorder(new EmptyBorder(10, 10, 10, 10));

            list.setCellRenderer(new LeaderboardCellRenderer());
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setFixedCellHeight(64);
            list.setFont(appFont);
            list.setOpaque(false);

            // Hover effects
            list.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int idx = list.locationToIndex(e.getPoint());
                    if (idx != hoveredIndex) {
                        hoveredIndex = idx;
                        list.repaint();
                    }
                }
            });
            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    hoveredIndex = -1;
                    list.repaint();
                }
            });

            JScrollPane sp = new JScrollPane(list);
            sp.setBorder(null);
            sp.setOpaque(false);
            sp.getViewport().setOpaque(false);

            card.add(sp, BorderLayout.CENTER);
            add(card);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            Color top = new Color(18, 24, 31);
            Color bottom = new Color(33, 47, 61);
            g2.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }
    }

    // Rounded leaderboard card with shadow
    private class RoundedCardPanel extends JPanel {
        RoundedCardPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            int arc = 18;
            Graphics2D g2 = (Graphics2D) g.create();

            // shadow
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
            g2.setColor(Color.BLACK);
            for (int i = 0; i < 6; i++) {
                g2.fillRoundRect(8 - i, 8 - i, getWidth() - 8 + i * 2, getHeight() - 8 + i * 2, arc + i, arc + i);
            }

            // card background
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(new Color(40, 44, 50));
            g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public Insets getInsets() {
            return new Insets(10, 10, 10, 10);
        }
    }

    // Custom cell renderer for leaderboard entries
    private class LeaderboardCellRenderer implements ListCellRenderer<PlayerEntry> {
        @Override
        public Component getListCellRendererComponent(JList<? extends PlayerEntry> list,
                                                      PlayerEntry value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(true);
            row.setBorder(new EmptyBorder(8, 10, 8, 10));

            // Rank (with medal icons for top 3)
            JLabel rankLabel = new JLabel();
            rankLabel.setFont(appFont.deriveFont(Font.BOLD, 16f));
            rankLabel.setPreferredSize(new Dimension(44, 44));
            rankLabel.setHorizontalAlignment(SwingConstants.CENTER);

            switch (index) {
                case 0 -> rankLabel.setText("1");
                case 1 -> rankLabel.setText("2");
                case 2 -> rankLabel.setText("3");
                default -> rankLabel.setText(String.valueOf(index + 1));
            }

            JLabel nameLabel = new JLabel(value.username);
            nameLabel.setFont(appFont.deriveFont(Font.PLAIN, 16f));

            JLabel scoreLabel = new JLabel(String.valueOf(value.score), SwingConstants.RIGHT);
            scoreLabel.setFont(appFont.deriveFont(Font.BOLD, 16f));

            row.add(rankLabel, BorderLayout.WEST);
            row.add(nameLabel, BorderLayout.CENTER);
            row.add(scoreLabel, BorderLayout.EAST);

            Color normalBg = new Color(40, 44, 50);
            Color hoveredBg = new Color(60, 65, 72);
            Color selectedBg = new Color(76, 131, 255);

            if (isSelected) {
                row.setBackground(selectedBg);
                nameLabel.setForeground(Color.WHITE);
                scoreLabel.setForeground(Color.WHITE);
                rankLabel.setForeground(Color.WHITE);
            } else if (index == hoveredIndex) {
                row.setBackground(hoveredBg);
                nameLabel.setForeground(Color.WHITE);
                scoreLabel.setForeground(Color.WHITE);
                rankLabel.setForeground(Color.WHITE);
            } else {
                row.setBackground(normalBg);
                nameLabel.setForeground(Color.WHITE);
                scoreLabel.setForeground(Color.LIGHT_GRAY);
                rankLabel.setForeground(Color.WHITE);
            }
            return row;
        }
    }

    // Simple data holder
    private static class PlayerEntry {
        final String username;
        final int score;

        PlayerEntry(String username, int score) {
            this.username = username;
            this.score = score;
        }
    }

    // Load top 10 players
    private void loadTop10Scores() {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            String sql = "SELECT p.username, s.score " +
                         "FROM GameScores s " +
                         "JOIN Players p ON s.player_id = p.id " +
                         "ORDER BY s.score DESC LIMIT 10";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                boolean any = false;
                while (rs.next()) {
                    any = true;
                    listModel.addElement(new PlayerEntry(rs.getString("username"), rs.getInt("score")));
                }
                if (!any) {
                    listModel.addElement(new PlayerEntry("No players found", 0));
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to load leaderboard:\n" + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LeaderboardApp());
    }
}
