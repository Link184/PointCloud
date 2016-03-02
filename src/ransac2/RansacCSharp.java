package ransac2;

import java.util.Random;

public class RansacCSharp {
            final int MAX_LINES = 4;
            final int SAMPLE_SIZE = 10;
            final int RANSAC_CONSENSUS = 50;
            final double RANSAC_TOLERANCE = 7.5;
            final double D2R = Math.PI / 180.0;
            double[] linesA = new double[MAX_LINES];
            double[] linesB = new double[MAX_LINES];
            double[] pointCloud;
            boolean draw = false;
            int lineCount = 0;

    public RansacCSharp() {
    //        Width = 500;
    //        Height = 525;
    //        Text = "RANSAC Demo";
            createNoisyPointCloud();
            extractLines();
            draw = true;
    }

    private void createNoisyPointCloud() {
            Random rnd = new Random();
            for (int i = 0; i < 90; i++) {
                double length = 150 / Math.cos((i - 45) * D2R);
                pointCloud[i] = length + rnd.nextDouble() * 16 - 8;
                pointCloud[i + 90] = length + rnd.nextDouble() * 16 - 8;
                pointCloud[i + 180] = length + rnd.nextDouble() * 16 - 8;
                pointCloud[i + 270] = length + rnd.nextDouble() * 16 - 8;
            }
    }

    private void extractLines() {
            int[] pointStatus = new int[360];
            for (int seed = 0; seed < 360; seed++) {
                int[] sample = new int[SAMPLE_SIZE];
                for (int i = 0; i < SAMPLE_SIZE; i++) sample[i] = (seed + i) % 360;

                int[] fitPoints = new int[360];
                int fitCount = 0;

                double a = 0;
                double b = 0;
                leastSquaresFit(pointCloud, sample, SAMPLE_SIZE, a, b);

                for (int i = 0; i < 360; i++) {
                    if (pointStatus[i] == 0) {
                        // Convert scan vectors to cartesian coordinates.
                        double x = Math.cos(i * D2R) * pointCloud[i];
                        double y = Math.sin(i * D2R) * pointCloud[i];

                        // Claim points close to sample line.
                        if (distanceToLine(x, y, a, b) < RANSAC_TOLERANCE)
                        fitPoints[fitCount++] = i;
                    }
                }

                if (fitCount > RANSAC_CONSENSUS) {
                    // Refresh line and add to collection.
                    leastSquaresFit(pointCloud, fitPoints, fitCount, a, b);
                    linesA[lineCount] = a;
                    linesB[lineCount] = b;
                    lineCount++;

                    // Update point cloud status.
                    for (int i = 0; i < fitCount; i++) pointStatus[fitPoints[i]] = lineCount;
                }
            }
    }

    private void leastSquaresFit(double[] pointCloud, int[] selection, int count, double a, double b) {
            double sumX = 0;
            double sumY = 0;
            double sumXX = 0;
            double sumYY = 0;
            double sumXY = 0;

            for (int i = 0; i < count; i++) {
                double x = Math.cos(selection[i] * D2R) * pointCloud[selection[i]];
                double y = Math.sin(selection[i] * D2R) * pointCloud[selection[i]];
                sumX += x;
                sumXX += Math.pow(x, 2);
                sumY += y;
                sumYY += Math.pow(y, 2);
                sumXY += x * y;
            }

            a = (count * sumXY - sumX * sumY) / (count * sumXX - Math.pow(sumX, 2));
            b = (sumY * sumXX - sumX * sumXY) / (count * sumXX - Math.pow(sumX, 2));
    }

    private double distanceToLine(double x, double y, double a, double b)
            {
            double ao = -1.0 / a;
            double bo = y - ao * x;
            double px = (b - bo) / (ao - a);
            double py = ((ao * (b - bo)) / (ao - a)) + bo;

            return Math.sqrt(Math.pow(x - px, 2) + Math.pow(y - py, 2));
            }

    //protected void OnPaint(PaintEventArgs e) {
    //        if (draw) {
    //            draw = false;
    //            Graphics g = e.Graphics;
    //
    //            Pen pen = new Pen(Color.Gray, 4);
    //            for (int i = 0; i < lineCount; i++) {
    //                int x1 = 0;
    //                int y1 = 250 + (int)(linesA[i] * -250 + linesB[i]);
    //                int x2 = 500;
    //                int y2 = 250 + (int)(linesA[i] * 250 + linesB[i]);
    //                g.DrawLine(pen, x1, y1, x2, y2);
    //            }
    //
    //            pen = new Pen(Color.Red, 4);
    //            for (int i = 0; i < 360; i++) {
    //                int x = 250 + (int)(Math.Cos(i * D2R) * pointCloud[i]);
    //                int y = 250 + (int)(Math.Sin(i * D2R) * pointCloud[i]);
    //                g.DrawEllipse(pen, x, y, 1, 1);
    //            }
    //        }
    //}
}
