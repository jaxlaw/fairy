package com.mewmew.fairy.v1.pipe;

import java.util.Iterator;

public interface ObjectPipe<InputType, OutputType>
{
    void open(Output<OutputType> output);
    void process(Iterator<InputType> input, Output<OutputType> output);
    void close(Output<OutputType> output);
}
