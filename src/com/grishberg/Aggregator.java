package com.grishberg;

import com.grishberg.interfaces.IAggregator;
import com.grishberg.models.KeyContainer;
import com.grishberg.models.ResultContainer;
import com.grishberg.models.UserInfoContainer;
import com.grishberg.parser.ResultWriter;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by g on 08.11.15.
 */
public class Aggregator implements IAggregator {
    private List<ResultContainer> data;
    private long mappersCount;
    private long responseCount;
    private int fraction;
    DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
    private static Logger log = Logger.getLogger(Aggregator.class.getName());

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
        data.addAll(results);

        responseCount++;
        if (fraction == 0 || responseCount % fraction == 0) {
            float per = ((float) responseCount / (float) mappersCount) * 100.0f;

            Date date = new Date();
            String dateFormatted = formatter.format(date);
            String msg = String.format("[%s] reduce %f, total count = %d", dateFormatted, per, data.size());
            System.out.println(msg);
            log.info(msg);
        }
    }

    public int printResults(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            file.mkdirs();
        }
        boolean isFirst = true;
        boolean isFirstData = true;

        long startTime = System.currentTimeMillis();
        Collections.sort(data);
        long endTime = System.currentTimeMillis();
        System.out.printf("sort time = %d ms, sort count =%d\n", endTime - startTime, data.size());

        String lastKey = null;
        if (data.size() > 0) {
            lastKey = data.get(0).getKey();
        }

        ResultWriter writer = new ResultWriter(fileName + "/result.json");
        writer.write(String.format("{\"result\":[\n\t{\n\t\t\"key\":\"%s\",\n\t\t\"data\":["
                , lastKey));

        int count = 0;
        for (int i = 0; i < data.size(); i++) {
            ResultContainer resultContainer = data.get(i);
            count++;

            if (!resultContainer.getKey().equals(lastKey)) {
                writer.write(String.format("],\n\t\t\"count\":%d\n\t}", count));
                if (i < data.size() - 1) {
                    writer.write(String.format(",\n\t{\n\t\t\"key\":%s\",\n\t\t\"data\":["
                            , resultContainer.getKey()));
                    isFirstData = true;
                }
                count = 0;
            }
            if (isFirstData) {
                isFirstData = false;
            } else {
                writer.write(",");
            }
            writer.write(resultContainer.toString());
            lastKey = resultContainer.getKey();
        }
        writer.write(String.format("\t\t],\n\t\t\"count\":%d\n\t}", count));
        writer.write("\n\t]\n}");
        writer.close();
        return data.size();
    }
}
