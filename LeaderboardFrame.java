import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class LeaderboardFrame extends JFrame {
    public LeaderboardFrame() {
        setTitle("Leaderboard");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        DefaultListModel<String> model = new DefaultListModel<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT p.username, s.score " +
                         "FROM GameScores s " +
                         "JOIN Players p ON s.player_id = p.id " +
                         "ORDER BY s.score DESC LIMIT 10";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addElement(rs.getString("username") + " - " + rs.getInt("score"));
            }
        } catch (SQLException e) {
        }

        JList<String> list = new JList<>(model);
        add(new JScrollPane(list), BorderLayout.CENTER);
        setVisible(true);
    }
}
