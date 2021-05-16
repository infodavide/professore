module org.infodavid.professore.desktop {
    exports org.infodavid.util.swing.converter;
    exports org.infodavid.util.swing.action;
    exports org.infodavid.professore.desktop;
    exports org.infodavid.util.swing;

    requires transitive java.datatransfer;
    requires transitive java.desktop;
    requires transitive java.prefs;
    requires transitive logback.classic;
    requires transitive logback.core;
    requires transitive org.apache.commons.lang3;
    requires transitive org.infodavid.professore.core;
    requires transitive org.infodavid.professore.utils;
    requires transitive org.slf4j;
    requires transitive com.formdev.flatlaf;
    requires transitive gs.collections;
    requires transitive gs.collections.api;
    requires transitive humanize.slim;
}
