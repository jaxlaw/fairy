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

import com.mewmew.fairy.v1.pipe.LineInputIterator;

import java.util.Map;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.IOException;
import java.io.File;

import org.codehaus.jackson.map.ObjectMapper;

public class SimpleJsonIterator implements Iterator<Map<String, Object>>
{
    private final LineInputIterator delegate;
    private final ObjectMapper mapper = new ObjectMapper();

    public SimpleJsonIterator(LineInputIterator delegate)
    {
        this.delegate = delegate;
    }

    public boolean hasNext()
    {
        return delegate.hasNext();
    }

    public Map<String, Object> next()
    {
        Map<String, Object> map = null ;
        do {
            String line = delegate.next();
            try {
                map = mapper.readValue(line, Map.class);
                return map;
            }
            catch (IOException e) {
            }
        }
        while(map == null && delegate.hasNext());
        throw new NoSuchElementException();
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
