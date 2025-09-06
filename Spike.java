import java.awt.Rectangle;

public class Spike {
    private final int startX, startY;
    private final boolean isLevel4Obstacle;
    private final Rectangle rect;
    private int velX;

    public Spike(int x, int y, int width, int height, int velX) {
        this(x, y, width, height, velX, false);
    }

    public Spike(int x, int y, int width, int height, int velX, boolean isLevel4Obstacle) {
        this.rect = new Rectangle(x, y, width, height);
        this.velX = velX;
        this.startX = x;
        this.startY = y;
        this.isLevel4Obstacle = isLevel4Obstacle;
    }

    public Rectangle getRect() {
        return rect;
    }

    public void update(int panelWidth) {
        rect.x += velX;

        // Only bounce if it is NOT Level 4 obstacle
        if (!isLevel4Obstacle) {
            if (rect.x <= 0 || rect.x + rect.width >= panelWidth) {
                velX = -velX;
            }
        }
    }

    public void reset() {
        rect.x = startX;
        rect.y = startY;
    }
}
                