package com.grishberg.parser;

/**
 * Created by fesswood on 29.10.15.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by fesswood on 29.10.15.
 */
public class SettingsReader {

    private final File currentFile;
    public ArrayList<String> resultArray;
    String propFileName = "config.properties";
    String result = "";
    InputStream inputStream;

    public SettingsReader(String file) {
        if(file != null){
            propFileName = file;
        }
        currentFile = new File(file);
        resultArray = new ArrayList<>();
    }

    /**
     * читаем файл
     * @param splitString
     * @return
     */
    public ArrayList<String> readProperty(String splitString){
        try {
            Scanner input = new Scanner(new FileInputStream(currentFile));
            while (input.hasNextLine()) {
                String line = input.nextLine();
                line = line.replace("urls =", "");
                String[] splitedLine = line.split(splitString);
                for (int i = 0; i < splitedLine.length; i++) {
                    resultArray.add(splitedLine[i]);
                }
                System.out.println(line);
            }
            input.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultArray;
    }

    /**
     * Читаем проперти внутри проекта
     * @param splitString
     * @return
     * @throws IOException
     */
    public ArrayList<String> getPropValues(String splitString) throws IOException {
        resultArray.clear();
        try {
            Properties prop = new Properties();
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            String urls = prop.getProperty("urls");
            String[] splitedLine = urls.split(splitString);
            for (int i = 0; i < splitedLine.length; i++) {
                resultArray.add(splitedLine[i].trim());
            }
            System.out.println("urls "+urls);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
        return resultArray;
    }

    /**
     * Читаем проперти не внутри проекта
     * @param ExternalProperty
     * @param splitString
     * @return
     * @throws IOException
     */
    public ArrayList<String> getExternalPropValues(String externalProperty,String splitString) throws IOException {
        resultArray.clear();
        try {
            Properties prop = new Properties();
            inputStream = new FileInputStream(new File(externalProperty));
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            String urls = prop.getProperty("urls");
            String[] splitedLine = urls.split(splitString);
            for (int i = 0; i < splitedLine.length; i++) {
                resultArray.add(splitedLine[i]);
            }
            System.out.println("urls "+urls);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            if(inputStream != null){
                inputStream.close();
            };
        }
        return resultArray;
    }

    public List<String> getUrls() {
        try {
            return getExternalPropValues(propFileName , ",");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}