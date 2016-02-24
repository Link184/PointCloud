package ransac;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Holds a 2D line: ax + by + c = 0.
 */
public class Line implements Serializable {
    public double a;
    public double b;
    public double c;

    /**
     * Point-to-line distance.
     * See http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
     */
    public double distance(Point2D.Double point) {
        return Math.abs(a * point.x + b * point.y + c) / Math.sqrt(a * a + b * b);
    }
};
