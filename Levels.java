import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Levels {

    public static List<Level> createLevels(int panelWidth, int panelHeight, int groundHeight) {
        List<Level> levels = new ArrayList<>();

       // Level 1 (example)
       levels.add(new Level(
            List.of(
                new Spike(panelWidth / 4, panelHeight - groundHeight - 40, 60, 40, 0),   // stationary
                new Spike(panelWidth / 2, panelHeight - groundHeight - 40, 60, 40, -1),  // moves left
                new Spike(3 * panelWidth / 4, panelHeight - groundHeight - 40, 60, 40, 1) // moves right
            ),
            new Rectangle(panelWidth - 150, panelHeight - groundHeight - 80, 40, 80)      // door
        ));
        
        // Level 2 (example)
        levels.add(new Level(
            new ArrayList<>(), // No spikes
            new Rectangle(panelWidth - 150, panelHeight - groundHeight - 80, 40, 80) // Door
        ));

        // Level 3
        levels.add(new Level(
            List.of(), // No spikes initially
            new Rectangle(panelWidth - 150, panelHeight - groundHeight - 80, 40, 80) // Door initial position
        ));


       // Level 4: Sliding Door Trap 
        levels.add(new Level(
            new ArrayList<>(), // start with no spikes; they will be spawned when the door moves
            new Rectangle(panelWidth / 2 - 20, panelHeight - groundHeight - 80, 40, 80) // Door centered
        ));

        // Level 5
        levels.add(new Level(
            List.of(
                new Spike(panelWidth / 3, panelHeight - groundHeight - 40, 60, 40, 0)
            ),
            new Rectangle(panelWidth - 600, panelHeight - groundHeight - 80, 40, 80) // Door
        ));

        // Level 6
levels.add(new Level(
    List.of(
        new Spike(panelWidth / 5, panelHeight - groundHeight - 40, 60, 40),
        new Spike(2 * panelWidth / 5, panelHeight - groundHeight - 40, 60, 40),
        new Spike(3 * panelWidth / 5, panelHeight - groundHeight - 40, 60, 40),
        new Spike(4 * panelWidth / 5, panelHeight - groundHeight - 40, 60, 40)
    ),
    new Rectangle(50, panelHeight - groundHeight - 80, 40, 80) // Door on left
));

        // Level 7 (example)
        levels.add(new Level(
            List.of(
                new Spike(panelWidth / 2, panelHeight - groundHeight - 40, 60, 40, -1), // Spike 2 - moves LEFT
                new Spike(3 * panelWidth / 4, panelHeight - groundHeight - 40, 60, 40, 1) // Spike 3 - moves RIGHT
            ),
            new Rectangle(panelWidth - 150, panelHeight - groundHeight - 80, 40, 80) // Door
        ));

        // Level 8 (example)
        levels.add(new Level(
            List.of(
                new Spike(panelWidth / 2, panelHeight - groundHeight - 40, 60, 40, -1), // Spike 2 - moves LEFT
                new Spike(3 * panelWidth / 4, panelHeight - groundHeight - 40, 60, 40, 1) // Spike 3 - moves RIGHT
            ),
            new Rectangle(panelWidth - 150, panelHeight - groundHeight - 80, 40, 80) // Door
        ));

        return levels;
    }
}