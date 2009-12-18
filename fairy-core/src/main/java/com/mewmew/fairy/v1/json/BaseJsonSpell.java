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

import com.mewmew.fairy.v1.cli.Param;
import com.mewmew.fairy.v1.pipe.ObjectPipe;
import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.pipe.Sink;
import com.mewmew.fairy.v1.pipe.PipeUtil;
import com.mewmew.fairy.v1.pipe.Source;
import com.mewmew.fairy.v1.spell.PipeSpell;
import com.mewmew.fairy.v1.spell.Spell;
import com.mewmew.fairy.v1.book.Pipe;
import org.apache.commons.io.LineIterator;
import org.codehaus.jackson.map.MappingJsonFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;

public abstract class BaseJsonSpell<OutputType> extends PipeSpell implements Sink<OutputType>
{
    protected static final MappingJsonFactory factory = new MappingJsonFactory();

    Output<OutputType> output;
    ObjectPipe<Map<String, Object>, OutputType> pipe;

    @Param (desc = "whether input is read in via jackson streaming, otherwise expect ")
    boolean streaming ;

    @Param (desc = "input type", option="I")
    String inputType ;

    protected BaseJsonSpell()
    {
    }

    @Override
    public void process(InputStream in, OutputStream out)
    {
        try {
            Iterator<Map<String, Object>> iterator = null;

            if (inputType != null) {
                JsonRegistry registry = new JsonRegistry("inputs.json");
                Map<String, Map<String, Object>> map = registry.loadAsMap("name");
                Map<String, Object> json = map.get(inputType);
                Spell spell = Pipe.toSpell(((ArrayList<String>) json.get("pipe")).toArray(new String[0]));
                if (spell instanceof Source) {
                    // TODO : do some magic type variable checking here.
                    iterator = ((Source)spell).createIterator(in);
                }
                // TODO : any pipe that is a Sink<Map<String, Object>> should also work by using PipedOutputStream chaining... 
            }

            if (iterator == null) {
                if (streaming) {
                    // HACK : ugly workaround , replace me
                    iterator = (Iterator<Map<String, Object>>) (Object) new JsonArrayIterator<Map>(factory.createJsonParser(in), Map.class);
                }
                else {
                    iterator = new SimpleJsonIterator(new LineIterator(new InputStreamReader(in)));
                }
            }

            pipe = createPipe();
            output = createOutput(out);
            PipeUtil.process(iterator, pipe, output);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
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

    protected abstract ObjectPipe<Map<String, Object>, OutputType> createPipe();
}