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
package com.mewmew.fairy.v1.cli;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

public class StringParsers
{
    private static final StringParsers SINGLETION = new StringParsers();

    public static StringParsers getInstance()
    {
        return SINGLETION;
    }

    private final Map<Class, StringParser> parsers;
    private final ReflectionTranslator magicalTranslator = new ReflectionTranslator();

    public StringParsers()
    {
        this.parsers = StringParsers.defaultTranslators();
    }

    public StringParsers addParser(StringParser t)
    {
        Type type = t.getClass().getGenericInterfaces()[0];
        if (type instanceof ParameterizedType) {
            parsers.put((Class) ((ParameterizedType) type).getActualTypeArguments()[0], t);
        }
        return this ;
    }

    public <T> T parse(Class<T> type, String v)
    {
        if (v == null) {
            return null ;
        }
        StringParser<T> translator = parsers.get(type);
        if (translator != null) {
            return translator.parse(v);
        }
        else {
            return magicalTranslator.translate(type, v);
        }
    }

    public static Map<Class, StringParser> defaultTranslators()
    {
        Map<Class, StringParser> map = new HashMap<Class, StringParser>();
        map.put(int.class, new StringParser<Integer>()
        {
            public Integer parse(String v)
            {
                return Integer.parseInt(v);
            }
        });
        map.put(double.class, new StringParser<Double>()
        {
            public Double parse(String v)
            {
                return Double.parseDouble(v);
            }
        });
        map.put(long.class, new StringParser<Long>()
        {
            public Long parse(String v)
            {
                return Long.parseLong(v);
            }
        });
        map.put(boolean.class, new StringParser<Boolean>()
        {
            public Boolean parse(String v)
            {
                return Boolean.parseBoolean(v);
            }
        });
        map.put(float.class, new StringParser<Float>()
        {
            public Float parse(String v)
            {
                return Float.parseFloat(v);
            }
        });
        map.put(byte.class, new StringParser<Byte>()
        {
            public Byte parse(String v)
            {
                return Byte.parseByte(v);
            }
        });
        map.put(short.class, new StringParser<Short>()
        {
            public Short parse(String v)
            {
                return Short.parseShort(v);
            }
        });
        map.put(char.class, new StringParser<Character>()
        {
            public Character parse(String v)
            {
                return v.length() == 0 ? ' ' : v.charAt(0);
            }
        });
        map.put(String.class, new StringParser<String>()
        {
            public String parse(String v)
            {
                return v;
            }
        });
        map.put(List.class, new StringParser<List<String>>()
        {
            public List<String> parse(String v)
            {
                return Arrays.asList(v.split(","));
            }
        });
        map.put(String[].class, new StringParser<String[]>()
        {
            public String[] parse(String v)
            {
                return v.split(",");
            }
        });
        map.put(File.class, new StringParser<File>()
        {
            public File parse(String v)
            {
                return new File(v);
            }
        });
         map.put(Pattern.class, new StringParser<Pattern>()
        {
            public Pattern parse(String v)
            {
                return Pattern.compile(v);
            }
        });
        return map;
    }

    public static Class findClass(String className)
    {
        Class c = PRIMITIVE_TYPES.get(className);
        if (c != null) {
            return c ;
        }
        if (!className.contains(".")) {
            className = "java.lang."+className ;
        }
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
        }
        return null;
    }

    public static class ReflectionTranslator
    {
        public <T> T translate(Class<T> to, String v)
        {
            try {
                Constructor<T> ctor = null;
                try {
                    ctor = to.getConstructor(String.class);
                }
                catch (NoSuchMethodException e) {
                }
                if (ctor != null) {
                    boolean wasInaccessible = !ctor.isAccessible();
                    if (wasInaccessible) {
                        ctor.setAccessible(true);
                    }
                    try {
                        return ctor.newInstance(v);
                    }
                    finally {
                        ctor.setAccessible(!wasInaccessible);
                    }
                }
                Method valueOf = to.getDeclaredMethod("valueOf", String.class);
                if (valueOf != null) {
                    return (T) valueOf.invoke(null, v);
                }
                throw new IllegalArgumentException("no magic works");
            }
            catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public static final Map<String, Class<?>> PRIMITIVE_TYPES = ImmutableMap.<String, Class<?>>builder()
        .put("int", int.class)
        .put("short", short.class)
        .put("byte", byte.class)
        .put("char", char.class)
        .put("long", long.class)
        .put("float", float.class)
        .put("double", double.class)
        .put("boolean", boolean.class)
        .build();

}
