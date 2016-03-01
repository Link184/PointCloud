package ransac2;

import org.ddogleg.fitting.modelset.DistanceFromModel;
import org.ddogleg.fitting.modelset.ModelManager;
import ransac.*;

import java.util.ArrayList;
import java.util.List;

public class RansacThread implements Runnable {
    private List<Point2D> points;
    private List<Line2D> lines;

    public RansacThread(int[][] densityMatrix){
        points = new ArrayList<Point2D>();
        for (int x = 0; x < densityMatrix.length; x++) {
            for (int y = 0; y < densityMatrix[x].length; y++) {
                if (densityMatrix[x][y] > 0)
                    points.add(new Point2D(x, y));
            }
        }
    }

    @Override
    public void run() {
        ModelManager<Line2D> manager = new LineManager();
        org.ddogleg.fitting.modelset.ModelGenerator<Line2D,Point2D> generator = new LineGenerator();
        DistanceFromModel<Line2D,Point2D> distance = new DistanceFromLine();

        org.ddogleg.fitting.modelset.ModelMatcher<Line2D,Point2D> alg =
                new org.ddogleg.fitting.modelset.ransac.Ransac<Line2D,Point2D>(234234,manager,generator,distance,500,30);

        if( !alg.process(points) )
            throw new RuntimeException("Robust fit failed!");

        // let's look at the results
        Line2D found = alg.getModelParameters();

        // notice how all the noisy points were removed and an accurate line was estimated?
        System.out.println("Found line   "+ found);
        System.out.println("Match set size = "+alg.getMatchSet().size());


    }
}
