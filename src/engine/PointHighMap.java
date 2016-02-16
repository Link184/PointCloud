package engine;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PointHighMap extends PointsWorker implements Runnable{
    private Lock lock = new ReentrantLock();
    public PointHighMap(int mesUnit, int tolerance) {
        super(mesUnit, tolerance);
    }

    @Override
    public void run() {
        synchronized (PointsWorker.class) {
            System.out.println("Now we will map high points...");
            for (int i = 0; i < yCapacity; i++) {
                List<float[]> sortedList = sortSurfaceZX(i);
                float[][] surface = createSurfaceHigh(sortedList);
                float[] projectedSurface = projectTo2DHigh(surface);
                linkProjectedRows(i, projectedSurface);
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
        float[][] surfaceHighMap = new float[zCapacity + 1][xCapacity +1];
        for (float[] f: sortedList) {
            surfaceHighMap[(int) (zOrigin + (f[2] * mesUnits))][(int) (xOrigin + (f[0] * mesUnits))] = f[1];
        }
        return surfaceHighMap;
    }

    @Override
    public int[] projectTo2DDens(int[][] surface) {
        return new int[0];
    }

    @Override
    public float[] projectTo2DHigh(float[][] surface) {
        float[] projectedRow = new float[xCapacity+1];
        int[] counter = new int[xCapacity+1];
        for (float[] aSurface : surface) {
            for (int j = 0; j < aSurface.length; j++) {
//                float[] highPoints = getHighPoint(aSurface);
//                float min = highPoints[0];
//                float max = highPoints[1];
//                projectedRow[j] = Math.abs(max) + Math.abs(min);
                if (aSurface[j] != 0) {
                    projectedRow[j] += Math.abs(aSurface[j]);
                    counter[j]++;
                }
            }
        }
        for (int i = 0; i < projectedRow.length; i++){
            projectedRow[i] /= (float)counter[i];
        }
        return fillZeros(projectedRow);
    }

    @Override
    public void linkProjectedRows(int index, float[] row) {
        if (index <= yOrigin) {
            projectedPCTo2DHighMap[Math.abs(((int) (minY * mesUnits))) - index + 1] = row;
        } else {
            projectedPCTo2DHighMap[index] = row;
        }
    }

    @Override
    public void linkProjectedRows(int index, int[] row) {
    }

    private float[] fillZeros(float[] row) {
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

    private float[] getHighPoint(float[] row){
        float min = 0, max = 0;
        for (float f: row) {
            if (max < f) max = f;
            if (min > f) min = f;
        }
        return new float[]{min, max};
    }
}
