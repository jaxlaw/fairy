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

import com.mewmew.fairy.v1.cli.Param;
import com.mewmew.fairy.v1.cli.StringParsers;
import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.pipe.BaseObjectPipe;
import com.mewmew.fairy.v1.pipe.ObjectPipe;
import com.mewmew.fairy.v1.pipe.Source;
import com.mewmew.fairy.v1.pipe.MappingIterator;
import com.mewmew.fairy.v1.spell.Help;
import com.mewmew.fairy.v1.spell.BaseLineSpell;
import com.mewmew.fairy.v1.map.MapFunction;
import com.mewmew.fairy.v1.json.JsonOutput;
import com.mewmew.fairy.v1.json.OutputFormat;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.LineIterator;

@Help(desc = "cut line into JSON")
public class Cut extends BaseLineSpell<Map<String, Object>>
        implements MapFunction<String, Map<String, Object>>, Source<Map<String, Object>>
{
    @Param
    private String delimiter = "\t+";

    @Param
    private Pattern regex;

    private String as[] ;
    private Class types[] ;

    @Param(option = "O", name = "format", desc = "PRETTY, LINE, COMPACT", defaultValue = "LINE")
    private OutputFormat outputFormat = OutputFormat.LINE;

    public Cut(@Param(desc = "comman separated list of column names", option="as", name="as") String as[])
    {
        if (as != null) {
            this.as = as;
            this.types = new Class[as.length];
            for (int i = 0; i < as.length; i++) {
                String a = as[i];
                String parts[] = a.split(":");
                if (parts.length == 2) {
                    this.as[i] = parts[0];
                    types[i] = StringParsers.findClass(parts[1]);
                }
            }
        }
    }

    public Cut(String delimiter, String[] as)
    {
        this(as);
        this.delimiter = delimiter;
    }

    public Cut(Pattern regex, String[] as)
    {
        this(as);
        this.regex = regex;
    }

    @Override
    public void before()
    {
        super.before();
    }

    @Override
    public Output<Map<String, Object>> createOutput(OutputStream out) throws IOException
    {
        return JsonOutput.createOutput(out, outputFormat);
    }

    @Override
    protected ObjectPipe<String, Map<String, Object>> createPipe()
    {
        return new BaseObjectPipe<String, Map<String, Object>>(this);
    }

    public void each(String input, Output<Map<String, Object>> mapOutput) throws IOException
    {
        if (regex != null) {
            Matcher matcher = regex.matcher(input);
            if (matcher.matches()) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    putValue(map, i-1, matcher.group(i));
                }
                if (!map.isEmpty()) {
                    mapOutput.output(map);
                }
            }
        }
        else {
            String[] values = input.split(delimiter);
            Map<String, Object> map = new HashMap<String, Object>();
            if (values.length > 0) {
                for (int i = 0; i < values.length; i++) {
                    putValue(map, i, values[i]);
                }
                mapOutput.output(map);
            }
        }
    }

    private void putValue(Map<String, Object> map, int i, String value)
    {
        if (as != null && as.length > i) {
            map.put(as[i], types[i] != null ? StringParsers.getInstance().parse(types[i], value) : value);
        }
        else {
            map.put("c" + i, value);
        }
    }

    public Iterator<Map<String, Object>> createIterator(InputStream in)
    {
        return new MappingIterator((Iterator<String>)new LineIterator(new InputStreamReader(in)), this);
    }

    public static void main(String[] args) throws FileNotFoundException
    {
        Iterator<Map<String, Object>> iter = new Cut(null).createIterator(new FileInputStream("/Users/jax/.gconsole/caches/gonsole.xno.ningops.net.txt"));
        while (iter.hasNext()) {
            Map<String, Object> map = iter.next();
            System.out.println(map);
        }
    }
}