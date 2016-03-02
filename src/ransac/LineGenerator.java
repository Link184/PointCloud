package ransac;

import org.ddogleg.fitting.modelset.ModelGenerator;

import java.util.List;

public class LineGenerator implements ModelGenerator<Line2D,Point2D> {

    // a point at the origin (0,0)
    Point2D origin = new Point2D();

    @Override
    public boolean generate(List<Point2D> dataSet, Line2D output) {
        Point2D p1 = dataSet.get(0);
        Point2D p2 = dataSet.get(1);

        // create parametric line equation
        double slopeX = p2.x - p1.x;
        double slopeY = p2.y - p1.y;

        // find the closet point to the origin
        // find the closest point on the line to the point
        double t = slopeX * ( origin.x - p1.x) + slopeY * ( origin.y - p1.y);
        t /= slopeX * slopeX + slopeY * slopeY;

        output.x = p1.x + t*slopeX;
        output.y = p1.y + t*slopeY;

        return true;
    }

    @Override
    public int getMinimumPoints() {
        return 2;
    }
}
