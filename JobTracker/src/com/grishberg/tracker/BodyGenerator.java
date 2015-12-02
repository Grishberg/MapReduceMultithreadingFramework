package com.grishberg.tracker;

import com.grishberg.common.job.IJobTracker;
import com.grishberg.httpserver.IBodyGenerator;
import com.grishberg.httpserver.MultipartContainer;

import java.util.List;

/**
 * Created by g on 29.11.15.
 */
public class BodyGenerator implements IBodyGenerator {
    public static final String FILE = "file";
    public static final String CLASS_PATH = "classPath";
    public static final String TARGET_INSEX = "/";
    public static final String TARGET_START = "/start";
    public static final String TARGET_STATUS = "/status";
    public static final String TARGET_UPLOAD = "/upload";
    IJobTracker mJobTracker;

    public BodyGenerator(IJobTracker jobTracker) {
        mJobTracker = jobTracker;
    }

    @Override
    public String generateBody(String target, List<MultipartContainer> multipart) {
        if (TARGET_INSEX.equals(target)) {
            return generateIndex();
        }
        if (TARGET_START.equals(target)) {
            return generateStartJob();
        }
        if (TARGET_STATUS.equals(target)) {
            return generateGetStatus();
        }
        if (TARGET_UPLOAD.equals(target)) {
            return generateUpload(multipart);
        }
        return null;
    }

    private String generateUpload(List<MultipartContainer> multipart) {
        byte[] data = null;
        String cp = null;
        int size = 0;
        for (MultipartContainer mc : multipart) {
            if (FILE.equals(mc.getName())) {
                data = mc.getData();
                size = data != null ? data.length : 0;
            } else if (CLASS_PATH.equals(mc.getName())) {
                cp = new String(mc.getData());
            }
        }
        if (mJobTracker != null) {
            mJobTracker.sendJob(cp, data);
        }

        StringBuilder sb = new StringBuilder();
        int port = mJobTracker.getHttpPort();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("    <head>\n");
        sb.append("        <title>File Upload</title>\n");
        sb.append("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        sb.append("        </meta>\n");
        sb.append("    </head>\n");
        sb.append("    <body>\n");
        sb.append(String.format("<h1>uploaded %d bytes</h1><br/>\n", size));
        sb.append(String.format("<br/><a href=\"http://localhost:%d/status\">get status</a>\n", port));
        sb.append(String.format("<br/><a href=\"http://localhost:%d/start\">start job</a>\n", port));
        sb.append("<br/>");
        sb.append("        <form method=\"POST\" action=\"upload\" enctype=\"multipart/form-data\" >\n");
        sb.append("            File:\n");
        sb.append("            <input type=\"file\" name=\"file\" /> <br/>\n");
        sb.append("            Select classPath: <input type=\"text\" name=\"classPath\"/><br/>");
        sb.append("            <br/>\n");
        sb.append("            <input type=\"submit\" value=\"Upload\" name=\"upload\"/>\n");
        sb.append("        </form>\n");
        sb.append("    </body>\n");
        sb.append("</html>");
        if (mJobTracker != null) {
            mJobTracker.sendGetStatus();
        }
        return sb.toString();
    }

    private String generateGetStatus() {
        StringBuilder sb = new StringBuilder();
        int port = mJobTracker.getHttpPort();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("    <head>\n");
        sb.append("        <title>File Upload</title>\n");
        sb.append("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        sb.append("        </meta>\n");
        sb.append("    </head>\n");
        sb.append("    <body>\n");
        sb.append("    Job started...<br/>\n");
        sb.append(String.format("<br/><a href=\"http://localhost:%d/status\">get status</a>\n", port));
        sb.append(String.format("<br/><a href=\"http://localhost:%d/start\">start job</a>\n", port));
        sb.append("<br/>");
        sb.append("        <form method=\"POST\" action=\"upload\" enctype=\"multipart/form-data\" >\n");
        sb.append("            File:\n");
        sb.append("            <input type=\"file\" name=\"file\" /> <br/>\n");
        sb.append("            Select classPath: <input type=\"text\" name=\"classPath\"/><br/>");
        sb.append("            <br/>\n");
        sb.append("            <input type=\"submit\" value=\"Upload\" name=\"upload\"/>\n");
        sb.append("        </form>\n");
        sb.append("    </body>\n");
        sb.append("</html>");
        if (mJobTracker != null) {
            mJobTracker.sendGetStatus();
        }
        return sb.toString();
    }

    private String generateStartJob() {
        StringBuilder sb = new StringBuilder();
        int port = mJobTracker.getHttpPort();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("    <head>\n");
        sb.append("        <title>File Upload</title>\n");
        sb.append("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        sb.append("        </meta>\n");
        sb.append("    </head>\n");
        sb.append("    <body>\n");
        sb.append("    Job started...<br/>\n");
        sb.append(String.format("<br/><a href=\"http://localhost:%d/status\">get status</a>\n", port));
        sb.append(String.format("<br/><a href=\"http://localhost:%d/start\">start job</a>\n", port));
        sb.append("<br/>");
        sb.append("        <form method=\"POST\" action=\"upload\" enctype=\"multipart/form-data\" >\n");
        sb.append("            File:\n");
        sb.append("            <input type=\"file\" name=\"file\" /> <br/>\n");
        sb.append("            Select classPath: <input type=\"text\" name=\"classPath\"/><br/>");
        sb.append("            <br/>\n");
        sb.append("            <input type=\"submit\" value=\"Upload\" name=\"upload\"/>\n");
        sb.append("        </form>\n");
        sb.append("    </body>\n");
        sb.append("</html>");
        if (mJobTracker != null) {
            mJobTracker.sendStartJobCmd();
        }

        return sb.toString();
    }

    private String generateIndex() {
        StringBuilder sb = new StringBuilder();
        int port = mJobTracker.getHttpPort();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("    <head>\n");
        sb.append("        <title>File Upload</title>\n");
        sb.append("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        sb.append("        </meta>\n");
        sb.append("    </head>\n");
        sb.append("    <body>\n");
        sb.append(String.format("   <br/><a href=\"http://localhost:%d/status\">get status</a>\n", port));
        sb.append(String.format("   <br/><a href=\"http://localhost:%d/start\">start job</a>\n", port));
        sb.append("<br/>");
        sb.append("        <form method=\"POST\" action=\"upload\" enctype=\"multipart/form-data\" >\n");
        sb.append("            File:\n");
        sb.append("            <input type=\"file\" name=\"file\" /> <br/>\n");
        sb.append("            Select classPath: <input type=\"text\" name=\"classPath\"/><br/>");
        sb.append("            <br/>\n");
        sb.append("            <input type=\"submit\" value=\"Upload\" name=\"upload\"/>\n");
        sb.append("        </form>\n");
        sb.append("    </body>\n");
        sb.append("</html>");

        return sb.toString();
    }
}
