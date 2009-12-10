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

import com.mewmew.fairy.v1.map.MapFunction;

import java.io.IOException;

public class OutputMapFunctionWrapper<I, O> implements Output<I>
{
    private final MapFunction<I, O> mapFunction;
    private final Output<O> output;

    private OutputMapFunctionWrapper(MapFunction<I, O> mapFunction, Output<O> output)
    {
        this.mapFunction = mapFunction;
        this.output = output;
    }

    public void output(I obj) throws IOException
    {
        this.mapFunction.each(obj, output);
    }

    public void close() throws IOException
    {
        if (this.output != null) {
            this.output.close();
        }
    }

    public static <I, O> OutputMapFunctionWrapper<I, O> chain(MapFunction<I, O> mapFunction, Output<O> output)
    {
        return new OutputMapFunctionWrapper<I,O>(mapFunction, output);
    }
}
