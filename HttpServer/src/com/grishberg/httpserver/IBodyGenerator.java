package com.grishberg.httpserver;

import java.util.List;

/**
 * Created by g on 29.11.15.
 */
public interface IBodyGenerator {
    String generateBody(String target, List<MultipartContainer> multipart);
}
