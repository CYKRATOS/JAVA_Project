import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public final class LeaderboardPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel model;

    public LeaderboardPanel() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"Username", "Level", "Score", "Date"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadScores();
    }

    public void loadScores() {
        model.setRowCount(0);
        try (ResultSet rs = GameDAO.getTopScores()) {
            while (rs != null && rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("username"),
                        rs.getInt("level"),
                        rs.getInt("score"),
                        rs.getTimestamp("timestamp")
                });
            }
        } catch (SQLException e) {
        }
    }
}
