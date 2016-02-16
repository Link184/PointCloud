package engine;

import java.util.List;

public interface Points{

    int[][] createSurfaceDens(List<float[]> sortedList);
    float[][] createSurfaceHigh(List<float[]> sortedList);
    int[] projectTo2DDens(int[][] surface);
    float[] projectTo2DHigh(float[][] surface);
    void linkProjectedRows(int index, int[] row);
    void linkProjectedRows(int index, float[] row);
}
