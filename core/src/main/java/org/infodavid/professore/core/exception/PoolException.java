package org.infodavid.professore.core.exception;

/**
 * The Class PoolException.
 */
public class PoolException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 35776547693347672L;

    /**
     * Instantiates a new pool exception.
     */
    public PoolException() {
        super();
    }

    /**
     * Instantiates a new exception.
     * @param message            the message
     * @param cause              the cause
     * @param enableSuppression  the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public PoolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Instantiates a new exception.
     * @param message the message
     * @param cause   the cause
     */
    public PoolException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new exception.
     * @param message the message
     */
    public PoolException(String message) {
        super(message);
    }

    /**
     * Instantiates a new exception.
     * @param cause the cause
     */
    public PoolException(Throwable cause) {
        super(cause);
    }
}
