package com.grishberg.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g on 08.11.15.
 */
public class ResultContainer {
    private KeyContainer key;
    private List<UserInfoContainer> data;

    public ResultContainer() {
        key = null;
        data = new ArrayList<>();
    }

    public void setKey(KeyContainer key) {
        this.key = key;
    }

    public void addData(List<UserInfoContainer> newData) {
        data.addAll(newData);
    }

    public void addData(UserInfoContainer newData) {
        data.add(newData);
    }

    public KeyContainer getKey() {
        return key;
    }

    public List<UserInfoContainer> getData() {
        return data;
    }

    @Override
    public String toString() {
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
    }
}
