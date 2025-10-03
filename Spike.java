import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Spike {
    private final Rectangle rect;
    private final int startX, startY;

    private final boolean reactive;           // moves when triggered
    private boolean triggered = false;  // has it started moving?
    private int direction = 1;          // 1 = right, -1 = left
    private final int moveDistance = 150; // max pixels to move
    private int movedSoFar = 0;

    private int triggerDistance = 200;  // default trigger distance
    private Color spikeColor = new Color(180, 50, 50); // realistic reddish metallic

    // -----------------------
    // Constructors
    // -----------------------

    // Stationary spike
    public Spike(int x, int y, int width, int height) {
        this.rect = new Rectangle(x, y, width, height);
        this.startX = x;
        this.startY = y;
        this.reactive = false;
    }

    // Reactive spike (Level 1)
    public Spike(int x, int y, int width, int height, int direction) {
        this.rect = new Rectangle(x, y, width, height);
        this.startX = x;
        this.startY = y;
        this.reactive = true;
        this.direction = direction; // 1 = right, -1 = left
    }

    // -----------------------
    // Getters / Setters
    // -----------------------
    public Rectangle getRect() {
        return rect;
    }

    public boolean isReactive() {
        return reactive;
    }

    public boolean getTriggered() {
        return triggered;
    }

    public void trigger() {
        triggered = true;
    }

    public int getTriggerDistance() {
        return triggerDistance;
    }

    public void setTriggerDistance(int distance) {
        this.triggerDistance = distance;
    }

    public void setSpikeColor(Color color) {
        this.spikeColor = color;
    }

    // -----------------------
    // Reset & Update
    // -----------------------
    public void reset() {
        rect.x = startX;
        rect.y = startY;
        movedSoFar = 0;
        triggered = false;
    }

    public void update() {
        if (reactive && triggered && movedSoFar < moveDistance) {
            int step = 4; // pixels per frame
            rect.x += step * direction;
            movedSoFar += step;
        }
    }

    // -----------------------
    // Draw the spike
    // -----------------------
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int spikeWidth = rect.width;
        int spikeHeight = rect.height;

        // Base rectangle (solid)
        g2.setColor(spikeColor.darker());
        g2.fillRect(rect.x, rect.y + spikeHeight / 2, spikeWidth, spikeHeight / 2);

        // Pointed tip (triangle)
        int[] xPoints = { rect.x, rect.x + spikeWidth / 2, rect.x + spikeWidth };
        int[] yPoints = { rect.y + spikeHeight / 2, rect.y, rect.y + spikeHeight / 2 };
        g2.setColor(spikeColor);
        g2.fillPolygon(xPoints, yPoints, 3);

        // Optional highlight for 3D effect
        g2.setColor(new Color(255, 200, 200, 180)); // semi-transparent white
        int[] highlightX = { rect.x + spikeWidth / 4, rect.x + spikeWidth / 2, rect.x + 3 * spikeWidth / 4 };
        int[] highlightY = { rect.y + spikeHeight / 2, rect.y + spikeHeight / 4, rect.y + spikeHeight / 2 };
        g2.fillPolygon(highlightX, highlightY, 3);
    }
}
