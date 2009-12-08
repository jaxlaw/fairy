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

import org.codehaus.jackson.map.MappingJsonFactory;

import java.io.IOException;
import java.util.Map;

public class TestJsonArrayIterator
{
    public static void main(String[] args) throws IOException
    {
        JsonArrayIterator<Integer> iter = new JsonArrayIterator<Integer>(new MappingJsonFactory().createJsonParser(
                "[1,2,3,4,5]"
        ), Integer.class);

        printIterator(iter);

        printIterator(new JsonArrayIterator<Map>(new MappingJsonFactory().createJsonParser(
                "{\"h\":1, \"items\":[{\"a\":1,\"b\":\"abc\"}, {\"a\":2,\"b\":3}], \"abc\":1}"
        ), Map.class));

    }

    private static <T> void printIterator(JsonArrayIterator<T> iter)
    {
        while (iter.hasNext()) {
            T integer = iter.next();
            System.out.println(integer);
        }
    }

}
