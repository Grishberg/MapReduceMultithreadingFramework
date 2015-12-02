package com.grishberg.multithreading;

import com.grishberg.interfaces.IAggregator;
import com.grishberg.models.KeyContainer;
import com.grishberg.models.ResultContainer;
import com.grishberg.models.UserInfoContainer;
import com.grishberg.parser.RdrRaw;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by g on 08.11.15.
 */
public class TaskRunnable implements Runnable {
    private String path;
    private List<String> list;
    private IAggregator aggregator;
    private List<String> filter;
    private List<ResultContainer> data;

    public TaskRunnable(String path, List<String> filter, IAggregator aggregator) {
        this.aggregator = aggregator;
        this.path = path;
        this.filter = filter;
    }

    @Override
    public void run() {
        //System.out.println(String.format("start map fn=%s", path));
        String line;
        BufferedReader in;
        data = new ArrayList<>();

        try {
            in = new BufferedReader(new FileReader(path));

            line = in.readLine();

            int count = 0;
            while (line != null) {
                count++;
                line = in.readLine();
                if (count == 1) continue;
                RdrRaw columns = RdrRaw.getInstance(line);
                if (columns == null) continue;
                if (columns.dstHost.length() == 0) continue;
                String url = columns.dstHost + columns.dstParam;
                String strKey = null;
                for (int i = 0; i < filter.size(); i++) {
                    strKey = filter.get(i);
                    if (url != null && url.contains(strKey)) {
                        //System.out.printf(" >> key=%s, url=%s\n",strKey, url);
                        UserInfoContainer userInfo = new UserInfoContainer();
                        userInfo.setUserId(columns.userId);
                        userInfo.setUserIp(columns.userIp);
                        putResults(url, userInfo);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        aggregator.putResults(data);
    }

    private void putResults(String key, UserInfoContainer results) {

        ResultContainer container = new ResultContainer();
        container.setKey(key);
        container.setData(results);
        data.add(container);
    }
}
