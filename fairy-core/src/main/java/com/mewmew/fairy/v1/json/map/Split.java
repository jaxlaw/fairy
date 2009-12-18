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
import java.util.List;
import java.util.Arrays;
import java.io.IOException;

public class Split extends JsonMapFunction
{
    final List<String> columns ;
    final String delimiter ;

    public Split(String[] columns, String delimiter)
    {
        this.delimiter = delimiter;
        this.columns = Arrays.asList(columns);
    }

    public Split split(String column)
    {
        this.columns.add(column);
        return this ;
    }

    @Override
    public void each(Map<String, Object> input, Output<Map<String, Object>> mapOutput) throws IOException
    {
        for (String column : columns) {
            Object value = input.get(column);
            if (value != null) {
                String part[] = value.toString().split(delimiter);
                int i = 0 ;
                for (String p : part) {
                    input.put(column+i, part[i++]);    
                }
            }
        }
    }
}
