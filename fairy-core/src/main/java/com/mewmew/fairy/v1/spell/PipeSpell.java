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
package com.mewmew.fairy.v1.spell;

import com.mewmew.fairy.v1.pipe.Pipe;
import com.mewmew.fairy.v1.cli.Param;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public abstract class PipeSpell extends Spell implements Pipe
{
    @Param (desc= "input file, default standard in")
    private File inputFile ;
    private InputStream in ;    

    protected PipeSpell()
    {
        in = null;
    }

    @Override
    public void before()
    {
        super.before();
        if (inputFile != null) {
            try {
                in = new FileInputStream(inputFile);
            }
            catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            in = System.in ;
        }
    }

    public InputStream getInputStream()
    {
        return in;
    }

    @Override
    public void cast()
    {
        process(in, getOutputStream());
    }

    public abstract void process(InputStream in, OutputStream out);
}
