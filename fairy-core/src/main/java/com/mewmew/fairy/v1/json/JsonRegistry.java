package com.mewmew.fairy.v1.json;

import com.mewmew.fairy.v1.pipe.LineInputIterator;
import org.codehaus.jackson.JsonGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        LineInputIterator iterator = new LineInputIterator(file);
        while (iterator.hasNext()) {
            Map<String, Object> json = JsonOutput.MAPPER.readValue(iterator.next(), Map.class);
            if (json.containsKey(key)) {
                map.put(json.get(key).toString(), json);
            }
        }
        return map;
    }

    public List<Map<String, Object>> loadAsList() throws IOException
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        LineInputIterator iterator = new LineInputIterator(file);
        while (iterator.hasNext()) {
            Map<String, Object> json = JsonOutput.MAPPER.readValue(iterator.next(), Map.class);
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

}
