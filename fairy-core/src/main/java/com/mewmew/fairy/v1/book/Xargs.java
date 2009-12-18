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

import com.mewmew.fairy.v1.spell.LineSpell;
import com.mewmew.fairy.v1.spell.Help;
import com.mewmew.fairy.v1.cli.Param;
import com.mewmew.fairy.v1.cli.Args;
import com.mewmew.fairy.v1.pipe.MultiThreadedObjectPipe;
import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.pipe.ObjectPipe;
import com.mewmew.fairy.v1.pipe.BaseObjectPipe;
import com.mewmew.fairy.v1.map.MapFunction;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.LineIterator;

@Help(desc = "execute command using input")
public class Xargs extends LineSpell implements MapFunction<String, String>
{
    @Param(desc="number of parallel processes to use")
    int processes;
    @Param(option = "I", name = "token", defaultValue = "\\{\\}", desc="token to replace, default {}")
    String token;
    @Args (desc="the command to run")
    String cmd[];

    @Override
    public void before()
    {
        super.before();
        if (StringUtils.equals("{}", token)) {
            token = "\\{\\}";
        }
    }

    @Override
    protected ObjectPipe<String, String> createPipe()
    {
        final BaseObjectPipe<String, String> baseObjectPipe = new BaseObjectPipe<String, String>(this);
        if (processes > 0) {
            return new MultiThreadedObjectPipe<String, String>(processes, baseObjectPipe);
        }
        return baseObjectPipe;
    }

    @Override
    public void each(String input, Output<String> output) throws IOException
    {
        String c[] = new String[cmd.length];
        for (int i = 0; i < cmd.length; i++) {
            if (StringUtils.contains(cmd[i], token) || StringUtils.contains(cmd[i], "{}")) {
                c[i] = cmd[i].replaceAll(token, input);
            }
            else {
                c[i] = cmd[i];
            }
        }
        if (c.length > 0) {
            try {
                exec(null, c, true, output);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void exec(Map<String, String> env, String cmd[], boolean redirectError, Output<String> output) throws IOException, InterruptedException
    {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        if (env != null) {
            pb.environment().putAll(env);
        }
        if (redirectError) {
            pb.redirectErrorStream(true);
        }
        final Process p = pb.start();
        if (!redirectError) {
            new Thread(new Runnable()
            {
                public void run()
                {
                    try {
                        LineIterator err = new LineIterator(new InputStreamReader(p.getErrorStream()));
                        while (err.hasNext()) {
                            err.next();
                        }
                    }
                    finally {
                    }
                }
            }).start();
        }
        LineIterator out = new LineIterator(new InputStreamReader(p.getInputStream()));
        while (out.hasNext()) {
            output.output(out.nextLine());
        }
        int code = p.waitFor();
        if (code != 0) {
            throw new RuntimeException(String.format("return != 0, code = %d", code));
        }
    }
}
