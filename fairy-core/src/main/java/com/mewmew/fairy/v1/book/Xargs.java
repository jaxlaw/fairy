package com.mewmew.fairy.v1.book;

import com.mewmew.fairy.v1.spell.LineSpell;
import com.mewmew.fairy.v1.spell.Help;
import com.mewmew.fairy.v1.cli.Param;
import com.mewmew.fairy.v1.cli.Args;
import com.mewmew.fairy.v1.pipe.PullObjectPipe;
import com.mewmew.fairy.v1.pipe.MultiThreadedObjectPipe;
import com.mewmew.fairy.v1.pipe.Output;
import com.google.common.base.Joiner;

import java.io.IOException;

@Help(desc="execute command using input")
public class Xargs extends LineSpell
{
    @Param
    int threads ;
    @Args
    String cmd[];

    @Override
    protected PullObjectPipe<String, String> createPipe()
    {
        return new MultiThreadedObjectPipe<String, String>(threads){
            @Override
            public void _each(String input, Output<String> output) throws IOException
            {
                output.output(String.format("%s %s %s", Thread.currentThread(), Joiner.on(" ").join(cmd), input));
            }
        };
    }
}
