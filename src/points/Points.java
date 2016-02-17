package points;

import java.util.List;

public interface Points{

    int[][] createSurfaceDens(List<float[]> sortedList);
    float[][] createSurfaceHigh(List<float[]> sortedList);
    void projectTo2DDens(int[][] surface);
    void findHighestPoints(float[][] surface);
}
