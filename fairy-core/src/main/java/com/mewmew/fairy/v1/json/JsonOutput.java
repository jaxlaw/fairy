/*
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
*/
package com.mewmew.fairy.v1.json;

import com.mewmew.fairy.v1.pipe.Output;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

public class JsonOutput implements Output<Map<String, Object>>
{
    static final JsonFactory FACTORY = new JsonFactory();
    static final ObjectMapper MAPPER = new ObjectMapper();

    JsonGenerator jgen ;
    ObjectMapper mapper ;

    public JsonOutput(OutputStream out) throws IOException
    {
        this(FACTORY.createJsonGenerator(out, JsonEncoding.UTF8), MAPPER);
    }

    public JsonOutput(JsonGenerator jgen) throws IOException
    {
        this(jgen, MAPPER);
    }

    public JsonOutput(JsonGenerator jsonGenerator, ObjectMapper mapper) throws IOException
    {
        this.jgen = jsonGenerator;
        this.mapper = mapper;
        jgen.writeStartArray();
    }

    public void output(Map<String, Object> obj) throws IOException
    {
        mapper.writeValue(jgen, obj);
    }

    public void close() throws IOException
    {
        jgen.writeEndArray();
        jgen.close();
    }

    public JsonGenerator getJgen()
    {
        return jgen;
    }

    public ObjectMapper getMapper()
    {
        return mapper;
    }

    public static Output<Map<String, Object>> createOutput(OutputStream out, OutputFormat format) throws IOException
    {
        final JsonGenerator jsonGenerator = FACTORY.createJsonGenerator(out, JsonEncoding.UTF8);
        switch (format) {
            case PRETTY:
                jsonGenerator.useDefaultPrettyPrinter();
                break ;
            case LINE:
                jsonGenerator.setPrettyPrinter(new LinePrettyPrinter());
                break ;
        }
        return new JsonOutput(jsonGenerator, JsonOutput.MAPPER);
    }
}
