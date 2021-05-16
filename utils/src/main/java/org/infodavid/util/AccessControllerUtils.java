package org.infodavid.util;

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * The Class AccessControllerUtils.
 */
public class AccessControllerUtils {

    /** The Constant DEBUG. */
    private static final boolean DEBUG = true;

    /** The singleton. */
    private static WeakReference<AccessControllerUtils> instance = null;

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized AccessControllerUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new AccessControllerUtils());
        }

        return instance.get();
    }

    /**
     * Instantiates a new util.
     */
    private AccessControllerUtils() {
        super();
    }

    /**
     * Do privileged.
     * @param <T> the generic type
     * @param action the action
     * @return the t
     */
    public <T> T doPrivileged(final PrivilegedAction<T> action) {
        if (DEBUG) {
            return action.run();
        }

        return AccessController.doPrivileged(action);
    }

    /**
     * Do privileged.
     * @param <T> the generic type
     * @param action the action
     * @return the t
     * @throws PrivilegedActionException the privileged action exception
     */
    public <T> T doPrivileged(final PrivilegedExceptionAction<T> action) throws PrivilegedActionException {
        if (DEBUG) {
            try {
                return action.run();
            }
            catch (final Exception e) {
                throw new PrivilegedActionException(e);
            }
        }

        return AccessController.doPrivileged(action);
    }
}
