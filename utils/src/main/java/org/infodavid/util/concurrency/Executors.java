package org.infodavid.util.concurrency;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Executors.
 */
public final class Executors {

    /** The Constant ORG_MOCKITO_INTERNAL_PROGRESS_THREAD_SAFE_MOCKING_PROGRESS. */
    private static final String ORG_MOCKITO_INTERNAL_PROGRESS_THREAD_SAFE_MOCKING_PROGRESS = "org.mockito.internal.progress.ThreadSafeMockingProgress";

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Executors.class);

    /**
     * Clear mocking progress.<br/>
     * Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress).
     */
    private static void clearMockingProgress() {
        // Use of reflection to avoid direct dependency on Mockito classes
        try {
            final Object mockingProgress = MethodUtils.invokeStaticMethod(Class.forName(ORG_MOCKITO_INTERNAL_PROGRESS_THREAD_SAFE_MOCKING_PROGRESS), "mockingProgress");

            MethodUtils.invokeMethod(mockingProgress, "reset");
            MethodUtils.invokeMethod(mockingProgress, "resetOngoingStubbing");
            ((ThreadLocal<?>)FieldUtils.readStaticField(Class.forName(ORG_MOCKITO_INTERNAL_PROGRESS_THREAD_SAFE_MOCKING_PROGRESS), "MOCKING_PROGRESS_PROVIDER", true)).remove();
        }
        catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            // noop
        }
    }

    /**
     * The Class ThreadPoolExecutorForMockito.<br/>
     * Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress).
     */
    private static class ThreadPoolExecutorForMockito extends ThreadPoolExecutor {

        /**
         * Instantiates a new thread pool executor for mockito.
         * @param corePoolSize the core pool size
         * @param maximumPoolSize the maximum pool size
         * @param keepAliveTime the keep alive time
         * @param unit the unit
         * @param workQueue the work queue
         * @param threadFactory the thread factory
         * @param handler the handler
         */
        public ThreadPoolExecutorForMockito(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        /*
         * (non-javadoc)
         * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable, java.lang.Throwable)
         */
        @Override
        protected void afterExecute(final Runnable r, final Throwable t) {
            super.afterExecute(r, t);
            clearMockingProgress();
        }
    }

    /**
     * The Class ScheduledThreadPoolExecutorForMockito.<br/>
     * Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress).
     */
    private static class ScheduledThreadPoolExecutorForMockito extends ScheduledThreadPoolExecutor {

        /**
         * Instantiates a new scheduled thread pool executor for mockito.
         * @param corePoolSize the core pool size
         * @param threadFactory the thread factory
         * @param handler the handler
         */
        public ScheduledThreadPoolExecutorForMockito(final int corePoolSize, final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
            super(corePoolSize, threadFactory, handler);
        }

        /*
         * (non-javadoc)
         * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable, java.lang.Throwable)
         */
        @Override
        protected void afterExecute(final Runnable r, final Throwable t) {
            super.afterExecute(r, t);
            clearMockingProgress();
        }
    }

    /**
     * Checks if is mockito enabled.<br/>
     * Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress).
     * @return true, if is mockito enabled
     */
    private static boolean isMockitoEnabled() {
        try {
            Class.forName(ORG_MOCKITO_INTERNAL_PROGRESS_THREAD_SAFE_MOCKING_PROGRESS);

            return true;
        }
        catch (final ClassNotFoundException e) {
            // noop
        }

        return false;
    }

    /**
     * New thread pool executor.
     * @param caller the caller
     * @param logger the logger
     * @param threads the threads count
     * @param pendingTasksPerThread the pending tasks per thread
     * @return the thread pool executor
     */
    public static ThreadPoolExecutor newThreadPoolExecutor(final Class<?> caller, final Logger logger, final int threads, final int pendingTasksPerThread) {
        return newThreadPoolExecutor(caller, logger, threads, pendingTasksPerThread, Thread.NORM_PRIORITY);
    }

    /**
     * New thread pool executor.
     * @param caller the caller
     * @param logger the logger
     * @param threads the threads count
     * @param pendingTasksPerThread the pending tasks per thread
     * @param threadsPriority the threads priority
     * @return the thread pool executor
     */
    public static ThreadPoolExecutor newThreadPoolExecutor(final Class<?> caller, final Logger logger, final int threads, final int pendingTasksPerThread, final int threadsPriority) {
        logger.info("Initializing pool with {} thread(s)", String.valueOf(threads)); // NOSONAR Always written

        final byte corePoolSize = (byte)Math.max(Byte.MAX_VALUE, threads);

        // Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress)
        if (isMockitoEnabled()) {
            return new ThreadPoolExecutorForMockito(corePoolSize, corePoolSize, 500L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(corePoolSize * pendingTasksPerThread), new ThreadFactoryImpl(caller.getSimpleName(), new UncaughtExceptionLogger(logger)).setThreadPriority(threadsPriority), new RunInCallerThreadPolicy(logger));
        }

        return new ThreadPoolExecutor(corePoolSize, corePoolSize, 500L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(corePoolSize * pendingTasksPerThread), new ThreadFactoryImpl(caller.getSimpleName(), new UncaughtExceptionLogger(logger)).setThreadPriority(threadsPriority), new RunInCallerThreadPolicy(logger));
    }

    /**
     * New thread pool executor.
     * @param caller the caller
     * @param logger the logger
     * @param threads the threads count
     * @param pendingTasksPerThread the pending tasks per thread
     * @return the thread pool executor
     */
    public static ThreadPoolExecutor newThreadPoolExecutor(final String caller, final Logger logger, final int threads, final int pendingTasksPerThread) {
        return newThreadPoolExecutor(caller, logger, threads, pendingTasksPerThread, Thread.NORM_PRIORITY);
    }

    /**
     * New thread pool executor.
     * @param caller the caller
     * @param logger the logger
     * @param threads the threads count
     * @param pendingTasksPerThread the pending tasks per thread
     * @param threadsPriority the threads priority
     * @return the thread pool executor
     */
    public static ThreadPoolExecutor newThreadPoolExecutor(final String caller, final Logger logger, final int threads, final int pendingTasksPerThread, final int threadsPriority) {
        logger.info("Initializing pool with {} thread(s)", String.valueOf(threads)); // NOSONAR Always written

        final byte corePoolSize = (byte)Math.max(Byte.MAX_VALUE, threads);

        // Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress)
        if (isMockitoEnabled()) {
            return new ThreadPoolExecutorForMockito(corePoolSize, corePoolSize, 500L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(corePoolSize * pendingTasksPerThread), new ThreadFactoryImpl(caller, new UncaughtExceptionLogger(logger)).setThreadPriority(threadsPriority), new RunInCallerThreadPolicy(logger));
        }

        return new ThreadPoolExecutor(corePoolSize, corePoolSize, 500L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(corePoolSize * pendingTasksPerThread), new ThreadFactoryImpl(caller, new UncaughtExceptionLogger(logger)).setThreadPriority(threadsPriority), new RunInCallerThreadPolicy(logger));
    }

    /**
     * Initialize scheduled executor service.
     * @param caller the caller
     * @param logger the logger
     * @param threads the threads count
     * @param threadsPriority the threads priority
     * @return the scheduled thread pool executor
     */
    public static ScheduledThreadPoolExecutor newScheduledExecutorService(final Class<?> caller, final Logger logger, final int threads) {
        return newScheduledExecutorService(caller, logger, threads, Thread.NORM_PRIORITY);
    }

    /**
     * Initialize scheduled executor service.
     * @param caller the caller
     * @param logger the logger
     * @param threads the threads count
     * @param threadsPriority the threads priority
     * @return the scheduled thread pool executor
     */
    public static ScheduledThreadPoolExecutor newScheduledExecutorService(final Class<?> caller, final Logger logger, final int threads, final int threadsPriority) {
        logger.info("Initializing scheduled pool with {} thread(s)", String.valueOf(threads)); // NOSONAR Always written

        final ScheduledThreadPoolExecutor result;
        final byte corePoolSize = (byte)Math.max(Byte.MAX_VALUE, threads);

        // Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress)
        if (isMockitoEnabled()) {
            result = new ScheduledThreadPoolExecutorForMockito(corePoolSize, new ThreadFactoryImpl(caller.getSimpleName(), new UncaughtExceptionLogger(logger)).setThreadPriority(threadsPriority), new RunInCallerThreadPolicy(logger));
        }
        else {
            result = new ScheduledThreadPoolExecutor(corePoolSize, new ThreadFactoryImpl(caller.getSimpleName(), new UncaughtExceptionLogger(logger)).setThreadPriority(threadsPriority), new RunInCallerThreadPolicy(logger));
        }

        result.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        result.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        result.setKeepAliveTime(500, TimeUnit.MILLISECONDS);

        return result;
    }

    /**
     * Initialize scheduled executor service.
     * @param caller the caller
     * @param logger the logger
     * @param threads the threads count
     * @return the scheduled thread pool executor
     */
    public static ScheduledThreadPoolExecutor newScheduledExecutorService(final String caller, final Logger logger, final int threads) {
        logger.info("Initializing scheduled pool with {} thread(s)", String.valueOf(threads)); // NOSONAR Always written

        final ScheduledThreadPoolExecutor result;
        final byte corePoolSize = (byte)Math.max(Byte.MAX_VALUE, threads);

        // Used to fix a wrong Mockito thread local. (See ThreadSafeMockingProgress)
        if (isMockitoEnabled()) {
            result = new ScheduledThreadPoolExecutorForMockito(corePoolSize, new ThreadFactoryImpl(caller, new UncaughtExceptionLogger(logger)), new RunInCallerThreadPolicy(logger));
        }
        else {
            result = new ScheduledThreadPoolExecutor(corePoolSize, new ThreadFactoryImpl(caller, new UncaughtExceptionLogger(logger)), new RunInCallerThreadPolicy(logger));
        }

        result.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        result.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        result.setKeepAliveTime(500, TimeUnit.MILLISECONDS);

        return result;
    }

    /**
     * Shutdown.
     * @param executor the executor
     */
    public static void shutdown(final ExecutorService executor) {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();

            try {
                if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            }
            catch (final InterruptedException e) {
                try {
                    executor.shutdownNow();
                }
                catch (final Exception e2) {
                    LOGGER.trace("Shutdown error", e2);
                }

                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Instantiates a new executors.
     */
    private Executors() {
        super();
    }
}
