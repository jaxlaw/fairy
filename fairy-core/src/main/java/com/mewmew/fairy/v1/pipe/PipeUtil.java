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
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PipeUtil
{
    public static <InputType, OutputType> void process(Iterator<InputType> input, MapFunction<InputType, OutputType> map, Output<OutputType> output) throws IOException
    {
        while (input.hasNext()) {
            try {
                map.each(input.next(), output);
            }
            catch (NoSuchElementException e) {                
            }
        }
    }

    public static <InputType, OutputType> void processFirstN(
            Iterator<InputType> input, MapFunction<InputType, OutputType> map, Output<OutputType> output, int numLines) throws IOException
    {
        int count = 0;
        while (input.hasNext()) {
            map.each(input.next(), output);
            if (++count > numLines) {
                break;
            }
        }
    }
}
