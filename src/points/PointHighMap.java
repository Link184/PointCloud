package points;

import engine.FileOperations;

import java.util.List;

public class PointHighMap extends PointsWorker implements Runnable{
    private float[][] negativeHighValues = new float[yCapacity + 1][xCapacity + 1];
    private float[][] positiveHighValues = new float[yCapacity + 1][xCapacity + 1];

    public PointHighMap(int mesUnit, int tolerance) {
        super(mesUnit, tolerance);
    }

    @Override
    public void run() {
        synchronized (PointsWorker.class) {
            for (int i = 0; i < zCapacity; i++) {
                List<float[]> sortedList = sortSurfaceYX(i);
                float[][] surface = createSurfaceHigh(sortedList);
                findHighestPoints(surface, i);
                System.out.print("\rProcessed... " + i + " High units");
            }
            linkHighMap();
            System.out.println();

            FileOperations.exportToFile(projectedPCTo2DHighMap, FileOperations.DENSITY_HIGH_FILE);
            FileOperations.printImage(projectedPCTo2DHighMap, FileOperations.HIGH_IMAGE);
//            MatrixDithering.floydSteinbergDithering(projectedPCTo2DHighMap);
//            FileOperations.printImage(projectedPCTo2DHighMap, FileOperations.HIGH_IMAGE_DITHERED);
        }
    }

    @Override
    public int[][] createSurfaceDens(List<float[]> sortedList) {
        return new int[0][];
    }

    @Override
    public float[][] createSurfaceHigh(List<float[]> sortedList){
        float[][] surfaceHighMap = new float[yCapacity + 1][xCapacity +1];
        for (float[] f: sortedList) {
            surfaceHighMap[(int) (yOrigin + (f[1] * mesUnits))][(int) (xOrigin + (f[0] * mesUnits))] = f[2];
        }
        return surfaceHighMap;
    }

    @Override
    public void projectTo2DDens(int[][] surface) {
    }

    @Override
    public void findHighestPoints(float[][] surface, int index) {
        for (int i = 0; i < surface.length; i++) {
            for (int j = 0; j < surface[i].length; j++) {
                float abs = Math.abs(surface[i][j]);
                if (negativeHighValues[i][j] < abs && index <= zOrigin) {
                    negativeHighValues[i][j] = abs;
                }
                if (positiveHighValues[i][j] < abs && index > zOrigin) {
                    positiveHighValues[i][j] = abs;
                }
            }
        }
    }

    private void linkHighMap() {
        for (int i = 0; i < projectedPCTo2DHighMap.length; i++) {
            for (int j = 0; j < projectedPCTo2DHighMap[i].length; j++) {
                projectedPCTo2DHighMap[i][j] = negativeHighValues[i][j] + positiveHighValues[i][j];
            }
        }
    }

    private float[] excludeNaN(float[] row) {
        float[] result = new float[row.length];
        for (int i = 0; i < row.length; i++) {
            if (Float.isNaN(row[i])) {
                result[i] = 0f;
            } else {
                result[i] = row[i];
            }
        }
        return result;
    }

    public float[] getHighPoint(float[] row){
        float min = 0, max = 0;
        for (float f: row) {
            if (max < f) max = f;
            if (min > f) min = f;
        }
        return new float[]{min, max};
    }
}
