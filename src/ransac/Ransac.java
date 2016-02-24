package ransac;

//import javax.sound.sampled.Line;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/**
 * RANSAC line-detection algorithm.
 * All parameters are public fields.
 *
 * Modifications to avoid overfitting to "dense areas" (in a hallway, have many
 * more points to our sides than we do in front).
 * - If R2 from best-fit line is bad, store the original line (computed based on
 *   a sample, and with enough consenting points) rather than the new
 *   (overfitted) least-squares line.
 * - Run the greedy RANSAC algorithm repeatedly, and choose a result that
 *   matches enough points in less than the maximum number of iterations.
 * - Additionally in the step above, don't select a result if it has many
 *   confounded points (points nearby more than one line).
 *
 * Ideas for further improvement:
 * - If we run 10 times now and we're in a bad spot, we stupidly return the last
 *   result. Instead of rejecting results based on thresholds, score results,
 *   and select the best.
 */
public class Ransac {
    /// Iterations of the greedy line-finder.
    public int metaIterations = 10;
    /// Iterations random sampling to find one line.
    public int iterations = 100;
    /// Size of initial sample.
    public int sampleSize = 5;
    /// Width of laser beam to consider for initial sample.
    public int sampleBeamWidth = 10;
    /// Max distance of a point that still counts as being on the line.
    public double maxBelongDist = 0.05;
    /// Min number of points on a line to count it as valid.
    public int minConsensus = 30;
    /// R2 threshold under which a best-fit-line is bad.
    public double badR2threshold = 0.95;
    /// How many points can be confounded (fit multiple lines) for the result to still be valid.
    public int confoundThreshold = 15;
    /// Number of laser readings.
    final public int numLaser = 181;
    /// Tester object to store intermediary data in. Null unless testing.
    TestRansac tester = null;

    public Ransac() {
    }

    public Ransac(TestRansac tester0) {
        tester = tester0;
    }

    /// Repeatedly runs RANSAC until it finds a good-enough solution.
    public Line[] findLines(final double[] laser) {
        long timer = System.currentTimeMillis();
        ArrayList<Line> lines = new ArrayList<Line>();
        int totalIter = 0;

        int metaIter;
        for(metaIter = 0; metaIter < metaIterations; metaIter++) {
            lines.clear();
            int iter = findLinesGreedy(laser, lines);
            totalIter += iter;
            int confounds = countConfounds(laser, lines);
            //System.out.format("metaIter=%d found %d lines (%d confounds) in %d greedy iterations\n",
            //                  metaIter, lines.size(), confounds, iter);
            if(iter < iterations && confounds < confoundThreshold) {
                // This is a good catch because:
                // - it ran for few iterations, therefore many points were matched
                // - there are few confounds, therefore there are no "duplicate" lines
                metaIter++; // for stats only
                break;
            }
        }
        // If we don't break early, we just return the lines from the last attempt.

        timer = System.currentTimeMillis() - timer;
        //System.out.format("RANSAC ran %d meta-iterations (%d total iterations) " +
        //                  "in %.3f seconds, found %d lines.\n",
        //                  metaIter, totalIter, timer / 1000.0, lines.size());
        if(tester != null)
            tester.totalIter = totalIter;
        return lines.toArray(new Line[0]); // I can't believe Java requires the type like this.
    }

    /**
     * Finds lines in the given laser readings, using RANSAC.
     * Fills in the lines array, and returns the number of iterations ran.
     */
    public int findLinesGreedy(final double[] laser, ArrayList<Line> lines) {
        // Points considered for next matching.
        Point2D.Double[] points = laser2cartesian(laser);

        int iter;
        for(iter = 0; iter < iterations; iter++) {
            //System.out.format("Iteration %d\n", iter);
            // Quit early if out of points.
            if(points.length < minConsensus)
                break;
            // TODO: also break early if we run X times in a row unsuccessfully?

            // Get an initial sample.
            Point2D.Double[] sample = getInitialSample(points);

            // Compute best-fit line.
            Line bestFit = new Line();
            bestFitLine(sample, bestFit);

            // See how many points fit this line well.
            ArrayList<Integer> consenting = new ArrayList<Integer>();
            for(int i = 0; i < points.length; i++) {
                if(bestFit.distance(points[i]) < maxBelongDist)
                    consenting.add(i);
            }

            // Save intermediary data for testing.
            if(tester != null) {
                tester.points.add(points);
                tester.sample.add(sample);
                tester.sampleFitLine.add(bestFit);
                tester.consenting.add(consenting);
            }

            // If there is consensus, compute new best-fit line of consenting
            // points, store it, and remove consenting points from further
            // consideration.
            if(consenting.size() >= minConsensus) {
                Point2D.Double[] selected = new Point2D.Double[consenting.size()];
                for(int i = 0; i < consenting.size(); i++)
                    selected[i] = points[consenting.get(i)];
                Line newBestFit = new Line();
                double r2 = bestFitLine(selected, newBestFit);
                //System.out.format("iter=%d Consensus r2 = %f\n", iter, r2);
                if(r2 < badR2threshold) {
                    // Go out on a limb and save the original line, rather than
                    // the new "best fit" line. This is a HACK against the new
                    // line fitting points with high density too well...
                    lines.add(bestFit);
                } else {
                    lines.add(newBestFit);
                }
                points = removeIndices(points, consenting);
            } else {
                // Otherwise discard the line and try again.
                bestFit = null;
            }

            if(tester != null)
                tester.consentingFitLine.add(bestFit);
        }
        return iter;
    }

    /// Converts the laser readings to cartesian points.
    private Point2D.Double[] laser2cartesian(final double laser[]) {
        /*
        System.out.format("{ %f", laser[0]);
        for(int i = 1; i < numLaser; i++)
            System.out.format(", %f", laser[i]);
        System.out.format(" }\n");
        */

        assert(laser.length == numLaser);
        Point2D.Double[] points = new Point2D.Double[numLaser];
        Vector2D tmp = new Vector2D();
        for(int i = 0; i < numLaser; i++) {
            tmp.setPol(laser[i], i * Math.PI / 180);
            points[i] = new Point2D.Double(tmp.getX(), tmp.getY());
        }
        return points;
    }

    /// Get an initial sample out of a set of points sorted by angle.
    private Point2D.Double[] getInitialSample(Point2D.Double[] points) {
        Random rand = new Random();
        assert(sampleBeamWidth <= points.length);
        int beamBegin = rand.nextInt(points.length - sampleBeamWidth + 1);
        if(tester != null)
            tester.beamBegin.add(beamBegin);
        ArrayList<Integer> indices = new ArrayList<Integer>(sampleBeamWidth);
        for(int i = 0; i < sampleBeamWidth; i++)
            indices.add(i, beamBegin + i);
        Collections.shuffle(indices);
        Point2D.Double[] sample = new Point2D.Double[sampleSize];
        for(int i = 0; i < sampleSize; i++)
            sample[i] = points[indices.get(i)];
        return sample;
    }

    /**
     * Returns copy of array, with specified indices removed.
     */
    private Point2D.Double[] removeIndices(Point2D.Double[] points, ArrayList<Integer> indices) {
        // Create new point list, removing matched points.
        Point2D.Double[] copy = new Point2D.Double[points.length];
        for(int i = 0; i < points.length; i++)
            copy[i] = points[i];
        for(int index : indices)
            copy[index] = null;
        Point2D.Double[] newPoints = new Point2D.Double[points.length - indices.size()];
        int pos = 0;
        for(Point2D.Double p : copy) {
            if(p != null) {
                assert(pos < newPoints.length);
                newPoints[pos] = p;
                pos++;
            }
        }
        return newPoints;
    }

    /**
     * Finds the least-squares best-fit line for a given set of points.
     * According to http://mathworld.wolfram.com/LeastSquaresFitting.html
     * (formulae 16 and on).
     *
     * Uses a trick to handle vertical lines: it just swaps the x and y
     * coordinates to turn it into a horizontal line and avoid division by
     * near-zero.
     */
    double bestFitLine(Point2D.Double[] points, Line line) {
        int n = points.length;

        double ss_xx = 0, ss_yy = 0, ss_xy = 0,
                mean_x = 0, mean_y = 0;
        for(Point2D.Double p : points) {
            ss_xx += p.x * p.x;
            ss_yy += p.y * p.y;
            ss_xy += p.x * p.y;
            mean_x += p.x;
            mean_y += p.y;
        }
        mean_x /= n;
        mean_y /= n;
        ss_xx -= n * mean_x * mean_x;
        ss_yy -= n * mean_y * mean_y;
        ss_xy -= n * mean_x * mean_y;

        double r2 = ss_xy * ss_xy / (ss_xx * ss_yy);
        /*
        System.out.format("n=%d R2=%f\n", n, r2);
        System.out.format("{ (%.4f, %.4f)", points[0].x, points[0].y);
        for(int i = 1; i < points.length; i++)
            System.out.format(", (%.4f, %.4f)", points[i].x, points[i].y);
        System.out.format(" }\n");
        */

        double slope = ss_xy / ss_xx;
        if(slope >= -1 && slope <= 1) {
            // Line closer to horizontal.
            double m = ss_xy / ss_xx;
            double b = mean_y - m * mean_x;
            line.a = m;
            line.b = -1;
            line.c = b;
        } else {
            // Line closer to vertical.
            double rm = ss_xx / ss_xy;
            double rb = mean_x - rm * mean_y;
            line.a = -1;
            line.b = rm;
            line.c = rb;
        }
        return r2;
    }

    /*
     * Returns number of points that are within maxBelongDist of more than one
     * line.
     */
    int countConfounds(final double[] laser, ArrayList<Line> lines) {
        Point2D.Double[] points = laser2cartesian(laser);
        int count = 0;
        for(Point2D.Double p : points) {
            int claimers = 0;
            for(Line l : lines) {
                if(l.distance(p) < maxBelongDist)
                    claimers++;
            }
            if(claimers > 1)
                count++;
        }
        return count;
    }
};