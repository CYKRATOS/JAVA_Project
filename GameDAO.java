
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameDAO {

    public static void saveOrUpdateScore(int playerId, int score) {
        String query = "INSERT INTO GameScores (player_id, score) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE score = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, playerId);   // for INSERT
            stmt.setInt(2, score);      // for INSERT
            stmt.setInt(3, score);      // for UPDATE
            stmt.executeUpdate();       // executes either INSERT or UPDATE
        } catch (SQLException e) {
        }
    }

    public static ResultSet getTopScores() {
        String query = "SELECT p.username, g.level, g.score, g.timestamp "
                + "FROM GameScores g JOIN Players p ON g.player_id=p.id "
                + "ORDER BY g.score DESC LIMIT 10";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            return stmt.executeQuery();
        } catch (SQLException e) {
            return null;
        }
    }
}
