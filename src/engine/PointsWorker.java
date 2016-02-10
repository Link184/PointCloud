package engine;

import java.util.ArrayList;
import java.util.List;

public class PointsWorker {

    private static List<float[]> allPoints = new ArrayList<float[]>();

    private int mesUnits = 10;
    private float maxX, minX;
    private float maxY, minY;
    private float maxZ, minZ;
    private float sceneLength;
    private float sceneWidth;
    private float sceneHeight;

    private int xCapacity;
    private int yCapacity;
    private int zCapacity;
    private final static float mesUnit = 0.1f;

    private int xOrigin, yOrigin, zOrigin;

    private int[][] projectedPCTo2D;

    public PointsWorker(int mesUnit) {
        this.mesUnits = mesUnit;
        float[] extremes = extremeValues();
        maxX = extremes[3]; minX = extremes[0];
        maxY = extremes[4]; minY = extremes[1];
        maxZ = extremes[5]; minZ = extremes[2];

        sceneLength = maxX + Math.abs(minX);
        sceneWidth = maxY + Math.abs(minY);
        sceneHeight = maxZ + Math.abs(minZ);

        xCapacity = ((int) (sceneLength * mesUnits));
        yCapacity = ((int) (sceneWidth * mesUnits));
        zCapacity = ((int) (sceneHeight * mesUnits));

        int[] origins = getPCOrigin();
        xOrigin = origins[0]; yOrigin = origins[1]; zOrigin = origins[2];

        projectedPCTo2D = new int[yCapacity][xCapacity];
    }

    public void start() {
        System.out.println("Xcap: " + xCapacity + " Ycap: " + yCapacity + " Zcap: " + zCapacity);
        System.out.println("Array Origins: [ XOrigin: " + xOrigin + "" +
                ", YOrigin: " + yOrigin + ", ZOrigin: " + zOrigin + " ]");
        for (int i = 0; i < yCapacity; i++) {
            List<float[]> sortedList = sortSurfaceZX(i);
            int[][] surface = createSurface(sortedList);
            int[] projectedRow = projectTo2D(surface);
            linkProjectedRows(i, projectedRow);
            System.out.print("\r Processed... " + i + " cm");
        }
        System.out.println();
        FileOperations.writePC(projectedPCTo2D);
    }

    public List<float[]> sortSurfaceZX(int surfaceDepth){
        float partY = ((float) surfaceDepth) / mesUnits;
        float negativeWay = minY;
        List<float[]> surface = new ArrayList<float[]>();
        float localUnit = 0.1f;
        if (partY >= Math.abs(negativeWay)) {
            // positive Y points
            for (float[] allPoint : allPoints) {
                if (allPoint[1] > 0 && allPoint[1] < (partY + negativeWay) && allPoint[1] > (partY  + negativeWay - localUnit)) {
                    surface.add(allPoint);
                }
            }
        } else {
            // negative Y points
            for (float[] allPoint: allPoints) {
                if (allPoint[1] < 0 && Math.abs(allPoint[1]) < partY && Math.abs(allPoint[1]) > (partY - localUnit)) {
                    surface.add(allPoint);
                }
            }
        }
        return surface;
    }

    private int[][] createSurface(List<float[]> sortedList){
        int[][] surface = new int[zCapacity + 1][xCapacity + 1];
        for (float[] f: sortedList) {
            surface[(int) (zOrigin + (f[2] * mesUnits))][(int) (xOrigin + (f[0] * mesUnits))]++;
        }
        return surface;
    }

    private int[] projectTo2D(int[][] surface){
        int[] projectedRow = new int[xCapacity+1];
        for (int[] aSurface : surface) {
            for (int j = 0; j < aSurface.length; j++) {
                projectedRow[j] += aSurface[j];
            }
        }
        return projectedRow;
    }

    private void linkProjectedRows(int index, int[] row) {
        if (index <= yOrigin) {
            projectedPCTo2D[Math.abs(((int) (minY * mesUnits))) - index + 1] = row;
        } else {
            projectedPCTo2D[index] = row;
        }
    }

    private int[] getPCOrigin(){
        int xOrigin, yOrigin, zOrigin;
        xOrigin = ((int) ((sceneLength - maxX) * mesUnits));
        yOrigin = ((int) ((sceneWidth - maxY) * mesUnits));
        zOrigin = ((int) ((sceneHeight - maxZ) * mesUnits));
        System.out.println("SLen: " + sceneLength + " MaxX: " + maxX);
        return new int[]{xOrigin, yOrigin, zOrigin};
    }

    private float[] extremeValues(){
        float maxX = 0, minX = 0;
        float maxY = 0, minY = 0;
        float maxZ = 0, minZ = 0;
        for (float[] f: allPoints) {
            if (f[0] < minX) minX = f[0];
            if (f[1] < minY) minY = f[1];
            if (f[2] < minZ) minZ = f[2];
            if (f[0] > maxX) maxX = f[0];
            if (f[1] > maxY) maxY = f[1];
            if (f[2] > maxZ) maxZ = f[2];
        }
        return new float[]{minX, minY, minZ, maxX, maxY, maxZ};
    }

    public void printStatistics() {
        System.out.println("H: " + sceneHeight + " W: " + sceneWidth + " L: " + sceneLength);

        System.out.println("Points Count: " + allPoints.size());
    }

    public static List<float[]> getAllPoints() {
        return allPoints;
    }

}
