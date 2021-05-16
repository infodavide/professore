package org.infodavid.util.swing;

import java.util.Map;

import javax.swing.JComponent;

import org.infodavid.util.collection.EterogenicMap;

/**
 * The Class ComponentsMap.
 */
public class ComponentsMap extends EterogenicMap<String,JComponent> {

    /**
     * Instantiates a new map.
     */
    public ComponentsMap() {
        super();
    }

    /**
     * Instantiates a new map.
     * @param delegate the delegate
     */
    public ComponentsMap(final Map<String,JComponent> delegate) {
        super(delegate);
    }
}
