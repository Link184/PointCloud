import configuration.Configuration;
import engine.FileOperations;
import points.PointDensityMap;
import points.PointHighMap;
import points.PointsWorker;
import points.VectorMap;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String... a) {
        StringBuilder pointCloud = FileOperations.readPC();

        String[] stringLinesNumber = pointCloud.toString().split("\n");
        List<Float> allPoints = new ArrayList<Float>();

        for (int i = 0; i < stringLinesNumber.length; i++) {
            String[] split = stringLinesNumber[i].split("(\t)");
            for (String s: split) {
                allPoints.add(Float.valueOf(s));
            }
        }

        for (int i=0;i<(allPoints.size()/3); i++) {
            PointsWorker.getAllPoints().add(new float[]{allPoints.get(3*i), allPoints.get(3*i+1), allPoints.get(3*i+2)});
        }

        Configuration config = new Configuration(Configuration.Precision.CENTIMETERS, 0);

        Thread pointDensityMap = new Thread(new PointDensityMap(config));
        Thread pointHighMap = new Thread(new PointHighMap(config));
        Thread vectorMap = new Thread(new VectorMap(config));
        pointDensityMap.start();
        pointHighMap.start();
        vectorMap.start();


//        double[] points = new double[allPoints.size()];
//        for (int i = 0; i < allPoints.size(); i++) {
//            points[i] = allPoints.get(i);
//        }
//
//        Ransac ransac = new Ransac();
//        Line[] lines = ransac.findLines(points);
//        System.out.println(TestRansac.totalIter);
//
//        Collections.addAll(TestRansac.sampleFitLine, lines);
//        TestRansac.main(null);
    }

}
