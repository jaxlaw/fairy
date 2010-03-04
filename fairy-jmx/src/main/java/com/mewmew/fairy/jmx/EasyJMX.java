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
package com.mewmew.fairy.jmx;

import com.mewmew.fairy.jmx.annotation.JMX;

import javax.management.*;
import javax.management.modelmbean.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// original idea from martint. this is for illustration in a brown bag only. may not be production quality. use at your own risk.
public class EasyJMX
{
    private final Pattern pattern = Pattern.compile("(get|set|is)(.+)");
    private final MBeanServer mbeanServer;

    public EasyJMX(MBeanServer mbeanServer)
    {
        this.mbeanServer = mbeanServer;
    }

    public void register(String name, Object monitoredObject)
    {
        try {
            ObjectName objectName = new ObjectName(name);
            RequiredModelMBean mbean = new RequiredModelMBean((createMBeanInfo(monitoredObject.getClass())));
            mbean.setManagedResource(monitoredObject, "objectReference");
            try {
                mbeanServer.registerMBean(mbean, objectName);
            }
            catch (InstanceAlreadyExistsException ex) {
                mbeanServer.unregisterMBean(objectName);
                mbeanServer.registerMBean(mbean, objectName);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ModelMBeanInfo createMBeanInfo(Class clazz) throws IntrospectionException
    {
        List<ModelMBeanOperationInfo> operations = new ArrayList<ModelMBeanOperationInfo>();
        Map<String, Attribute> attributes = new HashMap<String, Attribute>();
        String className = clazz.getName();
        while (clazz != null) {
            for (Method method : clazz.getMethods()) {
                JMX annotation = method.getAnnotation(JMX.class);
                if (annotation == null) {
                    continue;
                }

                String name = method.getName();
                String description = annotation.description();
                Matcher matcher = pattern.matcher(name);

                String optype = "operation";
                if (matcher.matches()) {
                    String type = matcher.group(1);
                    String attributeName = matcher.group(2);

                    Attribute attribute = attributes.get(attributeName);
                    if (attribute == null) {
                        attribute = new Attribute(attributeName, description);
                        attributes.put(attributeName, attribute);
                    }
                    if ((type.equals("get") || type.equals("is")) && method.getParameterTypes().length == 0) {
                        attribute.descriptor.setField("getMethod", method.getName());
                        attribute.getter = method;
                        optype = type;
                    }
                    else if (type.equals("set") && method.getParameterTypes().length == 1) {
                        attribute.descriptor.setField("setMethod", method.getName());
                        attribute.setter = method;
                        optype = type;
                    }
                }
                DescriptorSupport operation = new DescriptorSupport();
                operation.setField("name", method.getName());
                operation.setField("class", className);
                operation.setField("descriptorType", "operation");
                operation.setField("optype", optype);
                operations.add(new ModelMBeanOperationInfo(description, method, operation));
            }

            clazz = clazz.getSuperclass();
        }

        ModelMBeanAttributeInfo[] modelMBeanAttributeInfos = new ModelMBeanAttributeInfo[attributes.size()];
        int i = 0;
        for (Attribute attribute : attributes.values()) {
            modelMBeanAttributeInfos[i++] = attribute.toModelMBeanAttributeInfo();
        }

        return new ClientMBeanInfo(new ModelMBeanInfoSupport(
                className,
                null,
                modelMBeanAttributeInfos,
                new ModelMBeanConstructorInfo[0],
                operations.toArray(new ModelMBeanOperationInfo[operations.size()]),
                new ModelMBeanNotificationInfo[0]));
    }

    static class Attribute
    {
        String name, description;
        DescriptorSupport descriptor;
        Method getter;
        Method setter;

        public Attribute(String name, String description)
        {
            this.name = name;
            this.description = description;
            descriptor = new DescriptorSupport();
            descriptor.setField("name", name);
            descriptor.setField("descriptorType", "attribute");
        }

        ModelMBeanAttributeInfo toModelMBeanAttributeInfo() throws IntrospectionException
        {
            return new ModelMBeanAttributeInfo(
                    name,
                    description,
                    getter,
                    setter,
                    descriptor);
        }

    }

    // For JMX client, we don't want operations for getters and setters
    static class ClientMBeanInfo extends ModelMBeanInfoSupport
    {
        public ClientMBeanInfo(ModelMBeanInfo mbinfo)
        {
            super(mbinfo);
        }

        @Override
        public ClientMBeanInfo clone()
        {
            return new ClientMBeanInfo(this);
        }

        private Object writeReplace()
        {
            List<ModelMBeanOperationInfo> operations = new ArrayList<ModelMBeanOperationInfo>();
            for (MBeanOperationInfo opInfo : this.getOperations()) {
                ModelMBeanOperationInfo info = (ModelMBeanOperationInfo) opInfo;
                Descriptor desc = info.getDescriptor();
                String optype = (String) desc.getFieldValue("optype");
                if (!"get".equals(optype) && !"set".equals(optype) && !"is".equals(optype)) {
                    operations.add(info);
                }
            }
            try {
                return new ModelMBeanInfoSupport(
                        this.getClassName(),
                        this.getDescription(),
                        (ModelMBeanAttributeInfo[]) this.getAttributes(),
                        (ModelMBeanConstructorInfo[]) this.getConstructors(),
                        operations.toArray(new ModelMBeanOperationInfo[operations.size()]),
                        (ModelMBeanNotificationInfo[]) this.getNotifications(),
                        this.getMBeanDescriptor());
            }
            catch (MBeanException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
