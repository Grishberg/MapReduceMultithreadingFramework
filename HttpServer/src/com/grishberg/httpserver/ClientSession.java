package com.grishberg.httpserver;

import com.grishberg.common.job.IJobTracker;
import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.protocol.*;
import org.apache.http.protocol.HttpContext;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Обрабатывает запрос клиента.
 */
public class ClientSession implements HttpAsyncRequestHandler<org.apache.http.HttpRequest> {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final int STATE_READ_HEADERS = 1;
    public static final int STATE_READ_CONTENT = 2;
    private final IBodyGenerator mBodyGenerator;

    public ClientSession(final IBodyGenerator bodyGenerator) {
        super();
        mBodyGenerator = bodyGenerator;
    }

    public HttpAsyncRequestConsumer<org.apache.http.HttpRequest> processRequest(
            final org.apache.http.HttpRequest request,
            final HttpContext context) {
        // Buffer request content in memory for simplicity
        return new BasicAsyncRequestConsumer();
    }

    public void handle(
            final org.apache.http.HttpRequest request,
            final HttpAsyncExchange httpexchange,
            final HttpContext context) throws HttpException, IOException {
        HttpResponse response = httpexchange.getResponse();
        handleInternal(request, response, context);
        httpexchange.submitResponse(new BasicAsyncResponseProducer(response));
    }

    private void handleInternal(
            final org.apache.http.HttpRequest request,
            final HttpResponse response,
            final HttpContext context) throws HttpException, IOException {

        String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
            throw new MethodNotSupportedException(method + " method not supported");
        }
        if (checkCommands(request, response)) {
            return;
        }
    }

    private boolean checkCommands(org.apache.http.HttpRequest request, HttpResponse response) throws IOException {
        String target = request.getRequestLine().getUri();
        List<MultipartContainer> multipart = null;
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();

            if (entity != null) {
                Header[] headers = request.getHeaders(CONTENT_TYPE);
                String boundary = getBoundary(headers);
                System.out.printf("content length %d\n", entity.getContentLength());
                multipart = extractContentFromEntity(boundary, entity);
            }
        }
        if(mBodyGenerator != null){
            NStringEntity responseEntity = new NStringEntity(
                    mBodyGenerator.generateBody(target, multipart));
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            response.setEntity(responseEntity);
            return true;
        }

        return false;
    }

    private List<MultipartContainer> extractContentFromEntity(String boundary, HttpEntity entity) throws IOException {
        List<MultipartContainer> result = new ArrayList<>();

        byte[] boundaryArray = boundary.getBytes();
        byte[] rnrn = new byte[]{0x0D, 0x0A, 0x0D, 0x0A};
        byte[] rn = new byte[]{0x0D, 0x0A};
        byte[] end = new byte[]{0x2D, 0x2D};

        InputStream is = entity.getContent();
        byte[] buffer = new byte[is.available()];
        is.read(buffer);

        int offset = 0;

        int state = 0;
        int len = buffer.length;
        int startContentPos = -1;
        int endContentPos = -1;
        int pos = 0;
        int headerEnd = 0;
        String name = "";
        while (true) {
            if (startContentPos >= 0) {
                int buflen = endContentPos - startContentPos;
                if (buflen > 0) {
                    byte[] buf = new byte[buflen];
                    System.arraycopy(buffer, startContentPos, buf, 0, buflen);
                    result.add(new MultipartContainer(buf, name));
                    offset = endContentPos;
                }
            }
            // поиск начала блока
            pos = findBoundary(buffer, boundaryArray, offset);
            if (pos < 0) {
                break;
            }
            offset = pos + boundaryArray.length + 2;
            pos += boundaryArray.length + 2;
            state = STATE_READ_HEADERS;

            // поиск начала данных
            headerEnd = findBoundary(buffer, rnrn, offset);
            if (headerEnd < 0) {
                break;
            }
            offset = headerEnd + rnrn.length;
            name = readMultipartHeaders(buffer, pos, headerEnd);
            startContentPos = offset;
            state = STATE_READ_CONTENT;
            endContentPos = findBoundary(buffer, boundaryArray, offset);
            endContentPos -= 2;
        }
        return result;
    }

    private String getBoundary(Header[] headers) {
        String result = null;
        if (headers == null || headers.length < 1) {
            return null;
        }
        int pos = headers[0].getValue().indexOf('=');
        if (pos >= 0) {
            result = headers[0].getValue().substring(pos + 1);
        }
        return "--" + result;
    }

    private int findBoundary(byte[] arr, byte[] b, int startPos) {
        int cmp = 0;
        for (int i = startPos; i < arr.length - b.length; i++) {
            cmp = 0;
            for (int j = 0; j < b.length; j++) {
                if (arr[i + j] != b[j]) {
                    break;
                }
                cmp++;
            }
            if (cmp == b.length) {
                return i;
            }
        }
        return -1;
    }

    private String readMultipartHeaders(byte[] buf, int start, int end) {
        String name = "";
        int len = end - start + 1;
        byte[] b = new byte[len];
        System.arraycopy(buf, start, b, 0, len);
        String buffer = new String(b);
        String[] arr = buffer.split("\r\n");
        int pos = 0;
        int endPos = 0;
        int quote = 0;
        for (String s : arr) {
            if (s.startsWith("Content-Disposition:")) {
                String[] values = s.split(";");
                for (String headerValue : values) {
                    if (headerValue.trim().startsWith("name=")) {
                        pos = headerValue.indexOf('=');
                        endPos = headerValue.length() - 1;
                        quote = headerValue.indexOf('"', pos);
                        if (quote >= 0) {
                            pos = quote;
                            endPos = headerValue.indexOf('"', pos + 1);
                        }
                        if (pos >= 0) {
                            name = headerValue.substring(pos + 1, endPos);
                        }
                    }
                }
            }
        }
        return name;
    }
}