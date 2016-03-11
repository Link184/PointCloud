import configuration.Configuration;
import points.PointDensityMap;
import points.PointHighMap;
import points.VectorMap;

public class Main {

    public static void main(String... a) {
        Configuration config = new Configuration("file_easy.xyz", Configuration.Precision.CENTIMETERS, 0, 200);

        Thread pointDensityMap = new Thread(new PointDensityMap(config));
        pointDensityMap.setPriority(Thread.MAX_PRIORITY);
        pointDensityMap.start();
        Thread pointHighMap = new Thread(new PointHighMap(config));
        pointHighMap.start();
        Thread vectorMap = new Thread(new VectorMap(config));
        vectorMap.setPriority(Thread.MIN_PRIORITY);
        vectorMap.start();
    }

}
