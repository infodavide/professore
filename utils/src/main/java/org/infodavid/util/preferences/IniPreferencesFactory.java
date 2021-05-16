package org.infodavid.util.preferences;

import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * Class IniPreferencesFactory.
 *
 * <pre>
 * Usage:
 * -Djava.util.prefs.PreferencesFactory=org.infodavid.util.preferences.IniPreferencesFactory
 * </pre>
 *
 * @see IniPreferences
 */
public class IniPreferencesFactory implements PreferencesFactory {

    /*
     * (non-javadoc)
     * @see java.util.prefs.PreferencesFactory#userRoot()
     */
    @Override
    public Preferences userRoot() {
        return IniPreferences.getUserRoot();
    }

    /*
     * (non-javadoc)
     * @see java.util.prefs.PreferencesFactory#systemRoot()
     */
    @Override
    public Preferences systemRoot() {
        return Preferences.systemRoot();
    }
}
