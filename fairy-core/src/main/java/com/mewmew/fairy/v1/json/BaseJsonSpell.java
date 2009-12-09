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
import com.mewmew.fairy.v1.pipe.LineInputIterator;
import com.mewmew.fairy.v1.pipe.PullObjectPipe;
import com.mewmew.fairy.v1.pipe.ObjectPipe;
import com.mewmew.fairy.v1.pipe.PullObjectPipeWrapper;
import com.mewmew.fairy.v1.spell.PipeSpell;
import com.mewmew.fairy.v1.cli.Param;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Iterator;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.MappingJsonFactory;

public abstract class BaseJsonSpell<OutputType> extends PipeSpell
{
    protected static final MappingJsonFactory factory = new MappingJsonFactory();

    Output<OutputType> output;
    PullObjectPipe<Map<String, Object>, OutputType> pipe;

    @Param (desc = "whether input is read in via jackson streaming, otherwise expect ")
    boolean streamingInput ;

    protected BaseJsonSpell()
    {
    }

    public BaseJsonSpell(InputStream in)
    {
        super(in);
    }

    @Override
    public void process(InputStream in, OutputStream out)
    {
        try {
            Iterator<Map<String, Object>> iterator ;
            if (streamingInput) {
                // HACK : ugly workaround , replace me
                iterator = (Iterator<Map<String, Object>>) (Object) new JsonArrayIterator<Map>(factory.createJsonParser(in), Map.class);
            }
            else {
                iterator = new SimpleJsonIterator(new LineInputIterator(in));
            }
            (pipe = createPullPipe(createPipe())).process(iterator, output = createOutput(out));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void after()
    {
        if (output != null) {
            try {
                pipe.close(output);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        super.after();
    }

    protected PullObjectPipe<Map<String, Object>, OutputType> createPullPipe(ObjectPipe<Map<String, Object>, OutputType> pipe)
    {
        return new PullObjectPipeWrapper<Map<String, Object>,OutputType>(pipe);
    }

    protected abstract Output<OutputType> createOutput(OutputStream out) throws IOException;
    protected abstract ObjectPipe<Map<String, Object>, OutputType> createPipe();
}