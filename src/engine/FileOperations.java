package engine;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class FileOperations {

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

    public static void writePC(int[][] source){
        File file = new File("source", "file.txt");
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] aSource : source) {
            for (int anASource : aSource) {
                if (anASource < 1000) {
                    if (anASource != 0) {
                        stringBuilder.append(anASource).append("\t").append("\t");
                    } else stringBuilder.append(".").append("\t").append("\t");
                } else {
                    if (anASource != 0) {
                        stringBuilder.append(anASource).append("\t");
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
}
