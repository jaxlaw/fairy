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
package com.mewmew.fairy.v1.json.map;

import com.mewmew.fairy.v1.json.map.JsonMapFunction;
import com.mewmew.fairy.v1.pipe.Output;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public class Rename extends JsonMapFunction
{
    Map<String, String> mappings = new HashMap<String, String>();

    public Rename(String[] renames)
    {
        for (String rename : renames) {
            String part[] = rename.split(":");
            mappings.put(part[0], part[1]);
        }
    }

    public Rename rename(String from, String to)
    {
        mappings.put(from, to);
        return this ;
    }

    @Override
    public void each(Map<String, Object> input, Output<Map<String, Object>> mapOutput) throws IOException
    {
        for (Map.Entry<String, String> entry : mappings.entrySet()) {
            Object value = input.get(entry.getKey());
            if (value != null) {
                input.remove(entry.getKey());
                input.put(entry.getValue(), value);
            }
        }
    }
}
