package org.infodavid.util.exception;

/**
 * The Class PoolException.
 */
public class PoolException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4198471754820984118L;

    /**
     * Instantiates a new pool exception.
     */
    public PoolException() {
        super();
    }

    /**
     * Instantiates a new pool exception.
     * @param message the message
     */
    public PoolException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new pool exception.
     * @param message the message
     * @param cause the cause
     */
    public PoolException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new pool exception.
     * @param message the message
     * @param cause the cause
     * @param enableSuppression the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public PoolException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Instantiates a new pool exception.
     * @param cause the cause
     */
    public PoolException(final Throwable cause) {
        super(cause);
    }
}
