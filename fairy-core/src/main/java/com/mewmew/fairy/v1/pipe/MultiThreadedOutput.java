package com.mewmew.fairy.v1.pipe;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;

public class MultiThreadedOutput<T> implements Output<T>
{
    private final Output<T> output;
    private final ExecutorService executor;

    public MultiThreadedOutput(Output<T> output, int numThreads)
    {
        this(output, numThreads, Math.min(numThreads, 50) * 100);
    }

    public MultiThreadedOutput(Output<T> output, int numThreads, int queueSize)
    {
        this(output, new ThreadPoolExecutor(numThreads, numThreads,
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
                }));
    }

    public MultiThreadedOutput(Output<T> output, ExecutorService exe)
    {
        this.output = output;
        this.executor = exe;
    }

    public void output(final T obj)
    {
        executor.execute(new Runnable()
        {
            public void run()
            {
                output.output(obj);
            }
        });
    }

    public void close()
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
    }
}
