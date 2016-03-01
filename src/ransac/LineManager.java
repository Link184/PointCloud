package ransac;

import org.ddogleg.fitting.modelset.ModelManager;

public class LineManager implements ModelManager<Line2D> {
    @Override
    public Line2D createModelInstance() {
        return new Line2D();
    }

    @Override
    public void copyModel(Line2D src, Line2D dst) {
        dst.x = src.x;
        dst.y = src.y;
    }
}