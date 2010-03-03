/*
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
*/
package com.mewmew.fairy.v1.book;

import com.mewmew.fairy.v1.cli.Param;
import com.mewmew.fairy.v1.json.JsonOutput;
import com.mewmew.fairy.v1.json.OutputFormat;
import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.spell.Help;
import com.mewmew.fairy.v1.spell.Spell;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.*;

@Help(desc = "JMX cli interface")
public class Jmx extends Spell {

    @Param String host ;
    @Param int port ;
    @Param String objectName = null;
    @Param(option="O") private OutputFormat outputFormat;
    @Param private String[] columnOrder;


    // TODO : add -A -O and -V and make host a list or from standard in

    @Override
    public void cast()
    {
        try
        {
            describe(host, port, objectName == null ? null : new ObjectName(objectName), JsonOutput.createOutput(getOutputStream(), outputFormat, columnOrder));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    
    private void describe(String host, int port, ObjectName objName, Output<Map<String, Object>> output)
    {
        try {
            List<Map<String, Object>> array = new ArrayList<Map<String, Object>>();
            Map<String, Object> container = new HashMap<String, Object>();
            for (ObjectName desc : new TreeSet<ObjectName>(getMBeanServerConnection(host, port).queryNames(objName, null))) {
                Map<String, Object> json = new HashMap<String, Object>();
                MBeanServerConnection beanServerConnection = getMBeanServerConnection(host, port);
                MBeanInfo info = beanServerConnection.getMBeanInfo(desc);
                json.put("objectName", desc.toString());
                List<Map<String, Object>> attrs = new ArrayList<Map<String, Object>>();
                for (MBeanAttributeInfo attr : info.getAttributes()) {
                    Map<String, Object> attrJson = new HashMap<String, Object>();
                    attrJson.put("type", attr.getType());
                    attrJson.put("description", attr.getDescription());
                    attrJson.put("name", attr.getName());
                    attrJson.put("isReadable", attr.isReadable());
                    attrJson.put("isWritable", attr.isWritable());
                    attrs.add(attrJson);
                }
                json.put("attributes", attrs);
                array.add(json);
            }
            container.put("host", host);
            container.put("jmxPort", port);
            container.put("mbeans", array) ;
            output.output(container);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MBeanServerConnection getMBeanServerConnection(String host, int port) throws IOException
    {
        String url = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port);
        JMXServiceURL urlObj = new JMXServiceURL(url);
        JMXConnector jmxConn = JMXConnectorFactory.connect(urlObj);
        return jmxConn.getMBeanServerConnection();
    }
}
