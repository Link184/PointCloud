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
            Vector<HoughLine> lines = transform.getLines(200);

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
            FileOperations.printVectorImage(getIntersection(vectorMatrix), "VectorPointsImage.png");
        }
    }

    public int[][] getIntersection(int[][] vectorMatrix) {
        for (int i = 1; i < vectorMatrix.length - 1; i++) {
            for (int j = 1; j < vectorMatrix[i].length - 1; j++) {
                int topPoint = vectorMatrix[i-1][j];
                int botPoint = vectorMatrix[i+1][j];
                int leftPoint = vectorMatrix[i][j-1];
                int rightPoint = vectorMatrix[i][j+1];
                int medRegionValue = (topPoint + botPoint + leftPoint + rightPoint) / 4;
                if (medRegionValue != vectorMatrix[i][j]) {
                    vectorMatrix[i][j] = 0;
                }
            }
        }
        return vectorMatrix;
    }
}
