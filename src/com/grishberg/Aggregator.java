package com.grishberg;

import com.grishberg.interfaces.IAggregator;
import com.grishberg.models.KeyContainer;
import com.grishberg.models.ResultContainer;
import com.grishberg.models.UserInfoContainer;
import com.grishberg.parser.ResultWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by g on 08.11.15.
 */
public class Aggregator implements IAggregator {
    private List<ResultContainer> data;
    private long mappersCount;
    private long responseCount;
    private int fraction;

    public Aggregator() {
        data = new ArrayList<>();
    }

    @Override
    public void setMappersCount(long count) {
        mappersCount = count;
        fraction = (int) (mappersCount / 100);
    }

    @Override
    synchronized public void putResults(List<ResultContainer> results) {
        boolean isFound = false;
        for (ResultContainer result : results) {
            /*for (int i = 0; i < data.size(); i++) {
                ResultContainer currentResult = data.get(i);
                isFound = false;
                if (currentResult.getKey().compareTo(result.getKey()) == 0) {
                    isFound = true;
                    currentResult.addData(result.getData());
                    break;
                }
            }*/
            if (!isFound) {
                ResultContainer container = new ResultContainer();
                container.setKey(result.getKey());
                container.addData(result.getData());
                data.add(container);
            }
        }

        responseCount++;
        if (fraction == 0 || responseCount % fraction == 0) {
            float per = ((float) responseCount / (float) mappersCount) * 100.0f;
            System.out.println(String.format("reduce %f", per));
        }
    }

    public int printResults(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            file.mkdirs();
        }
        boolean isFirst = true;
        ResultWriter writer = new ResultWriter(fileName + "/result.json");
        writer.write("{\"result\":[\n");

        for (ResultContainer resultContainer : data) {
            if (isFirst) {
                isFirst = false;
            } else {
                writer.write(",\n");
            }
            writer.write(resultContainer.toString());
        }
        writer.write("\n\t]\n}");
        writer.close();
        return data.size();
    }
}
