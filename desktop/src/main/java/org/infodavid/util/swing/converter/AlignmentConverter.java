package org.infodavid.util.swing.converter;

import java.lang.ref.WeakReference;
import java.util.Locale;

import javax.swing.SwingConstants;

/**
 * The Class AlignmentConverter.
 */
public final class AlignmentConverter {

    /** The Constant BOTTOM. */
    public static final String BOTTOM = "bottom";

    /** The Constant CENTER. */
    public static final String CENTER = "center";

    /** The Constant LEFT. */
    public static final String LEFT = "left";

    /** The Constant RIGHT. */
    public static final String RIGHT = "right";

    /** The Constant TOP. */
    public static final String TOP = "top";

    /** The singleton. */
    private static WeakReference<AlignmentConverter> singleton = null;

    /**
     * Return the singleton.
     * @return the singleton
     */
    public static AlignmentConverter getSingleton() {
        if (singleton == null || singleton.get() == null) {
            singleton = new WeakReference<>(new AlignmentConverter());
        }

        return singleton.get();
    }

    /**
     * constructor.
     */
    private AlignmentConverter() {
        super();
    }

    /**
     * Transforms an integer alignment constant to a string.
     * @param value integer alignment constant
     * @return the alignment
     */
    public String toString(final int value) {
        final String result;

        if (value == SwingConstants.LEFT) {
            result = LEFT;
        }
        else if (SwingConstants.TOP == value) {
            result = TOP;
        }
        else if (SwingConstants.BOTTOM == value) {
            result = BOTTOM;
        }
        else if (SwingConstants.RIGHT == value) {
            result = RIGHT;
        }
        else {
            result = CENTER;
        }

        return result;
    }

    /**
     * Transforms a string to an integer alignment constant.
     * @param value string representation
     * @return the alignment
     */
    public int valueOf(final String value) {
        final String lc = value.toLowerCase(Locale.ENGLISH);
        final int result;

        if (lc.equals(LEFT)) {
            result = SwingConstants.LEFT;
        }
        else if (lc.equals(TOP)) {
            result = SwingConstants.TOP;
        }
        else if (lc.equals(RIGHT)) {
            result = SwingConstants.RIGHT;
        }
        else if (lc.equals(BOTTOM)) {
            result = SwingConstants.BOTTOM;
        }
        else {
            result = SwingConstants.CENTER;
        }

        return result;
    }
}
