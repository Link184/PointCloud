package engine;

public class PointsFilter {
    public static int getMedMatrixValue(int[][] projectedMatrix) {
        int projPointsCount = 0;
        int projPointsSum = 0;
        for (int i = 0; i<projectedMatrix.length; i++) {
            for (int j = 0; j<projectedMatrix[i].length; j++){
                if (projectedMatrix[i][j] != 0) {
                    projPointsCount++;
                    projPointsSum += projectedMatrix[i][j];
                }
            }
        }
        return projPointsSum/projPointsCount;
    }

    public static float getMedMatrixValue(float[][] projectedMatrix) {
        int projPointsCount = 0;
        float projPointsSum = 0;
        for (int i = 0; i<projectedMatrix.length; i++) {
            for (int j = 0; j<projectedMatrix[i].length; j++){
                if (projectedMatrix[i][j] > 0f) {
                    projPointsCount++;
                    projPointsSum += projectedMatrix[i][j];
                }
            }
        }
        return projPointsSum/projPointsCount;
    }

    public static int[][] getMedRowsValue(int[][] projectedMatrix) {
        int[][] projRowSums = new int[3][projectedMatrix.length];
        for (int i = 0; i<projectedMatrix.length; i++) {
            int projPointCount = 0;
            int projRowSum = 0;
            for (int j = 0; j<projectedMatrix[i].length; j++){
                if (projectedMatrix[i][j] != 0) {
                    projRowSum += projectedMatrix[i][j];
                    projPointCount++;
                }
            }
            projRowSums[0][i] = projRowSum;
            projRowSums[1][i] = projPointCount;
        }
        for (int i = 0; i<projRowSums[0].length; i++) {
            if (projRowSums[0][i] != 0 && projRowSums[1][i] != 0) {
                projRowSums[2][i] = projRowSums[0][i] / projRowSums[1][i];
            }
        }
        return projRowSums;
    }

    public static void computeMatrixDensity(int accuracy, int[][] projectedMatrix) {
        for (int i = 1; i<projectedMatrix.length - 1; i++) {
            for (int j = 1; j < projectedMatrix[i].length -1 ; j++) {
                boolean biggerRegion = projectedMatrix[i][j] > projectedMatrix[i-1][j] &&
                        projectedMatrix[i][j] > projectedMatrix[i+1][j] &&
                        projectedMatrix[i][j] > projectedMatrix[i][j-1] &&
                        projectedMatrix[i][j] > projectedMatrix[i][j+1];
                if (projectedMatrix[i][j] >= accuracy && biggerRegion) {
                    int topPoint = projectedMatrix[i-1][j];
                    int botPoint = projectedMatrix[i+1][j];
                    int leftPoint = projectedMatrix[i][j-1];
                    int rightPoint = projectedMatrix[i][j+1];
                    int medNeighborValue = (topPoint + botPoint + leftPoint + rightPoint) / 4;
                    projectedMatrix[i][j] += medNeighborValue;
                    projectedMatrix[i-1][j] = projectedMatrix[i][j] - medNeighborValue;
                    projectedMatrix[i+1][j] = projectedMatrix[i][j] - medNeighborValue;
                    projectedMatrix[i][j-1] = projectedMatrix[i][j] - medNeighborValue;
                    projectedMatrix[i][j+1] = projectedMatrix[i][j] - medNeighborValue;
                }
            }
        }
    }

    public static void clearLowDensityPoints(int[][] projectedMatrix) {
        int medValue = getMedMatrixValue(projectedMatrix);
        for (int i = 0; i<projectedMatrix.length; i++) {
            for (int j = 0; j < projectedMatrix[i].length; j++) {
                if (projectedMatrix[i][j] < medValue) {
                    projectedMatrix[i][j] = 0;
                }
            }
        }
    }

    public static void destroyMetaPoints(int[][] projectedMatrix) {
        int stepRow = projectedMatrix.length / 10;
        int stepCol = projectedMatrix[0].length / 10;
        int[][] trashMatrix = new int[stepRow][stepCol];
        for (int i = 0; i<projectedMatrix.length; i += stepRow) {
            for (int j = 0; j < projectedMatrix[i].length; j += stepCol) {
                boolean letsGo = i >= stepRow && j >=stepCol &&
                        i <= projectedMatrix.length - stepRow &&
                        j <= projectedMatrix[i].length - stepCol;
                if (letsGo) {
                    int topPoint = 0;
                    int botPoint = 0;
                    int leftPoint = 0;
                    int rightPoint = 0;
                    for (int k=0; k<stepRow; k++) {
                        topPoint += projectedMatrix[i - k][j];
                        botPoint += projectedMatrix[i + k][j];
                    }
                    for (int k=0; k<stepCol; k++) {
                        leftPoint += projectedMatrix[i][j - k];
                        rightPoint += projectedMatrix[i][j + k];
                    }
                    int medCellValue = (topPoint + botPoint + leftPoint + rightPoint) / 4;
                    trashMatrix[i/stepRow][j/stepCol] = medCellValue;
                }
            }
        }
        int medTrashMatrixValue = getMedMatrixValue(trashMatrix);
        for (int i = 0; i<trashMatrix.length; i++) {
            for (int j = 0; j < trashMatrix[i].length; j++) {
                boolean letsGo = trashMatrix[i][j] < medTrashMatrixValue &&
                        (i+1)*stepRow < projectedMatrix.length &&
                        (j+1)*stepCol < projectedMatrix[0].length;
                if (letsGo) {
                    for (int k = i*stepRow; k<(i+1)*stepRow; k++) {
                        for (int l = j*stepCol; l < (j+1)*stepCol; l++) {
                            projectedMatrix[k][l] = 30000;
                        }
                    }
                }
            }
        }
    }

    public static void cleanByHigh(int[][] projectedMatrix, float[][] highMap){
        float medHighValue = getMedMatrixValue(highMap);
        int count = 0;
        for (int i = 0; i < highMap.length; i++) {
            for (int j = 0; j < highMap[i].length; j++) {
                if (highMap[i][j] < medHighValue) {
                    projectedMatrix[i][j] = 0;
                    count++;
                }
            }
        }
        System.out.println("MedHighValue: " + medHighValue + " count: " + count);

    }

    public static void preparesFinalMatrix(int[][] projectedMatrix) {
        for (int i = 1; i<projectedMatrix.length - 1; i++) {
            for (int j = 1; j < projectedMatrix[i].length - 1; j++) {
                int topPoint = projectedMatrix[i-1][j];
                int botPoint = projectedMatrix[i+1][j];
                int leftPoint = projectedMatrix[i][j-1];
                int rightPoint = projectedMatrix[i][j+1];
                boolean equilibrium = ((topPoint == botPoint) || (leftPoint == rightPoint)
                        || (topPoint == leftPoint) || (topPoint == rightPoint)
                        || (botPoint == leftPoint) || (botPoint == rightPoint))
                        && projectedMatrix[i][j] > 0;
                if (equilibrium) {
                    projectedMatrix[i-1][j] =
                            projectedMatrix[i+1][j] =
                                    projectedMatrix[i][j-1] =
                                            projectedMatrix[i][j+1] = 0;
                    projectedMatrix[i][j] /= 10000;
                }
            }
        }
    }

    public static void cleanMatrix(int[][] projectedMatrix) {
        for (int i = 1; i<projectedMatrix.length - 1; i++) {
            for (int j = 1; j < projectedMatrix[i].length - 1; j++) {
                int topPoint = projectedMatrix[i-1][j];
                int botPoint = projectedMatrix[i+1][j];
                int leftPoint = projectedMatrix[i][j-1];
                int rightPoint = projectedMatrix[i][j+1];
                boolean isTrash;
            }
        }
    }
}
