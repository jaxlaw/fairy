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
package com.mewmew.fairy.jmx.examples;

import com.mewmew.fairy.jmx.EasyJMX;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer
{
    public static void main(String[] args) throws Exception
    {
        ExecutorService exe = Executors.newFixedThreadPool(1);
        EchoServer echo = new EchoServer();
        echo.run();
        Thread.currentThread().join();
    }

    private void run() throws MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException, MalformedObjectNameException
    {
        final MBeanServer mbServer = ManagementFactory.getPlatformMBeanServer();
        mbServer.registerMBean(new StandardMBean(new EchoMBean()
        {
            @Override
            public String getEcho(String hello)
            {
                return hello + " , date = " + new Date();
            }

            @Override
            public long getRandom()
            {
                return (long) (Math.random() * 100 );
            }

            @Override
            public void makeAnother(String name) throws MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException, MalformedObjectNameException
            {
                new EasyJMX(mbServer).register(
                        String.format("jax.example:type=EchoMBean,name=%s", name),
                        new AnotherObject(name)
                );
            }


        }, EchoMBean.class) , new ObjectName("jax.example:type=EchoMBean"));
    }
}
