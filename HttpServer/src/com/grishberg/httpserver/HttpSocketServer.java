package com.grishberg.httpserver;

import com.grishberg.common.job.IJobTracker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;

import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.entity.NFileEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

/**
 * Embedded HTTP/1.1 file server based on a non-blocking I/O model and capable of direct channel
 * (zero copy) data transfer.
 */
public class HttpSocketServer extends Thread {
    private final IJobTracker mJobTracker;
    private IBodyGenerator mBodyGenerator;
    private final int mPort;
    private Thread mConnectionThread;
    private HttpServer server;

    public HttpSocketServer(IJobTracker jobTracker, int port, IBodyGenerator bodyGenerator) {
        mJobTracker = jobTracker;
        mPort = port;
        mBodyGenerator = bodyGenerator;
    }

    public boolean startServer() {
        start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (server != null) {
                    server.shutdown(5, TimeUnit.SECONDS);
                }
            }
        });
        return true;
    }

    public void stopServer() {
        if (mConnectionThread != null) {
            mConnectionThread.interrupt();
        }
    }

    @Override
    public void run() {
        super.run();

        SSLContext sslcontext = null;
        if (mPort == 8443) {
            // Initialize SSL context
            URL url = HttpSocketServer.class.getResource("/my.keystore");
            if (url == null) {
                System.out.println("Keystore not found");
                System.exit(1);
            }
            try {
                sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(url, "secret".toCharArray(), "secret".toCharArray())
                        .build();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        IOReactorConfig config = IOReactorConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();

        HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(mPort)
                .setServerInfo("Test/1.1")
                .setIOReactorConfig(config)
                .setSslContext(sslcontext)
                .setExceptionLogger(ExceptionLogger.STD_ERR)
                .registerHandler("*", new ClientSession(mBodyGenerator))
                .create();

        try {
            server.start();
            server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            if (server != null) {
                server.shutdown(5, TimeUnit.SECONDS);
            }
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}