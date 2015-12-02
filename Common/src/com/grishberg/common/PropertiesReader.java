package com.grishberg.common;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * Created by g on 29.11.15.
 */
public class PropertiesReader {
    private static final String CONFIG_NAME = "config.properties";
    private Properties property;

    public PropertiesReader(Map<String, String> defaults) {
        FileInputStream fis;
        property = new Properties();
        try {
            File file = new File(CONFIG_NAME);
            if (!file.exists()) {
                createProperties(defaults);
            }
            fis = new FileInputStream(CONFIG_NAME);
            property.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createProperties(Map<String, String> defaults) {
        Properties applicationProps = new Properties();
        for (Map.Entry<String, String> entry : defaults.entrySet()) {
            applicationProps.put(entry.getKey(), entry.getValue());
        }
        try {
            FileOutputStream out = new FileOutputStream(CONFIG_NAME);
            applicationProps.store(out, "---No Comment---");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readString(String param) {
        return property.getProperty(param);
    }

    public int readInt(String param, int defaultValue) {
        int res = defaultValue;
        String val = property.getProperty(param);
        if (val != null) {
            try {
                res = Integer.valueOf(val);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return res;
    }
}
