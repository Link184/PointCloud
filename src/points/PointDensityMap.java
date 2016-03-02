package points;

import configuration.Configuration;
import engine.FileOperations;

import java.util.ArrayList;
import java.util.List;

public class PointDensityMap extends PointsWorker implements Runnable{

    private static List<Double> tmp = new ArrayList<Double>();

    public PointDensityMap(Configuration config) {
        super(config);
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
