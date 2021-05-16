package org.infodavid.util.swing.converter;

import java.awt.Point;
import java.lang.ref.WeakReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class PointConverter.
 */
public final class PointConverter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PointConverter.class);

    /** The singleton. */
    private static WeakReference<PointConverter> singleton = null;

    /**
     * Return the singleton.
     * @return the singleton
     */
    public static PointConverter getSingleton() {
        if (singleton == null || singleton.get() == null) {
            singleton = new WeakReference<>(new PointConverter());
        }

        return singleton.get();
    }

    /**
     * Instantiates a new point converter.
     */
    private PointConverter() {
        super();
    }

    /**
     * Transforms the point to a string (x,y).
     * @param value the point
     * @return a string representation
     */
    public String toString(final Point value) {
        return "" + value.getX() + ',' + value.getY();
    }

    /**
     * Transforms the string (x, y) to a point object.
     * @param value string representation
     * @return the point object
     */
    public Point valueOf(final String value) {
        final String[] values = value == null ? null : value.split(",");
        Point result = null;

        if (values != null) {
            final int[] i = new int[2];

            for (int j = 0; j < 2; j++) {
                try {
                    i[j] = (int)Double.parseDouble(values[j]);
                }
                catch (final Exception e) {
                    LOGGER.warn("Cannot evaluate point: " + value, e);
                }
            }

            result = new Point(i[0], i[1]);
        }

        return result;
    }
}
