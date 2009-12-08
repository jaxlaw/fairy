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
import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.pipe.PullObjectPipe;
import com.mewmew.fairy.v1.pipe.BaseObjectPipe;
import com.mewmew.fairy.v1.pipe.ObjectPipe;
import com.mewmew.fairy.v1.pipe.FirstNPipe;
import com.mewmew.fairy.v1.spell.Help;
import com.mewmew.fairy.v1.spell.LineSpell;

import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;

@Help(desc="print the first n lines")
public class Head extends LineSpell
{
    @Param
    private int numLines;

    public Head()
    {
    }

    public Head(InputStream in, int n)
    {
        super(in);
        this.numLines = n;
    }

    @Override
    protected PullObjectPipe<String, String> createPullPipe(ObjectPipe<String, String> pipe)
    {
        return new FirstNPipe<String, String>(pipe, numLines);
    }
}
