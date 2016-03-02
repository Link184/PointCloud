package ransac;

import org.ddogleg.fitting.modelset.DistanceFromModel;

import java.util.List;

public class DistanceFromLine implements DistanceFromModel<Line2D,Point2D> {

    // parametric line equation
    double x0, y0;
    double slopeX,slopeY;

    @Override
    public void setModel(Line2D param) {
        x0 = param.x;
        y0 = param.y;

        slopeX = -y0;
        slopeY = x0;
    }

    @Override
    public double computeDistance(Point2D p) {

        // find the closest point on the line to the point
        double t = slopeX * ( p.x - x0) + slopeY * ( p.y - y0);
        t /= slopeX * slopeX + slopeY * slopeY;

        double closestX = x0 + t*slopeX;
        double closestY = y0 + t*slopeY;

        // compute the Euclidean distance
        double dx = p.x - closestX;
        double dy = p.y - closestY;

        return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * There are some situations where processing everything as a list can speed things up a lot.
     * This is not one of them.
     */
    @Override
    public void computeDistance(List<Point2D> obs, double[] distance) {
        for( int i = 0; i < obs.size(); i++ ) {
            distance[i] = computeDistance(obs.get(i));
        }
    }
}