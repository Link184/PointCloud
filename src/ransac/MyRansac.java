package ransac;

import java.util.ArrayList;
import java.util.List;

public class MyRansac {
    public double curr_a, curr_b, curr_x_mue, curr_y_mue;
    public List<Integer> x = new ArrayList<Integer>();
    public List<Integer> y = new ArrayList<Integer>();
    public char[] ransac;
    public double ransacError;

    public MyRansac(List<Integer> x_, List<Integer> y_) {
        this.x = x_;
        this.y = y_;
        this.ransac = new char[x.size()];
    }

    // removes the worst outlier
    public void removeOutlier() {
        // point 1 (x1, y1)
        double x1 = 0, y1 = curr_b;

        // point 2 (x2, y2)
        double x2 = 1, y2 = curr_a + curr_b;

        int max_i = 0;
        double max_dist = Math.abs(((x2 - x1) * (y1 - y.get(0)) - (x1 - x.get(0)) * (y2 - y1)) / Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
        //System.out.println("x="+x[0]+" y="+y[0]+" dist="+max_dist);

        for (int i = 1; i < x.size(); i++) {
            double dist = Math.abs(((x2 - x1) * (y1 - y.get(i)) - (x1 - x.get(0)) * (y2 - y1)) / Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
            //System.out.println("x="+x[i]+" y="+y[i]+" dist="+dist);
            if (dist > max_dist) {
                max_i = i;
                max_dist = dist;
            }
        }

        List<Integer> x_new = new ArrayList<Integer>(x.size() - 1);
        List<Integer> y_new = new ArrayList<Integer>(y.size() - 1);

        int index = 0;
        for (int i = 0; i < x.size(); i++) {
            if (i == max_i)
                continue;

            x_new.add(index, x.get(i));
            y_new.add(index, y.get(i));
            index++;
        }

        x = x_new;
        y = y_new;
    }

    // check fitting model
    public boolean checkForOutlier(double max_dist) {
        // point 1 (x1, y1)
        double x1 = 0, y1 = curr_b;

        // point 2 (x2, y2)
        double x2 = 1, y2 = curr_a + curr_b;

        for (int i = 0; i < x.size(); i++) {
            double dist = Math.abs(((x2 - x1) * (y1 - y.get(i)) - (x1 - x.get(i)) * (y2 - y1)) / Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
            if (dist > max_dist)
                return true;
        }

        return false;
    }

    // calculates the fitting line
    public void calculateFittingLine() {
        // x_mue
        double x_mue = 0;
        for (int i = 0; i < x.size(); i++)
            x_mue += x.get(i);
        x_mue /= x.size();

        // y_mue
        double y_mue = 0;
        for (int i = 0; i < x.size(); i++)
            y_mue += y.get(i);
        y_mue /= x.size();

        // a
        double os = 0;
        for (int i = 0; i < x.size(); i++)
            os += (y.get(i) - y_mue) * (x.get(i) - x_mue);

        double us = 0;
        for (int i = 0; i < x.size(); i++)
            us += (x.get(i) - x_mue) * (x.get(i) - x_mue);

        double a = os / us;

        // b
        double b = y_mue - a * x_mue;

        // save results
        curr_a = a;
        curr_b = b;
        curr_x_mue = x_mue;
        curr_y_mue = y_mue;
    }

    // count outlier
    public int countOutlier(double max_dist) {
        // point 1 (x1, y1)
        double x1 = 0, y1 = curr_b;

        // point 2 (x2, y2)
        double x2 = 1, y2 = curr_a + curr_b;

        int count = 0;
        ransacError = 0;
        for (int i = 0; i < x.size(); i++) {
            double dist = Math.abs(((x2 - x1) * (y1 - y.get(i)) - (x1 - x.get(i)) * (y2 - y1))) / Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
            System.out.println("x=" + x.get(i) + " y=" + y.get(i) + " dist=" + dist);
            if (dist > max_dist) {
                count++;
                if (ransac[i] != 'M')
                    ransac[i] = 'O';
            } else {
                if (ransac[i] != 'M')
                    ransac[i] = 'C';
                ransacError += dist;
            }
        }

        return count;
    }

    // fitting line for RANSAC
    public void calculateFittingLineRANSAC() {
        // x_mue
        double x_mue = 0;
        for (int i = 0; i < x.size(); i++) {
            if (ransac[i] == 'M')
                x_mue += x.get(i);
        }
        x_mue /= x.size() - 2;

        // y_mue
        double y_mue = 0;
        for (int i = 0; i < x.size(); i++) {
            if (ransac[i] == 'M')
                y_mue += y.get(i);
        }
        y_mue /= x.size() - 2;

        // a
        double os = 0;
        for (int i = 0; i < x.size(); i++)
            if (ransac[i] == 'M')
                os += (y.get(i) - y_mue) * (x.get(i) - x_mue);

        double us = 0;
        for (int i = 0; i < x.size(); i++)
            if (ransac[i] == 'M')
                us += (x.get(i) - x_mue) * (x.get(i) - x_mue);

        double a = os / us;

        // b
        double b = y_mue - a * x_mue;

        // save results
        curr_a = a;
        curr_b = b;
        curr_x_mue = x_mue;
        curr_y_mue = y_mue;
    }

    public static void start(int[][] densityMatrix) {
        boolean LEAST = true;
        boolean RANSAC = true;

        // data
        List<Integer> x = new ArrayList<Integer>();
        List<Integer> y = new ArrayList<Integer>();

        for (int i = 0; i < densityMatrix.length; i++) {
            for (int j = 0; j < densityMatrix[i].length; j++) {
                if (densityMatrix[i][j] > 0) {
                    x.add(i);
                    y.add(j);
                }
            }
        }

        // set tolerance
        double tolerance = 20;

        boolean ready = false;
        int iteration = 1;

        // ***********************************************************************
        // Least Squares
        if (LEAST) {
            MyRansac ls = new MyRansac(x, y);

            System.out.println("*** STARTING LEAST SQUARES ***\n");
            while (!ready) {
                ls.calculateFittingLine();
                System.out.println("   modell " + iteration + " a=" + ls.curr_a + " b=" + ls.curr_b);

                if (!ls.checkForOutlier(tolerance)) {
                    System.out.println("   ready\n");
                    ready = true;
                } else {
                    System.out.println("   remove one point\n");
                    ls.removeOutlier();
                }
                iteration++;
            }

            System.out.print("   Given points:\n   ");
            for (int j = 0; j < ls.x.size(); j++)
                System.out.print("P" + j + "(" + ls.x.get(j) + ", " + ls.y.get(j) + ") ");
            System.out.println("\n\n*** FINISHED LEAST SQUARES ***\n");
        }
        // ***********************************************************************

        if (!RANSAC)
            System.exit(0);

        // ***********************************************************************
        // RANSAC
        System.out.println("*** STARTING RANSAC ***\n");
        ready = false;
        MyRansac ransac = new MyRansac(x, y);

        // set parameters
        int outliersTolerance = 2;

        // save best result
        double minError = Double.MAX_VALUE;
        int minIndex1;
        int minIndex2;

        int countMinReached = 0;
        iteration = 1;

        while (!ready) {
            // initialize
            for (int j = 0; j < x.size(); j++)
                ransac.ransac[j] = 'P';

            // calculate consensus set

            // 1. choose 2 random points
            int zuffi1 = (int) (x.size() * Math.random());
            int zuffi2;
            while (true) {
                zuffi2 = (int) (x.size() * Math.random());
                if (zuffi1 != zuffi2)
                    break;
            }

            // set model
            ransac.ransac[zuffi1] = 'M';
            ransac.ransac[zuffi2] = 'M';

            // 2. fitting the model with these two points
            ransac.calculateFittingLineRANSAC();

            System.out.print("   Current model:\n      ");
            for (int j = 0; j < ransac.x.size(); j++)
                if (ransac.ransac[j] == 'M')
                    System.out.print("P" + j + "(" + ransac.x.get(j) + ", " + ransac.y.get(j) + ") ");
            System.out.println("\n      modell " + iteration + " a=" + ransac.curr_a + " b=" + ransac.curr_b);

            // 3. calculate the outlier and make a decision
            int cOutlier = ransac.countOutlier(tolerance);
            System.out.println("\n      outlier: " + cOutlier);

            if (cOutlier <= outliersTolerance) {
                System.out.println("      good model");
                System.out.println("      Error: " + ransac.ransacError + "\n");

                // better result than before?
                if (ransac.ransacError < minError) {
                    System.out.println("         ############################################### ");
                    minIndex1 = zuffi1;
                    minIndex2 = zuffi2;
                    minError = ransac.ransacError;
                    countMinReached = 0;

                    System.out.print("         Given points to current model:\n         ");
                    for (int j = 0; j < ransac.x.size(); j++)
                        if ((ransac.ransac[j] == 'C') || (ransac.ransac[j] == 'M'))
                            System.out.print("P" + j + "(" + ransac.x.get(j) + ", " + ransac.y.get(j) + ") ");

                    System.out.print("\n\n         Outlier\n         ");
                    for (int j = 0; j < ransac.x.size(); j++)
                        if (ransac.ransac[j] == 'O')
                            System.out.print("P" + j + "(" + ransac.x.get(j) + ", " + ransac.y.get(j) + ") ");
                    System.out.print("\n");
                    System.out.println("         ############################################### \n");
                } else
                    countMinReached++;

                // no better results for some times
                if (countMinReached >= 4)
                    ready = true;
            } else
                System.out.println("      bad model\n");
            iteration++;
        }
        System.out.println("\n\n*** FINISHED RANSAC ***\n");
        // ***********************************************************************
    }
}