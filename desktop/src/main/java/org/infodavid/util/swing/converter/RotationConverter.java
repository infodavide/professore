package org.infodavid.util.swing.converter;

import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * The Class RotationConverter.
 */
public final class RotationConverter {

    /** the suffix used to specify that the value is in degrees. */
    public static final String DEGREES = "deg";

    /** the suffix used to specify that the value is in radians. */
    public static final String RADIANS = "rad";

    /** The singleton. */
    private static WeakReference<RotationConverter> singleton = null;

    /**
     * return the singleton.
     * @return the singleton
     */
    public static RotationConverter getSingleton() {
        if (singleton == null || singleton.get() == null) {
            singleton = new WeakReference<>(new RotationConverter());
        }

        return singleton.get();
    }

    /**
     * Instantiates a new rotation converter.
     */
    private RotationConverter() {
        super();
    }

    /**
     * transform the rotation specified in radians to a string.
     * @param value the rotation value in radians
     * @return the string representation
     */
    public String toString(final double value) {
        return Math.toDegrees(value) + DEGREES;
    }

    /**
     * Decodes the string.
     * @param value the string representation of the rotation value
     * @return the rotation in radians or degrees (depends on suffix)
     */
    public double valueOf(final String value) {
        String s = value.trim().toLowerCase(Locale.ENGLISH);
        byte position = (byte)s.lastIndexOf(RADIANS);
        double result;

        if (position == -1) { // valeur en degres
            position = (byte)s.lastIndexOf(DEGREES);

            if (position != -1) {
                s = s.substring(0, position);
            }

            result = java.lang.Math.toRadians(Double.parseDouble(s));
        }
        else { // valeur en radians
            s = s.substring(0, position);
            result = Double.parseDouble(s);
        }

        return result;
    }
}
