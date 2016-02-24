package ransac;

import points.PointDensityMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class TestRansac extends JPanel {
    final int numLaser = 181;
    double[] laser     = null;
    Ransac ransac      = null;
    Line[] result      = null;
    int displayedIter  = 0;
    final double scale = 50;

    // Stuff filled-in by Ransac.
    protected ArrayList<Point2D.Double[]> points;
    protected ArrayList<Integer> beamBegin;
    protected ArrayList<Point2D.Double[]> sample;
    protected ArrayList<Line> sampleFitLine;
    protected ArrayList<ArrayList<Integer>> consenting;
    protected ArrayList<Line> consentingFitLine;
    protected int totalIter;

    private class MyKeyListener extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if(c == 'n' || c == 'N') {
                if(displayedIter + 1 < totalIter)
                    displayedIter++;
                else
                    System.out.println("This is the last iteration.");
            } else if(c == 'p' || c == 'P') {
                if(displayedIter > 0)
                    displayedIter--;
                else
                    System.out.println("This is the first iteration.");
            }
            repaint();
        }
    }

    public TestRansac() {
        super();
        setPreferredSize(new Dimension(800,600));

        // In hallway, directly along it.
        double[] hereLaser = {
            0.705001, 0.705084, 0.705382, 0.705895, 0.706624, 0.707571,
            0.708737, 0.710123, 0.711732, 0.713566, 0.715629, 0.717923,
            0.720452, 0.723220, 0.726232, 0.729493, 0.733008, 0.736783,
            0.740824, 0.745138, 0.749733, 0.754617, 0.759799, 0.765287,
            0.771092, 0.777225, 0.783698, 0.790523, 0.797713, 0.805284,
            0.813251, 0.821631, 0.830441, 0.839702, 0.849434, 0.859660,
            0.870405, 0.881694, 0.893557, 0.906025, 0.919130, 0.932910,
            0.947403, 0.962652, 0.978705, 0.995612, 1.013428, 1.032216,
            1.052041, 1.072977, 1.079572, 1.102650, 1.127089, 1.235946,
            1.265420, 1.296741, 1.330069, 1.365585, 1.403491, 1.444016,
            1.487420, 1.533998, 1.584089, 1.638080, 1.696419, 1.759625,
            1.828306, 1.825693, 1.851840, 1.963579, 2.057409, 2.161351,
            2.244790, 2.543253, 2.697631, 2.872899, 3.009756, 3.127908,
            3.384229, 3.687541, 4.051937, 4.497777, 5.055602, 5.773401,
            7.113069, 7.113069, 7.113069, 7.113069, 7.113069, 7.113069,
            7.113069, 7.113069, 7.113069, 7.113069, 7.113069, 7.113069,
            7.149321, 5.773401, 5.055602, 4.497777, 4.051937, 3.687541,
            3.384229, 3.127908, 3.009756, 3.023427, 3.038156, 4.753521,
            2.244790, 2.161351, 2.057409, 1.963579, 1.851840, 1.825693,
            1.834484, 1.824343, 1.814866, 1.806036, 1.797839, 1.790261,
            1.783289, 1.776913, 1.771121, 1.765906, 1.761257, 1.757169,
            1.753635, 1.750649, 1.127089, 1.102650, 1.079572, 1.072977,
            1.052041, 1.032216, 1.013428, 0.995612, 0.978705, 0.962652,
            0.947403, 0.932910, 0.919130, 0.906025, 0.893557, 0.881694,
            0.870405, 0.859660, 0.849434, 0.839702, 0.830441, 0.821631,
            0.813251, 0.805284, 0.797713, 0.790523, 0.783698, 0.777225,
            0.771092, 0.765287, 0.759799, 0.754617, 0.749733, 0.745138,
            0.740824, 0.736783, 0.733008, 0.729493, 0.726232, 0.723220,
            0.720452, 0.717923, 0.715629, 0.713566, 0.711732, 0.710123,
            0.708737, 0.707571, 0.706624, 0.705895, 0.705382, 0.705084,
            0.705001
        };

        /*
        // In hallway, at an angle.
        double[] hereLaser = {
            0.576925, 0.573079, 0.569456, 0.566050, 0.562855, 0.559865,
            0.557075, 0.554481, 0.552078, 0.549863, 0.547832, 0.545982,
            0.544309, 0.542812, 0.541487, 0.540333, 0.539348, 0.538531,
            0.537879, 0.537393, 0.537072, 0.536915, 0.536921, 0.537091,
            0.537426, 0.537925, 0.538589, 0.539420, 0.540419, 0.541587,
            0.542926, 0.544439, 0.546127, 0.547993, 0.550041, 0.552274,
            0.554694, 0.557308, 0.560117, 0.563128, 0.566346, 0.569776,
            0.573424, 0.577296, 0.581400, 0.585743, 0.590333, 0.595179,
            0.600291, 0.605677, 0.611350, 0.617321, 0.623602, 0.630206,
            0.637149, 0.644447, 0.652114, 0.660171, 0.668637, 0.677532,
            0.686881, 0.696707, 0.707037, 0.717902, 0.729332, 0.741363,
            0.754031, 0.767379, 0.781452, 0.796299, 0.811974, 0.828537,
            0.846054, 0.864598, 0.884250, 0.905099, 0.927247, 0.950803,
            0.975894, 1.002660, 1.031260, 1.061874, 1.094706, 1.129991,
            1.167995, 1.209027, 1.253444, 1.301663, 1.354171, 1.411544,
            1.474463, 1.543748, 1.620385, 1.705578, 1.800807, 1.907916,
            2.029232, 2.167731, 2.327284, 2.513018, 2.731877, 2.993493,
            3.558366, 3.885082, 4.210296, 5.125303, 5.787992, 7.126720,
            7.126720, 7.126720, 7.126720, 7.126720, 7.126720, 7.126720,
            7.126720, 7.126720, 7.126720, 7.126720, 7.483691, 6.515046,
            5.769989, 5.168074, 5.184367, 4.302453, 3.940675, 3.852147,
            3.595122, 3.371221, 3.035427, 2.868890, 2.720468, 2.587403,
            2.467469, 2.358852, 2.260058, 2.169846, 2.087173, 2.011163,
            1.941069, 1.876251, 1.816161, 1.709918, 1.659407, 1.656869,
            1.614438, 1.571959, 1.532114, 1.494686, 1.459478, 1.426318,
            1.395048, 1.365528, 1.337632, 1.311247, 1.286268, 1.262601,
            1.240162, 1.218873, 1.198662, 1.179465, 1.161223, 1.143881,
            1.127388, 1.111699, 1.096771, 1.082565, 1.069044, 1.056175,
            1.043928, 1.032273, 1.021183, 1.010634, 1.000603, 0.991069,
            0.982012, 0.973412, 0.965254, 0.957521, 0.950199, 0.943273,
            0.936731
        };
        */


        /*
        // Coming out of a room.
        double[] hereLaser = {
            1.893646, 1.897673, 1.902298, 1.907528, 1.831982, 1.673673,
            1.540976, 1.428174, 1.331136, 1.246798, 1.172844, 1.107489,
            1.049334, 0.997269, 0.950401, 0.908003, 0.869479, 0.834334,
            0.802154, 0.772590, 0.745346, 0.720170, 0.696843, 0.675179,
            0.655015, 0.636208, 0.618634, 0.602183, 0.586758, 0.572274,
            0.558653, 0.545828, 0.533738, 0.522327, 0.511546, 0.501351,
            0.491702, 0.468024, 0.459619, 0.451647, 0.444079, 0.436892,
            0.430063, 0.423572, 0.419032, 0.428166, 0.437843, 0.448109,
            0.459011, 0.470605, 0.482951, 0.496118, 0.510185, 0.525237,
            0.541376, 2.130317, 2.110596, 2.091871, 2.074102, 2.057256,
            2.041301, 2.026205, 2.011941, 1.998483, 1.985806, 1.973889,
            1.962710, 1.952251, 1.942493, 1.933420, 1.925017, 1.917270,
            1.910168, 1.893528, 1.887710, 1.882503, 1.928325, 1.924204,
            1.920688, 1.917769, 1.915445, 1.913710, 1.912563, 1.912002,
            1.912026, 1.912634, 1.913829, 1.915611, 1.917984, 1.920950,
            1.924516, 1.928685, 1.933466, 1.938864, 1.944889, 1.951551,
            1.958859, 1.966826, 1.975465, 1.949405, 1.942648, 1.953111,
            1.974841, 1.986822, 1.999564, 2.013090, 2.027424, 2.042593,
            2.058625, 2.075549, 2.082217, 2.100928, 2.177579, 2.198874,
            2.221275, 2.244830, 2.269591, 2.295614, 2.322958, 2.351688,
            2.381875, 2.413594, 2.446926, 2.481960, 2.518793, 2.557527,
            2.598278, 2.572097, 2.616078, 2.676716, 2.725830, 2.777644,
            0.697304, 0.686836, 0.676883, 0.667415, 0.658406, 0.649834,
            0.641676, 0.633911, 0.626521, 0.619489, 0.618694, 0.621721,
            0.617249, 0.613027, 0.609047, 0.605302, 0.601785, 0.598491,
            0.595414, 0.592548, 0.589889, 0.587432, 0.585173, 0.583109,
            0.581237, 0.579552, 0.578053, 0.576738, 0.575604, 0.574649,
            0.573872, 0.573272, 0.572847, 0.572598, 0.572523, 0.572623,
            0.572898, 0.573348, 0.573974, 0.574776, 0.575756, 0.576916,
            0.578257, 0.579782, 0.581492, 0.583390, 0.585479, 0.587764,
            0.590246
        };
        */


        // Looking inside a room (used in final presentation).
//        double[] hereLaser = {
//                0.494890, 0.497769, 0.500836, 0.504095, 0.507553, 0.511215,
//                0.515089, 0.519182, 0.579077, 0.584115, 0.589422, 0.595010,
//                0.600890, 0.607075, 0.613577, 0.620412, 0.627594, 0.635141,
//                0.643070, 0.651401, 0.660156, 0.669356, 0.679026, 0.689194,
//                0.699887, 0.711139, 0.722983, 0.735457, 0.748601, 0.762462,
//                0.777087, 0.792532, 0.808855, 0.826123, 0.844408, 0.863791,
//                0.884362, 0.906220, 0.929479, 0.954262, 0.980711, 1.008987,
//                1.039268, 1.071762, 1.106704, 1.127846, 1.118026, 1.111163,
//                1.154457, 1.227159, 1.279864, 1.337729, 1.401524, 1.464559,
//                1.518599, 1.775216, 1.882713, 2.004723, 2.144347, 2.305636,
//                2.305044, 2.508173, 2.755118, 3.057043, 3.434472, 3.919600,
//                4.565958, 5.469604, 6.821808, 6.821808, 6.821808, 6.821808,
//                6.821808, 6.821808, 6.821808, 6.821808, 6.821808, 6.821808,
//                6.821808, 7.741536, 6.691903, 6.524196, 5.358528, 4.875009,
//                4.472780, 4.133034, 3.842348, 3.590890, 3.371290, 3.177917,
//                3.006394, 2.853266, 2.715771, 2.591675, 2.452464, 2.449047,
//                2.467751, 2.487509, 4.316584, 4.354415, 4.394270, 4.436231,
//                4.480383, 4.526822, 4.575646, 1.686059, 1.660012, 1.618355,
//                1.579209, 1.542373, 1.507668, 1.474931, 1.444018, 1.399569,
//                1.372223, 1.374145, 1.397177, 1.421436, 1.447001, 1.701472,
//                1.734316, 1.769003, 1.805668, 1.844461, 1.885545, 1.929103,
//                1.975338, 2.024477, 2.076772, 2.132509, 2.192007, 2.255627,
//                2.323781, 2.396934, 2.475623, 2.560459, 2.652153, 2.751528,
//                2.859544, 2.977330, 3.106223, 3.247813, 3.404013, 3.577139,
//                3.770025, 3.986179, 4.229990, 4.507025, 4.824457, 0.942829,
//                0.939214, 0.935911, 0.932915, 0.932241, 0.937921, 0.935797,
//                0.933966, 0.932427, 0.931176, 0.930212, 0.929534, 0.929139,
//                0.929029, 0.929201, 0.929657, 0.930398, 0.931424, 0.932736,
//                0.934338, 0.936231, 0.938419, 0.940904, 0.943690, 0.946782,
//                0.950186, 0.953905, 0.957946, 0.962316, 0.967022, 0.972072,
//                0.977474,
//        };

        double[] data = PointDensityMap.lines;

        laser = hereLaser;

        points            = new ArrayList<Point2D.Double[]>();
        beamBegin         = new ArrayList<Integer>();
        sample            = new ArrayList<Point2D.Double[]>();
        sampleFitLine     = new ArrayList<Line>();
        consenting        = new ArrayList<ArrayList<Integer>>();
        consentingFitLine = new ArrayList<Line>();

        // Run RANSAC, saving intermediary steps for visualization.
        ransac = new Ransac(this);
        result = ransac.findLines(laser);

        assert(points.size()            == totalIter);
        assert(beamBegin.size()         == totalIter);
        assert(sample.size()            == totalIter);
        assert(sampleFitLine.size()     == totalIter);
        assert(consenting.size()        == totalIter);
        assert(consentingFitLine.size() == totalIter);

        addKeyListener(new MyKeyListener());
    }

    @Override
    protected void paintComponent(Graphics g) {
        int height = getHeight(), width = getWidth();
        int xcenter = width / 2, ycenter = 10;

        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);

        /*
        // Draw test lines.
        g.setColor(Color.magenta);
        Line testVert  = new Line(), // line x = 3
             testHoriz = new Line(); // line y = 8/2
        testVert.a = 1;
        testVert.b = 0;
        testVert.c = -3;
        testHoriz.a = 0;
        testHoriz.b = 2;
        testHoriz.c = -8;
        if(displayedIter % 2 == 0)
            myDrawLine(g, testVert);
        else
            myDrawLine(g, testHoriz);
        */

        // Draw laser beams.
        g.setColor(Color.green);
        for(int i = 0; i < numLaser; i++) {
            Vector2D v = new Vector2D();
            v.setPol(laser[i], i * Math.PI / 180);
            double x = v.getX() * scale,
                    y = v.getY() * scale;
            x += xcenter;
            y += ycenter;
            g.drawLine(xcenter, ycenter, (int)x, (int)y);
        }

        // Draw iteration number.
        g.setColor(Color.black);
        assert(displayedIter >= 0 && displayedIter < totalIter);
        g.drawString("Iteration: " + (displayedIter + 1) + " of " + totalIter
                        + " (n and p to change)",
                0, height / 2 - 20);

        // Draw points being considered in current iteration.
        g.setColor(Color.blue);
        for(Point2D.Double p : points.get(displayedIter)) {
            double x = p.x * scale + xcenter,
                    y = p.y * scale + ycenter;
            g.drawOval((int)x - 2, (int)y - 2, 4, 4);
        }

        // Draw beginning of random beam.
        /*
        g.setColor(Color.cyan);
        {
            Point2D.Double[] iterPoints = points.get(displayedIter);
            int index = beamBegin.get(displayedIter);
            Point2D.Double p = iterPoints[index];
            double x = p.x * scale + xcenter,
                   y = p.y * scale + ycenter;
            g.drawOval((int)x - 3, (int)y - 3, 6, 6);
        }
        */

        // Draw sample.
        g.setColor(Color.cyan);
        for(Point2D.Double p : sample.get(displayedIter)) {
            double x = p.x * scale + xcenter,
                    y = p.y * scale + ycenter;
            g.fillOval((int)x - 2, (int)y - 2, 4, 4);
        }

        // Draw best-fit line through sample.
        g.setColor(Color.red);
        myDrawLine(g, sampleFitLine.get(displayedIter));

        // Draw consenting points.
        g.setColor(Color.magenta);
        for(int index : consenting.get(displayedIter)) {
            Point2D.Double p = points.get(displayedIter)[index];
            double x = p.x * scale + xcenter,
                    y = p.y * scale + ycenter;
            g.drawOval((int)x - 2, (int)y - 2, 4, 4);
        }

        // Show square error of best-fit line through sample.
        g.setColor(Color.black);
        double errSample = calcSquareDist(sampleFitLine.get(displayedIter),
                points.get(displayedIter),
                consenting.get(displayedIter));
        errSample /= sample.get(displayedIter).length;
        g.drawString("Sample error:       " + errSample, 0, height / 2);

        // Draw best-fit line through consenting points.
        g.setColor(Color.black);
        if(consentingFitLine.get(displayedIter) != null)
            myDrawLine(g, consentingFitLine.get(displayedIter));

        // Show square error of best-fit line through sample.
        g.setColor(Color.black);
        if(consentingFitLine.get(displayedIter) != null) {
            double errCons = calcSquareDist(consentingFitLine.get(displayedIter),
                    points.get(displayedIter),
                    consenting.get(displayedIter));
            errCons /= consenting.get(displayedIter).size();
            g.drawString("Consensus error: " + errCons, 0, height / 2 + 20);
        } else {
            g.drawString("No consensus", 0, height / 2 + 20);
        }
    }

    private static double calcSquareDist(Line line, Point2D.Double[] points,
                                         ArrayList<Integer> indices) {
        double sum = 0;
        for(int index : indices) {
            double dist = line.distance(points[index]);
            sum += dist * dist;
        }
        return sum;
    }

    /// Draws the given line on the given graphics object.
    private void myDrawLine(Graphics g, Line l) {
        int height = getHeight(), width = getWidth();
        int xcenter = width / 2, ycenter = 10;

        double x1, y1, x2, y2;
        double slope = -l.a / l.b;
        if(slope >= -1 && slope <= 1) {
            // Line is closer to horizontal.
            double m = -l.a / l.b,
                    b = -l.c / l.b;
            x1 = -xcenter / scale;
            y1 = m * x1 + b;
            x2 = xcenter / scale;
            y2 = m * x2 + b;
        } else {
            // Line is closer to vertical.
            double rm = -l.b / l.a,
                    rb = -l.c / l.a;
            y1 = -height / scale;
            x1 = rm * y1 + rb;
            y2 = height / scale;
            x2 = rm * y2 + rb;
        }
        // In the canvas coordinate system:
        double x1s = x1 * scale + xcenter,
                y1s = y1 * scale + ycenter,
                x2s = x2 * scale + xcenter,
                y2s = y2 * scale + ycenter;
        g.drawLine((int)x1s, (int)y1s, (int)x2s, (int)y2s);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame();
        TestRansac panel = new TestRansac();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        panel.requestFocus(); // grab the keyboard

        // Quit if the window is closed.
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
    }

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        // from: http://download.oracle.com/javase/tutorial/uiswing/examples/components/index.html#FrameDemo
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

    }
};
