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
package com.mewmew.fairy.v1.book;

import com.mewmew.fairy.v1.spell.LineSpell;
import com.mewmew.fairy.v1.spell.Help;
import com.mewmew.fairy.v1.cli.Param;
import com.mewmew.fairy.v1.cli.Args;
import com.mewmew.fairy.v1.pipe.PullObjectPipe;
import com.mewmew.fairy.v1.pipe.MultiThreadedObjectPipe;
import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.pipe.ObjectPipe;
import com.mewmew.fairy.v1.pipe.BaseObjectPipe;
import com.google.common.base.Joiner;

import java.io.IOException;

@Help(desc="execute command using input")
public class Xargs extends LineSpell
{
    @Param
    int threads ;
    @Args
    String cmd[];

    @Override
    protected ObjectPipe<String, String> createPipe()
    {
        return new MultiThreadedObjectPipe<String, String>(threads, new BaseObjectPipe<String, String>()
        {
            @Override
            public void each(String input, Output<String> output) throws IOException
            {
                output.output(String.format("%s %s %s", Thread.currentThread(), Joiner.on(" ").join(cmd), input));
            }
        });
    }
}
