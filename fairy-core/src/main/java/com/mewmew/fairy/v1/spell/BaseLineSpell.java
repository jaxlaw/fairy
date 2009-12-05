package com.mewmew.fairy.v1.spell;

import com.mewmew.fairy.v1.pipe.ObjectPipe;
import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.pipe.LineInputIterator;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Iterator;

public abstract class BaseLineSpell<OutputType> extends PipeSpell implements ObjectPipe<String, OutputType>
{
    Output<OutputType> output;

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
            process(new LineInputIterator(in), output = createOutput(out));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void after()
    {
        if (output != null) {
            output.close();
        }
        super.after();
    }

    public void process(Iterator<String> input, Output<OutputType> output)
    {
        while (input.hasNext()) {
            String s = input.next();
            output.output(eachLine(s));
        }
    }

    public void open(Output<OutputType> outputTypeOutput)
    {

    }

    public void close(Output<OutputType> outputTypeOutput)
    {

    }

    protected abstract OutputType eachLine(String line);

    protected abstract Output<OutputType> createOutput(OutputStream out);

}
