package ransac3.test;

import ransac3.AnotherRansac;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class App extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = -3606005788347551394L;
    List<Double> lineParameters;
    LineParamEstimator lpEstimator = new LineParamEstimator(0.03);
    List<Point2D> pointData = new ArrayList<Point2D>();
    int numForEstimate = 2;
    int numSamples = 500;
    int numOutliers = 90800;
    double desiredProbabilityForNoOutliers = 0.999;
    double maximalOutlierPercentage = 1.0 - (double) numSamples * 0.9
            / (double) (numSamples + numOutliers);
    double noiseSpreadRadius = 0.02;
    double outlierSpreadRadius = 0.5;
    double newX, newY, dx, dy;
    int winSize = 600;
    boolean[] best;

    public App() {
        super("Test");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new Dimension(winSize + 40, winSize + 60));
        setResizable(false);
    }

    public static void start(int[][] matrix) {
        // get random direction
        App a = new App();
        // add 'numSamples' points
        a.pointData.clear();
        Random rand = new Random(new Date().getTime());
        double angle = rand.nextDouble() * Math.PI / 2.0;
        a.dx = Math.cos(angle);
        a.dy = Math.sin(angle);
        for (int i = 0; i < a.numSamples; i++) {
            a.newX = i * a.dx / a.numSamples + a.noiseSpreadRadius
                    * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1);
            a.newY = i * a.dy / a.numSamples + a.noiseSpreadRadius
                    * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1);
            a.pointData.add(new Point2D(a.newX, a.newY));
            // drawPlot(g2,newX,newY,4,0x336699,true);
        }

//        for (int i = 0; i < matrix.length; i++) {
//            for (int j = 0; j < matrix[i].length; j++) {
//                if (matrix[i][j] > 0) {
//                    a.pointData.add(new Point2D(i, j));
//                }
//            }
//        }
//        System.out.println(a.pointData.size());

        // 'numOutliers' points
        double centerX = 0.5 * a.dx;
        double centerY = 0.5 * a.dy;
        for (int i = 0; i < a.numOutliers; i++) {
            double an = rand.nextDouble();
            double r = rand.nextDouble();
            a.newX = centerX + a.outlierSpreadRadius * r
                    * Math.cos(2.0 * an * Math.PI);
            a.newY = centerY + a.outlierSpreadRadius * r
                    * Math.sin(2.0 * an * Math.PI);
            a.pointData.add(new Point2D(a.newX, a.newY));
        }
        AnotherRansac<Point2D, Double> ransac = new AnotherRansac<Point2D, Double>(
                a.lpEstimator, a.numForEstimate, a.maximalOutlierPercentage);
        ransac.compute(a.pointData, a.desiredProbabilityForNoOutliers);
        a.best = ransac.getBestVotes();
        a.lineParameters = ransac.getParameters();
        a.setVisible(true);
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0xffffff));
        g2.fillRect(0, 0, winSize + 40, winSize + 60);
        g2.translate(20, 40);
        g2.setColor(new Color(0xcccccc));
        for (int i = 0; i < 11; i++) {
            g2.drawLine(-20, i * winSize / 10, winSize + 20, i * winSize / 10);
            g2.drawLine(i * winSize / 10, -20, i * winSize / 10, winSize + 20);
        }

        drawLine(g2, -dy, dx, 0.0, 0.0, 3, 0x9999ff);
        for (int i = 0; i < best.length; i++) {
            drawPlot(g2, pointData.get(i).getX(), pointData.get(i).getY(), 2,
                    0x336699, best[i]);
        }
        // System.out.println(p);
        // g2.drawString("" + p, 20, 550);
        drawLine(g2, lineParameters.get(0), lineParameters.get(1),
                lineParameters.get(2), lineParameters.get(3), 3, 0xff0000);
        g2.setColor(new Color(0x000000));
        g2.drawString("line params: " + lineParameters.get(0) + ", "
                + lineParameters.get(1) + ", " + lineParameters.get(2) + ", "
                + lineParameters.get(3), 0, 600);
    }

    private void drawPlot(Graphics2D g, double x, double y, int size, int rgb,
                          boolean isFill) {
        int pX = (int) Math.round(x * winSize);
        int pY = (int) Math.round(y * winSize);
        Color color = g.getColor();
        g.setColor(new Color(rgb));
        if (isFill) {
            g.fillRect(pX - size, pY - size, size * 2, size * 2);
        } else {
            g.drawOval(pX - size, pY - size, size * 2, size * 2);
        }
        g.setColor(color);
    }

    private void drawLine(Graphics2D g, double nX, double nY, double aX,
                          double aY, int size, int rgb) {
        int y1 = (int) Math.round((nX * (aX + 1.0) / nY + aY) * winSize);
        int y2 = (int) Math.round((nX * (aX - 1.0) / nY + aY) * winSize);
        Color color = g.getColor();
        Stroke stroke = g.getStroke();
        g.setColor(new Color(rgb));
        g.setStroke(new BasicStroke(size));
        g.drawLine(-winSize - 20, y1, winSize + 20, y2);
        g.setColor(color);
        g.setStroke(stroke);
    }

}