package org.infodavid.util.swing.converter;

import java.awt.Dimension;
import java.lang.ref.WeakReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DimensionConverter.
 */
public final class DimensionConverter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DimensionConverter.class);

    /** The singleton. */
    private static WeakReference<DimensionConverter> singleton = null;

    /**
     * Return the singleton.
     * @return the singleton
     */
    public static DimensionConverter getSingleton() {
        if (singleton == null || singleton.get() == null) {
            singleton = new WeakReference<>(new DimensionConverter());
        }

        return singleton.get();
    }

    /**
     * Instantiates a new dimension converter.
     */
    private DimensionConverter() {
        super();
    }

    /**
     * transform the dimension instance to string represensation.
     * @param value the dimension
     * @return the string representation
     */
    public String toString(final Dimension value) {
        return "" + value.width + ',' + value.height;
    }

    /**
     * transform a string to a new dimension instance.
     * @param value string representation
     * @return a new dimension instance
     */
    public Dimension valueOf(final String value) {
        final String[] values = value == null ? null : value.split(",");
        Dimension result = null;

        if (values != null) {
            final int[] i = new int[2];

            for (int j = 0; j < 2; j++) {
                try {
                    i[j] = (int)Double.parseDouble(values[j]);
                }
                catch (final Exception e) {
                    LOGGER.warn("Cannot evaluate dimension: " + value, e);
                }
            }

            result = new Dimension(i[0], i[1]);
        }

        return result;
    }
}
