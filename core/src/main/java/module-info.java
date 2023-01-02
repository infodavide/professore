module org.infodavid.professore.core {
    exports org.infodavid.professore.core;
    exports org.infodavid.professore.core.midi;

    opens org.infodavid.professore.core;

    requires transitive org.apache.commons.pool2;
    requires com.github.benmanes.caffeine;
    requires gs.collections.api;
    requires gs.collections;
    requires transitive org.apache.commons.lang3;
    requires transitive java.desktop;
    requires transitive org.slf4j;
    requires org.apache.commons.io;
}
