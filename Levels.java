import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Levels {

    public static List<Level> createLevels(int panelWidth, int panelHeight, int groundHeight) {
        List<Level> levels = new ArrayList<>();

        // Level 1
        levels.add(new Level(
            List.of(new Spike(panelWidth / 4, panelHeight - groundHeight - 30, 30, 30, 2)),
            new Rectangle(panelWidth - 150, panelHeight - groundHeight - 80, 30, 80)
        ));

        // Level 2
        levels.add(new Level(
            List.of(
                new Spike(panelWidth / 5, panelHeight - groundHeight - 30, 30, 30, 3),
                new Spike(panelWidth / 2, panelHeight - groundHeight - 30, 30, 30, 2)
            ),
            new Rectangle(panelWidth - 150, panelHeight - groundHeight - 80, 30, 80)
        ));

        // Level 3
        levels.add(new Level(
            List.of(
                new Spike(panelWidth / 8, panelHeight - groundHeight - 30, 30, 30, 2),
                new Spike(panelWidth / 4, panelHeight - groundHeight - 30, 30, 30, 3),
                new Spike(panelWidth / 3, panelHeight - groundHeight - 30, 30, 30, 2)
            ),
            new Rectangle(panelWidth - 200, panelHeight - groundHeight - 80, 30, 80)
        ));

        // Level 4
        levels.add(new Level(
                List.of(), // empty list of spikes
                new Rectangle(panelWidth - 200, panelHeight - groundHeight - 80, 30, 80) // door on right
        ));

        return levels;
    }
}
