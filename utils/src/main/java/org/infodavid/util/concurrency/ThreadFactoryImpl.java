package org.infodavid.util.concurrency;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Class ThreadFactoryImpl.
 */
public class ThreadFactoryImpl implements ThreadFactory {

    /** The exception handler. */
    private final UncaughtExceptionHandler exceptionHandler;

    /** The group. */
    private final ThreadGroup group;

    /** The name prefix. */
    private final String namePrefix;

    /** The thread number. */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /** The thread priority. */
    private int threadPriority = Thread.NORM_PRIORITY;

    /**
     * Instantiates a new thread factory.
     * @param name the name
     * @param exceptionHandler the exception handler
     */
    public ThreadFactoryImpl(final String name, final UncaughtExceptionHandler exceptionHandler) {
        final SecurityManager s = System.getSecurityManager();
        group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = name + "-pool-thread-";
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Gets the name prefix.
     * @return the namePrefix
     */
    public String getNamePrefix() {
        return namePrefix;
    }

    /**
     * Gets the thread priority.
     * @return the threadPriority
     */
    public int getThreadPriority() {
        return threadPriority;
    }

    /*
     * (non-javadoc)
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    /*
     * (non-Javadoc)
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(final Runnable r) {
        final Thread result = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);

        if (result.isDaemon()) {
            result.setDaemon(false);
        }

        result.setPriority(threadPriority);
        result.setUncaughtExceptionHandler(exceptionHandler);

        return result;
    }

    /**
     * Sets the thread priority.
     * @param threadPriority the threadPriority to set
     * @return the thread factory impl
     */
    public ThreadFactoryImpl setThreadPriority(final int threadPriority) {
        this.threadPriority = threadPriority;

        return this;
    }
}
