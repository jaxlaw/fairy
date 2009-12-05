package com.mewmew.fairy.v1.pipe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

public class LineInputIterator implements Iterator<String>
{
    private final BufferedReader br ;
    String line ;

    public LineInputIterator(InputStream in) throws IOException
    {
        br = new BufferedReader(new InputStreamReader(in));
        line = br.readLine();
    }

    public LineInputIterator(Reader r) throws IOException
    {
        br = new BufferedReader(r);
        line = br.readLine(); 
    }

    public LineInputIterator(File f) throws IOException
    {
        br = new BufferedReader(new FileReader(f));
        line = br.readLine();
    }

    public boolean hasNext()
    {
        return line != null;
    }

    public String next()
    {
        try {
            return line;
        }
        finally {
            try {
                line = br.readLine();
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
