package points;

import engine.FileOperations;

import java.util.List;

public class PointHighMap extends PointsWorker implements Runnable{
    public PointHighMap(int mesUnit, int tolerance) {
        super(mesUnit, tolerance);
    }

    @Override
    public void run() {
        synchronized (PointsWorker.class) {
            System.out.println("Now we will map high points...");
            for (int i = 0; i < zCapacity; i++) {
                List<float[]> sortedList = sortSurfaceYX(i);
                float[][] surface = createSurfaceHigh(sortedList);
                findHighestPoints(surface);
                System.out.print("\rProcessed... " + i + " High units");
            }
            System.out.println();

            FileOperations.exportToFile(projectedPCTo2DHighMap);
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
            surfaceHighMap[(int) (yOrigin + (f[1] * mesUnits))][(int) (xOrigin + (f[0] * mesUnits))] = f[1];
        }
        return surfaceHighMap;
    }

    @Override
    public int[] projectTo2DDens(int[][] surface) {
        return new int[0];
    }

    @Override
    public void findHighestPoints(float[][] surface) {
        for (int i = 0; i < surface.length - 1; i++) {
            for (int j = 0; j < surface[i].length - 1; j++) {
                if (projectedPCTo2DHighMap[i][j] < Math.abs(surface[i][j])) {
                    projectedPCTo2DHighMap[i][j] += Math.abs(surface[i][j]);
                }
            }
        }
    }

    @Override
    public void linkProjectedRows(int index, float[] row) {
    }

    @Override
    public void linkProjectedRows(int index, int[] row) {
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
