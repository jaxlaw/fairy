package com.mewmew.fairy.v1.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class JsonArrayIterator<T> implements Iterator<T>
{
    T curr;
    final JsonParser parser;
    final Class<T> clazz;

    public JsonArrayIterator(JsonParser parser, Class<T> clazz) throws IOException
    {
        this.parser = parser;
        this.clazz = clazz;
        // skip to the nearest array
        while (parser.getCurrentToken() != JsonToken.START_ARRAY && parser.nextToken() != JsonToken.END_OBJECT) ;
        parser.nextToken();
        next();
    }

    public boolean hasNext()
    {
        return curr != null;
    }

    public T next()
    {
        try {
            return curr;
        }
        finally {
            try {
                curr = parser.readValueAs(clazz);
            }
            catch (IOException e) {
                curr = null;
                throw new RuntimeException(e);
            }
        }
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) throws IOException
    {
        JsonArrayIterator<Integer> iter = new JsonArrayIterator<Integer>(new MappingJsonFactory().createJsonParser(
                "[1,2,3,4,5]"
        ), Integer.class);

        printIterator(iter);

        printIterator(new JsonArrayIterator<Map>(new MappingJsonFactory().createJsonParser(
                "{\"h\":1, \"items\":[{\"a\":1,\"b\":\"abc\"}, {\"a\":2,\"b\":3}], \"abc\":1}"
        ), Map.class));

    }

    private static <T> void printIterator(JsonArrayIterator<T> iter)
    {
        while (iter.hasNext()) {
            T integer = iter.next();
            System.out.println(integer);
        }
    }
}
