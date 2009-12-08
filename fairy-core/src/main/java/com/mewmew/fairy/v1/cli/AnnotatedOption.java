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

import com.mewmew.fairy.v1.cli.Param;

import java.lang.reflect.Field;

class AnnotatedOption
{
    final Class clazz ;
    final Field field ;
    final Param param ;
    String opt , name;

    AnnotatedOption(Class clazz, Field field, Param param)
    {
        this.clazz = clazz;
        this.field = field;
        this.param = param;

        if (param.option().isEmpty()) {
            opt = field.getName().substring(0,1).toLowerCase();
        }
        else {
            opt = param.option() ;
        }

        if (param.name().isEmpty()) {
            name = field.getName().toLowerCase();
        }
        else {
            name = param.name() ;
        }
    }

    public String toString()
    {
        return String.format("AnnotatedOption(%s %s %s %s)", clazz, field, opt, name);
    }

    public Param getParam()
    {
        return param;
    }

    public String getOpt()
    {
        return opt;
    }

    public String getName()
    {
        return name;
    }

    public void setOpt(String s)
    {
        opt = s ;
    }
}
