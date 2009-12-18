package com.mewmew.fairy.v1.pipe;

import org.apache.commons.io.LineIterator;

import java.util.Map;
import java.util.Iterator;
import java.util.Queue;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.io.IOException;

import com.mewmew.fairy.v1.map.MapFunction;

public class MappingIterator<I, O> implements Iterator<O>
{
    private final Iterator<I> iterator;
    private final MapFunction<I, O> mapFunction;
    private final Output<O> bufferedOutput ;
    private final Queue<O> queue ;

    public MappingIterator(Iterator<I> iterator, MapFunction<I, O> mapFunction)
    {
        this.iterator = iterator;
        this.mapFunction = mapFunction;
        this.queue = new LinkedList<O>();
        this.bufferedOutput = new Output<O>()
        {
            public void output(O obj) throws IOException
            {
                queue.add(obj);
            }

            public void close() throws IOException
            {
                queue.clear();
            }
        };
    }

    public boolean hasNext()
    {
        advance();
        return !queue.isEmpty();
    }

    public O next()
    {
        advance();
        if (!queue.isEmpty()) {
            return queue.poll();
        }
        else {
            throw new NoSuchElementException();
        }
    }

    private void advance()
    {
        while(queue.isEmpty() && iterator.hasNext()) {
            try {
                mapFunction.each(iterator.next(), bufferedOutput);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
