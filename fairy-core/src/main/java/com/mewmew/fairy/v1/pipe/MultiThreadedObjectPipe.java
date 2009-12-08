package com.mewmew.fairy.v1.pipe;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.io.IOException;

public abstract class MultiThreadedObjectPipe<InputType, OutputType> extends BaseObjectPipe<InputType, OutputType>
{
    private final ExecutorService executor;

    public MultiThreadedObjectPipe(int numThreads)
    {
        this(numThreads, Math.min(numThreads, 50) * 100);
    }

    public MultiThreadedObjectPipe(int numThreads, int queueSize)
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
                }));
    }

    public MultiThreadedObjectPipe(ExecutorService exe)
    {
        this.executor = exe;
    }

    @Override
    public void close(Output<OutputType> outputTypeOutput)
    {
        try {
            /*
             This is from experience InputType don't trust executor.shutdown and executor.awaitTermination
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
        super.close(outputTypeOutput);
    }

    @Override
    public final void each(final InputType input, final Output<OutputType> output)
    {
        executor.execute(new Runnable()
        {
            public void run()
            {
                try {
                    MultiThreadedObjectPipe.this._each(input, output);
                }
                catch (IOException e) {                    
                }
            }
        });
    }

    public abstract void _each(InputType input, Output<OutputType> output) throws IOException;
}
