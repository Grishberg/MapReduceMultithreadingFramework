package com.grishberg.models;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ResultKeyContainer implements Comparable<ResultKeyContainer> {
    private long count;
    private String url;

    public ResultKeyContainer() {
        setCount(0);
        setUrl("");
    }

    public ResultKeyContainer(String url) {
        setCount(0);
        setUrl(url);
    }

    public ResultKeyContainer(String url, int count) {
        setCount(count);
        setUrl(url);
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return String.format("{ \"url\" : \"%s\", \"count\" : %d }", url, count);
    }

    @Override
    public int compareTo(ResultKeyContainer o) {
        int cmp = url.compareTo(o.url);
        return cmp;
    }
}
