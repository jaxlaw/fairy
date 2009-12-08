package com.mewmew.fairy.v1.pipe;

import java.util.Iterator;
import java.io.IOException;

public abstract class BaseObjectPipe<InputType, OutputType> implements PullObjectPipe<InputType, OutputType>
{
    public void process(Iterator<InputType> input, Output<OutputType> output) throws IOException
    {
        while (input.hasNext()) {
            each(input.next(), output);
        }
    }

    public void each(InputType s, Output<OutputType> output) throws IOException
    {
        
    }

    public void open(Output<OutputType> outputTypeOutput)
    {

    }

    public void close(Output<OutputType> outputTypeOutput)
    {
        outputTypeOutput.close();
    }
}
