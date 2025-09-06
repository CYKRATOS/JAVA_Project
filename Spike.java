import java.awt.*;

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

    public void draw(Graphics g) {
    g.setColor(Color.RED);

    int spikeCount = 3; // number of small triangles
    int spikeWidth = rect.width / spikeCount;
    int spikeHeight = rect.height;

    for (int i = 0; i < spikeCount; i++) {
        int x = rect.x + i * spikeWidth;
        int[] xPoints = { x, x + spikeWidth / 2, x + spikeWidth };
        int[] yPoints = { rect.y + spikeHeight, rect.y, rect.y + spikeHeight };
        g.fillPolygon(xPoints, yPoints, 3);
    }
}

}
                