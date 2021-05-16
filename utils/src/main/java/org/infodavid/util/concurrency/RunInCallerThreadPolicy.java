package org.infodavid.util.concurrency;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;

/**
 * The Class RunInCallerThreadPolicy.
 */
public class RunInCallerThreadPolicy implements RejectedExecutionHandler {

    /** The logger. */
    private final Logger logger;

    /**
     * Instantiates a new run in main thread policy.
     * @param logger the logger
     */
    public RunInCallerThreadPolicy(final Logger logger) {
        super();

        this.logger = logger;
    }

    /*
     * (non-Javadoc)
     * @see java.util.concurrent.RejectedExecutionHandler#rejectedExecution(java.lang.Runnable, java.util.concurrent.ThreadPoolExecutor)
     */
    @Override
    public void rejectedExecution(final Runnable r, final ThreadPoolExecutor e) {
        if (e.isShutdown()) {
            logger.warn("Rejected task, executor service is not active, task cannot be executed");
        }
        else {
            logger.info("Task as been blocked, it will be executed in the main thread");

            r.run();
        }
    }
}
