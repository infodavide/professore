package org.infodavid.util.swing.component;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

/**
 * The Class CustomCheckBox.
 */
public class CustomCheckBox extends JCheckBox { // NOSONAR Inheritance

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3797515848673316945L;

    /** The Constant COLOR_PROPERTY. */
    private static final String COLOR_PROPERTY = "color";

    /** The Constant COLORED_PROPERTY. */
    private static final String COLORED_PROPERTY = "colored";

    /** The Constant RISED_PROPERTY. */
    private static final String RISED_PROPERTY = "rised";

    /** The colored. */
    private boolean colored = false;

    /** The rised. */
    private boolean rised = false;

    /** The selected color. */
    private Colors selectedColor = Colors.JUG_GREEN;

    /**
     * Instantiates a new custom check box.
     */
    public CustomCheckBox() {
        super();

        setPreferredSize(new Dimension(128, 26));
    }

    /**
     * Creates a check box where properties are taken from the Action supplied.
     * @param a the {@code Action} used to specify the new check box
     */
    public CustomCheckBox(final Action a) {
        this();

        setAction(a);
    }

    /**
     * Instantiates a new custom check box.
     * @param icon the icon
     */
    public CustomCheckBox(final Icon icon) {
        this(null, icon, false);
    }

    /**
     * Creates a check box with an icon and specifies whether or not it is initially selected.
     * @param icon the Icon image to display
     * @param selected a boolean value indicating the initial selection state. If <code>true</code> the check box is selected
     */
    public CustomCheckBox(final Icon icon, final boolean selected) {
        this(null, icon, selected);
    }

    /**
     * Creates an initially unselected check box with text.
     * @param text the text of the check box.
     */
    public CustomCheckBox(final String text) {
        this(text, null, false);
    }

    /**
     * Creates a check box with text and specifies whether or not it is initially selected.
     * @param text the text of the check box.
     * @param selected a boolean value indicating the initial selection state. If <code>true</code> the check box is selected
     */
    public CustomCheckBox(final String text, final boolean selected) {
        this(text, null, selected);
    }

    /**
     * Creates an initially unselected check box with the specified text and icon.
     * @param text the text of the check box.
     * @param icon the Icon image to display
     */
    public CustomCheckBox(final String text, final Icon icon) {
        this(text, icon, false);
    }

    /**
     * Creates a check box with text and icon, and specifies whether or not it is initially selected.
     * @param text the text of the check box.
     * @param icon the Icon image to display
     * @param selected a boolean value indicating the initial selection state. If <code>true</code> the check box is selected
     */
    public CustomCheckBox(final String text, final Icon icon, final boolean selected) {
        super(text, icon, selected);

        setPreferredSize(new Dimension(128, 26));
    }

    /**
     * Gets the selected color.
     * @return the selected color
     */
    public Colors getSelectedColor() {
        return selectedColor;
    }

    /**
     * Checks if is colored.
     * @return true, if is colored
     */
    public boolean isColored() {
        return colored;
    }

    /**
     * Checks if is rised.
     * @return true, if is rised
     */
    public boolean isRised() {
        return rised;
    }

    /**
     * Sets the colored.
     * @param value the new colored
     */
    public void setColored(final boolean value) {
        final boolean old = value;
        colored = value;

        firePropertyChange(COLORED_PROPERTY, old, value);
        repaint();
    }

    /**
     * Sets the rised.
     * @param value the new rised
     */
    public void setRised(final boolean value) {
        final boolean old = rised;
        rised = value;

        firePropertyChange(RISED_PROPERTY, old, value);
    }

    /**
     * Sets the selected color.
     * @param value the new selected color
     */
    public void setSelectedColor(final Colors value) {
        final Colors old = selectedColor;
        selectedColor = value;

        firePropertyChange(COLOR_PROPERTY, old, value);
        repaint();
    }

    /**
     * Sets the ui.
     * @param value the new ui
     */
    public void setUi(final javax.swing.plaf.ComponentUI value) {
        ui = new CustomCheckBoxUI(this);
    }

    /*
     * (non-javadoc)
     * @see javax.swing.AbstractButton#setUI(javax.swing.plaf.ButtonUI)
     */
    @Override
    public void setUI(final javax.swing.plaf.ButtonUI value) {
        super.setUI(new CustomCheckBoxUI(this));
    }

    /*
     * (non-javadoc)
     * @see java.awt.Component#toString()
     */
    @Override
    public String toString() {
        return "CustomCheckBox";
    }

    /*
     * (non-javadoc)
     * @see javax.swing.JComponent#setUI(javax.swing.plaf.ComponentUI)
     */
    @Override
    protected void setUI(final javax.swing.plaf.ComponentUI value) {
        super.setUI(new CustomCheckBoxUI(this));
    }
}
