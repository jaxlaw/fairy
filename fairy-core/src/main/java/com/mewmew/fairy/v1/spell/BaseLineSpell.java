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
package com.mewmew.fairy.v1.spell;

import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.pipe.LineInputIterator;
import com.mewmew.fairy.v1.pipe.PullObjectPipe;
import com.mewmew.fairy.v1.pipe.ObjectPipe;
import com.mewmew.fairy.v1.pipe.PullObjectPipeWrapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public abstract class BaseLineSpell<OutputType> extends PipeSpell
{
    Output<OutputType> output;
    PullObjectPipe<String, OutputType> pipe;

    protected BaseLineSpell()
    {
    }

    public BaseLineSpell(InputStream in)
    {
        super(in);
    }

    @Override
    public void process(InputStream in, OutputStream out)
    {
        try {
            (pipe = createPullPipe(createPipe())).process(new LineInputIterator(in), output = createOutput(out));
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

    protected PullObjectPipe<String, OutputType> createPullPipe(ObjectPipe<String, OutputType> pipe)
    {
        return new PullObjectPipeWrapper<String,OutputType>(pipe);
    }
    
    protected abstract Output<OutputType> createOutput(OutputStream out);
    protected abstract ObjectPipe<String, OutputType> createPipe();
}
