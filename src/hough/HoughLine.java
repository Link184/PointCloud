package hough;

import points.PointsWorker;

import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

/**
 * Represents a linear line as detected by the hough transform. 
 * This line is represented by an angle theta and a radius from the centre. 
 *
 * @author Olly Oechsle, University of Essex, Date: 13-Mar-2008 
 * @version 1.0
 */
public class HoughLine extends Line2D.Float{

  protected double theta;
  protected double r;

  // Line equation: y = slope * x + b
  protected double slope;
  protected double interceptor;

  /**
   * Initialises the hough line
   */
  public HoughLine(double theta, double r, int width, int height) {
    this.theta = theta;
    this.r = r;
// During processing h_h is doubled so that -ve r values
    int houghHeight = (int) (Math.sqrt(2) * Math.max(height, width)) / 2;

    // Find edge points and vote in array
    float centerX = width / 2;
    float centerY = height / 2;

    // Draw edges in output array
    double tsin = Math.sin(theta);
    double tcos = Math.cos(theta);

    if (theta < Math.PI * 0.25 || theta > Math.PI * 0.75) {
      int x1=0, y1= 0;
      int x2=0, y2=height-1;

      x1=(int) ((((r - houghHeight) - ((y1 - centerY) * tsin)) / tcos) + centerX);
      x2=(int) ((((r - houghHeight) - ((y2 - centerY) * tsin)) / tcos) + centerX);

      setLine(x1, y1, x2, y2);
    }
    else {
      int x1= 0, y1=0;
      int x2=width-1, y2=0;

      y1=(int) ((((r - houghHeight) - ((x1 - centerX) * tcos)) / tsin) + centerY);
      y2=(int) ((((r - houghHeight) - ((x2 - centerX) * tcos)) / tsin) + centerY);

      setLine(x1, y1, x2, y2);
    }
    slope = (getY2() - getY1()) / (getX2() - getX1());
    interceptor = getY1() - slope * getX1();
  }

  public HoughLine(int x1, int y1, int x2, int y2) {
    setLine(x1, y1, x2, y2);
    slope = (getY2() - getY1()) / (getX2() - getX1());
    interceptor = getY1() - slope * getX1();
  }

  /**
   * Draws the line on the image of your choice with the RGB colour of your choice.
   */
  public void draw(BufferedImage image, int color) {

    int height = image.getHeight();
    int width = image.getWidth();

    // During processing h_h is doubled so that -ve r values
    int houghHeight = (int) (Math.sqrt(2) * Math.max(height, width)) / 2;

    // Find edge points and vote in array
    float centerX = width / 2;
    float centerY = height / 2;

    // Draw edges in output array
    double tsin = Math.sin(theta);
    double tcos = Math.cos(theta);

    if (theta < Math.PI * 0.25 || theta > Math.PI * 0.75) {
      // Draw vertical-ish lines
      for (int y = 0; y < height; y++) {
        int x = (int) ((((r - houghHeight) - ((y - centerY) * tsin)) / tcos) + centerX);
        if (x < width && x >= 0) {
          PointsWorker.getVectorMatrix()[x][y] = color;
          image.setRGB(x, y, color);
        }
      }
    } else {
      // Draw horizontal-sh lines
      for (int x = 0; x < width; x++) {
        int y = (int) ((((r - houghHeight) - ((x - centerX) * tcos)) / tsin) + centerY);
        if (y < height && y >= 0) {
          PointsWorker.getVectorMatrix()[x][y] = color;
          image.setRGB(x, y, color);
        }
      }
    }

  }

  public double getSlope() {
    return slope;
  }

  public double getInterceptor() {
    return interceptor;
  }

}