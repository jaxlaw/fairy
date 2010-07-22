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

import com.google.common.collect.ImmutableMap;
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

    @Param(option = "H")
    String host;
    @Param
    int port;
    @Param
    String objectName = null;
    @Param(option = "O", defaultValue = "PRETTY")
    private OutputFormat outputFormat;
    @Param
    private String[] columnOrder;
    @Param(option = "A")
    String[] attributes;


    // TODO : add -A -O and -V and make host a list or from standard in

    @Override
    public void cast() {
        if (attributes != null && objectName != null) {
            try {
                getAttributes(host, port, objectName, attributes, JsonOutput.createOutput(getOutputStream(), outputFormat, columnOrder));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                describe(host, port, objectName == null ? null : new ObjectName(objectName), JsonOutput.createOutput(getOutputStream(), outputFormat, columnOrder));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getAttributes(final String host, final int port, final String objectName, final String[] attributes, final Output<Map<String, Object>> output) {
        withMBeanServerConnection(host, port, new MBeanServerCallback() {
            public void withConnection(MBeanServerConnection conn) throws Exception {
                Map<String, Object> container = new HashMap<String, Object>();
                List<Map<String, Object>> array = new ArrayList<Map<String, Object>>();
                AttributeList attrs = conn.getAttributes(new ObjectName(objectName), attributes);
                for (int i = 0; i < attrs.size(); i++) {
                    Attribute attr = (Attribute) attrs.get(i);
                    array.add(ImmutableMap.of("name", attr.getName(), "value", attr.getValue()));
                }
                container.put("host", host);
                container.put("jmxPort", port);
                container.put("objectName", objectName);
                container.put("attributes", array);
                output.output(container);
            }
        });
    }


    private void describe(final String host, final int port, final ObjectName objName, final Output<Map<String, Object>> output) {
        withMBeanServerConnection(host, port, new MBeanServerCallback() {
            public void withConnection(MBeanServerConnection conn) throws Exception {
                List<Map<String, Object>> array = new ArrayList<Map<String, Object>>();
                Map<String, Object> container = new HashMap<String, Object>();
                for (ObjectName desc : new TreeSet<ObjectName>(conn.queryNames(objName, null))) {
                    Map<String, Object> json = new HashMap<String, Object>();
                    MBeanInfo info = conn.getMBeanInfo(desc);
                    json.put("objectName", desc.toString());
                    json.put("info", info);
                    array.add(json);
                }
                container.put("host", host);
                container.put("jmxPort", port);
                container.put("mbeans", array);
                output.output(container);
            }
        });
    }

    private void withMBeanServerConnection(String host, int port, MBeanServerCallback callback) {
        JMXConnector connector = null;
        try {
            connector = JMXConnectorFactory.connect(new JMXServiceURL(String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port)));
            callback.withConnection(connector.getMBeanServerConnection());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                connector.close();
            } catch (Exception e) {
            }
        }
    }

    interface MBeanServerCallback {
        void withConnection(MBeanServerConnection conn) throws Exception;
    }
}
