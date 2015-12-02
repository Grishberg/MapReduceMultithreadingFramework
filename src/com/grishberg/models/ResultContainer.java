package com.grishberg.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g on 08.11.15.
 */
public class ResultContainer implements Comparable<ResultContainer> {
    private String key;
    private UserInfoContainer data;

    public ResultContainer() {
        key = null;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setData(UserInfoContainer newData) {
        data = newData;
    }

    public String getKey() {
        return key;
    }

    public UserInfoContainer getData() {
        return data;
    }

    @Override
    public String toString() {
        return data.toString();
        /*
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();
        sb.append("\t{\"key\":").append(key.toString()).append(",\n\t\"data\":[");
        for (UserInfoContainer container : data) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(',');
            }
            sb.append(container.toString());
        }
        sb.append("]\n\t}");
        return sb.toString();
        */
    }

    @Override
    public int compareTo(ResultContainer o) {
        if (key == null) {
            return -1;
        }
        if (o == null) {
            return 1;
        }
        return key.compareTo(o.key);
    }
}
