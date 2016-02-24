package ransac;

import java.io.Serializable;

/**
 * Holds a 2D vector.
 * Dir is in radians, from 0 to 2pi.
 */
public class Vector2D implements Serializable {
    private static final long serialVersionUID = 1L;

    private double mag;
    private double dir; // rad

    public Vector2D() {
        mag = dir = 0;
    }

    public double getX() {
        return mag * Math.cos(dir);
    }

    public double getY() {
        return mag * Math.sin(dir);
    }

    public double getMag() {
        return mag;
    }

    public double getDir() {
        return dir;
    }

    public void setX(double x) {
        setPol(Math.sqrt(x * x + getY() * getY()), myatan2(getY(), x));
    }

    public void setY(double y) {
        setPol(Math.sqrt(getX() * getX() + y * y), myatan2(y, getX()));
    }

    public void setCart(double x, double y) {
        setPol(Math.sqrt(x * x + y * y), myatan2(y, x));
    }

    public void setMag(double mag0) {
        mag = mag0;
    }

    public void setDir(double dir0) {
        assert(dir0 >= 0 && dir0 <= 2 * Math.PI);
        dir = dir0;
    }

    public void setPol(double mag0, double dir0) {
        mag = mag0;
        dir = dir0;
    }

    public Vector2D plus(Vector2D other) {
        Vector2D result = new Vector2D();
        result.setCart(getX() + other.getX(), getY() + other.getY());
        return result;
    }

    public Vector2D mul(double value) {
        Vector2D result = new Vector2D();
        result.setPol(getMag() * value, getDir());
        return result;
    }

    /// atan2(y, x) returning [0, 2pi]
    static protected double myatan2(double y, double x) {
        double result = Math.atan2(y, x);
        if(result < 0)
            result += 2 * Math.PI;
        return result;
    }
};