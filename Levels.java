import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Levels {

    public static List<Level> createLevels(int panelWidth, int panelHeight, int groundHeight) {
        List<Level> levels = new ArrayList<>();

       levels.add(new Level(
            List.of(
                new Spike(panelWidth / 4, panelHeight - groundHeight - 40, 60, 40),    // Spike 1 - stationary
                new Spike(panelWidth / 2, panelHeight - groundHeight - 40, 60, 40, -1), // Spike 2 - moves LEFT
                new Spike(3 * panelWidth / 4, panelHeight - groundHeight - 40, 60, 40, 1) // Spike 3 - moves RIGHT
            ),
            new Rectangle(panelWidth - 150, panelHeight - groundHeight - 80, 40, 80) // Door
        ));
        
        // Level 2 (example)
        levels.add(new Level(
            new ArrayList<>(), // No spikes
            new Rectangle(panelWidth - 150, panelHeight - groundHeight - 80, 40, 80) // Door
        ));

        // Level 3 (example)
        levels.add(new Level(
            List.of(
                new Spike(panelWidth / 4, panelHeight - groundHeight - 40, 60, 40),    // Spike 1 - stationary
                new Spike(panelWidth / 2, panelHeight - groundHeight - 40, 60, 40, -1), // Spike 2 - moves LEFT
                new Spike(3 * panelWidth / 4, panelHeight - groundHeight - 40, 60, 40, 1) // Spike 3 - moves RIGHT
            ),
            new Rectangle(panelWidth - 150, panelHeight - groundHeight - 80, 40, 80) // Door
        ));

        // Level 4 (example)
        levels.add(new Level(
            List.of(
                new Spike(panelWidth / 4, panelHeight - groundHeight - 40, 60, 40),    // Spike 1 - stationary
                new Spike(panelWidth / 2, panelHeight - groundHeight - 40, 60, 40, -1), // Spike 2 - moves LEFT
                new Spike(3 * panelWidth / 4, panelHeight - groundHeight - 40, 60, 40, 1) // Spike 3 - moves RIGHT
            ),
            new Rectangle(panelWidth - 150, panelHeight - groundHeight - 80, 40, 80) // Door
        ));

        // Level 5 (example)
        levels.add(new Level(
            List.of(
                new Spike(panelWidth / 4, panelHeight - groundHeight - 40, 60, 40),    // Spike 1 - stationary
                new Spike(panelWidth / 2, panelHeight - groundHeight - 40, 60, 40, -1), // Spike 2 - moves LEFT
                new Spike(3 * panelWidth / 4, panelHeight - groundHeight - 40, 60, 40, 1) // Spike 3 - moves RIGHT
            ),
            new Rectangle(panelWidth - 150, panelHeight - groundHeight - 80, 40, 80) // Door
        ));

        // Level 6 (example)
        levels.add(new Level(
            List.of(
                new Spike(panelWidth / 4, panelHeight - groundHeight - 40, 60, 40),    // Spike 1 - stationary
                new Spike(panelWidth / 2, panelHeight - groundHeight - 40, 60, 40, -1), // Spike 2 - moves LEFT
                new Spike(3 * panelWidth / 4, panelHeight - groundHeight - 40, 60, 40, 1) // Spike 3 - moves RIGHT
            ),
            new Rectangle(panelWidth - 150, panelHeight - groundHeight - 80, 40, 80) // Door
        ));

        // Level 7 (example)
        levels.add(new Level(
            List.of(
                new Spike(panelWidth / 4, panelHeight - groundHeight - 40, 60, 40),    // Spike 1 - stationary
                new Spike(panelWidth / 2, panelHeight - groundHeight - 40, 60, 40, -1), // Spike 2 - moves LEFT
                new Spike(3 * panelWidth / 4, panelHeight - groundHeight - 40, 60, 40, 1) // Spike 3 - moves RIGHT
            ),
            new Rectangle(panelWidth - 150, panelHeight - groundHeight - 80, 40, 80) // Door
        ));

        // Level 8 (example)
        levels.add(new Level(
            List.of(
                new Spike(panelWidth / 4, panelHeight - groundHeight - 40, 60, 40),    // Spike 1 - stationary
                new Spike(panelWidth / 2, panelHeight - groundHeight - 40, 60, 40, -1), // Spike 2 - moves LEFT
                new Spike(3 * panelWidth / 4, panelHeight - groundHeight - 40, 60, 40, 1) // Spike 3 - moves RIGHT
            ),
            new Rectangle(panelWidth - 150, panelHeight - groundHeight - 80, 40, 80) // Door
        ));

        return levels;
    }
}
