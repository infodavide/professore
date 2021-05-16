package org.infodavid.util.swing.renderer;

import java.awt.Component;
import java.nio.file.Path;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * The Class PathRenderer.
 */
public class PathRenderer extends BasicComboBoxRenderer { // NOSONAR

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5033549929957847023L;

    /** The combo. */
    private final JComboBox<?> component;

    /**
     * Instantiates a new renderer.
     * @param component the component
     */
    public PathRenderer(final JComboBox<?> component) {
        super();

        this.component = component;
    }

    /**
     * Gets the list cell renderer component.
     * @param list the list
     * @param value the value
     * @param index the index
     * @param isSelected the is selected
     * @param cellHasFocus the cell has focus
     * @return the list cell renderer component
     */
    @SuppressWarnings({
            "rawtypes"
    })
    @Override
    public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value == null) {
            setText("None");
        }
        else {
            final Path path = (Path)value;

            setText(path.getFileName().toString());
        }

        setEnabled(component.isEnabled());
        setIcon(null);

        return this;
    }
}
