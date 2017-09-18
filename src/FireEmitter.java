import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brothers on 9/16/2017.
 */
public class FireEmitter extends Emitter {
    @Override
    public List<Particle> emit(double x, double y) {
        List<Particle> particles = new ArrayList<>();
        int numParticals = 10;


        for (int i = 0; i < numParticals; i++) {
            Particle p = new Particle(x, y, new java.awt.geom.Point2D.Double((Math.random() - 0.5) * 0.4, Math.random() * -2),
                    10, 0.8, Color.rgb(230, 20, 20), BlendMode.ADD);
            p.setLife(0.5);
            particles.add(p);
        }
        return particles;
    }
}
