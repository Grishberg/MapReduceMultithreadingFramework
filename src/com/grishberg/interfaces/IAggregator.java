package com.grishberg.interfaces;

import com.grishberg.models.KeyContainer;
import com.grishberg.models.ResultContainer;
import com.grishberg.models.UserInfoContainer;

import java.util.List;

/**
 * Created by g on 08.11.15.
 */
public interface IAggregator {
    void putResults(List<ResultContainer> results);
    void setMappersCount(long count);
}
