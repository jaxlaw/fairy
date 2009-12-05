package com.mewmew.fairy.v1.pipe;

import java.io.OutputStream;
import java.io.InputStream;

public interface Pipe
{
    void process(InputStream in, OutputStream out);
}
