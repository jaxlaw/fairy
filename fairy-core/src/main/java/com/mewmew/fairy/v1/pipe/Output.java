package com.mewmew.fairy.v1.pipe;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.JsonGenerationException;

import java.io.IOException;

public interface Output<T>
{
    public void output(T obj) throws IOException;
    public void close();
}
