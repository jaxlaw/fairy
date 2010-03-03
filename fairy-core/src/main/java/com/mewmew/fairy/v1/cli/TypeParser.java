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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TypeParser implements StringParser<TypeParser.TypedObject>
{
    public static TypedObject parseObject(String type, String value) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        if (type.equals("int"))
        {
            return new TypedObject(Integer.class, Integer.parseInt(value));
        }
        if (type.equals("float"))
        {
            return new TypedObject(Float.class, Float.parseFloat(value));
        }
        if (type.equals("long"))
        {
            return new TypedObject(Long.class, Long.parseLong(value));
        }
        if (type.equals("double"))
        {
            return new TypedObject(Double.class, Double.parseDouble(value));
        }
        if (type.equals("boolean"))
        {
            return new TypedObject(Boolean.class, Boolean.valueOf(value));
        }
        type = canonicalize(type);
        Class c = Class.forName(type);
        Method m = null;
        try
        {
            m = c.getMethod("valueOf", String.class);
        }
        catch (NoSuchMethodException e)
        {
            return new TypedObject(String.class, value);
        }
        return new TypedObject(c, m.invoke(null, value));
    }

    public static String canonicalize(String type)
    {
        if (type.equals("int"))
        {
            return int.class.getName();
        }
        if (type.equals("float"))
        {
            return float.class.getName();
        }
        if (type.equals("long"))
        {
            return long.class.getName();
        }
        if (type.equals("double"))
        {
            return double.class.getName();
        }
        if (type.equals("boolean"))
        {
            return boolean.class.getName();
        }
        if (!type.contains("."))
        {
            type = String.format("java.lang.%s", type);
        }
        return type;
    }

    @Override
    public TypedObject parse(String v)
    {
        String[] parts = v.split(":");
        try
        {
            return parseObject(parts[0], parts[1]);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static class TypedObject
    {
        Class type ;
        Object object ;

        public TypedObject(Class type, Object object)
        {
            this.type = type;
            this.object = object;
        }
    }
}
