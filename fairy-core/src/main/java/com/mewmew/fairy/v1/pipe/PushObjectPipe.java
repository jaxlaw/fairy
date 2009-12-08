package com.mewmew.fairy.v1.pipe;

import java.io.IOException;

public interface PushObjectPipe<InputType, OutputType>
{
    void open(Output<OutputType> output);
    void each(InputType input, Output<OutputType> output) throws IOException;
    void close(Output<OutputType> output);
}