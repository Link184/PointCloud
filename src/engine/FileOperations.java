package engine;

import java.io.*;

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
        for (int i = source.length-1; i>=0 ; i--) {
            for (int j = 0; j<source[i].length; j++) {
                if (source[i][j] < 1000) {
                    if (source[i][j] != 0) {
                        stringBuilder.append(source[i][j]).append("\t").append("\t");
                    } else stringBuilder.append(".").append("\t").append("\t");
                } else {
                    if (source[i][j] != 0) {
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
}
