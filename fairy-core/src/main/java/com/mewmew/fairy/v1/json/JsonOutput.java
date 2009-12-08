package com.mewmew.fairy.v1.json;

import com.mewmew.fairy.v1.pipe.Output;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class JsonOutput implements Output<Map<String, Object>>
{
    JsonGenerator jgen ;
    ObjectMapper mapper ;

    public void output(Map<String, Object> obj) throws IOException
    {
        mapper.writeValue(jgen, obj);

    }

    public void close()
    {

    }
}
