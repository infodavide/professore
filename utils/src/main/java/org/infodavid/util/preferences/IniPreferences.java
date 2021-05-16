package org.infodavid.util.preferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.infodavid.util.AccessControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class IniPreferences.
 */
public class IniPreferences extends AbstractPreferences {

    /** The cache. */
    private static Map<String,Map<String,String>> cache = null;

    /** The file. */
    private static Path file;

    /** The Constant INIT_FILE_EXTENSION. */
    private static final String INIT_FILE_EXTENSION = ".ini";

    /** The Constant IS_MODIFIED. */
    private static final AtomicBoolean IS_MODIFIED = new AtomicBoolean(false);

    /** The Constant LAST_SYNC_TIME. */
    private static final AtomicLong LAST_SYNC_TIME = new AtomicLong(-1);

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(IniPreferences.class);

    /** The Constant MODIFICATION_TIME. */
    private static final AtomicLong MODIFICATION_TIME = new AtomicLong(-1);

    /** The Constant ROOT_NAME. */
    private static final String ROOT_NAME = "main";

    /** The Constant SECTION_SEPARATOR. */
    private static final String SECTION_SEPARATOR = ".";

    /** The Constant SYNC_INTERVAL. */
    private static final int SYNC_INTERVAL = Math.max(30, AccessControllerUtils.getInstance().doPrivileged((PrivilegedAction<Integer>)() -> Integer.getInteger("java.util.prefs.syncInterval", 30)).intValue()) * 1000; // NOSONAR Cast necessary

    /** The Constant TIMER. */
    private static final Timer TIMER = new Timer(true);

    /**
     * The user root.
     */
    private static IniPreferences userRoot;

    static {
        userRoot = new IniPreferences();

        // Add shutdown hook to flush cached prefs on normal termination
        AccessControllerUtils.getInstance().doPrivileged(new PrivilegedAction<Void>() { // NOSONAR No lambda
            @Override
            public Void run() {
                Runtime.getRuntime().addShutdownHook(new Thread(null, null, "InitPreferencesSyncThread", 0, false) {
                    @Override
                    public void run() {
                        TIMER.cancel();
                        syncAll();
                    }
                });
                return null;
            }
        });
    }

    /**
     * Gets the user root.
     * @return the user root
     */
    public static Preferences getUserRoot() {
        return userRoot;
    }

    /**
     * Gets the cache.
     * @return the cache
     */
    private static synchronized Map<String,Map<String,String>> getCache() {
        if (cache == null) {
            cache = new TreeMap<>();
        }

        return cache;
    }

    /**
     * Load cache.
     * @throws BackingStoreException the backing store exception
     */
    private static void loadCache() throws BackingStoreException {
        if (file == null) {
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Loading preferences...");
        }

        try {
            AccessControllerUtils.getInstance().doPrivileged(new PrivilegedExceptionAction<Void>() { // NOSONAR No lambda
                @Override
                public Void run() throws IOException {
                    if (!Files.exists(file)) {
                        LOGGER.warn("File does not exists: {}", file);

                        return null;
                    }

                    LOGGER.info("Loading preferences from file: {}", file);

                    final Map<String,Map<String,String>> entries = getCache();

                    synchronized (entries) {
                        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                            Map<String,String> sectionData = null;
                            String line;

                            while ((line = StringUtils.trim(reader.readLine())) != null) { // NOSONAR Number of continue
                                if (line.length() == 0 || line.charAt(0) == '#') {
                                    continue;
                                }

                                if (line.charAt(0) == '[') {
                                    final String section = line.replace("[", "").replace("]", "").trim();

                                    LOGGER.info("Reading: {}", section);

                                    if (line.startsWith('[' + ROOT_NAME + ']')) {
                                        sectionData = entries.computeIfAbsent(ROOT_NAME, k -> new TreeMap<>());
                                    }
                                    else {
                                        sectionData = entries.computeIfAbsent(section, k -> new TreeMap<>());
                                    }

                                    continue;
                                }

                                if (sectionData == null) {
                                    continue;
                                }

                                final String[] parts = line.split("=", 2);

                                if (parts.length == 1) {
                                    sectionData.put(parts[0], StringUtils.EMPTY);
                                }
                                else if (parts.length == 2) {
                                    sectionData.put(parts[0], parts[1]);
                                }
                            }
                        }
                    }

                    return null;
                }
            });
        }
        catch (final PrivilegedActionException e) {
            throw new BackingStoreException(e.getException());
        }
    }

    /**
     * Sync all.
     */
    private static void syncAll() {
        try {
            userRoot.flush();
        }
        catch (final BackingStoreException e) {
            LOGGER.warn("Couldn't flush user preferences", e);
        }
    }

    /**
     * Attempt to write back cache to the backing store. If the attempt succeeds, lastSyncTime will be updated (the new value will correspond exactly to the data thust written back, as we hold the file lock, which prevents a concurrent write. If the attempt fails, a BackingStoreException is thrown and both the backing store (prefsFile) and lastSyncTime will be unaffected by this call. This call will NEVER leave prefsFile in a corrupt state.
     * @throws BackingStoreException the backing store exception
     */
    private static void writeCache() throws BackingStoreException {
        if (file == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No specified file for preferences");
            }

            return;
        }

        if (!IS_MODIFIED.get()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No modification found in preferences");
            }

            return;
        }

        if (MODIFICATION_TIME.get() > LAST_SYNC_TIME.get()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Writing preferences...");
            }

            final Map<String,Map<String,String>> entries = getCache();

            synchronized (entries) {
                try {
                    IS_MODIFIED.set(AccessControllerUtils.getInstance().doPrivileged(new PrivilegedExceptionAction<Boolean>() { // NOSONAR No lambda
                        @Override
                        public Boolean run() throws IOException {
                            try (final BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                                for (final Entry<String,Map<String,String>> sectionEntry : entries.entrySet()) {
                                    final Map<String,String> sectionData = sectionEntry.getValue();

                                    writer.write('[');
                                    writer.write(sectionEntry.getKey());
                                    writer.write(']');
                                    writer.newLine();

                                    for (final Entry<String,String> entry : sectionData.entrySet()) {
                                        writer.write(entry.getKey());
                                        writer.write('=');
                                        writer.write(entry.getValue());
                                        writer.newLine();
                                    }
                                }

                                writer.flush();
                                LAST_SYNC_TIME.set(System.currentTimeMillis());

                                return Boolean.FALSE;
                            }
                        }
                    }).booleanValue());

                }
                catch (final PrivilegedActionException e) {
                    throw new BackingStoreException(e.getException());
                }
            }
        }
        else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Preferences are already synchronized");
            }
        }
    }

    /** The file name. */
    private String fileName;

    /**
     * Instantiates a new ini preferences.
     */
    private IniPreferences() {
        this(null, "");
    }

    /**
     * Instantiates a new ini preferences.
     * @param parent the parent
     * @param name the name
     */
    private IniPreferences(final IniPreferences parent, final String name) {
        super(parent, name);

        if (StringUtils.isNotEmpty(name)) {
            if (name.startsWith(".")) {
                fileName = name.substring(1) + INIT_FILE_EXTENSION;
            }
            else {
                fileName = name + INIT_FILE_EXTENSION;
            }

            file = Paths.get(fileName);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Preferences created: {}", name());
        }

        newNode = AccessControllerUtils.getInstance().doPrivileged(new PrivilegedAction<Boolean>() { // NOSONAR No lambda
            @Override
            public Boolean run() {
                return Boolean.valueOf(file == null || !Files.exists(file));
            }
        }).booleanValue();

        try {
            loadCache();
        }
        catch (final BackingStoreException e) {
            LOGGER.warn("Cannot load " + name() + " preferences", e);
        }
    }

    /*
     * (non-javadoc)
     * @see java.util.prefs.AbstractPreferences#flush()
     */
    @Override
    public void flush() throws BackingStoreException {
        if (isRemoved()) {
            return;
        }

        sync();
    }

    /*
     * (non-javadoc)
     * @see java.util.prefs.AbstractPreferences#isUserNode()
     */
    @Override
    public boolean isUserNode() {
        return true;
    }

    /*
     * (non-javadoc)
     * @see java.util.prefs.AbstractPreferences#sync()
     */
    @Override
    public synchronized void sync() throws BackingStoreException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Synchronizing {} preferences", name());
        }

        synchronized (TIMER) {
            TIMER.cancel();
        }

        final Preferences[] preferences = cachedChildren();

        if (preferences != null && preferences.length > 0) {
            for (final Preferences preference : preferences) {
                preference.sync();
            }
        }

        try {
            AccessControllerUtils.getInstance().doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws BackingStoreException {
                    writeCache();

                    return null;
                }
            });
        }
        catch (final Exception e) {
            throw new BackingStoreException(e);
        }
    }

    /**
     * Modifed.
     */
    private void modifed() {
        final Map<String,Map<String,String>> entries = getCache();

        synchronized (entries) {
            IS_MODIFIED.set(true);
            MODIFICATION_TIME.set(System.currentTimeMillis());
        }

        synchronized (TIMER) {
            TIMER.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        sync();
                    }
                    catch (final BackingStoreException e) {
                        LOGGER.error("Unable to synchronize " + name() + " preferences", e);
                    }
                }
            }, SYNC_INTERVAL);
        }
    }

    /*
     * (non-javadoc)
     * @see java.util.prefs.AbstractPreferences#childrenNamesSpi()
     */
    @Override
    protected String[] childrenNamesSpi() {
        final Set<String> names = new TreeSet<>();
        final Map<String,Map<String,String>> entries = getCache();

        synchronized (entries) {
            for (final Entry<String,Map<String,String>> sectionEntry : entries.entrySet()) {
                names.add(sectionEntry.getKey());
            }
        }

        return names.toArray(new String[names.size()]);
    }

    /*
     * (non-javadoc)
     * @see java.util.prefs.AbstractPreferences#childSpi(java.lang.String)
     */
    @Override
    protected AbstractPreferences childSpi(final String name) {
        return new IniPreferences(this, name() + '.' + name);
    }

    /*
     * (non-javadoc)
     * @see java.util.prefs.AbstractPreferences#flushSpi()
     */
    @Override
    protected void flushSpi() throws BackingStoreException {
        // noop
    }

    /*
     * (non-javadoc)
     * @see java.util.prefs.AbstractPreferences#getSpi(java.lang.String)
     */
    @Override
    protected String getSpi(final String key) {
        final Map<String,Map<String,String>> entries = getCache();

        synchronized (entries) {
            final String section = StringUtils.defaultString(StringUtils.substringBeforeLast(key, SECTION_SEPARATOR), ROOT_NAME);
            final String entry = StringUtils.substringAfterLast(key, SECTION_SEPARATOR);
            final MutableObject<String> result = new MutableObject<>();

            entries.computeIfPresent(section, (k, v) -> {
                result.setValue(v.get(entry));

                return v;
            });

            LOGGER.debug("Value for {}:{} is {}", section, entry, result);

            return result.getValue();
        }
    }

    /*
     * (non-javadoc)
     * @see java.util.prefs.AbstractPreferences#keysSpi()
     */
    @Override
    protected String[] keysSpi() {
        final Set<String> keys = new TreeSet<>();
        final Map<String,Map<String,String>> cache = getCache();

        synchronized (cache) {
            for (final Entry<String,Map<String,String>> sectionEntry : cache.entrySet()) {
                final Map<String,String> sectionData = sectionEntry.getValue();

                for (final Entry<String,String> entry : sectionData.entrySet()) {
                    keys.add(entry.getKey());
                }
            }
        }

        return keys.toArray(new String[keys.size()]);
    }

    /*
     * (non-javadoc)
     * @see java.util.prefs.AbstractPreferences#putSpi(java.lang.String, java.lang.String)
     */
    @Override
    protected void putSpi(final String key, final String value) {
        final Map<String,Map<String,String>> entries = getCache();

        synchronized (entries) {
            final String section = StringUtils.defaultString(StringUtils.substringBeforeLast(key, SECTION_SEPARATOR), ROOT_NAME);
            final String entry = StringUtils.substringAfterLast(key, SECTION_SEPARATOR);

            entries.computeIfAbsent(section, k -> new TreeMap<>()).put(entry, value);

            LOGGER.debug("Adding value: {} for {}:{}", value, section, entry);
        }

        modifed();
    }

    /*
     * (non-javadoc)
     * @see java.util.prefs.AbstractPreferences#removeNodeSpi()
     */
    @Override
    protected void removeNodeSpi() throws BackingStoreException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-javadoc)
     * @see java.util.prefs.AbstractPreferences#removeSpi(java.lang.String)
     */
    @Override
    protected void removeSpi(final String key) {
        final Map<String,Map<String,String>> entries = getCache();

        synchronized (entries) {
            final String section = StringUtils.defaultString(StringUtils.substringBeforeLast(key, SECTION_SEPARATOR), ROOT_NAME);
            final String entry = StringUtils.substringAfterLast(key, SECTION_SEPARATOR);

            entries.get(section).remove(entry);

            LOGGER.debug("Removing {}:{}", section, entry);
        }

        modifed();
    }

    /*
     * (non-javadoc)
     * @see java.util.prefs.AbstractPreferences#syncSpi()
     */
    @Override
    protected void syncSpi() throws BackingStoreException {
        flushSpi();
    }
}
