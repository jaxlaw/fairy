package com.mewmew.fairy.v1.json;

import com.mewmew.fairy.v1.pipe.LineInputIterator;

import java.util.Map;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.IOException;
import java.io.File;

import org.codehaus.jackson.map.ObjectMapper;

public class SimpleJsonIterator implements Iterator<Map<String, Object>>
{
    private final LineInputIterator delegate;
    private final ObjectMapper mapper = new ObjectMapper();

    public SimpleJsonIterator(LineInputIterator delegate)
    {
        this.delegate = delegate;
    }

    public boolean hasNext()
    {
        return delegate.hasNext();
    }

    public Map<String, Object> next()
    {
        Map<String, Object> map = null ;
        do {
            String line = delegate.next();
            try {
                map = mapper.readValue(line, Map.class);
                return map;
            }
            catch (IOException e) {
            }
        }
        while(map == null && delegate.hasNext());
        throw new NoSuchElementException();
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) throws IOException
    {
        SimpleJsonIterator iter = new SimpleJsonIterator(new LineInputIterator(new File("/Users/jax/galaxy.json")));
        while (iter.hasNext()) {
            Map<String, Object> stringObjectMap = iter.next();
            System.out.println(stringObjectMap);
        }
    }
}
