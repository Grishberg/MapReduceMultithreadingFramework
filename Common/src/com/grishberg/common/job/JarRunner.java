package com.grishberg.common.job;

import com.grishberg.common.io.DataInContainer;
import com.grishberg.common.io.Writable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.Attributes;
import java.util.zip.CRC32;

/**
 * A class loader for loading jar files, both local and remote.
 */
public class JarRunner extends URLClassLoader {
    public static final String METHOD_MAP = "map";
    public static final String METHOD_REDUCE = "reduce";
    public static final String METHOD_INIT_REDUCE = "initReduce";
    private URL url;
    byte[] params;
    private String className;
    private DataInContainer parameter;
    private IJobClient jobClient;
    private IJobTracker jobTracker;
    private Thread mThread;
    private String mMethod;
    private Object mInstance;
    private Method mSendResultMethod;

    public static JarRunner newInstance() throws MalformedURLException {
        URL urls[] = {};
        JarRunner runner = new JarRunner(urls);
        return runner;
    }

    public static boolean saveJar(byte[] data) {
        String fileName = getJobPath();
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        System.out.println(String.format("crc32: %08X", crc32.getValue()));
        try {
            Path path = Paths.get(fileName);
            Files.write(path, data); //creates, overwrites
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public JarRunner(URL[] urls) {
        super(urls);
    }

    public void stop() {
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
    }

    public void addFile(String path) throws MalformedURLException {
        String urlPath = "jar:file://" + path + "!/";
        addURL(new URL(urlPath));
    }

    public boolean runMap(String className, DataInContainer parameter, IJobClient parent) {
        this.className = className;
        this.parameter = parameter;
        this.jobClient = parent;
        mMethod = METHOD_MAP;
        if (mThread != null && mThread.isAlive()) {
            System.out.println("alreadyRunning");
            mThread.interrupt();
        }
        mThread = new Thread(mRunJobRunnable);
        mThread.start();

        return true;
    }

    public boolean runReduce(String className, DataInContainer parameter, IJobTracker parent) {
        this.className = className;
        this.parameter = parameter;
        this.jobTracker = parent;
        mMethod = METHOD_INIT_REDUCE;
        if (mThread != null && mThread.isAlive()) {
            System.out.println("alreadyRunning");
            mThread.interrupt();
        }
        mThread = new Thread(mRunJobRunnable);
        mThread.start();

        return true;
    }

    public void sendResult(DataInContainer in) {
        if (mInstance != null && mSendResultMethod != null) {
            try {
                mSendResultMethod.invoke(mInstance, new Object[]{in});
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private Runnable mRunJobRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL urls[] = {};
                addFile(getJobPath());
                System.out.println("Second attempt...cn=" + className);
                Class c = loadClass(className);
                if (c != null) {
                    mInstance = c.newInstance();
                    Method m = null;
                    if (METHOD_MAP.equals(mMethod)) {
                        m = c.getMethod(mMethod, new Class[]{DataInContainer.class, IJobClient.class});
                    } else if (METHOD_INIT_REDUCE.equals(mMethod)) {
                        m = c.getMethod(mMethod, new Class[]{IJobTracker.class});
                        mSendResultMethod = c.getMethod(METHOD_REDUCE, new Class[]{DataInContainer.class});
                        mSendResultMethod.setAccessible(true);
                    }
                    m.setAccessible(true);
                    int mods = m.getModifiers();
                    if (m.getReturnType() != void.class
                            || !Modifier.isPublic(mods)) {
                        throw new NoSuchMethodException(mMethod);
                    }
                    try {

                        if (METHOD_MAP.equals(mMethod)) {
                            m.invoke(mInstance, new Object[]{parameter, jobClient});
                            if (jobClient != null) {
                                jobClient.onJobEnded();
                            }
                        } else if (METHOD_INIT_REDUCE.equals(mMethod)) {
                            m.invoke(mInstance, new Object[]{jobTracker});
                            if (jobTracker != null) {
                                jobTracker.onJobEnded();
                            }
                        }
                    } catch (IllegalAccessException e) {
                        // This should not happen, as we have disabled access checks
                    }
                }
                System.out.println("Success!");
            } catch (Exception ex) {
                System.out.println("Failed.");
                ex.printStackTrace();
                if (jobClient != null) {
                    jobClient.onJobFailed();
                }
            }
        }
    };

    public static String getJobPath() {
        String localPath = getLocalPath();
        return localPath + JobClientConst.JOB_CACHE_NAME;
    }

    private static String getLocalPath() {
        String path = "/tmp";
        try {
            path = JarRunner.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * Returns the name of the jar file main class, or null if no "Main-Class"
     * manifest attributes was defined.
     */
    public String getMainClassName() throws IOException {
        URL u = new URL("jar", "", url + "!/");
        JarURLConnection uc = (JarURLConnection) u.openConnection();
        Attributes attr = uc.getMainAttributes();
        return attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
    }

    /**
     * Invokes the application in this jar file given the name of the main class
     * and an array of arguments. The class must define a static method "main"
     * which takes an array of String arguemtns and is of return type "void".
     *
     * @param name the name of the main class
     * @param args the arguments for the application
     * @throws ClassNotFoundException    if the specified class could not be found
     * @throws NoSuchMethodException     if the specified class does not contain a "main" method
     * @throws InvocationTargetException if the application raised an exception
     */
    public void invokeClass(String name, String[] args)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException {
        Class c = loadClass(name);
        Method m = c.getMethod("main", new Class[]{args.getClass()});
        m.setAccessible(true);
        int mods = m.getModifiers();
        if (m.getReturnType() != void.class || !Modifier.isStatic(mods)
                || !Modifier.isPublic(mods)) {
            throw new NoSuchMethodException("main");
        }
        try {
            m.invoke(null, new Object[]{args});
        } catch (IllegalAccessException e) {
            // This should not happen, as we have disabled access checks
        }
    }
}