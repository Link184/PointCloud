package points;

import configuration.Configuration;
import engine.FileOperations;
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

//            Pdf.start(lines, projectedPCTo2DDensMap.length, projectedPCTo2DDensMap[0].length);
            try {
                ImageIO.write(img, "png", file);
                // print hough array
                ImageIO.write(transform.getHoughArrayImage(), "png", new File("hough.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOperations.printVectorImage(vectorMatrix, "VectorImage.png");

            List<int[]> intersections = findIntersections(lines);

            Map<Integer, List<int[]>> vectorPoints = divedeVectorsByIntersetionMap(lines, intersections);

            FileOperations.exportToSVG(lines, intersections, FileOperations.SVG_FILE);
            FileOperations.exportToSVG(sortVectorsMap(vectorPoints), null, "divided_vectors.svg");
//            for (int[] i: intersections) {
//                System.out.println(i[0] + " " + i[1]);
//            }

            FileOperations.printImage(buildIntersectionsMap(intersections, vectorMatrix), FileOperations.INTERSECTIONS_MAP);
        }
    }

    /**
     * algorithm witch must find cross vector intersection points
     * @param vectors vector list of HoughLines
     * @return list of intersection points
     */
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


    /**
     *
     * @param intersections list of intersection points
     * @param sourceMatrix model of required matrix for return
     * @return intersection points matrix
     */
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

    /**
     *
     * @param vectors vectors received from hough algorithm
     * @param intersections list of intersection points
     * @return map of multiple lines received from division of vectors by intersection points
     */
    public Map<Integer, List<int[]>> divedeVectorsByIntersetionMap(Vector<HoughLine> vectors, List<int[]> intersections) {
        Map<Integer, List<int[]>> vectorPoints = new LinkedHashMap<Integer, List<int[]>>();
        int count = 0;
        for (int i = 0; i < vectors.size(); i++) {
            for (int[] ints: intersections) {
                boolean isLies = ints[1] == ((int) (vectors.get(i).getSlope() * ints[0] + vectors.get(i).getInterceptor()));
//                        ints[0] == (line.getInterceptor() + ints[1]) / line.getSlope();
                if (isLies) {
                    setNameByKey(i, new int[]{ints[0], ints[1]}, vectorPoints);
                    count++;
                }
            }
        }
        System.out.println("found Liars: " + count);
        System.out.println("inters size: " + intersections.size());
        System.out.println("Map Size: " + vectorPoints.size());
        return vectorPoints;
    }

    /**
     *
     * @param map map of divided vectors by intersection points
     * @return vector array of HoughLines
     */
    public Vector<HoughLine> sortVectorsMap(Map<Integer, List<int[]>> map) {
        Vector<HoughLine> result = new Vector<HoughLine>();
        //sorting map
        for (Map.Entry<Integer, List<int[]>> entry: map.entrySet()) {
            int max = 0, index1 = 0, index2 = 0;
            Map<Integer, List<int[]>> linesLength = new LinkedHashMap<Integer, List<int[]>>();
            for (int i = 0; i < entry.getValue().size(); i++) {
                for (int j = i; j < entry.getValue().size(); j++) {
                    int distance = (int) Math.sqrt(((int) Math.pow(entry.getValue().get(j)[0] - entry.getValue().get(i)[0], 2))
                            + ((int) Math.pow(entry.getValue().get(j)[1] - entry.getValue().get(i)[1], 2)));
                    setNameByKey(j, new int[]{distance, i, j}, linesLength);
                }
            }

            for (Map.Entry<Integer, List<int[]>> entry1: linesLength.entrySet()) {
                for (int i = 0; i < entry1.getValue().size(); i++) {
                    if (entry1.getValue().get(i)[0] > max && entry1.getValue().get(i)[0] < 100000) {
                        max = entry1.getValue().get(i)[0];
                        index1 = entry1.getValue().get(i)[1];
                        index2 = entry1.getValue().get(i)[2];
                    }
                }
            }
            result.add(new HoughLine(entry.getValue().get(index1)[0],
                    entry.getValue().get(index1)[1],
                    entry.getValue().get(index2)[0],
                    entry.getValue().get(index2)[1]));
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

    private void setNameByKey(Integer key, int[] value, Map<Integer, List<int[]>> map) {
        List<int[]> tmpList = new ArrayList<int[]>();
        if (!map.containsKey(key)) {
            map.put(key, tmpList);
        }
        for(Map.Entry<Integer, List<int[]>> entry: map.entrySet()){
            if (entry.getKey().equals(key)){
                tmpList.addAll(entry.getValue());
                tmpList.add(value);
                map.put(key, tmpList);
            }
        }
    }

}
