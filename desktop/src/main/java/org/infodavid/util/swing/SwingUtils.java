package org.infodavid.util.swing;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.UnaryOperator;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.util.preferences.IniPreferencesFactory;
import org.infodavid.util.swing.action.ActionProvider;
import org.infodavid.util.swing.action.ClearAction;
import org.infodavid.util.swing.action.SelectAllAction;
import org.infodavid.util.swing.component.CustomCheckBox;
import org.infodavid.util.swing.converter.ColorConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SwingUtils.
 */
public final class SwingUtils {

    /** The Constant CANNOT_LOAD_ICON_PATTERN. */
    private static final String CANNOT_LOAD_ICON_PATTERN = "Cannot load icon: ";

    /** The Constant ICON_NOT_FOUND_PATTERN. */
    private static final String ICON_NOT_FOUND_PATTERN = "Icon not found: {}";

    /** The singleton. */
    private static WeakReference<SwingUtils> instance = null;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwingUtils.class);

    static {
        System.setProperty("java.util.prefs.PreferencesFactory", IniPreferencesFactory.class.getName());
    }

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized SwingUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new SwingUtils());
        }

        return instance.get();
    }

    /**
     * Instantiates a new util.
     */
    private SwingUtils() {
        super();
    }

    /**
     * Adds the default window icon.
     * @param frame the frame
     * @param properties the properties
     */
    public void addDefaultWindowIcon(final JFrame frame, final Properties properties) { // NOSONAR Argument can be used in the future
        final String icon = "/icons/default_window_icon.png";

        try (InputStream in = getClass().getResourceAsStream(icon)) {
            if (in == null) {
                LOGGER.warn("Cannot load window icon: {}", icon);
            }
            else {
                frame.setIconImage(ImageIO.read(in));
            }
        }
        catch (final IOException e) {
            LOGGER.warn("Cannot load window icon: " + icon, e);
        }
    }

    /**
     * Apply.
     * @param component the component
     * @param function the function
     * @param recursive the recursive
     */
    public void apply(final Component component, final UnaryOperator<Component> function, final boolean recursive) {
        final Container c = component instanceof Container ? (Container)component : null;

        if (c != null) {
            for (final Component comp : c.getComponents()) {
                function.apply(component);

                if (comp instanceof Container && recursive) {
                    apply(comp, function, recursive);
                }
            }
        }
    }

    /**
     * Center on screen.
     * @param frame the frame
     * @param dialog the dialog
     */
    public void centerOnScreen(final JFrame frame, final JDialog dialog) {
        final Rectangle screen = frame.getGraphicsConfiguration().getBounds();

        dialog.setLocation(screen.x + (screen.width - dialog.getWidth()) / 2, screen.y + (screen.height - dialog.getHeight()) / 2);
    }

    /**
     * Creates the application context.
     * @param clazz the clazz
     * @return the swing application context
     */
    public SwingApplicationContext createApplicationContext(final Class<?> clazz) {
        return createApplicationContext(clazz, null);
    }

    /**
     * Creates the application context.
     * @param clazz the clazz
     * @param resourcePath the resource path
     * @return the swing application context
     */
    public SwingApplicationContext createApplicationContext(final Class<?> clazz, final String resourcePath) {
        final SwingApplicationContext result = new SwingApplicationContext(clazz);
        String path = Constants.DEFAULT_RESOURCE_PATH.substring(1).replace('/', '.') + "default_application_resources";

        LOGGER.info("Loading default resources from: {}", path);

        result.addResources(path);

        if (StringUtils.isNotEmpty(resourcePath)) {
            path = resourcePath.replace(".properties", "").substring(1).replace('/', '.');

            LOGGER.info("Loading resources from: {}", path);

            result.addResources(path);
        }

        return result;
    }

    /**
     * Creates the button.
     * @param properties the properties
     * @param context the context
     * @param key the key
     * @return the button
     */
    public AbstractButton createButton(final Properties properties, final SwingApplicationContext context, final String key) {
        final String type = properties.getProperty(key + Constants.CLASS_SUFFIX);
        final AbstractButton result;

        if (CustomCheckBox.class.getSimpleName().equalsIgnoreCase(type)) {
            result = new CustomCheckBox();
        }
        else if (JToggleButton.class.getSimpleName().equalsIgnoreCase(type)) {
            result = new JToggleButton();
        }
        else {
            result = new JButton();
        }

        updateButton(result, properties, context, key);

        return result;
    }

    /**
     * Creates the buttons.
     * @param properties the properties
     * @param context the context
     * @param parent the parent panel or box
     * @param key the key
     * @param components the map of created components indexed by their keys
     * @return the given container
     */
    public Container createButtons(final Properties properties, final SwingApplicationContext context, final Container parent, final String key, final ComponentsMap components) { // NOSONAR Parameters count
        byte orientation = SwingConstants.HORIZONTAL;

        if (parent.getLayout() instanceof BoxLayout) {
            final BoxLayout layout = (BoxLayout)parent.getLayout();

            if (layout.getAxis() == BoxLayout.Y_AXIS || layout.getAxis() == BoxLayout.PAGE_AXIS) {
                orientation = SwingConstants.VERTICAL;
            }
        }

        final String[] items = StringUtils.split(properties.getProperty(key + Constants.ITEMS_SUFFIX), ',');

        if (items == null || items.length == 0) {
            LOGGER.warn("No items defined for parent: {}", key);

            return parent;
        }

        parent.getInsets().set(2, 4, 2, 4);

        int w = parent.getPreferredSize().width;
        int h = parent.getPreferredSize().height;

        for (final String item : items) {
            if (StringUtils.isEmpty(item)) {
                continue;
            }

            if (item.charAt(0) == '-') {
                final JSeparator separator = new JSeparator(orientation);

                separator.getInsets().set(2, 4, 2, 4);
                parent.add(separator);
            }
            else {
                final String itemKey = key + '.' + item;
                final AbstractButton button = createButton(properties, context, itemKey);

                button.setMargin(new Insets(2, 4, 2, 4));
                parent.add(button);
                components.put(itemKey, button);

                if (orientation == SwingConstants.HORIZONTAL) {
                    w += button.getPreferredSize().width;
                    h = Math.max(button.getPreferredSize().height, h);
                }
                else {
                    w = Math.max(button.getPreferredSize().width, w);
                    h += button.getPreferredSize().height;
                }
            }
        }

        parent.setPreferredSize(new Dimension(w, h));

        return parent;
    }

    /**
     * Creates the check box.
     * @param properties the properties
     * @param context the context
     * @param parent the parent
     * @param constraints the constraints
     * @param row the row
     * @param key the key
     * @return the custom check box
     */
    public JCheckBox createCheckBox(final Properties properties, final SwingApplicationContext context, final JPanel parent, final GridBagConstraints constraints, final byte row, final String key) {
        if (constraints != null) {
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.gridx = 0;
            constraints.gridy = row;
            constraints.gridwidth = 2;
        }

        final JCheckBox result = new CustomCheckBox(getCapitalizedString(properties, context, key, Constants.LABEL_SUFFIX));

        update(result, properties, context, key);

        if (constraints != null && parent != null) {
            parent.add(result, constraints);
        }

        return result;
    }

    /**
     * Creates the combo box.
     * @param <T> the generic type
     * @param properties the properties
     * @param context the context
     * @param parent the parent
     * @param constraints the constraints
     * @param row the row
     * @param key the key
     * @param clazz the clazz
     * @return the j combo box
     */
    public <T> JComboBox<T> createComboBox(final Properties properties, final SwingApplicationContext context, final JComponent parent, final GridBagConstraints constraints, final byte row, final String key, final Class<T> clazz) { // NOSONAR Parameters count
        if (constraints != null && parent != null) {
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.gridx = 0;
            constraints.gridy = row;
            constraints.gridwidth = 1;

            final JLabel label = new JLabel(getCapitalizedString(properties, context, key, Constants.LABEL_SUFFIX));

            update(label, properties, context, key + Constants.LABEL_SUFFIX);
            parent.add(label, constraints);

            constraints.gridx = 1;
        }

        final JComboBox<T> result = new JComboBox<>();

        update(result, properties, context, key);

        if (constraints != null && parent != null) {
            parent.add(result, constraints);
        }

        return result;
    }

    /**
     * Creates the grid bag constraints.
     * @return the grid bag constraints
     */
    public GridBagConstraints createGridBagConstraints() {
        final GridBagConstraints result = new GridBagConstraints();

        result.fill = GridBagConstraints.HORIZONTAL;
        result.insets = new Insets(2, 2, 2, 2);
        result.weightx = 1;
        result.weighty = 1;
        result.gridx = 0;

        return result;
    }

    /**
     * Creates the label.
     * @param properties the properties
     * @param context the context
     * @param parent the parent
     * @param constraints the constraints
     * @param row the row
     * @param key the key
     * @param defaultValue the default value
     * @return the label
     */
    public JLabel createLabel(final Properties properties, final SwingApplicationContext context, final JComponent parent, final GridBagConstraints constraints, final byte row, final String key, final String defaultValue) {
        if (constraints != null && parent != null) {
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.gridx = 0;
            constraints.gridy = row;
            constraints.gridwidth = 1;

            final JLabel label = new JLabel(getCapitalizedString(properties, context, key, Constants.LABEL_SUFFIX));

            update(label, properties, context, key + Constants.LABEL_SUFFIX);
            parent.add(label, constraints);

            constraints.gridx = 1;
        }

        final JLabel result = new JLabel(StringUtils.defaultString(getCapitalizedString(properties, context, key, Constants.TEXT_SUFFIX), defaultValue));

        update(result, properties, context, key);

        if (constraints != null && parent != null) {
            parent.add(result, constraints);
        }

        return result;
    }

    /**
     * Creates the labels.
     * @param properties the properties
     * @param context the context
     * @param parent the parent
     * @param constraints the constraints
     * @param startRow the start row
     * @param keys the keys
     * @param defaultValue the default value
     * @param components the map of created components indexed by their keys
     */
    public void createLabels(final Properties properties, final SwingApplicationContext context, final JComponent parent, final GridBagConstraints constraints, final byte startRow, final String[] keys, final String defaultValue, final ComponentsMap components) { // NOSONAR Parameters count
        byte row = startRow;

        for (final String key : keys) {
            components.put(key, createLabel(properties, context, parent, constraints, row++, key, defaultValue));
        }
    }

    /**
     * Creates the menu.
     * @param properties the properties
     * @param context the context
     * @param key the key
     * @param components the map of created components indexed by their keys
     * @return the menu
     */
    public JMenu createMenu(final Properties properties, final SwingApplicationContext context, final String key, final ComponentsMap components) {
        final JMenu result = new JMenu();

        update(result, properties, context, key);
        components.put(key, result);

        final String[] items = StringUtils.split(properties.getProperty(key + Constants.ITEMS_SUFFIX), ',');

        if (items == null || items.length == 0) {
            LOGGER.warn("No items defined for menu: {}", key);

            return result;
        }

        for (final String item : items) {
            if (StringUtils.isEmpty(item)) {
                continue;
            }

            if (item.charAt(0) == '-') {
                result.addSeparator();
            }
            else {
                final String itemKey = key + '.' + item;
                final JMenuItem menuItem = createMenuItem(properties, context, itemKey);

                result.add(menuItem);
                components.put(itemKey, menuItem);
            }
        }

        return result;
    }

    /**
     * Creates the menu.
     * @param properties the properties
     * @param context the context
     * @param key the key
     * @param components the map of created components indexed by their keys
     * @return the menu
     */
    public JMenuBar createMenuBar(final Properties properties, final SwingApplicationContext context, final String key, final ComponentsMap components) {
        final JMenuBar result = new JMenuBar();

        update(result, properties, context, key);
        components.put(key, result);

        final String[] items = StringUtils.split(properties.getProperty(key + Constants.ITEMS_SUFFIX), ',');

        if (items == null || items.length == 0) {
            LOGGER.warn("No items defined for menu bar: {}", key);

            return result;
        }

        for (final String item : items) {
            if (StringUtils.isEmpty(item)) {
                continue;
            }

            if (item.charAt(0) == '-') {
                // noop
            }
            else {
                final String itemKey = key + '.' + item;
                final JMenu menu = createMenu(properties, context, itemKey, components);

                result.add(menu);
                components.put(itemKey, menu);
            }
        }

        return result;
    }

    /**
     * Creates the menu item.
     * @param properties the properties
     * @param context the context
     * @param key the key
     * @return the menu item
     */
    public JMenuItem createMenuItem(final Properties properties, final SwingApplicationContext context, final String key) {
        final JMenuItem result = new JMenuItem();

        updateMenuItem(result, properties, context, key);

        return result;
    }

    /**
     * Builds the popup menu.
     * @param properties the properties
     * @param context the context
     * @param key the key
     * @param components the map of created components indexed by their keys
     * @return the popup menu
     */
    public JPopupMenu createPopupMenu(final Properties properties, final SwingApplicationContext context, final String key, final ComponentsMap components) {
        final JPopupMenu result = new JPopupMenu();

        update(result, properties, context, key);
        components.put(key, result);

        final String[] items = StringUtils.split(properties.getProperty(key + Constants.ITEMS_SUFFIX), ',');

        if (items == null || items.length == 0) {
            LOGGER.warn("No items defined for popup menu: {}", key);

            return result;
        }

        for (final String item : items) {
            if (StringUtils.isEmpty(item)) {
                continue;
            }

            if (item.charAt(0) == '-') {
                result.addSeparator();
            }
            else {
                final String itemKey = key + '.' + item;
                final JMenuItem menuItem = createMenuItem(properties, context, itemKey);

                result.add(menuItem);
                components.put(itemKey, menuItem);
            }
        }

        return result;
    }

    /**
     * Creates the progress bar.
     * @param properties the properties
     * @param context the context
     * @param parent the parent
     * @param constraints the constraints
     * @param row the row
     * @param key the key
     * @param min the min
     * @param max the max
     * @return the j progress bar
     */
    public JProgressBar createProgressBar(final Properties properties, final SwingApplicationContext context, final JPanel parent, final GridBagConstraints constraints, final byte row, final String key, final int min, final int max) {
        if (constraints != null && parent != null) {
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.gridx = 0;
            constraints.gridy = row;
            constraints.gridwidth = 1;

            final JLabel label = new JLabel(getCapitalizedString(properties, context, key, Constants.LABEL_SUFFIX));

            update(label, properties, context, key + Constants.LABEL_SUFFIX);
            parent.add(label, constraints);

            constraints.gridx = 1;
        }

        final JProgressBar result = new JProgressBar(min, max);

        update(result, properties, context, key);

        if (constraints != null && parent != null) {
            parent.add(result, constraints);
        }

        return result;
    }

    /**
     * Creates the slider.
     * @param properties the properties
     * @param context the context
     * @param parent the parent
     * @param constraints the constraints
     * @param row the row
     * @param key the key
     * @param orientation the orientation
     * @param min the min
     * @param max the max
     * @param initialValue the initial value
     * @return the slider
     */
    public JSlider createSlider(final Properties properties, final SwingApplicationContext context, final JPanel parent, final GridBagConstraints constraints, final byte row, final String key, final int orientation, final int min, final int max, final int initialValue) {
        if (constraints != null && parent != null) {
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.gridx = 0;
            constraints.gridy = row;
            constraints.gridwidth = 1;

            final JLabel label = new JLabel(getCapitalizedString(properties, context, key, Constants.LABEL_SUFFIX));

            update(label, properties, context, key + Constants.LABEL_SUFFIX);
            parent.add(label, constraints);

            constraints.gridx = 1;
        }

        final JSlider result = new JSlider(orientation, min, max, context.getPreferences().getInt(key + Constants.VALUE_SUFFIX, initialValue));
        final String value = properties.getProperty(key + Constants.TOOL_TIP_TEXT_SUFFIX);

        if (StringUtils.isNotEmpty(value)) {
            result.setToolTipText(value);
        }

        update(result, properties, context, key);

        if (constraints != null && parent != null) {
            parent.add(result, constraints);
        }

        result.addChangeListener(e -> {
            final JSlider source = (JSlider)e.getSource();

            LOGGER.info("Value of {} changed to: {}", key, String.valueOf(source.getValue()));

            context.getPreferences().putInt(key + Constants.VALUE_SUFFIX, source.getValue());
        });

        return result;
    }

    /**
     * Creates the text field.
     * @param properties the properties
     * @param context the context
     * @param parent the parent
     * @param constraints the constraints
     * @param row the row
     * @param key the key
     * @param defaultValue the default value
     * @return the text field
     */
    public JTextField createTextField(final Properties properties, final SwingApplicationContext context, final JComponent parent, final GridBagConstraints constraints, final byte row, final String key, final String defaultValue) {
        if (constraints != null && parent != null) {
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.gridx = 0;
            constraints.gridy = row;
            constraints.gridwidth = 1;

            final JLabel label = new JLabel(getCapitalizedString(properties, context, key, Constants.LABEL_SUFFIX));

            update(label, properties, context, key + Constants.LABEL_SUFFIX);
            parent.add(label, constraints);

            constraints.gridx = 1;
        }

        final JTextField result = new JTextField(StringUtils.defaultString(getCapitalizedString(properties, context, key, Constants.TEXT_SUFFIX), defaultValue));

        update(result, properties, context, key);

        if (constraints != null && parent != null) {
            parent.add(result, constraints);
        }

        return result;
    }

    /**
     * Creates the text fields.
     * @param properties the properties
     * @param context the context
     * @param parent the parent
     * @param constraints the constraints
     * @param startRow the start row
     * @param keys the keys
     * @param defaultValue the default value
     * @param components the map of created components indexed by their keys
     */
    public void createTextFields(final Properties properties, final SwingApplicationContext context, final JComponent parent, final GridBagConstraints constraints, final byte startRow, final String[] keys, final String defaultValue, final ComponentsMap components) { // NOSONAR Parameters count
        byte row = startRow;

        for (final String key : keys) {
            components.put(key, createTextField(properties, context, parent, constraints, row++, key, defaultValue));
        }
    }

    /**
     * Creates the titled border.
     * @param properties the properties
     * @param context the context
     * @param key the key
     * @param justification the justification
     * @return the titled border
     */
    public TitledBorder createTitledBorder(final Properties properties, final SwingApplicationContext context, final String key, final int justification) {
        final String value = properties.getProperty(key + Constants.TITLE_SUFFIX);
        final TitledBorder result;

        if (StringUtils.isNotEmpty(value)) {
            result = BorderFactory.createTitledBorder(context.getResources().getCapitalizedString(value));
        }
        else {
            result = BorderFactory.createTitledBorder(context.getResources().getCapitalizedString(key));
        }

        result.setTitleJustification(justification);

        return result;
    }

    /**
     * Creates the tool bar.
     * @param properties the properties
     * @param context the context
     * @param key the key
     * @param components the map of created components indexed by their keys
     * @return the tool bar
     */
    public JToolBar createToolBar(final Properties properties, final SwingApplicationContext context, final String key, final ComponentsMap components) {
        final JToolBar result = new JToolBar();

        update(result, properties, context, key);
        components.put(key, result);

        final String[] items = StringUtils.split(properties.getProperty(key + Constants.ITEMS_SUFFIX), ',');

        if (items == null || items.length == 0) {
            LOGGER.warn("No items defined for tool bar: {}", key);

            return result;
        }

        for (final String item : items) {
            if (StringUtils.isEmpty(item)) {
                continue;
            }

            if (item.charAt(0) == '-') {
                result.addSeparator();
            }
            else {
                final String itemKey = key + '.' + item;
                final AbstractButton button = createButton(properties, context, itemKey);

                result.add(button);
                components.put(itemKey, button);
            }
        }

        return result;
    }

    /**
     * Enable.
     * @param component the component
     * @param value the value
     * @param recursive the recursive
     */
    public void enable(final Component component, final boolean value, final boolean recursive) {
        final Container c = component instanceof Container ? (Container)component : null;

        if (c != null) {
            for (final Component comp : c.getComponents()) {
                comp.setEnabled(value);

                if (comp instanceof Container && recursive) {
                    enable(comp, value, recursive);
                }
            }
        }
    }

    /**
     * Search for components into a specified container.
     * @param <T> the generic type
     * @param container the container
     * @param clazz the class
     * @return the matching components
     */
    public <T> Collection<T> findComponentsByClass(final Container container, final Class<T> clazz) {
        final Set<T> results = new LinkedHashSet<>();

        findComponentsByClass(container, clazz, new LinkedHashSet<>(), results);

        return results;
    }

    /**
     * Search for components into a specified container.
     * @param <T> the generic type
     * @param container the container
     * @param name the name
     * @return the matching components
     */
    public <T> Collection<T> findComponentsByName(final Container container, final String name) {
        final Set<T> results = new LinkedHashSet<>();

        findComponentsByName(container, name, new LinkedHashSet<>(), results);

        return results;
    }

    /**
     * Gets the capitalized string.
     * @param properties the properties
     * @param context the context
     * @param key the key
     * @param suffix the suffix
     * @return the capitalized string
     */
    public String getCapitalizedString(final Properties properties, final SwingApplicationContext context, final String key, final String suffix) {
        // key of the component resource from definition properties file
        String result = properties.getProperty(key + suffix);

        if (StringUtils.isEmpty(result)) {
            // use of the simple name of the component (without its parent path)
            result = StringUtils.substringAfterLast(key, org.infodavid.util.StringUtils.DOT);
        }

        if (StringUtils.isNotEmpty(result)) {
            result = context.getResources().getCapitalizedString(result);
        }
        else {
            result = String.format(Constants.MISSING_RESOURCE_PATTERN, key + suffix);
        }

        return result;
    }

    /**
     * Load properties.
     * @param clazz the clazz
     * @return the properties
     */
    public Properties loadProperties(final Class<? extends Component> clazz) {
        final String path = Constants.DEFAULT_RESOURCE_PATH + clazz.getName();

        LOGGER.info("Loading new properties from resource: {}", path);

        return AccessController.doPrivileged(new PrivilegedAction<>() { // NOSONAR No lambda here
            public Properties run() {
                final Properties result = new Properties();

                try (InputStream in = SwingUtils.class.getResourceAsStream(path)) {
                    result.load(in);

                    LOGGER.info("{} properties loaded", String.valueOf(result.size()));
                }
                catch (final IOException | RuntimeException e) {
                    LOGGER.error("Cannot load properties from resource: {}", path);
                }

                return result;
            }
        });
    }

    /**
     * Load properties.
     * @param clazz the clazz
     * @param properties the properties
     * @return the properties
     */
    public Properties loadProperties(final Class<? extends Component> clazz, final Properties properties) {
        final String path = Constants.DEFAULT_RESOURCE_PATH + clazz.getName();

        LOGGER.info("Loading and updating properties from resource: {}", path);

        return AccessController.doPrivileged(new PrivilegedAction<>() { // NOSONAR No lambda here
            public Properties run() {
                final int previousSize = properties.size();

                try (InputStream in = SwingUtils.class.getResourceAsStream(path)) {
                    properties.load(in);

                    LOGGER.info("{} properties loaded", String.valueOf(properties.size() - previousSize));
                }
                catch (final IOException | RuntimeException e) {
                    LOGGER.error("Cannot load properties from resource: {}", path);
                }

                return properties;
            }
        });
    }

    /**
     * Scroll to bottom.
     * @param scrollPane the scroll pane
     */
    public void scrollToBottom(final JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(final AdjustmentEvent e) {
                final Adjustable adjustable = e.getAdjustable();

                adjustable.setValue(adjustable.getMaximum());
                scrollPane.getVerticalScrollBar().removeAdjustmentListener(this);
            }
        });
    }

    /**
     * Set some component properties for the specified component.</br>
     * (font, colors, tooltip text, text, etc...)
     * @param component the component
     * @param properties the properties
     * @param context the context
     * @param key the command string
     */
    public void update(final Component component, final Properties properties, final SwingApplicationContext context, final String key) { // NOSONAR Argument can be used in the future
        final ColorConverter colorConverter = ColorConverter.getSingleton();
        String value;

        component.setName(key);

        if (!JMenuItem.class.isInstance(component)) {
            value = context.getPreferences().get(key + Constants.FONT_SUFFIX, null); // font

            if (StringUtils.isEmpty(value)) {
                value = properties.getProperty(key + Constants.FONT_SUFFIX); // font
            }

            if (StringUtils.isNotEmpty(value)) {
                component.setFont(Font.decode(value));
            }
        }

        value = properties.getProperty(key + Constants.BACKGROUND_COLOR_SUFFIX); // background color

        if (StringUtils.isNotEmpty(value)) {
            component.setBackground(colorConverter.valueOf(value));
        }

        value = properties.getProperty(key + Constants.FOREGROUND_COLOR_SUFFIX); // background color

        if (StringUtils.isNotEmpty(value)) {
            component.setForeground(colorConverter.valueOf(value));
        }
    }

    /**
     * Set some component properties for the specified component.</br>
     * (font, colors, tooltip text, text, etc...)
     * @param component the component
     * @param properties the properties
     * @param context the context
     * @param key the command string
     */
    public void update(final JTextComponent component, final Properties properties, final SwingApplicationContext context, final String key) {
        final ColorConverter colorConverter = ColorConverter.getSingleton();
        String value = properties.getProperty(key + ".selection" + Constants.FOREGROUND_COLOR_SUFFIX); // couleur du texte de selection

        if (StringUtils.isNotEmpty(value)) {
            component.setSelectedTextColor(colorConverter.valueOf(value));
            component.setSelectionColor(colorConverter.valueOf(value));
        }

        value = properties.getProperty(key + ".selection" + Constants.BACKGROUND_COLOR_SUFFIX); // couleur du fond de selection

        if (StringUtils.isNotEmpty(value)) {
            component.setSelectionColor(colorConverter.valueOf(value));
        }

        update((Component)component, properties, context, key);
    }

    /**
     * Set some component properties for the specified component.</br>
     * (font, colors, tooltip text, text, etc...)
     * @param component the component
     * @param properties the properties
     * @param context the context
     * @param key the command string
     */
    public void updateButton(final AbstractButton component, final Properties properties, final SwingApplicationContext context, final String key) { // NOSONAR No complexity
        String value = properties.getProperty(key + Constants.ACTION_SUFFIX);

        if (StringUtils.isNotEmpty(value)) {
            if (Constants.CUT.equalsIgnoreCase(value)) {
                component.setAction(new DefaultEditorKit.CutAction());
            }
            else if (Constants.COPY.equalsIgnoreCase(value)) {
                component.setAction(new DefaultEditorKit.CopyAction());
            }
            else if (Constants.PASTE.equalsIgnoreCase(value)) {
                component.setAction(new DefaultEditorKit.PasteAction());
            }
            else if (Constants.SELECT_ALL.equalsIgnoreCase(value)) {
                component.setAction(new SelectAllAction(Constants.SELECT_ALL));
            }
            else if (Constants.CLEAR.equalsIgnoreCase(value)) {
                component.setAction(new ClearAction(Constants.CLEAR));
            }
            else {
                final Action action = ActionProvider.getInstance().getAction(value);

                if (action != null) {
                    component.setAction(action);
                }
            }
        }

        value = properties.getProperty(key + Constants.ICON_SUFFIX);

        if (StringUtils.isNotEmpty(value)) {
            try (InputStream in = getClass().getResourceAsStream(value)) {
                if (in == null) {
                    LOGGER.warn(ICON_NOT_FOUND_PATTERN, value);
                }
                else {
                    component.setIcon(new ImageIcon(ImageIO.read(in).getScaledInstance(16, 16, 0)));
                }
            }
            catch (final IOException e) {
                LOGGER.warn(CANNOT_LOAD_ICON_PATTERN + value, e);
            }
        }

        if (component instanceof JToggleButton) {
            final JToggleButton toggle = (JToggleButton)component;
            value = properties.getProperty(key + Constants.ON_TEXT_SUFFIX);

            if (StringUtils.isNotEmpty(value)) {
                value = context.getResources().getCapitalizedString(value);

                toggle.putClientProperty(Constants.ON_TEXT_SUFFIX, value);

                if (toggle.isSelected()) {
                    toggle.setText(value);
                }
            }

            value = properties.getProperty(key + Constants.OFF_TEXT_SUFFIX);

            if (StringUtils.isNotEmpty(value)) {
                value = context.getResources().getCapitalizedString(value);

                toggle.putClientProperty(Constants.OFF_TEXT_SUFFIX, value);

                if (!toggle.isSelected()) {
                    toggle.setText(value);
                }
            }

            value = properties.getProperty(key + Constants.ON_TOOL_TIP_TEXT_SUFFIX);

            if (StringUtils.isNotEmpty(value)) {
                value = context.getResources().getCapitalizedString(value);

                toggle.putClientProperty(Constants.ON_TOOL_TIP_TEXT_SUFFIX, value);

                if (toggle.isSelected()) {
                    toggle.setToolTipText(value);
                }
            }

            value = properties.getProperty(key + Constants.OFF_TOOL_TIP_TEXT_SUFFIX);

            if (StringUtils.isNotEmpty(value)) {
                value = context.getResources().getCapitalizedString(value);

                toggle.putClientProperty(Constants.OFF_TOOL_TIP_TEXT_SUFFIX, value);

                if (!toggle.isSelected()) {
                    toggle.setToolTipText(value);
                }
            }

            value = properties.getProperty(key + Constants.ON_ICON_SUFFIX);

            if (StringUtils.isNotEmpty(value)) {
                try (InputStream in = getClass().getResourceAsStream(value)) {
                    if (in == null) {
                        LOGGER.warn(ICON_NOT_FOUND_PATTERN, value);
                    }
                    else {
                        final ImageIcon icon = new ImageIcon(ImageIO.read(in).getScaledInstance(16, 16, 0));

                        toggle.putClientProperty(Constants.ON_ICON_SUFFIX, icon);

                        if (toggle.isSelected()) {
                            toggle.setIcon(icon);
                        }
                    }
                }
                catch (final IOException e) {
                    LOGGER.warn(CANNOT_LOAD_ICON_PATTERN + value, e);
                }
            }

            value = properties.getProperty(key + Constants.OFF_ICON_SUFFIX);

            if (StringUtils.isNotEmpty(value)) {
                try (InputStream in = getClass().getResourceAsStream(value)) {
                    if (in == null) {
                        LOGGER.warn(ICON_NOT_FOUND_PATTERN, value);
                    }
                    else {
                        final ImageIcon icon = new ImageIcon(ImageIO.read(in).getScaledInstance(16, 16, 0));

                        toggle.putClientProperty(Constants.OFF_ICON_SUFFIX, icon);

                        if (!toggle.isSelected()) {
                            toggle.setIcon(icon);
                        }
                    }
                }
                catch (final IOException e) {
                    LOGGER.warn(CANNOT_LOAD_ICON_PATTERN + value, e);
                }
            }

            toggle.addItemListener(e -> {
                final JToggleButton source = (JToggleButton)e.getSource();

                if (source.isSelected()) {
                    String text = (String)source.getClientProperty(Constants.ON_TEXT_SUFFIX);

                    if (StringUtils.isNotEmpty(text)) {
                        source.setText(text);
                    }

                    text = (String)source.getClientProperty(Constants.ON_TOOL_TIP_TEXT_SUFFIX);

                    if (StringUtils.isNotEmpty(text)) {
                        source.setToolTipText(text);
                    }

                    final Object obj = toggle.getClientProperty(Constants.ON_ICON_SUFFIX);

                    if (obj instanceof ImageIcon) {
                        source.setIcon((ImageIcon)obj);
                    }
                }
                else {
                    String text = (String)source.getClientProperty(Constants.OFF_TEXT_SUFFIX);

                    if (StringUtils.isNotEmpty(text)) {
                        source.setText(text);
                    }

                    text = (String)source.getClientProperty(Constants.OFF_TOOL_TIP_TEXT_SUFFIX);

                    if (StringUtils.isNotEmpty(text)) {
                        source.setToolTipText(text);
                    }

                    final Object obj = toggle.getClientProperty(Constants.OFF_ICON_SUFFIX);

                    if (obj instanceof ImageIcon) {
                        source.setIcon((ImageIcon)obj);
                    }
                }
            });
        }

        // key of the text resource from definition properties file
        value = properties.getProperty(key + Constants.TEXT_SUFFIX);

        if (StringUtils.isEmpty(value) && component.getIcon() == null) {
            // use of the simple name of the component (without its parent path)
            value = StringUtils.substringAfterLast(key, org.infodavid.util.StringUtils.DOT);
        }

        if (StringUtils.isNotEmpty(value)) {
            component.setText(context.getResources().getCapitalizedString(value));
        }

        value = properties.getProperty(key + Constants.TOOL_TIP_TEXT_SUFFIX);

        if (StringUtils.isNotEmpty(value)) {
            component.setToolTipText(value);
        }

        value = properties.getProperty(key + Constants.MNEMONIC_SUFFIX);

        if (StringUtils.isNotEmpty(value)) {
            component.setMnemonic(value.charAt(0));
        }

        update(component, properties, context, key);
    }

    /**
     * Set some component properties for the specified component.</br>
     * (font, colors, tooltip text, text, etc...)
     * @param component the component
     * @param properties the properties
     * @param context the context
     * @param key the command string
     */
    public void updateMenuItem(final JMenuItem component, final Properties properties, final SwingApplicationContext context, final String key) {
        final String value = properties.getProperty(key + Constants.ACCELERATOR_SUFFIX);

        if (StringUtils.isNotEmpty(value)) {
            component.setAccelerator(KeyStroke.getKeyStroke(value));
        }

        updateButton(component, properties, context, key);
    }

    /**
     * Set some component properties for the specified component.</br>
     * (font, colors, tooltip text, text, etc...)
     * @param component the component
     * @param properties the properties
     * @param context the context
     * @param key the command string
     */
    public void updateWindow(final Window component, final Properties properties, final SwingApplicationContext context, final String key) {
        final String w = properties.getProperty(key + Constants.WIDTH_SUFFIX);
        final String h = properties.getProperty(key + Constants.HEIGHT_SUFFIX);

        if (StringUtils.isNumeric(w) && StringUtils.isNumeric(h)) {
            component.setSize(Integer.parseInt(w), Integer.parseInt(h));
        }

        update(component, properties, context, key);
    }

    /**
     * Find components by class.
     * @param <T> the generic type
     * @param container the container
     * @param clazz the clazz
     * @param processed the processed
     * @param results the results
     */
    @SuppressWarnings("unchecked")
    private <T> void findComponentsByClass(final Container container, final Class<T> clazz, final Set<Component> processed, final Set<T> results) {
        if (processed.contains(container)) {
            return;
        }

        processed.add(container);

        if (clazz.isInstance(container)) {
            results.add((T)container);

            return;
        }

        final Component[] components = container.getComponents();

        if (components == null) {
            return;
        }

        for (final Component c : components) {
            if (processed.contains(c)) {
                continue;
            }

            processed.add(c);

            if (clazz.isInstance(c)) {
                results.add((T)c);
            }
            else if (c instanceof Container) {
                findComponentsByClass((Container)c, clazz, processed, results);
            }
        }
    }

    /**
     * Find components by name.
     * @param <T> the generic type
     * @param container the container
     * @param name the name
     * @param processed the processed
     * @param results the results
     */
    @SuppressWarnings("unchecked")
    private <T> void findComponentsByName(final Container container, final String name, final Set<Component> processed, final Set<T> results) {
        if (processed.contains(container)) {
            return;
        }

        processed.add(container);

        if (name.equals(container.getName())) {
            results.add((T)container);

            return;
        }

        final Component[] components = container.getComponents();

        if (components == null) {
            return;
        }

        for (final Component c : components) {
            if (processed.contains(c)) {
                continue;
            }

            processed.add(c);

            if (name.equals(c.getName())) {
                results.add((T)c);
            }
            else if (c instanceof Container) {
                findComponentsByName((Container)c, name, processed, results);
            }
        }
    }
}
