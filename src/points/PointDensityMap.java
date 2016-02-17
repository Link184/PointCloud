package points;

import engine.FileOperations;
import engine.MatrixDithering;
import engine.PointsFilter;

import java.util.List;

public class PointDensityMap extends PointsWorker implements Runnable{

    public PointDensityMap(int mesUnit, int tolerance) {
        super(mesUnit, tolerance);
    }


    @Override
    public void run() {
        printStatistics();
        for (int i = 1; i < yCapacity + 1; i++) {
            List<float[]> sortedList = sortSurfaceZX(i);
            int[][] surface = createSurfaceDens(sortedList);
            int[] projectedRow = projectTo2DDens(surface);
            linkProjectedRows(i, projectedRow);
            System.out.print("\rProcessed... " + i + " Density units");
        }

        System.out.println();
        int[][] cleanProjectedMatrix = projectedPCTo2DDensMap.clone();

        synchronized (PointsWorker.class) {
            PointsFilter.cleanByHigh(projectedPCTo2DDensMap, projectedPCTo2DHighMap);

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
            FileOperations.exportToFile(projectedPCTo2DDensMap);
            FileOperations.printImage(projectedPCTo2DDensMap, FileOperations.DENSITY_IMAGE);

            MatrixDithering.floydSteinbergDithering(cleanProjectedMatrix);
            FileOperations.printImage(cleanProjectedMatrix, FileOperations.DENSITY_IMAGE_DITHERED);
//        System.out.println(PointsFilter.getMedMatrixValue(projectedPCTo2DDensMap));
//        int[][] medRowsValues = PointsFilter.getMedRowsValue(projectedPCTo2DDensMap);
//        for (int j = 0; j < medRowsValues[0].length; j++) {
//            System.out.println(medRowsValues[0][j] + "\t" + medRowsValues[1][j] + "\t" + medRowsValues[2][j]);
//        }
        }
    }

    @Override
    public int[][] createSurfaceDens(List<float[]> sortedList){
        int[][] surface = new int[zCapacity + 1][xCapacity + 1];
        for (float[] f: sortedList) {
            surface[(int) (zOrigin + (f[2] * mesUnits))][(int) (xOrigin + (f[0] * mesUnits))]++;
        }
        return surface;
    }

    @Override
    public float[][] createSurfaceHigh(List<float[]> sortedList) {
        return new float[0][];
    }

    @Override
    public void findHighestPoints(float[][] surface) {
    }

    @Override
    public int[] projectTo2DDens(int[][] surface){
        int[] projectedRow = new int[surface[0].length];
        for (int[] aSurface : surface) {
            for (int j = 0; j < aSurface.length; j++) {
                projectedRow[j] += aSurface[j];
            }
        }
        return projectedRow;
    }

    @Override
    public void linkProjectedRows(int index, int[] row) {
        if (index <= yOrigin) {
            projectedPCTo2DDensMap[Math.abs(((int) (minY * mesUnits))) - index + 1] = row;
        } else {
            projectedPCTo2DDensMap[index - 1] = row;
        }
    }

    @Override
    public void linkProjectedRows(int index, float[] row) {

    }

    public static List<float[]> getAllPoints() {
        return allPoints;
    }
}
