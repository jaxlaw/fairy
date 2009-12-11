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

import com.mewmew.fairy.v1.cli.Param;
import com.mewmew.fairy.v1.cli.StringParser;
import com.mewmew.fairy.v1.cli.ParserProvider;
import com.mewmew.fairy.v1.cli.AnnotatedCLI;
import com.mewmew.fairy.v1.cli.Args;

import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * The base class for all spells, this defines the common -o -h and -a options. 
 */
public abstract class Spell implements ParserProvider , Runnable
{
    @Param (desc = "output file, default standard out")
    private File outputFile ;
    @Param (desc = "append to output file", defaultValue = "false")
    private boolean append = false;
    @Param (desc = "print help", defaultValue = "false")
    private boolean help = false;

    private OutputStream out ;

    public void before()
    {
        if (outputFile != null) {
            try {
                out = new FileOutputStream(outputFile, append);
            }
            catch (FileNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        else {
            out = System.out ;
        }
    }

    public void after()
    {
        try {            
            out.close();
        }
        catch (IOException e) {
        }
    }

    public void run()
    {
        before();
        cast();
        after();
    }

    protected OutputStream getOutputStream()
    {
        return this.out;
    }
    
    public abstract void cast();

    public boolean isHelp()
    {
        return help;
    }

    public void printHelp(AnnotatedCLI cli, String msg)
    {
        Help annotation = getClass().getAnnotation(Help.class);
        String spellName = getClass().getSimpleName().toLowerCase();
        System.err.printf("%s : %s\n%s : %s\n\n", "Spell", spellName, "Description",
                annotation != null ? annotation.desc() : spellName);
        cli.printHelp(msg);
        Field f = cli.getArgs(getClass());
        if (f != null) {
            Args args = f.getAnnotation(Args.class);
            if (args != null && !args.desc().isEmpty()) {
                System.out.printf("  <args>\t\t %s\n", args.desc());
            }
        }
    }

    public StringParser[] getParsers()
    {
        return null;
    }
}
