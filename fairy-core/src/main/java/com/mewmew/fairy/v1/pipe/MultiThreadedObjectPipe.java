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
package com.mewmew.fairy.v1.pipe;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.io.IOException;

public class MultiThreadedObjectPipe<InputType, OutputType> implements ObjectPipe<InputType, OutputType>
{
    private final ExecutorService executor;
    private final ObjectPipe delegate ;

    public MultiThreadedObjectPipe(int numThreads, ObjectPipe delegate)
    {
        this(numThreads, Math.min(numThreads, 50) * 100, delegate);
    }

    public MultiThreadedObjectPipe(int numThreads, int queueSize, ObjectPipe delegate)
    {
        this(new ThreadPoolExecutor(numThreads, numThreads,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize)
                {
                    public boolean offer(Runnable runnable)
                    {
                        try {
                            return super.offer(runnable, Long.MAX_VALUE, TimeUnit.SECONDS);
                        }
                        catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return false;
                        }
                    }
                }), delegate);
    }

    public MultiThreadedObjectPipe(ExecutorService exe, ObjectPipe delegate)
    {
        this.executor = exe;
        this.delegate = delegate;
    }

    @Override
    public void close(Output<OutputType> outputTypeOutput) throws IOException
    {
        try {
            /*
             This is from experience I don't trust executor.shutdown and executor.awaitTermination
             is doing the right thing.
             */
            if (executor instanceof ThreadPoolExecutor) {
                ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;
                long remaining = 0;
                while ((remaining = tpe.getQueue().size()) > 0 || tpe.getActiveCount() > 0) {
                    Thread.sleep(500L);
                }
                tpe.shutdown();
                tpe.awaitTermination(5L, TimeUnit.SECONDS);
            }
            else {
                executor.shutdown();
                executor.awaitTermination(5L, TimeUnit.SECONDS);
            }
        }
        catch (InterruptedException e) {
        }
        delegate.close(outputTypeOutput);
    }

    public void open(Output<OutputType> outputTypeOutput) throws IOException
    {
        delegate.open(outputTypeOutput);
    }

    @Override
    public final void each(final InputType input, final Output<OutputType> output)
    {
        executor.execute(new Runnable()
        {
            public void run()
            {
                try {
                    delegate.each(input, output);
                }
                catch (IOException e) {                    
                }
            }
        });
    }
}
