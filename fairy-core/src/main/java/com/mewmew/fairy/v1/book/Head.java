package com.mewmew.fairy.v1.book;

import com.mewmew.fairy.v1.cli.Param;
import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.pipe.PullObjectPipe;
import com.mewmew.fairy.v1.pipe.BaseObjectPipe;
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
    protected PullObjectPipe<String, String> createPipe()
    {
        return new BaseObjectPipe<String, String>()
        {
            @Override
            public void process(Iterator<String> input, Output<String> output) throws IOException
            {
                int count = 0;
                while (input.hasNext()) {
                    output.output(input.next());
                    if (++count > numLines) {
                        break;
                    }
                }
            }
        };
    }
}