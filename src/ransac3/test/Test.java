package ransac3.test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Test extends JFrame {

    /**
     * Unique ID
     */
    private static final long serialVersionUID = 6538463951464863248L;

    public Test() {
        super("Test");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new Dimension(600, 600));
        setVisible(true);
        setResizable(false);
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0xffffff));
        g2.fillRect(0, 0, 600, 600);
        g2.translate(300, 300);
        g2.setColor(new Color(0xcccccc));
        for (int i = -9; i < 10; i++) {
            g2.drawLine(-300, i * 30, 300, i * 30);
            g2.drawLine(i * 30, -300, i * 30, 300);
        }
        g2.setStroke(new BasicStroke(2));
        List<Point2D> data = new ArrayList<Point2D>();
        List<Double> params;
        LineParamEstimator lpe = new LineParamEstimator(0.5);

        data.add(new Point2D(-8, 7));
        data.add(new Point2D(10, -1));
        data.add(new Point2D(-8.01, 7.96));
        data.add(new Point2D(-2.03, -1.94));
        data.add(new Point2D(-6.01, 5.56));
        data.add(new Point2D(8.03, -7.34));
        data.add(new Point2D(-3.01, 3.96));
        data.add(new Point2D(5.03, -3.34));
        data.add(new Point2D(-7.01, 7.96));
        data.add(new Point2D(-3.03, 2.94));
        data.add(new Point2D(6.71, -6.56));
        data.add(new Point2D(-8.23, 7.44));
        data.add(new Point2D(3.31, 4.96));
        data.add(new Point2D(-4.33, -3.44));
        data.add(new Point2D(-7.01, 7.96));
        data.add(new Point2D(-3.03, 2.94));
        data.add(new Point2D(5.71, -6.56));
        data.add(new Point2D(-6.23, 7.44));
        data.add(new Point2D(7.31, 4.96));
        data.add(new Point2D(-8.33, -3.44));
        params = lpe.leastSquaresEstimate(data);
        Point2D p = new Point2D(2.02, 0.69);
        if (lpe.agree(params, p)) {
            drawPlot(g2, p.getX(), p.getY(), 4, 0x66aa66, true);
        } else {
            drawPlot(g2, p.getX(), p.getY(), 4, 0xaa3333, true);
        }
        for (int i = 0; i < data.size(); i++) {
            drawPlot(g2, data.get(i).getX(), data.get(i).getY(), 4, 0xaaff00,
                    true);
        }
        drawLine(g2, params.get(0), params.get(1), params.get(2),
                params.get(3), 1, 0x3366aa);
        drawLine(g2, params.get(0), params.get(1), params.get(2)
                        - params.get(0) * 0.5, params.get(3) - params.get(1) * 0.5, 1,
                0x3366aa);
        drawLine(g2, params.get(0), params.get(1), params.get(2)
                        + params.get(0) * 0.5, params.get(3) + params.get(1) * 0.5, 1,
                0x3366aa);
    }

    private void drawPlot(Graphics2D g, double x, double y, int size, int rgb,
                          boolean isFill) {
        int pX = (int) Math.round(x * 30.0);
        int pY = (int) Math.round(y * 30.0);
        Color color = g.getColor();
        g.setColor(new Color(rgb));
        if (isFill) {
            g.fillRect(pX - size, pY - size, size * 2, size * 2);
        } else {
            g.drawOval(pX - size, pY - size, size * 2, size * 2);
        }
        g.setColor(color);
    }

    private void drawLine(Graphics2D g, double nX, double nY, double aX,
                          double aY, int size, int rgb) {
        int w = 30;
        int y1 = (int) Math.round((nX * (aX + 10.0) / nY + aY) * w);
        int y2 = (int) Math.round((nX * (aX - 10.0) / nY + aY) * w);
        Color color = g.getColor();
        Stroke stroke = g.getStroke();
        g.setColor(new Color(rgb));
        g.setStroke(new BasicStroke(size));
        g.drawLine(-300, y1, 300, y2);
        g.setColor(color);
        g.setStroke(stroke);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new Test();
    }
}