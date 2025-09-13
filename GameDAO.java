import java.sql.*;

public class GameDAO {

    public static void saveScore(int playerId, int level, int score) {
        String query = "INSERT INTO GameScores(player_id, level, score) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, playerId);
            stmt.setInt(2, level);
            stmt.setInt(3, score);
            stmt.executeUpdate();
        } catch (SQLException e) {
        }
    }

    public static ResultSet getTopScores() {
        String query = "SELECT p.username, g.level, g.score, g.timestamp " +
                       "FROM GameScores g JOIN Players p ON g.player_id=p.id " +
                       "ORDER BY g.score DESC LIMIT 10";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            return stmt.executeQuery();
        } catch (SQLException e) {
            return null;
        }
    }
}
