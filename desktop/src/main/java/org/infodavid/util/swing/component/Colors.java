package org.infodavid.util.swing.component;

import java.awt.Color;

/**
 * The Enum Colors.
 */
public enum Colors {

    /** The blue. */
    BLUE(new Color(0, 0, 162, 255), new Color(50, 62, 214, 255), new Color(0, 29, 252, 255)),

    /** The cyan. */
    CYAN(new Color(15, 109, 108, 255), new Color(0, 255, 255, 255), new Color(179, 255, 255, 255)),

    /** The gray. */
    GRAY(new Color(106, 106, 106, 255), new Color(156, 156, 156, 255), new Color(205, 205, 205, 255)),

    /** The green. */
    GREEN(new Color(0, 162, 0, 255), new Color(62, 214, 50, 255), new Color(29, 252, 0, 255)),

    /** The green lcd. */
    GREEN_LCD(new Color(15, 109, 93, 255), new Color(0, 185, 165, 255), new Color(48, 255, 204, 255)),

    /** The jug green. */
    JUG_GREEN(new Color(0x204524), new Color(0x32A100), new Color(0x81CE00)),

    /** The magenta. */
    MAGENTA(new Color(98, 0, 114, 255), new Color(255, 0, 255, 255), new Color(255, 179, 255, 255)),

    /** The orange. */
    ORANGE(new Color(150, 53, 26, 255), new Color(252, 81, 0, 255), new Color(253, 136, 0, 255)),

    /** The raith. */
    RAITH(new Color(0, 65, 125, 255), new Color(0, 106, 172, 255), new Color(130, 180, 214, 255)),

    /** The red. */
    RED(new Color(162, 0, 0, 255), new Color(214, 62, 50, 255), new Color(252, 29, 0, 255)),

    /** The white. */
    WHITE(new Color(220, 220, 220, 255), new Color(235, 235, 235, 255), Color.WHITE),

    /** The yellow. */
    YELLOW(new Color(162, 162, 0, 255), new Color(214, 214, 50, 255), new Color(252, 252, 29, 255));

    /** The dark. */
    private final Color dark;

    /** The light. */
    private final Color light;

    /** The medium. */
    private final Color medium;

    /**
     * Instantiates a new colors.
     * @param dark the dark
     * @param medium the medium
     * @param light the light
     */
    Colors(final Color dark, final Color medium, final Color light) {
        this.dark = dark;
        this.medium = medium;
        this.light = light;
    }

    /**
     * Gets the dark.
     * @return the dark
     */
    public Color getDark() {
        return dark;
    }

    /**
     * Gets the light.
     * @return the light
     */
    public Color getLight() {
        return light;
    }

    /**
     * Gets the medium.
     * @return the medium
     */
    public Color getMedium() {
        return medium;
    }
}
