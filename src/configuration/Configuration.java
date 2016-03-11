package configuration;

public class Configuration {
    private String sourceFile;
    private int tolerance = 0;
    private int precision = 100;
    private int threshold = 0;

    /**
     *
     * @param sourceFile file with point cloud data
     * @param precision precision of point cloud segmentation
     * @param tolerance directly proportionality with program speed
     * @param threshold threshold for hough transformation algorithm
     */
    public Configuration(String sourceFile, Precision precision, int tolerance, int threshold) {
        this.sourceFile = sourceFile;
        this.tolerance = tolerance;
        this.threshold = threshold;
        switch (precision) {
            case DECIMETERS:
                this.precision = 10;
                break;
            case CENTIMETERS:
                this.precision = 100;
                break;
            case MILIMETERS:
                this.precision = 1000;
                break;
            default:
                this.precision = 0;
        }

    }

    public enum Precision {DECIMETERS, CENTIMETERS, MILIMETERS}

    public int getTolerance() {
        return tolerance;
    }

    public int getPrecision() {
        return precision;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public int getThreshold() {
        return threshold;
    }
}
