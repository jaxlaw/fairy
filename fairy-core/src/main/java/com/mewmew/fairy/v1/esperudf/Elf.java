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
package com.mewmew.fairy.v1.esperudf;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * Elf is yet another magical creature that provides a bunch of useful magic ( registered with esper as UDF ). 
 */
public class Elf
{
    public static String toString(Object obj)
    {
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }


    public static long toLong(Object value)
    {
        return toLong(value, 0L);
    }

    public static long toLong(Object value, long defaultValue)
    {

        try {
            return Long.parseLong(value.toString());
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double toDouble(Object value, double defaultValue)
    {

        try {
            return Double.parseDouble(value.toString());
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double toDouble(Object value)
    {
        return toDouble(value, 0d);
    }

    public static Object nvl(Object value, Object defaultValue)
    {
        return value == null ? defaultValue : value;
    }

    public static Object decode(Object value, Object... args)
    {
        if (args.length % 2 != 1 || args.length < 2) {
            throw new IllegalArgumentException("not enough arguments");
        }
        if (value == null) {
            return null;
        }
        for (int i = 0; i < args.length - 2; i += 2) {
            if (value.equals(args[i])) {
                return args[i + 1];
            }
        }
        return args[args.length - 1];
    }

    public static DateTime toDate(String v)
    {
        return toDate(v, null);
    }

    public static DateTime toDate(String v, String defaultValue)
    {
        try {
            return new DateTime(v);
        }
        catch (Exception e) {
            return defaultValue == null ? new DateTime() : toDate(defaultValue, null);
        }
    }

    public static String trunc(String dateStr, String trunc)
    {
        return toDate(dateStr).toString(getFormatter(trunc));
    }

    private static DateTimeFormatter getFormatter(String trunc)
    {
        DateTimeFormatter f = formatters.get(trunc);
        if (f != null) {
            return f ;
        }
        else return ISODateTimeFormat.basicDateTimeNoMillis() ;
    }

    public static String replaceAll(String s, String regex, String replacement)
    {
        return s.replaceAll(regex, replacement) ;
    }

    public static String substring(String s, int idx, int end)
    {
        return s.substring(idx, end) ;
    }

    public static String substring(String s, int idx)
    {
        return s.substring(idx) ;
    }

    public static String split(String s, String regex, int idx)
    {
        return s.split(regex)[idx] ;
    }

    static DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder() ;
    static Map<String, DateTimeFormatter> formatters = ImmutableMap.<String, DateTimeFormatter>builder()
        .put("YY", builder.appendYear(4,4).toFormatter())
        .put("MM", builder.appendLiteral("-").appendMonthOfYear(2).toFormatter())
        .put("DD", builder.appendLiteral("-").appendDayOfMonth(2).toFormatter())
        .put("HH", builder.appendLiteral("T").appendHourOfDay(2).toFormatter())
        .put("MI", builder.appendLiteral(":").appendMinuteOfHour(2).toFormatter())
        .put("SS", builder.appendLiteral(":").appendSecondOfMinute(2).toFormatter())
        .build();

}
