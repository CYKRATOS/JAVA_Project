
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

public class Coin {

    private final int x, y, width, height;
    private boolean collected = false;

    // for small animation / glow
    private float glowPhase = 0;

    public Coin(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle getRect() {
        // slightly smaller hitbox for smoother collisions
        int margin = 5;
        return new Rectangle(x + margin, y + margin, width - 2 * margin, height - 2 * margin);
    }

    public void draw(Graphics2D g2) {
        if (collected) {
            return;
        }

        glowPhase += 0.1f;
        if (glowPhase > Math.PI * 2) {
            glowPhase = 0;
        }

        // pulsing glow
        float glow = (float) (Math.sin(glowPhase) * 0.5 + 0.5);
        Color coinColor = new Color(
                255,
                (int) (215 + 40 * glow),
                (int) (0 + 30 * glow)
        );

        g2.setColor(coinColor);
        g2.fill(new Ellipse2D.Double(x, y, width, height));

        // outline
        g2.setColor(Color.ORANGE.darker());
        g2.setStroke(new java.awt.BasicStroke(2));
        g2.draw(new Ellipse2D.Double(x, y, width, height));

        // highlight shine
        g2.setColor(new Color(255, 255, 255, 160));
        g2.fillOval(x + width / 3, y + height / 6, width / 3, height / 6);
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
