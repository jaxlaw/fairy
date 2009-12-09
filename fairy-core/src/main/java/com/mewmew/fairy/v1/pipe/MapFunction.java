package com.mewmew.fairy.v1.pipe;

import java.io.IOException;

public interface MapFunction<InputType, OutputType>
{
    public void each(InputType input, Output<OutputType> output) throws IOException;
}
