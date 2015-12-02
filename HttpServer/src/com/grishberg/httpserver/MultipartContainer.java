package com.grishberg.httpserver;

/**
 * Created by g on 19.11.15.
 */
public class MultipartContainer {
    private byte[] data;
    private String name;

    public MultipartContainer(byte[] data, String name) {
        this.data = data;
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
