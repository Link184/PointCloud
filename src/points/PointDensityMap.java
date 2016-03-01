package points;

import engine.FileOperations;
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

public class PointDensityMap extends PointsWorker implements Runnable{

    private static List<Double> tmp = new ArrayList<Double>();


    public PointDensityMap(int mesUnit, int tolerance) {
        super(mesUnit, tolerance);
    }


    @Override
    public void run() {
        for (int i = 0; i < zCapacity + 1; i++) {
            List<float[]> sortedList = sortSurfaceYX(i);
            int[][] surface = createSurfaceDens(sortedList);
            projectTo2DDens(surface);
            System.out.print("\rProcessed... " + i + " Density units");
        }

        System.out.println();
        int[][] cleanProjectedMatrix = projectedPCTo2DDensMap.clone();

        synchronized (PointsWorker.class) {
//            PointsFilter.cleanByHigh(projectedPCTo2DDensMap, projectedPCTo2DHighMap);

//
//            //Clear low density points
//            filterProjectedMatrix(tolerance, projectedPCTo2DDensMap);
//            //Increase density of high density regions
//            PointsFilter.computeMatrixDensity(300, projectedPCTo2DDensMap);
//            //Clear points lower than projectedMatrix medium value
//            PointsFilter.clearLowDensityPoints(projectedPCTo2DDensMap);
            //Clear low density regions
//            PointsFilter.destroyMetaPoints(projectedPCTo2DDensMap);


//        PointsFilter.preparesFinalMatrix(projectedPCTo2DDensMap);
//
            FileOperations.exportToFile(projectedPCTo2DDensMap, FileOperations.DENSITY_DENSITY_FILE);
            FileOperations.printImage(projectedPCTo2DDensMap, FileOperations.DENSITY_IMAGE);

//            MatrixDithering.floydSteinbergDithering(cleanProjectedMatrix);
//            FileOperations.printImage(cleanProjectedMatrix, FileOperations.DENSITY_IMAGE_DITHERED);

            HoughTransform transform = new HoughTransform(projectedPCTo2DDensMap.length, projectedPCTo2DDensMap[0].length);
            int count = 0;
            for (int i = 0; i < projectedPCTo2DDensMap.length; i++) {
                for (int j = 0; j < projectedPCTo2DDensMap[i].length; j++) {
                    if (projectedPCTo2DDensMap[i][j] > 30) {
                        transform.addPoint(i, j);
                        count++;
                    }
                }
            }
            System.out.println(count);

            String filename = "hough_image.png";
            File file = new File("source", filename);
            // load the file using Java's imageIO library
            BufferedImage img = new BufferedImage(projectedPCTo2DDensMap.length, projectedPCTo2DDensMap[0].length,
                    BufferedImage.TYPE_INT_RGB);

            // get the lines out
            Vector<HoughLine> lines = transform.getLines(300);

            // draw the lines back onto the image
            for (int j = 0; j < lines.size(); j++) {
                HoughLine line = lines.elementAt(j);
                line.draw(img, Color.RED.getRGB());
            }
            try {
                ImageIO.write(img, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                ImageIO.write(transform.getHoughArrayImage(), "png", new File("hough.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

//            App.start(projectedPCTo2DDensMap);
//            Thread ransac = new Thread(new RansacThread(projectedPCTo2DDensMap));
//            ransac.start();


        }
    }

    @Override
    public int[][] createSurfaceDens(List<float[]> sortedList){
        int[][] surface = new int[yCapacity + 1][xCapacity + 1];
        for (float[] f: sortedList) {
            surface[(int) (yOrigin + (f[1] * mesUnits))][(int) (xOrigin + (f[0] * mesUnits))]++;
        }
        return surface;
    }

    @Override
    public float[][] createSurfaceHigh(List<float[]> sortedList) {
        return new float[0][];
    }

    @Override
    public void findHighestPoints(float[][] surface, int index) {
    }

    @Override
    public void projectTo2DDens(int[][] surface){
        for (int i = 0; i < surface.length - 1; i++) {
            for (int j = 0; j < surface[i].length - 1; j++) {
                projectedPCTo2DDensMap[i][j] += surface[i][j];
            }
        }
    }

    public static List<float[]> getAllPoints() {
        return allPoints;
    }
}
