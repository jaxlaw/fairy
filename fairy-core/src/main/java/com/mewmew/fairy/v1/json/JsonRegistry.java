package com.mewmew.fairy.v1.json;

import com.mewmew.fairy.v1.pipe.Output;
import org.codehaus.jackson.JsonGenerator;
import org.apache.commons.io.LineIterator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonRegistry
{
    private static final File base = new File(new File(System.getProperty("user.home")), ".fairy");

    static {
        if (!base.exists()) {
            try {
                base.mkdirs();
            }
            catch (Exception e) {
            }
        }
    }

    final File file;

    public JsonRegistry(File file)
    {
        this.file = file;
    }

    public JsonRegistry(String filename)
    {
        this.file = new File(base, filename);
    }

    public JsonRegistry add(Map<String, Object> json) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        JsonGenerator jgen = JsonOutput.FACTORY.createJsonGenerator(writer);
        JsonOutput.MAPPER.writeValue(jgen, json);
        jgen.writeRawValue("\n");
        jgen.flush();
        jgen.close();
        writer.close();
        return this;
    }

    public Map<String, Map<String, Object>> loadAsMap(String key) throws IOException
    {
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
        LineIterator iterator = new LineIterator(new FileReader(file));
        while (iterator.hasNext()) {
            Map<String, Object> json = JsonOutput.MAPPER.readValue(iterator.nextLine(), Map.class);
            if (json.containsKey(key)) {
                map.put(json.get(key).toString(), json);
            }
        }
        return map;
    }

    public List<Map<String, Object>> loadAsList() throws IOException
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        LineIterator iterator = new LineIterator(new FileReader(file));
        while (iterator.hasNext()) {
            Map<String, Object> json = JsonOutput.MAPPER.readValue(iterator.nextLine(), Map.class);
            list.add(json);
        }
        return list;
    }

    public void compact(String key) throws IOException
    {
        Map<String, Map<String, Object>> map = loadAsMap(key);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        JsonGenerator jgen = JsonOutput.FACTORY.createJsonGenerator(writer);
        for (Map<String, Object> json : map.values()) {
            JsonOutput.MAPPER.writeValue(jgen, json);
        }
        jgen.writeRawValue("\n");
        jgen.flush();
        jgen.close();
        writer.close();
    }

    public void list(OutputStream os, String key)
    {
        Output<Map<String, Object>> output = null;
        try {
            output = JsonOutput.createOutput(os, OutputFormat.PRETTY);
            Map<String, Map<String, Object>> map = loadAsMap(key);
            for (Map<String, Object> objectMap : map.values()) {
                output.output(objectMap);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (output != null) {
                try {
                    output.close();
                }
                catch (IOException e) {
                }
            }
        }
    }

}
