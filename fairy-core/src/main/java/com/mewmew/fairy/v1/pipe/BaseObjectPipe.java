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
package com.mewmew.fairy.v1.pipe;

import java.util.Iterator;
import java.io.IOException;

public class BaseObjectPipe<InputType, OutputType> implements ObjectPipe<InputType, OutputType>
{
    private final MapFunction<InputType, OutputType> mapper ;

    public BaseObjectPipe(MapFunction<InputType, OutputType> mapper)
    {
        this.mapper = mapper;
    }

    public void each(InputType s, Output<OutputType> output) throws IOException
    {
        this.mapper.each(s, output);
    }

    public void open(Output<OutputType> outputTypeOutput) throws IOException
    {

    }

    public void close(Output<OutputType> outputTypeOutput) throws IOException
    {
        outputTypeOutput.close();
    }
}
