package org.infodavid.util.swing.action;

import java.io.IOError;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.Action;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.infodavid.util.ImplementationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ActionProvider.
 */
public class ActionProvider {

    /** The Constant INSTANCE. */
    private static final ActionProvider INSTANCE;

    /** The Constant LOGGER. */
    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(ActionProvider.class);

        try {
            INSTANCE = new ActionProvider();
        }
        catch (final IOException e) { // NOSONAR No contextual data
            LOGGER.error("Cannot initialize: " + ActionProvider.class.getName(), e);

            throw new IOError(e);
        }
    }

    /**
     * Gets the single instance.
     * @return the instance
     */
    public static ActionProvider getInstance() {
        return INSTANCE;
    }

    /** The actions. */
    private final ImplementationLoader<Action> actions;

    /**
     * Instantiates a new registry.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private ActionProvider() throws IOException {
        super();

        actions = ImplementationLoader.load(Action.class);

        if (actions.isEmpty()) {
            LOGGER.warn("No action registered");
        }
    }

    /**
     * Gets the action.
     * @param key the key of the element
     * @return the action or null
     */
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public Action getAction(final String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }

        Class clazz = actions.get(key);

        if (clazz == null) {
            final Iterator<Entry<String,Class<? extends Action>>> ite = actions.entryIterator();

            while (ite.hasNext()) {
                final Entry<String,Class<? extends Action>> entry = ite.next();

                if (StringUtils.endsWithIgnoreCase(key, '.' + entry.getKey())) {
                    clazz = entry.getClass();

                    break;
                }
            }
        }

        if (clazz == null) {
            LOGGER.warn("No action find for the given key: {}", key);

            return null;
        }

        final Constructor constructor = ConstructorUtils.getAccessibleConstructor(clazz, String.class);

        if (constructor == null) {
            LOGGER.warn("No constructor with String argument find in class: {}", clazz.getName());

            return null;
        }

        try {
            return (Action)constructor.newInstance(key);
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LOGGER.warn("Cannot instantiate action: {}", clazz.getName());

            return null;
        }
    }
}
