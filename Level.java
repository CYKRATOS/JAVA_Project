import java.awt.Rectangle;
import java.util.List;

public class Level {
    private final List<Spike> spikes;
    private final Rectangle door;

    public Level(List<Spike> spikes, Rectangle door) {
        this.spikes = spikes;
        this.door = door;
    }

    public List<Spike> getSpikes() { return spikes; }
    public Rectangle getDoor() { return door; }
}
