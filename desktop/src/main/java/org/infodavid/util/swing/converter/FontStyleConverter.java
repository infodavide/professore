package org.infodavid.util.swing.converter;

import java.awt.Font;
import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * The Class FontStyleConverter.
 */
public final class FontStyleConverter {

    /** bold font. */
    public static final String BOLD = "bold";

    /** text in italic. */
    public static final String ITALIC = "italic";

    /** plain text. */
    public static final String PLAIN = "plain";

    /** The singleton. */
    private static WeakReference<FontStyleConverter> singleton = null;

    /**
     * return the singleton.
     * @return the singleton
     */
    public static FontStyleConverter getSingleton() {
        if (singleton == null || singleton.get() == null) {
            singleton = new WeakReference<>(new FontStyleConverter());
        }

        return singleton.get();
    }

    /**
     * constructor.
     */
    private FontStyleConverter() {
        super();
    }

    /**
     * Transforms a font style to string representation.
     * @param style font style
     * @return string representation
     */
    public String toString(final int style) {
        final String result;

        if (style == Font.BOLD) {
            result = BOLD;
        }
        else if (Font.ITALIC == style) {
            result = ITALIC;
        }
        else if ((Font.ITALIC | Font.BOLD) == style) {
            result = BOLD + ITALIC;
        }
        else {
            result = PLAIN;
        }

        return result;
    }

    /**
     * Transforms a string in font style.
     * @param value the string value
     * @return the font style
     */
    public int valueOf(final String value) {
        final String t = value.toLowerCase(Locale.ENGLISH);
        final int result;

        if (t.equals(BOLD)) {
            result = Font.BOLD;
        }
        else if (t.equals(ITALIC)) {
            result = Font.ITALIC;
        }
        else if (t.equals(BOLD + ITALIC) || t.equals(ITALIC + '-' + BOLD) || t.equals(BOLD + '-' + ITALIC) || t.equals(ITALIC + BOLD)) {
            result = Font.ITALIC | Font.BOLD;
        }
        else {
            result = Font.PLAIN;
        }

        return result;
    }
}
