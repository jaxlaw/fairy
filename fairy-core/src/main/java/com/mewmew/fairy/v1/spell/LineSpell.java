package com.mewmew.fairy.v1.spell;

import com.mewmew.fairy.v1.pipe.BaseObjectPipe;
import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.pipe.PullObjectPipe;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;

public class LineSpell extends BaseLineSpell<String>

{
    public LineSpell()
    {
    }

    public LineSpell(InputStream in)
    {
        super(in);
    }

    @Override
    protected PullObjectPipe<String, String> createPipe()
    {
        return new BaseObjectPipe<String, String>()
        {
            @Override
            public void each(String line, Output<String> output) throws IOException
            {
                output.output(line);
            }
        };
    }

    @Override
    protected Output<String> createOutput(OutputStream out)
    {
        final PrintStream ps = out instanceof PrintStream ? (PrintStream) out : new PrintStream(out);
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
}
