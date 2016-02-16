package engine;

import java.util.ArrayList;
import java.util.List;

public abstract class PointsWorker implements Points{
    protected static List<float[]> allPoints = new ArrayList<float[]>();

    protected int mesUnits = 10;
    protected int tolerance = 100;
    protected float maxX, minX;
    protected float maxY, minY;
    protected float maxZ, minZ;
    protected float sceneLength;
    protected float sceneWidth;
    protected float sceneHeight;

    protected int xCapacity;
    protected int yCapacity;
    protected int zCapacity;
    protected final static float mesUnit = 0.1f;

    protected int xOrigin, yOrigin, zOrigin;

    protected static float[][] projectedPCTo2DHighMap;
    protected static int[][] projectedPCTo2DDensMap;

    protected final Object syncObj = new Object();

    public PointsWorker(int mesUnit, int tolerance) {
        this.mesUnits = mesUnit;
        this.tolerance = tolerance;
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

        projectedPCTo2DHighMap = new float[yCapacity][xCapacity];
        projectedPCTo2DDensMap = new int[yCapacity][xCapacity];
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

    protected void filterProjectedMatrix(int tolerance, int[][] projectedMatrix) {
        for (int i = 1; i<projectedMatrix.length - 1; i++) {
            for (int j = 1; j<projectedMatrix[i].length - 1; j++){
                if (projectedMatrix[i][j] < tolerance) {
                    projectedMatrix[i][j] = 0;
                }
            }
        }
    }

    protected int[][] transposeMatrix(int[][] sourceMatrix) {
        int[][] inversedMatrix = new int[sourceMatrix[0].length][sourceMatrix.length];
        for (int i=0; i<sourceMatrix[0].length; i++) {
            for (int j=0; j<sourceMatrix.length; j++) {
                inversedMatrix[i][j] = sourceMatrix[j][i];
            }
        }
        return inversedMatrix;
    }

    private int[] getPCOrigin(){
        int xOrigin, yOrigin, zOrigin;
        xOrigin = ((int) ((sceneLength - maxX) * mesUnits));
        yOrigin = ((int) ((sceneWidth - maxY) * mesUnits));
        zOrigin = ((int) ((sceneHeight - maxZ) * mesUnits));
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
        System.out.println("SLen: " + sceneLength + " MaxX: " + maxX);

        System.out.println("H: " + sceneHeight + " W: " + sceneWidth + " L: " + sceneLength);

        System.out.println("Points Count: " + allPoints.size());

        System.out.println("Xcap: " + xCapacity + " Ycap: " + yCapacity + " Zcap: " + zCapacity);
        System.out.println("Array Origins: [ XOrigin: " + xOrigin + "" +
                ", YOrigin: " + yOrigin + ", ZOrigin: " + zOrigin + " ]");
    }

    public static List<float[]> getAllPoints() {
        return allPoints;
    }
}
