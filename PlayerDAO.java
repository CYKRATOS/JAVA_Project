import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDAO {

    public static Player signup(String username, String password, String name) {
        String query = "INSERT INTO Players (username, password, name) VALUES (?, ?, ?)";
        try (var conn = DBConnection.getConnection();
             var stmt = conn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, name);
            int affected = stmt.executeUpdate();
            if (affected == 1) {
                var rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Player(id, username, name);
                }
            }
        } catch (Exception e) {}
        return null;
    }

    public static Player login(String username, String password) {
        String query = "SELECT id, name FROM Players WHERE username=? AND password=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                return new Player(id, username, name);
            }
        } catch (SQLException e) {}
        return null; // login failed
    }

}
