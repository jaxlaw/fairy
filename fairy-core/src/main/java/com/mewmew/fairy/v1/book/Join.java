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

import com.mewmew.fairy.v1.json.JsonSpell;
import com.mewmew.fairy.v1.json.JsonArrayIterator;
import com.mewmew.fairy.v1.json.SimpleJsonIterator;
import com.mewmew.fairy.v1.map.DefaultMapFunction;
import com.mewmew.fairy.v1.pipe.ObjectPipe;
import com.mewmew.fairy.v1.pipe.BaseObjectPipe;
import com.mewmew.fairy.v1.pipe.LineInputIterator;
import com.mewmew.fairy.v1.spell.Help;
import com.mewmew.fairy.v1.cli.Param;

import java.util.Map;
import java.io.File;
import java.io.IOException;

@Help(desc="hash join JSON stream with another json file")
public class Join extends JsonSpell
{
    @Param (desc="join json file")
    File join ;
    @Param (desc="join key")
    String key ;

    public Join()
    {
    }

    public Join(File join, String key)
    {
        this.join = join;
        this.key = key;
    }

    @Override
    protected ObjectPipe<Map<String, Object>, Map<String, Object>> createPipe()
    {
        try {
            return new BaseObjectPipe<Map<String, Object>, Map<String, Object>>(
                new com.mewmew.fairy.v1.json.map.Join(new SimpleJsonIterator(new LineInputIterator(join)), key));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
