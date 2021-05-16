package org.infodavid.util.swing.converter;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.util.swing.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ColorConverter.
 */
public final class ColorConverter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ColorConverter.class);

    /** The properties. */
    private static Properties properties = null;

    /** The singleton. */
    private static WeakReference<ColorConverter> singleton = null;

    /**
     * Return the singleton.
     * @return the singleton
     */
    public static synchronized ColorConverter getSingleton() {
        if (singleton == null || singleton.get() == null) {
            if (properties == null) {
                synchronized (ColorConverter.class) {
                    properties = AccessController.doPrivileged(new PrivilegedAction<>() { // NOSONAR No lambda here
                        public Properties run() {
                            final String path = Constants.DEFAULT_RESOURCE_PATH + ColorConverter.class.getName() + ".properties";
                            final Properties result = new Properties();

                            LOGGER.info("Load color definitions from resource: {}", path);

                            try (InputStream in = ColorConverter.class.getResourceAsStream(path)) {
                                result.load(in);

                                LOGGER.info("{} definitions loaded", String.valueOf(result.size()));
                            }
                            catch (final IOException e) {
                                LOGGER.error("Cannot load color definitions from resource: " + path, e);
                            }

                            return result;
                        }
                    });
                }
            }

            singleton = new WeakReference<>(new ColorConverter());
        }

        return singleton.get();
    }

    /** The recursive. */
    private boolean recursive = false;

    /**
     * Instantiates a new color converter.
     */
    private ColorConverter() {
        super();
    }

    /**
     * Gets the string.
     * @param value the value
     * @return the string
     */
    public String getString(final String value) {
        String result = properties.getProperty(StringUtils.lowerCase(value, Locale.ENGLISH));

        if (result == null) {
            LOGGER.warn("Cannot find property: {}", value);

            result = value;
        }

        return result;
    }

    /**
     * To string.
     * @param color the color
     * @return the string
     */
    public String toString(final Color color) {
        return "" + color.getRed() + ',' + color.getGreen() + ',' + color.getBlue() + ',' + color.getAlpha();
    }

    /**
     * Value of.
     * @param value the value
     * @return the color
     */
    public synchronized Color valueOf(final String value) {
        final String lc = StringUtils.lowerCase(value, Locale.ENGLISH);
        Color result = null;

        try {
            if (lc.startsWith("0x")) {
                result = new Color(Integer.parseInt(lc.substring(2), 16));
            }
            else if (lc.startsWith("#")) {
                result = new Color(Integer.parseInt(lc.substring(1), 16));
            }
            else {
                final int pos = lc.indexOf(",");

                if (pos != -1) { // couleur exprimee en r,v,b,a
                    final StringTokenizer st = new StringTokenizer(lc, ",");
                    short r = 0;
                    short v = 0;
                    short b = 0;

                    if (st.hasMoreElements()) {
                        r = Short.parseShort((String)st.nextElement());
                    }

                    if (st.hasMoreElements()) {
                        v = Short.parseShort((String)st.nextElement());
                    }

                    if (st.hasMoreElements()) {
                        b = Short.parseShort((String)st.nextElement());
                    }

                    if (st.hasMoreElements()) {
                        result = new Color(r, v, b, Short.parseShort((String)st.nextElement()));
                    }
                    else {
                        result = new Color(r, v, b);
                    }
                }
                else {
                    final String t = getString(lc);

                    if (recursive) { // couleur via rgb style css
                        result = Color.decode(lc);
                    }
                    else {
                        recursive = true;
                        result = valueOf(t);
                        recursive = false;
                    }
                }
            }
        }
        catch (final NumberFormatException e) {
            LOGGER.warn("Cannot evaluate color: " + value, e);

            result = Color.BLACK;
        }

        return result;
    }
}
