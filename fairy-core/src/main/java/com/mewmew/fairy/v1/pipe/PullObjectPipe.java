package com.mewmew.fairy.v1.pipe;

import java.util.Iterator;
import java.io.IOException;

public interface PullObjectPipe<InputType, OutputType> extends PushObjectPipe<InputType, OutputType>
{
    void process(Iterator<InputType> input, Output<OutputType> output) throws IOException;
}