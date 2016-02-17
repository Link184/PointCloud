package engine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class FileOperations {
    public static final String DENSITY_IMAGE = "density_image.png";
    public static final String DENSITY_IMAGE_DITHERED = "density_image_dithered.png";
    public static final String HIGH_IMAGE = "high_image.png";
    public static final String HIGH_IMAGE_DITHERED = "high_image_dithered.png";

    public static StringBuilder readPC(){
        StringBuilder stringBuilder = new StringBuilder();
        File file = new File("source", "file.xyz");
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

    public static void exportToFile(int[][] source){
        File file = new File("source", "fileDens.txt");
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

    public static void exportToFile(float[][] source){
        File file = new File("source", "fileHigh.txt");
        StringBuilder stringBuilder = new StringBuilder();

        //Reversed print of projected matrix
        for (int i = source.length-1; i>=0 ; i--) {
            for (int j = 0; j<source[i].length; j++) {
                if (source[i][j] != 0f) {
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
}
