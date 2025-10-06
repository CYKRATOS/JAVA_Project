import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDAO {

    // Signup new player: level_cleared defaults to 0
    public static Player signup(String username, String password, String name) {
        String query = "INSERT INTO Players (username, password, name) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, name);

            int affected = stmt.executeUpdate();
            if (affected == 1) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    // New player has level_cleared = 0
                    return new Player(id, username, name, 0);
                }
            }

        } catch (SQLException e) {
        }
        return null; // signup failed
    }

    // Login existing player
    public static Player login(String username, String password) {
        String query = "SELECT id, name, level_cleared FROM Players WHERE username=? AND password=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int levelCleared = rs.getInt("level_cleared");
                return new Player(id, username, name, levelCleared);
            }

        } catch (SQLException e) {
        }
        return null; // login failed
    }

    // Update player's highest level cleared
    public static void updatePlayerLevel(int playerId, int level) {
        String query = "UPDATE Players SET level_cleared=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, level);
            stmt.setInt(2, playerId);
            stmt.executeUpdate();

        } catch (SQLException e) {
        }
    }

    // Get player's current cleared level
    public static int getPlayerLevel(int playerId) {
        String query = "SELECT level_cleared FROM Players WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, playerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("level_cleared");
            }

        } catch (SQLException e) {
        }
        return 0; // default if not found
    }
}
