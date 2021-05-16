package org.infodavid.util;

import java.lang.ref.WeakReference;

/**
 * The Class TimeUtils.
 */
public final class TimeUtils {

    /** The singleton. */
    private static WeakReference<TimeUtils> instance = null;

    /**
     * The suffix in the delay string for minutes.
     */
    private static final String MIN_UNIT = "m";

    /**
     * The number of milliseconds in a minute.
     */
    private static final long MS_IN_MIN = 60000L;

    /**
     * The number of milliseconds in a second.
     */
    private static final long MS_IN_S = 1000L;

    /**
     * The suffix in the delay string for milliseconds.
     */
    private static final String MS_UNIT = "ms";

    /**
     * The suffix in the delay string for seconds.
     */
    private static final String S_UNIT = "s";

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized TimeUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new TimeUtils());
        }

        return instance.get();
    }
    /**
     * Instantiates a new util.
     */
    private TimeUtils() {
        super();
    }

    /**
     * Parses the duration (number as text).
     * @param duration the duration
     * @return The parsed delay in milliseconds
     */
    public long parseDuration(final String duration, final long defaultDuration) {
        long wait = defaultDuration;

        if (duration == null) {
            return defaultDuration;
        }

        long multiplier = 1L;

        final String trimDelay = duration.trim();
        String numericDelay = trimDelay;

        if (trimDelay.endsWith(MS_UNIT)) {
            numericDelay = trimDelay.substring(0, trimDelay.length() - 2);
        }
        else if (trimDelay.endsWith(S_UNIT)) {
            multiplier = multiplier * MS_IN_S;
            numericDelay = trimDelay.substring(0, trimDelay.length() - 1);
        }
        else if (trimDelay.endsWith(MIN_UNIT)) { // Not CSS2
            multiplier = multiplier * MS_IN_MIN;
            numericDelay = trimDelay.substring(0, trimDelay.length() - 1);
        }

        final int fractionIndex = numericDelay.indexOf('.');

        if (fractionIndex > -1) {
            if (fractionIndex > 0) {
                wait = Long.parseLong(numericDelay.substring(0, fractionIndex));
                wait *= multiplier;
            }

            numericDelay = numericDelay.substring(fractionIndex + 1);
            multiplier /= Math.pow(10, numericDelay.length());
        }

        if (numericDelay.length() > 0) {
            wait += Long.parseLong(numericDelay) * multiplier;
        }

        return wait;
    }
}
