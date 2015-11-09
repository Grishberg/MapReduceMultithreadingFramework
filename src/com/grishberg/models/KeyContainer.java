package com.grishberg.models;

/**
 * Created by g on 08.11.15.
 */
public class KeyContainer implements Comparable<KeyContainer> {
    private String key;
    private long count;

    public KeyContainer(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public int compareTo(KeyContainer o) {
        if (o == null) {
            return -1;
        }
        if (key == null) {
            return -1;
        }
        return key.compareTo(o.getKey());
    }

    @Override
    public String toString() {
        return String.format("\"%s\"", key);
    }
}
