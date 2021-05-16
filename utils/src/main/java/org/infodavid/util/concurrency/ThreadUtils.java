package org.infodavid.util.concurrency;

import java.lang.ref.WeakReference;

/**
 * The Class ThreadUtils.
 */
public final class ThreadUtils {

    /** The singleton. */
    private static WeakReference<ThreadUtils> instance = null;

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized ThreadUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new ThreadUtils());
        }

        return instance.get();
    }

    /**
     * Instantiates a new util.
     */
    private ThreadUtils() {
        super();
    }

    /**
     * Interrupt.
     * @param thread the thread
     * @param lock the lock
     * @param millis the timeout in millis
     * @throws InterruptedException the interrupted exception
     */
    public void interrupt(final Thread thread, final SleepLock lock, final long timeout) throws InterruptedException {
        lock.lock();

        final long endTime = System.currentTimeMillis() + timeout;

        try {
            while (thread.isAlive() && System.currentTimeMillis() < endTime) {
                lock.await(50);
            }

            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
        catch (final InterruptedException e) {
            thread.interrupt();

            throw e;
        }
        finally {
            lock.unlock();
        }
    }
}
