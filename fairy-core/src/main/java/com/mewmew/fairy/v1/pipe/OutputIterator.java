package com.mewmew.fairy.v1.pipe;

import java.util.Iterator;
import java.util.concurrent.Exchanger;

public class OutputIterator<T> implements Output<T>, Iterator<T>
{
    private volatile boolean isClosed = false ;
    private final Exchanger<T> exchanger = new Exchanger<T>();

    public void output(T obj)
    {
        try {
            exchanger.exchange(obj);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void close()
    {
        isClosed = true ;
    }

    public boolean hasNext()
    {
        return !isClosed;
    }

    public T next()
    {
        try {
            return exchanger.exchange(null);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
