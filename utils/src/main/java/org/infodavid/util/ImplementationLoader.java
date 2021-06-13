package org.infodavid.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ImplementationLoader.
 * @param <S> the generic type
 */
public class ImplementationLoader<S> {

    /** The Constant DEFAULT_RESOURCE_PATH. */
    public static final String DEFAULT_RESOURCE_PATH = "/META-INF/implementations/"; // NOSONAR Not an URL

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImplementationLoader.class);

    /**
     * Creates a new implementation loader for the given type, using the current thread's {@linkplain java.lang.Thread#getContextClassLoader context class loader}.
     * <p>
     * An invocation of this convenience method of the form <blockquote>
     *
     * <pre>
     * ImplementationLoader.load(<i>class</i>)
     * </pre>
     *
     * </blockquote> is equivalent to <blockquote>
     *
     * <pre>
     * ImplementationLoader.load(<i>class</i>, Thread.currentThread().getContextClassLoader())
     * </pre>
     *
     * </blockquote>
     * @param <S> the class of the service type
     * @param clazz The interface or class
     * @return A new implementation loader
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static <S> ImplementationLoader<S> load(final Class<S> clazz) throws IOException {
        return ImplementationLoader.load(clazz, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Creates a new implementation loader for the given type and class loader.
     * @param <S> the type
     * @param clazz The interface or class
     * @param loader The class loader to be used to load provider-configuration files and provider classes, or <tt>null</tt> if the system class loader (or, failing that, the bootstrap class loader) is to be used
     * @return A new implementation loader
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static <S> ImplementationLoader<S> load(final Class<S> clazz, final ClassLoader loader) throws IOException {
        return new ImplementationLoader<>(clazz, loader);
    }

    /** The clazz. */
    private final Class<S> clazz;

    /** The loader. */
    private final ClassLoader loader;

    /** The registry. */
    private final Map<String,Class<? extends S>> registry = new HashMap<>();

    /**
     * Instantiates a new implementation loader.
     * @param clazz the clazz
     * @param loader the loader
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private ImplementationLoader(final Class<S> clazz, final ClassLoader loader) throws IOException {
        this.clazz = Objects.requireNonNull(clazz, "Type cannot be null");
        this.loader = loader == null ? ClassLoader.getSystemClassLoader() : loader;

        reload();
    }

    /**
     * Entry iterator.
     * @return the iterator< entry< string, class<? extends s>>>
     */
    public Iterator<Entry<String,Class<? extends S>>> entryIterator() {
        return registry.entrySet().iterator();
    }

    /**
     * Gets the.
     * @param key the key
     * @return the class<? extends s>
     */
    public Class<? extends S> get(final String key) {
        return registry.get(key);
    }

    /**
     * Gets the or default.
     * @param key the key
     * @param defaultValue the default value
     * @return the or default
     */
    public Class<? extends S> getOrDefault(final Object key, final Class<? extends S> defaultValue) {
        return registry.get(key);
    }

    /**
     * Checks if is empty.
     * @return true, if is empty
     */
    public boolean isEmpty() {
        return registry.isEmpty();
    }

    /**
     * Iterator.
     * @return the iterator< class<? extends s>>
     */
    public Iterator<Class<? extends S>> iterator() {
        return registry.values().iterator();
    }

    /**
     * Size.
     * @return the size
     */
    public int size() {
        return registry.size();
    }

    /**
     * Reload.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    private void reload() throws IOException {
        registry.clear();

        final Enumeration<URL> ite = loader.getResources(DEFAULT_RESOURCE_PATH + clazz.getName());
        String[] parts;
        String entryKey;
        Class entryClazz;
        Iterator<String> lines;
        String line = "";

        while (ite.hasMoreElements()) {
            final URL url = ite.nextElement();

            try (InputStream in = url.openStream()) {
                lines = IOUtils.readLines(in, StandardCharsets.UTF_8).iterator();

                while (lines.hasNext()) {
                    line = lines.next();
                    parts = StringUtils.split(line, '=');

                    if (parts.length == 1) {
                        entryClazz = Class.forName(parts[0]);
                        entryKey = entryClazz.getName();
                    }
                    else {
                        entryClazz = Class.forName(parts[1]);
                        entryKey = parts[0];
                    }

                    if (entryClazz.isAssignableFrom(clazz)) {
                        registry.put(entryKey, entryClazz);

                        LOGGER.info("Implementation {} registered", entryClazz.getName());
                    }
                    else {
                        LOGGER.warn("{} is not a subclass of {}", entryClazz.getName(), clazz.getName());
                    }
                }
            }
            catch (final IOException e) {
                LOGGER.error("Cannot load implementations of: " + clazz.getName() + " from URL: " + url, e);
            }
            catch (final ClassNotFoundException e) {
                LOGGER.error("Cannot load implementation specified in line: {}", line);
            }
        }

        if (registry.isEmpty()) {
            LOGGER.warn("No implementation registered");
        }
    }
}
