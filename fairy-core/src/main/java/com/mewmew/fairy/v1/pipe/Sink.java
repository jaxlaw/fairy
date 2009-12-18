package com.mewmew.fairy.v1.pipe;

import java.io.OutputStream;
import java.io.IOException;

public interface Sink<T>
{
    Output<T> createOutput(OutputStream out) throws IOException;    
}
