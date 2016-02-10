package engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PointsWorker {
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    public static List<float[]> allPoints = new ArrayList<float[]>();

    float[] extremes;
    float maxX, minX;
    float maxY, minY;
    float maxZ, minZ;
    public float sceneLength;
    public float sceneWidth;
    public float sceneHeight;

    public int xCapacity;
    public int yCapacity;
    public int zCapacity;
    public final static float mesUnit = 0.1f;

    int[] origins;
    public int xOrigin, yOrigin, zOrigin;

    int[][] projectedPCTo2D;

    public PointsWorker() {
        extremes = extremeValues();
        maxX = extremes[3]; minX = extremes[0];
        maxY = extremes[4]; minY = extremes[1];
        maxZ = extremes[5]; minZ = extremes[2];

        sceneLength = maxX + Math.abs(minX);
        sceneWidth = maxY + Math.abs(minY);
        sceneHeight = maxZ + Math.abs(minZ);

        xCapacity = ((int) (sceneLength * 10));
        yCapacity = ((int) (sceneWidth * 10));
        zCapacity = ((int) (sceneHeight * 10));

        origins = getPCOrigin();
        xOrigin = origins[0]; yOrigin = origins[1]; zOrigin = origins[2];

        projectedPCTo2D = new int[yCapacity][xCapacity];
    }

//    public float[][][] voxelGrid = new float[((int) (sceneLength * 10))][((int) (sceneWidth * 10))][((int) (sceneHeight * 10))];

    public void createSurfacedPC() {
        for (int i = 0; i < yCapacity; i++) {
            List<float[]> sortedList = sortSurfaceZX(i);
            int[][] surface = createSurface(sortedList);
            int[] projectedRow = projectTo2D(surface);
            linkProjectedRows(i, projectedRow);
            System.out.print("\r Processed... " + i + " cm");
        }
        System.out.println();
//        Collections.reverse(projectedPCTo2D);
        FileOperations.writePC(projectedPCTo2D);

//        List<float[]> sortedList = sortSurfaceZX(70);
//        int[][] surface = createSurface(sortedList);
//        int[] projectedRow = projectTo2D(surface);
//        projectedPCTo2D[70] = projectedRow;
////        FileOperations.writePC(projectedPCTo2D);

        System.out.println("Xcap: " + xCapacity + " Ycap: " + yCapacity + " Zcap: " + zCapacity);
        System.out.println("Array Origins: [ XOrigin: " + xOrigin + "" +
                ", YOrigin: " + yOrigin + ", ZOrigin: " + zOrigin + " ]");

//        for(int[] row : surface) {
//            printRow(row);
//        }
//        System.out.println();
//        for (int i: projectedRow){
//            System.out.print(i + " ");
//        }

    }

    public List<float[]> sortSurfaceZX(int surfaceDepth){
        float partY = ((float) surfaceDepth) / 10;
        float positiveWay = maxY;
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
//        System.out.println("PartY: " + partY + " PositiveWay: " + positiveWay + " if relation: " + (partY <= ((float) positiveWay)/10));
        return surface;
    }

    public int[][] createSurface(List<float[]> sortedList){
        int[][] surface = new int[zCapacity + 1][xCapacity + 1];
        for (float[] f: sortedList) {
            surface[(int) (zOrigin + (f[2] * 10))][(int) (xOrigin + (f[0] * 10))]++;
        }
//        System.out.println("Positive on ZX: " + count);
        return surface;
    }

    public int[] projectTo2D(int[][] surface){
        int[] projectedRow = new int[xCapacity+1];
        for (int i = 0; i < surface.length; i++){
            for (int j = 0; j < surface[i].length; j++) {
                projectedRow[j] += surface[i][j];
            }
        }
        return projectedRow;
    }

    private void linkProjectedRows(int index, int[] row) {
        if (index <= yOrigin) {
            projectedPCTo2D[Math.abs(((int) (minY * 10))) - index + 1] = row;
        } else {
            projectedPCTo2D[index] = row;
        }
    }

    private int[] getPCOrigin(){
        int xOrigin, yOrigin, zOrigin;
        xOrigin = ((int) ((sceneLength - maxX) * 10));
        yOrigin = ((int) ((sceneWidth - maxY) * 10));
        zOrigin = ((int) ((sceneHeight - maxZ) * 10));
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

    public static void printRow(int[] row) {
        for (int i : row) {
            System.out.print(i);
            System.out.print("\t");
        }
        System.out.println();
    }

}
