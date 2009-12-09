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
import com.mewmew.fairy.v1.cli.Param;

import java.io.OutputStream;
import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;

public abstract class JsonSpell extends BaseJsonSpell
{
    private static final JsonFactory factory = new JsonFactory();
    private static final ObjectMapper mapper = new ObjectMapper();
    private JsonOutput jsonOutput;

    @Param(option = "P", name = "pretty", desc = "pretty print")
    private boolean pretty;

    @Param ( desc = "print out one json object on each line")
    private boolean lineMode;

    @Override
    protected Output<Map<String, Object>> createOutput(OutputStream out) throws IOException
    {
        final JsonGenerator jsonGenerator = factory.createJsonGenerator(out, JsonEncoding.UTF8);
        if (pretty) {
            jsonGenerator.useDefaultPrettyPrinter();
        } else if (lineMode) {
            jsonGenerator.setPrettyPrinter(new LinePrettyPrinter());
        }
        jsonOutput = new JsonOutput(jsonGenerator, mapper);
        return jsonOutput;
    }


}
