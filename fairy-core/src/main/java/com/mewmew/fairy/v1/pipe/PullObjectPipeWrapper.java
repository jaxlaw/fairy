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

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PullObjectPipeWrapper<InputType, OutputType> implements PullObjectPipe<InputType, OutputType>
{
    protected final ObjectPipe<InputType, OutputType> delegate;

    public PullObjectPipeWrapper(ObjectPipe<InputType, OutputType> delgate)
    {
        this.delegate = delgate;
    }

    public final void open(Output<OutputType> output) throws IOException
    {
        delegate.open(output);
    }

    public final void each(InputType input, Output<OutputType> output) throws IOException
    {
        delegate.each(input, output);
    }

    public final void close(Output<OutputType> output) throws IOException
    {
        delegate.close(output);
    }

    public void process(Iterator<InputType> input, Output<OutputType> output) throws IOException
    {
        while (input.hasNext()) {
            try {
                each(input.next(), output);
            }
            catch (NoSuchElementException e) {                
            }
        }
    }
}
