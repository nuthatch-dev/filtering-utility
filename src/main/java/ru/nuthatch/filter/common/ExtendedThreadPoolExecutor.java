package ru.nuthatch.filter.common;

import java.util.concurrent.*;

public class ExtendedThreadPoolExecutor extends ThreadPoolExecutor {
    public ExtendedThreadPoolExecutor(int corePoolSize,
                                      int maximumPoolSize,
                                      long keepAliveTime,
                                      TimeUnit unit,
                                      BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?> && ((Future<?>) r).isDone()) {
            try {
                Object _ = ((Future<?>) r).get();
            } catch (CancellationException cancellationException) {
                t = cancellationException;
            } catch (ExecutionException executionException) {
                t = executionException.getCause();
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
            if (t != null) {
                System.out.println("Ошибка при выполнении. " + t.getMessage() +
                        "\nРезультаты выполнения могут быть некорректны");
            }
        }
    }
}
