package ransac3.test;


import ransac3.ParameterEstimator;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * This is a estimator for 2D lines based on the equation:
 *     n_x*(x-a_x)+n_y*(y-a_y)=0
 * We use 4 parameters (n_x, n_y, a_x, a_y) to represent a line,
 * in which
 * (n_x, n_y) is the normal vector of the line, and
 * (n_x*n_x+n_y*n_y)=1.
 * (a_x, a_y) is a point one the line.
 * </pre>
 *
 * @author tiger
 */
public class LineParamEstimator implements ParameterEstimator<Point2D, Double> {
    private double deltaSquared;

    public LineParamEstimator(double delta) {
        this.deltaSquared = delta * delta;
    }

    /**
     * Is point the point belong to a line.<br>
     * The distance between point P(x, y) and line (n_x, n_y, a_x, a_y)<br>
     * can be calculated by the function:<br>
     * d(P,l)=(n_x*(p_x-a_x)+n_y*(p_y-a_y))/(n_x*n_x+n_y*n_y)^(1/2)<br>
     *
     * @param params
     *            The line parameters (n_x,n_y,a_x,a_y).
     * @param data
     *            The point to be tested.
     * @return True if the distance between this point and the line is smaller
     *         than 'delta'.
     */
    @Override
    public boolean agree(List<Double> params, Point2D data) {
        double nX = params.get(0);
        double nY = params.get(1);
        double aX = params.get(2);
        double aY = params.get(3);
        double pX = data.getX();
        double pY = data.getY();
        double d = nX * (pX - aX) + nY * (pY - aY);
        return ((d * d) < deltaSquared);
    }

    /**
     * Calculate the line parameters by two points.
     */
    @Override
    public List<Double> estimate(List<Point2D> data) {
        if (data.size() < 2) {
            return null;
        }
        double nX = data.get(1).getY() - data.get(0).getY();
        double nY = data.get(0).getX() - data.get(1).getX();
        double norm = Math.sqrt(nX * nX + nY * nY);
        List<Double> params = new ArrayList<Double>();
        params.add(nX / norm);
        params.add(nY / norm);
        params.add(data.get(0).getX());
        params.add(data.get(0).getY());
        return params;
    }

    @Override
    public List<Double> leastSquaresEstimate(List<Point2D> data) {
        int dataSize = data.size();
        if (dataSize < 2) {
            return null;
        }
        if (dataSize == 2) {
            return estimate(data);
        }
        double nX, nY, norm;
        double meanX = 0.0;
        double meanY = 0.0;
        // The entries of the symmetric covariance matrix
        double covMat11 = 0.0;
        double covMat12 = 0.0;
        double covMat21 = 0.0;
        double covMat22 = 0.0;
        for (int i = 0; i < dataSize; i++) {
            meanX += data.get(i).getX();
            meanY += data.get(i).getY();

            covMat11 += data.get(i).getX() * data.get(i).getX();
            covMat12 += data.get(i).getX() * data.get(i).getY();
            covMat22 += data.get(i).getY() * data.get(i).getY();
        }

        meanX /= dataSize;
        meanY /= dataSize;

        covMat11 -= dataSize * meanX * meanX;
        covMat12 -= dataSize * meanX * meanY;
        covMat22 -= dataSize * meanY * meanY;
        covMat21 = covMat12;

        if (covMat11 < 1e-12) {
            nX = 1.0;
            nY = 0.0;
        } else if (covMat22 < 1e-12) {
            nX = 0.0;
            nY = 1.0;
        } else {
            // lamda1 is the largest eigen-value of the covariance matrix
            // and is used to compute the eigen-vector corresponding to the
            // smallest eigenvalue, which isn't computed explicitly.
            double lamda1 = (covMat11 + covMat22 + Math
                    .sqrt((covMat11 - covMat22) * (covMat11 - covMat22) + 4
                            * covMat12 * covMat21)) / 2.0;
            nX = -covMat12;
            nY = lamda1 - covMat22;
            norm = Math.sqrt(nX * nX + nY * nY);
            nX /= norm;
            nY /= norm;
        }
        ArrayList<Double> parameters = new ArrayList<Double>();
        parameters.add(nX);
        parameters.add(nY);
        parameters.add(meanX);
        parameters.add(meanY);
        return parameters;
    }

}