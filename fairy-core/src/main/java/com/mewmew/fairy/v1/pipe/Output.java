package com.mewmew.fairy.v1.pipe;

public interface Output<T>
{
    public void output(T obj);
    public void close();
}
