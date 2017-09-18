import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Paint;

import java.awt.geom.Point2D;

/**
 * Created by Brothers on 9/16/2017.
 */
public class Particle {

    private double x, y;

    private Point2D velocity;

    private double radius;
    private double life = 1.0;
    private double decay;

    private Paint color;
    private BlendMode blendMode;

    public Particle(double x, double y, Point2D velocity, double radius, double expireTime, Paint color, BlendMode blendMode) {
        this.x = x;
        this.y = y;
        this.velocity = velocity;
        this.radius = radius;
        this.color = color;
        this.blendMode = blendMode;
        this.decay = 0.016 / expireTime;
    }

    public boolean isAlive() {
        return life > 0;
    }

    public void update() {
        x += velocity.getX();
        y += velocity.getY();

        life -= decay;
    }

    public void render(GraphicsContext g) {
        g.setGlobalAlpha(life);
        g.setGlobalBlendMode(blendMode);
        g.setFill(color);
        g.setEffect(new GaussianBlur());
        g.fillOval(x, y, radius, radius);
    }

    public void setLife(double life){
        this.life = life;
    }
}
