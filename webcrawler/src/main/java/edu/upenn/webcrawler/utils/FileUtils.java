package edu.upenn.webcrawler.utils;

import java.io.*;

public abstract class FileUtils {
    public static void mkdirs(String path) {
        File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }
    }
    public static void writeToFile(String string, String filePath) {
        FileOutputStream fos = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(filePath, true);
            fos.write(string.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readFile(String Path) {
        BufferedReader reader = null;
        String laststr = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString+"\n";
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }

    public static void delete(String filePath){
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
    }
}
