package com.grishberg.httpserver;

import java.io.IOException;

/**
 * Created by g on 14.11.15.
 */
public interface IClientSession {
    int send(String body);
}
