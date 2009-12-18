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
import java.util.Iterator;
import java.util.HashMap;
import java.io.IOException;

public class Join extends JsonMapFunction
{
    Map<Object, Map<String, Object>> joinTable ;
    private final String joinKey;

    public Join(Map<Object, Map<String, Object>> joinTable, String joinKey)
    {
        this.joinTable = joinTable;
        this.joinKey = joinKey;
    }

    public Join(Iterator<Map<String, Object>> iter, String joinKey)
    {
        this.joinKey = joinKey;
        this.joinTable = new HashMap<Object, Map<String, Object>>();
        while (iter.hasNext()) {
            Map<String, Object> objectMap = iter.next();
            Object joinValue = objectMap.get(joinKey) ;
            joinTable.put(joinValue, objectMap);
        }
    }
    
    @Override
    public void each(Map<String, Object> input, Output<Map<String, Object>> mapOutput) throws IOException
    {
        Object key = input.get(joinKey);
        if (key != null) {
            Map<String, Object> join = joinTable.get(key);
            if (join != null) {
                merge(join, input);
            }
        }
    }

    private void merge(Map<String, Object> from, Map<String, Object> to)
    {
        for (Map.Entry<String, Object> entry : from.entrySet()) {
            if (!to.containsKey(entry.getKey())) {
                to.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
