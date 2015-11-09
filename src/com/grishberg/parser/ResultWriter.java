package com.grishberg.parser;

import java.io.*;

/**
 * Created by g on 08.11.15.
 */
public class ResultWriter {
    private String fileName;
    private Writer writer = null;
    private boolean isOpened;

    public ResultWriter(String fileName) {
        this.fileName = fileName;
        try {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName), "utf-8"));
            isOpened = true;
        } catch (IOException ex) {
            // report
            ex.printStackTrace();
        } finally {

        }
    }

    public void close() {
        if (writer == null) return;
        try {
            writer.close();
        } catch (Exception ex) {/*ignore*/}
    }

    public void write(String data) {
        if (!isOpened) return;
        try {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
