package org.infodavid.util.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

/**
 * The Class RetryHelper.
 * @param <T> the generic type
 */
public class RetryHelper<T> {

    /** The logger. */
    private final Logger logger;

    /** The max retries. */
    private int maxRetries;

    /** The sleep time in ms. */
    private final long sleepTimeInMs;

    /** The halt on errors. */
    private final Class<? extends Exception>[] haltOnErrors;

    private final SleepLock sleepLock = new SleepLock();

    /**
     * Instantiates a new retry helper.
     * @param logger the logger
     * @param maxRetries the max retries
     * @param sleepTimeInMs the sleep time in ms
     * @param haltOnErrors the halt on errors
     */
    @SuppressWarnings("unchecked")
    public RetryHelper(final Logger logger, final int maxRetries, final long sleepTimeInMs, final Class<? extends Exception>... haltOnErrors) {
        super();

        this.haltOnErrors = haltOnErrors;
        this.logger = logger;
        this.maxRetries = maxRetries;
        this.sleepTimeInMs = sleepTimeInMs;
    }

    /**
     * Run.
     * @param function the function
     * @return the t
     * @throws Exception the exception
     */
    public T run(final Callable<T> function) throws Exception {
        try {
            return function.call();
        }
        catch (final NullPointerException e) {
            throw e;
        }
        catch (final Exception e) { // NOSONAR
            haltOnErrors(e);
            logger.debug("Recovery", e);

            return retry(function);
        }
    }

    /**
     * Halt on errors.
     * @param e the e
     * @throws Exception the exception
     */
    private void haltOnErrors(final Exception e) throws Exception { // NOSONAR
        if (haltOnErrors != null) {
            for (final Class<? extends Exception> item : haltOnErrors) {
                if (item.isInstance(e)) {
                    throw e;
                }
            }
        }
    }

    /**
     * Retry.
     * @param function the function
     * @return the t
     * @throws Exception the exception
     */
    private T retry(final Callable<T> function) throws Exception {
        Exception exception;

        sleepLock.lock();

        try {
            do {
                logger.warn("Processing failed, {} retrie(s) remaining. ({})", String.valueOf(maxRetries), function); // NOSONAR Always written

                try {
                    return function.call();
                }
                catch (final Exception e) {
                    haltOnErrors(e);

                    if (maxRetries > 0) {
                        logger.debug("Recovery", e);
                    }

                    exception = e;
                    maxRetries--;

                    if (sleepTimeInMs > 0) {
                        // attente avant nouvel essai
                        try {
                            sleepLock.await(sleepTimeInMs, TimeUnit.MILLISECONDS);
                        }
                        catch (final Exception e2) {
                            logger.trace("An error occured during sleep", e2);
                        }
                    }
                }
            }
            while (maxRetries > 0);
        }
        finally {
            sleepLock.unlock();
        }

        throw exception;
    }
}
