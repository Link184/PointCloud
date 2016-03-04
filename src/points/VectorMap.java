package points;

import configuration.Configuration;
import engine.FileOperations;
import engine.Pdf;
import hough.HoughLine;
import hough.HoughTransform;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class VectorMap extends PointsWorker implements Runnable{


    public VectorMap(Configuration config) {
        super(config);
    }

    @Override
    public float[][] createSurfaceHigh(List<float[]> sortedList) {
        return new float[0][];
    }

    @Override
    public void projectTo2DDens(int[][] surface) {

    }

    @Override
    public void findHighestPoints(float[][] surface, int index) {

    }

    @Override
    public void run() {
        synchronized (PointsWorker.class) {
            HoughTransform transform = new HoughTransform(projectedPCTo2DDensMap.length, projectedPCTo2DDensMap[0].length);
            for (int i = 0; i < projectedPCTo2DDensMap.length; i++) {
                for (int j = 0; j < projectedPCTo2DDensMap[i].length; j++) {
                    if (projectedPCTo2DDensMap[i][j] > 30) {
                        transform.addPoint(i, j);
                    }
                }
            }

            String filename = "hough_image.png";
            File file = new File("source", filename);
            // load the file using Java's imageIO library
            BufferedImage img = new BufferedImage(projectedPCTo2DDensMap.length, projectedPCTo2DDensMap[0].length,
                    BufferedImage.TYPE_INT_RGB);

            // get the lines out
            Vector<HoughLine> lines = transform.getLines(config.getThreshold());

            // draw the lines back onto the image
            for (int j = 0; j < lines.size(); j++) {
                HoughLine line = lines.elementAt(j);
                line.draw(img, Color.RED.getRGB());
            }

            Pdf.start(lines, projectedPCTo2DDensMap.length, projectedPCTo2DDensMap[0].length);
            try {
                ImageIO.write(img, "png", file);
                // print hough array
                ImageIO.write(transform.getHoughArrayImage(), "png", new File("hough.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOperations.printVectorImage(vectorMatrix, "VectorImage.png");
            FileOperations.exportToSVG(lines, FileOperations.SVG_FILE);

            List<int[]> intersections = findIntersections(lines);

            FileOperations.printImage(buildIntersectionsMap(intersections, vectorMatrix), FileOperations.INTERSECTIONS_MAP);
        }
    }

    public List<int[]> findIntersections(Vector<HoughLine> vectors) {
        List<int[]> intersections = new ArrayList<int[]>();
        for (int i = 0; i < vectors.size() - 1; i++){
            HoughLine line = vectors.elementAt(i);
            for (int j = i + 1; j < vectors.size(); j++) {
                HoughLine line2 = vectors.elementAt(j);
//                double firstY = line.getSlope() * line.getX2() + line.getInterceptor();
//                double secondY = line2.getSlope() * line.getX2() + line2.getInterceptor();
                int intersectionX = (int) ((line2.getInterceptor() - line.getInterceptor()) / (line.getSlope() - line2.getSlope()));
                int intersectionY = (int) (line.getSlope() * intersectionX + line.getInterceptor());
                intersections.add(new int[]{intersectionX, intersectionY});
            }
        }
        return intersections;
    }

    public int[][] buildIntersectionsMap(List<int[]> intersections, int[][] sourceMatrix) {
        int count = 0;
        int[][] intersectionsMap = new int[sourceMatrix.length][sourceMatrix[0].length];
        for (int i = 0; i < sourceMatrix.length; i++) {
            for (int j = 0; j < sourceMatrix[i].length; j++) {
                for (int[] ints: intersections) {
                    if (Math.abs(ints[0]) == i && Math.abs(ints[1]) == j) {
                        intersectionsMap[i][j] = Color.BLUE.getRGB();
                        count++;
                    }
                }
            }
        }

        System.out.println("If count: " + count);
        return intersectionsMap;
    }

}
