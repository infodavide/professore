module org.infodavid.professore.core {
    exports org.infodavid.professore.core;
    exports org.infodavid.professore.core.midi;

    opens org.infodavid.professore.core;

    requires transitive java.desktop;
    requires transitive org.apache.commons.pool2;
    requires transitive org.infodavid.professore.utils;
    requires com.github.benmanes.caffeine;
    requires gs.collections.api;
    requires gs.collections;
}
