package org.infodavid.util.swing;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

/**
 * The Class SwingApplicationContext.
 */
public class SwingApplicationContext {

    /** The main frame. */
    private JFrame mainFrame;

    /** The preferences. */
    private final Preferences preferences;

    /** The resources. */
    private final ResourceBundleDecorator resources = new ResourceBundleDecorator();

    /**
     * Instantiates a new application context.
     * @param clazz the clazz
     */
    public SwingApplicationContext(final Class<?> clazz) {
        super();

        preferences = Preferences.userNodeForPackage(clazz);
    }

    /**
     * Adds the resources.
     * @param baseName the base name
     */
    public void addResources(final String baseName) {
        resources.addDelegate(ResourceBundle.getBundle(baseName));
    }

    /**
     * Gets the locale.
     * @return the locale
     * @see java.util.ResourceBundle#getLocale()
     */
    public Locale getLocale() {
        return resources.getLocale();
    }

    /**
     * Gets the main frame.
     * @return the mainFrame
     */
    public JFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * Gets the preferences.
     * @return the preferences
     */
    public Preferences getPreferences() {
        return preferences;
    }

    /**
     * Gets the default resources.
     * @return the defaultResources
     */
    public ResourceBundleDecorator getResources() {
        return resources;
    }

    /**
     * Sets the main frame.
     * @param mainFrame the mainFrame to set
     */
    public void setMainFrame(final JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
}
