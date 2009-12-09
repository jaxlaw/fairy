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
import com.mewmew.fairy.v1.pipe.DefaultMapFunction;
import com.mewmew.fairy.v1.pipe.ObjectPipe;
import com.mewmew.fairy.v1.pipe.BaseObjectPipe;

import java.util.Map;

public class Json extends JsonSpell
{
    @Override
    protected ObjectPipe<Map<String, Object>, Map<String, Object>> createPipe()
    {
        return new BaseObjectPipe<Map<String, Object>, Map<String, Object>>(new DefaultMapFunction<Map<String, Object>>());
    }
}
