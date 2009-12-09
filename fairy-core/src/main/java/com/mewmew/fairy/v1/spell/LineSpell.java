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

import com.mewmew.fairy.v1.pipe.BaseObjectPipe;
import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.pipe.ObjectPipe;
import com.mewmew.fairy.v1.map.DefaultMapFunction;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class LineSpell extends BaseLineSpell<String>

{
    public LineSpell()
    {
    }

    public LineSpell(InputStream in)
    {
        super(in);
    }

    @Override
    protected ObjectPipe<String, String> createPipe()
    {
        return new BaseObjectPipe<String, String>(new DefaultMapFunction<String>());
    }

    @Override
    protected Output<String> createOutput(OutputStream out)
    {
        final PrintStream ps = out instanceof PrintStream ? (PrintStream) out : new PrintStream(out);
        return new Output<String>()
        {
            public void output(String obj)
            {
                ps.println(obj);
            }

            public void close()
            {
                ps.flush();
                ps.close();
            }
        };
    }
}
