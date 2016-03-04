import configuration.Configuration;
import points.PointDensityMap;
import points.PointHighMap;
import points.VectorMap;

public class Main {

    public static void main(String... a) {
        Configuration config = new Configuration("file_easy.xyz", Configuration.Precision.CENTIMETERS, 0, 200);

        Thread pointDensityMap = new Thread(new PointDensityMap(config));
        Thread pointHighMap = new Thread(new PointHighMap(config));
        Thread vectorMap = new Thread(new VectorMap(config));
        pointDensityMap.start();
        pointHighMap.start();
        vectorMap.start();
    }

}
