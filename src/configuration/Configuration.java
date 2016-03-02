package configuration;

public class Configuration {
    private int tolerance = 0;
    private int precision = 100;

    public Configuration(Precision precision, int tolerance) {
        this.tolerance = tolerance;
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
}
