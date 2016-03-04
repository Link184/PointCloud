package engine;

import hough.HoughLine;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Vector;

public class FileOperations {
    public static final String DENSITY_IMAGE = "density_image.png";
    public static final String DENSITY_IMAGE_DITHERED = "density_image_dithered.png";
    public static final String HIGH_IMAGE = "high_image.png";
    public static final String HIGH_IMAGE_DITHERED = "high_image_dithered.png";
    public static final String DENSITY_FILE = "fileDens.txt";
    public static final String DENSITY_HIGH_FILE = "fileHigh.txt";
    public static final String INTERSECTIONS_MAP = "intersection_iamge.png";
    public static final String SVG_FILE = "vectors.svg";

    public static StringBuilder readPC(String fileName){
        StringBuilder stringBuilder = new StringBuilder();
        File file = new File("source", fileName);
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            fileReader.close();
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder;
    }

    public static void exportToFile(int[][] source, String fileName){
        File file = new File("source", fileName);
        StringBuilder stringBuilder = new StringBuilder();

        //Reversed print of projected matrix
        for (int i = source.length-1; i>=0 ; i--) {
            for (int j = 0; j<source[i].length; j++) {
                if (source[i][j] < 1000) {
                    if (source[i][j] > 0) {
                        stringBuilder.append(source[i][j]).append("\t").append("\t");
                    } else stringBuilder.append(".").append("\t").append("\t");
                } else {
                    if (source[i][j] > 0) {
                        stringBuilder.append(source[i][j]).append("\t");
                    }else stringBuilder.append(".").append("\t").append("\t");
                }
            }
            stringBuilder.append("\n");
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(stringBuilder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportToFile(float[][] source, String fileName){
        File file = new File("source", fileName);
        StringBuilder stringBuilder = new StringBuilder();

        //Reversed print of projected matrix
        for (int i = source.length-1; i>=0 ; i--) {
            for (int j = 0; j<source[i].length; j++) {
                if (source[i][j] != 0) {
                    stringBuilder.append(source[i][j]).append("\t");
                } else {
                    stringBuilder.append(".").append("\t").append("\t").append("\t");
                }
            }
            stringBuilder.append("\n");
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(stringBuilder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printImage(int[][] source, String fileName){
        BufferedImage image = new BufferedImage(source.length + 1, source[0].length + 1, BufferedImage.TYPE_INT_RGB);
        for (int i = source.length-1; i>=0 ; i--) {
            for (int j = 0; j < source[i].length; j++) {
                image.setRGB(i, j, source[i][j]);
            }
        }
        File imageFile = new File("source", fileName);
        try {
            ImageIO.write(image, "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printImage(float[][] source, String fileName){
        BufferedImage image = new BufferedImage(source.length + 1, source[0].length + 1, BufferedImage.TYPE_INT_RGB);
        for (int i = source.length-1; i>=0 ; i--) {
            for (int j = 0; j < source[i].length; j++) {
                image.setRGB(i, j, (int) source[i][j]*100);
            }
        }
        File imageFile = new File("source", fileName);
        try {
            ImageIO.write(image, "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printVectorImage(int[][] source, String fileName) {
        BufferedImage image = new BufferedImage(source.length + 1, source[0].length + 1, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source[i].length; j++) {
                image.setRGB(i, j, source[i][j]);
            }
        }
        File imageFile = new File("source", fileName);
        try {
            ImageIO.write(image, "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportToSVG(Vector<HoughLine> vectors, String fileName) {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(SVGConstants.SVG_NAMESPACE_URI, "svg", null);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        svgGenerator.scale(1, 1);

        for (int i = 0; i < vectors.size(); i++) {
//            svgGenerator.draw(vectors.elementAt(i));
            HoughLine line = vectors.elementAt(i);
            int y1 = (int) (line.getSlope() * line.getX1() + line.getInterceptor());
            int y2 = (int) (line.getSlope() * line.getX2() + line.getInterceptor());
            int x1 = (int) ((y1 - line.getInterceptor()) / line.getSlope());
            int x2 = (int) ((y2 - line.getInterceptor()) / line.getSlope());

            svgGenerator.drawLine(x1, y1, x2, y2);
        }

        try {
            OutputStream outputStream = new FileOutputStream(new File("source", fileName));
            Writer out = new OutputStreamWriter(outputStream, "UTF-8");
            svgGenerator.stream(out);
            outputStream.flush();
            outputStream.close();
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (SVGGraphics2DIOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
