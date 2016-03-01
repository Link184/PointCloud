package ransac2;
import java.util.List;

/**
 * Given a set of points create a model hypothesis.  In most applications just a single hypothesis
 * will be generated.  In SFM applications geometric ambiguities can cause multiple hypotheses to be
 * created.
 *
 * @author Peter Abeles
 */
public interface ModelGenerator<Model,Point> {

    /**
     * Creates a new instance of the model
     *
     * @return New model instance
     */
    public Model createModelInstance();

    /**
     * Creates a list of hypotheses from the set of sample points.
     *
     * @param dataSet Set of sample points.  Typically the minimum number possible.
     * @param output Storage for generated model.
     * @return true if a model was generated, otherwise false is none were
     */
    public boolean generate( List<Point> dataSet , Model output );

    /**
     * The minimum number of points required to fit a data set
     *
     * @return Number of points.
     */
    public int getMinimumPoints();
}