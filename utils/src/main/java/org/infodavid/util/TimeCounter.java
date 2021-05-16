package org.infodavid.util;

import java.util.concurrent.Callable;

/**
 * The Class TimeCounter.
 * @param <V> the value type
 */
public class TimeCounter<V> {

    /** The callable. */
    private final Callable<V> callable;

    /**
     * Instantiates a new time counter.
     * @param callable the callable
     */
    public TimeCounter(final Callable<V> callable) {
        super();

        this.callable = callable;
    }

    /** The duration. */
    private long duration = 0;

    /**
     * Run.
     * @return the result
     * @throws Exception the exception
     */
    public V run() throws Exception {
        final long t1 = System.nanoTime();
        final V result;

        try {
            result = callable.call();
        }
        finally {
            duration = System.nanoTime() - t1;
        }

        return result;
    }

    /**
     * Gets the duration.
     * @return the duration
     */
    public long getDuration() {
        return duration;
    }
}
