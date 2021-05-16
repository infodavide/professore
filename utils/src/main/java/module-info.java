module org.infodavid.professore.utils {
    exports org.infodavid.util;
    exports org.infodavid.util.logging;
    exports org.infodavid.util.exception;
    exports org.infodavid.util.concurrency;
    exports org.infodavid.util.collection;
    exports org.infodavid.util.preferences;

    requires transitive org.apache.commons.io;
    requires transitive org.apache.commons.lang3;
    requires transitive org.slf4j;
    requires transitive logback.classic;
    requires transitive logback.core;
    requires transitive java.prefs;
}
