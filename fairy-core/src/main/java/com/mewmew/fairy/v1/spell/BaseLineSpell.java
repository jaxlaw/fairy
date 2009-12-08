package com.mewmew.fairy.v1.spell;

import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.pipe.LineInputIterator;
import com.mewmew.fairy.v1.pipe.PullObjectPipe;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public abstract class BaseLineSpell<OutputType> extends PipeSpell
{
    Output<OutputType> output;
    PullObjectPipe<String, OutputType> pipe;

    protected BaseLineSpell()
    {
    }

    public BaseLineSpell(InputStream in)
    {
        super(in);
    }

    @Override
    public void process(InputStream in, OutputStream out)
    {
        try {
            (pipe = createPipe()).process(new LineInputIterator(in), output = createOutput(out));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void after()
    {
        if (output != null) {
            pipe.close(output);
        }
        super.after();
    }

    protected abstract Output<OutputType> createOutput(OutputStream out);
    protected abstract PullObjectPipe<String, OutputType> createPipe();
}
