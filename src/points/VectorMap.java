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
import java.util.*;
import java.util.List;

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

            Map<Integer, List<HoughLine>> dividedVectorsMap = divedeVectorsByIntersetionMap(lines, intersections);

            FileOperations.exportToSVG(sortVectorsMap(dividedVectorsMap), "divided_vectors.svg");
//            for (int[] i: intersections) {
//                System.out.println(i[0] + " " + i[1]);
//            }

            FileOperations.printImage(buildIntersectionsMap(intersections, vectorMatrix), FileOperations.INTERSECTIONS_MAP);
        }
    }

    public List<int[]> findIntersections(Vector<HoughLine> vectors) {
        List<int[]> intersections = new ArrayList<int[]>();
        for (int i = 0; i < vectors.size() - 1; i++){
            HoughLine line = vectors.elementAt(i);
            for (int j = i + 1; j < vectors.size(); j++) {
                HoughLine line2 = vectors.elementAt(j);
                int intersectionX = (int) ((line2.getInterceptor() - line.getInterceptor()) / (line.getSlope() - line2.getSlope()));
                int intersectionY = (int) (line.getSlope() * intersectionX + line.getInterceptor());
                intersections.add(new int[]{intersectionX, intersectionY});
            }
        }
        return intersections;
    }

    public int[][] buildIntersectionsMap(List<int[]> intersections, int[][] sourceMatrix) {
        int[][] intersectionsMap = new int[sourceMatrix.length][sourceMatrix[0].length];
        for (int i = 0; i < sourceMatrix.length; i++) {
            for (int j = 0; j < sourceMatrix[i].length; j++) {
                for (int[] ints: intersections) {
                    if (Math.abs(ints[0]) == i && Math.abs(ints[1]) == j) {
                        intersectionsMap[i][j] = Color.BLUE.getRGB();
                    }
                }
            }
        }
        return intersectionsMap;
    }

    public Map<Integer, List<HoughLine>> divedeVectorsByIntersetionMap(Vector<HoughLine> vectors, List<int[]> intersections) {
        Map<Integer, List<HoughLine>> dividedVectors = new LinkedHashMap<Integer, List<HoughLine>>();
        int count = 0;
        for (int i = 0; i < vectors.size(); i++) {
            for (int[] ints: intersections) {
                boolean isLies = ints[1] == ((int) (vectors.get(i).getSlope() * ints[0] + vectors.get(i).getInterceptor()));
//                        ints[0] == (line.getInterceptor() + ints[1]) / line.getSlope();
                if (isLies) {
                    HoughLine newLine =  new HoughLine(vectors.get(i).x1, vectors.get(i).y1, ints[0], ints[1]);
                    setNameByKey(i, newLine, dividedVectors);
                    count++;
                }
            }
        }
        System.out.println("found Liars: " + count);
        System.out.println("inters size: " + intersections.size());
        System.out.println("Map Size: " + dividedVectors.size());
        return dividedVectors;
    }

    public Vector<HoughLine> sortVectorsMap(Map<Integer, List<HoughLine>> map) {
        Vector<HoughLine> result = new Vector<HoughLine>();
        Map<Integer, HoughLine> sortedMap = new LinkedHashMap<Integer, HoughLine>();
        List<Double> distances = new ArrayList<Double>();

        //sorting map
        for (Map.Entry<Integer, List<HoughLine>> entry: map.entrySet()) {
            for (HoughLine lines: entry.getValue()) {
                double distance = Math.sqrt(Math.pow(lines.x2 - lines.x1, 2) + Math.pow(lines.y2 - lines.y1, 2));
                distances.add(distance);
            }

            double max = 0; int index = 0;
            for (int i = 0; i < distances.size(); i++) {
                if (max < distances.get(i)) {
                    max = distances.get(i);
                    index = i;
                }
            }
            sortedMap.put(entry.getKey(), entry.getValue().get(index));
            distances.clear();
        }

        //make result
        for (Map.Entry<Integer, HoughLine> entry: sortedMap.entrySet()) {
            result.add(entry.getValue());
        }

        return result;
    }

    private void setNameByKey(Integer key, HoughLine value, Map<Integer, List<HoughLine>> map) {
        List<HoughLine> tmpList = new ArrayList<HoughLine>();
        if (!map.containsKey(key)) {
            map.put(key, tmpList);
        }
        for(Map.Entry<Integer, List<HoughLine>> entry: map.entrySet()){
            if (entry.getKey().equals(key)){
                tmpList.addAll(entry.getValue());
                tmpList.add(value);
                map.put(key, tmpList);
            }
        }
    }

}
