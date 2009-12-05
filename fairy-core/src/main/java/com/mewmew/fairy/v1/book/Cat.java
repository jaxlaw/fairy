package com.mewmew.fairy.v1.book;

import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.spell.BaseLineSpell;
import com.mewmew.fairy.v1.spell.Help;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

@Help(desc="cat a file to output")
public class Cat extends BaseLineSpell<String>
{
    public Cat()
    {
    }

    public Cat(InputStream in)
    {
        super(in);
    }

    @Override
    protected Output<String> createOutput(OutputStream out)
    {
        final PrintStream ps = out instanceof PrintStream ? (PrintStream)out : new PrintStream(out);
        return new Output<String>()
        {
            public void output(String obj)
            {
                ps.println(obj);
            }

            public void close()
            {
                ps.flush();
                ps.close();
            }
        };
    }

    @Override
    protected String eachLine(String line)
    {
        return line;
    }
}
