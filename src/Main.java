import engine.FileOperations;
import engine.PointsWorker;

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
        PointsWorker pointsWorker = new PointsWorker(10);
        pointsWorker.printStatistics();
        pointsWorker.start();
    }
}
