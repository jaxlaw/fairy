package com.mewmew.fairy.v1.json;

import com.mewmew.fairy.v1.pipe.Output;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.util.Map;
import java.util.List;

public class DelimitedOutput implements Output<Map<String, Object>>
{
    final PrintWriter pw;
    final String delimiter;
    final String startLine;
    final String endLine;
    private List<String> ordering;

    public DelimitedOutput(OutputStream out, String delimiter, String startLine, String endLine)
    {
        this.pw = new PrintWriter(out, true);
        this.delimiter = delimiter;
        this.startLine = startLine;
        this.endLine = endLine;
    }

    public DelimitedOutput(OutputStream out, String delimiter)
    {
        this.pw = new PrintWriter(out, true);
        this.delimiter = delimiter;
        this.startLine = null;
        this.endLine = null;
    }

    public DelimitedOutput setColumnOrdering(List<String> ordering)
    {
        this.ordering = ordering;
        return this;
    }

    public void output(Map<String, Object> obj) throws IOException
    {
        int i = 0;
        if (startLine != null) {
            pw.print(startLine);
        }
        if (ordering != null) {
            int size = ordering.size();
            for (String name : ordering) {
                pw.print(obj.get(name));
                if (++i < size) {
                    pw.print(delimiter);
                }
            }
        }
        else {
            int size = obj.size();
            for (Object v : obj.values()) {
                pw.print(v);
                if (++i < size) {
                    pw.print(delimiter);
                }
            }
        }
        if (endLine != null) {
            pw.print(endLine);
        }
        pw.println();
    }

    public void close() throws IOException
    {
        pw.flush();
        pw.close();
    }
}
