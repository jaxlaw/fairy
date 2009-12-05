package com.mewmew.fairy.v1.book;

import com.mewmew.fairy.v1.cli.AnnotatedCLI;
import com.mewmew.fairy.v1.cli.Param;
import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.spell.Help;

import java.io.InputStream;
import java.util.Iterator;

@Help(desc="print the first n lines")
public class Head extends Cat
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
    public void process(Iterator<String> input, Output<String> output)
    {
        int count = 0;
        while (input.hasNext()) {
            output.output(input.next());
            if (++count > numLines) {
                break;
            }
        }
    }
}
