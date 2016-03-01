package ransac3.test;

public class Point2D {
    /**
     * The x coordinate.
     */
    private double x;
    /**
     * The y coordinate.
     */
    private double y;

    /**
     * The constructor of new 2D point.
     *
     * @param x
     *            The x coordinate.
     * @param y
     *            The y coordinate.
     */
    public Point2D(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    /**
     * @param x
     *            The x coordinate.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return The x coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * @param y
     *            The y coordinate.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return The y coordinate.
     */
    public double getY() {
        return y;
    }

    /**
     * Formated string to represent the coordinates.
     *
     * @return The coordinates in the format "(x,y)".
     */
    public String toString() {
        return "("+this.x + ", " + this.y+")";
    }

}