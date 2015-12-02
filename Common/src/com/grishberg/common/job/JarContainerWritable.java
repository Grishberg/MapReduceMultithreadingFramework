package com.grishberg.common.job;

import com.grishberg.common.io.DataInContainer;
import com.grishberg.common.io.DataOutContainer;
import com.grishberg.common.io.Writable;

import java.io.IOException;

/**
 * Created by g on 14.11.15.
 */
public class JarContainerWritable implements Writable {
    private String className;
    private byte[] jarBody;

    public JarContainerWritable() {
        className = null;
        jarBody = null;
    }

    public JarContainerWritable(String className, byte[] jarBody) {
        this.className = className;
        this.jarBody = jarBody;
    }

    public String getJarClassName() {
        return className;
    }

    public byte[] getJarBody() {
        return jarBody;
    }

    @Override
    public void write(DataOutContainer out) {
        out.writeString(className);
        out.writeByteArray(jarBody);
    }

    @Override
    public void readFields(DataInContainer in) {
        className = in.getString();
        jarBody = in.getByteArray();
    }
}
