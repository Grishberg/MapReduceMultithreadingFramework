package com.grishberg;

import com.grishberg.common.io.DataInContainer;
import com.grishberg.common.io.DataOutContainer;
import com.grishberg.common.io.Writable;

import java.io.IOException;

/**
 * Created by g on 13.11.15.
 */
public class ExampleWritable implements Writable {
    String str;

    public ExampleWritable(DataInContainer in) {
        if (in == null) return;
        str = in.getString();
    }

    public ExampleWritable(String str) {
        this.str = str;
    }

    @Override
    public void write(DataOutContainer out) throws IOException {
        if (out == null) return;
        out.writeString(str);
    }

    @Override
    public void readFields(DataInContainer in) throws IOException {
        str = in.getString();
    }
}
