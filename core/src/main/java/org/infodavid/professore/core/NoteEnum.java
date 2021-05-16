package org.infodavid.professore.core;

import org.apache.commons.lang3.StringUtils;

/**
 * The Enum NoteEnum.
 */
public enum NoteEnum {

    /** The do. */
    DO("C", "Do", false),
    /** The do diesis. */
    DO_DIESIS("C#", "Do#", true),
    /** The re. */
    RE("D", "Re", false),
    /** The re diesis. */
    RE_DIESIS("D#", "Re#", true),
    /** The mi. */
    MI("E", "Mi", false),
    /** The fa. */
    FA("F", "Fa", false),
    /** The fa diesis. */
    FA_DIESIS("F#", "Fa#", true),
    /** The sol. */
    SOL("G", "Sol", false),
    /** The sol diesis. */
    SOL_DIESIS("G#", "Sol#", true),
    /** The la. */
    LA("A", "La", false),
    /** The la diesis. */
    LA_DIESIS("A#", "La#", true),
    /** The si. */
    SI("B", "Si", false);

    /** The alteration. */
    private boolean alteration;

    /** The english name. */
    private String englishName;

    /** The italian name. */
    private String italianName;

    /**
     * Instantiates a new note enum.
     * @param englishName the english name
     * @param italianName the italian name
     * @param alteration the alteration
     */
    NoteEnum(final String englishName, final String italianName, final boolean alteration) {
        this.englishName = englishName;
        this.italianName = italianName;
        this.alteration = alteration;
    }

    /**
     * Gets the english name.
     * @return the englishName
     */
    public String getEnglishName() {
        return englishName;
    }

    /**
     * Gets the italian name.
     * @return the italianName
     */
    public String getItalianName() {
        return italianName;
    }

    /**
     * Checks if is alteration.
     * @return the alteration
     */
    public boolean isAlteration() {
        return alteration;
    }

    /**
     * Next note.
     * @param alterations true to use alterations
     * @return the note enum
     */
    public NoteEnum next(final boolean alterations) {
        final NoteEnum[] values = values();
        for (byte i = 0; i < values.length; i++) {
            if (values[i].equals(this)) {
                for (byte j = (byte)(i + 1); j < values.length; j++) {
                    if (alterations) {
                        return values[j];
                    }

                    if (!values[j].isAlteration()) {
                        return values[j];
                    }
                }
            }
        }

        return DO;
    }

    /**
     * Parses the.
     * @param value the value
     * @return the note enum
     */
    public NoteEnum parse(final String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        for (final NoteEnum note : values()) {
            if (StringUtils.equalsIgnoreCase(value, note.englishName) || StringUtils.equalsIgnoreCase(value, note.italianName)) {
                return note;
            }
        }

        if ("D♭".equalsIgnoreCase(value) || "Re♭".equalsIgnoreCase(value)) {
            return DO_DIESIS;
        }

        if ("E♭".equalsIgnoreCase(value) || "Mi♭".equalsIgnoreCase(value)) {
            return RE_DIESIS;
        }

        if ("G♭".equalsIgnoreCase(value) || "Sol♭".equalsIgnoreCase(value)) {
            return FA_DIESIS;
        }

        if ("A♭".equalsIgnoreCase(value) || "La♭".equalsIgnoreCase(value)) {
            return SOL_DIESIS;
        }

        if ("B♭".equalsIgnoreCase(value) || "Si♭".equalsIgnoreCase(value)) {
            return LA_DIESIS;
        }

        return null;
    }

    /**
     * Previous note.
     * @param alterations true to use alterations
     * @return the note enum
     */
    public NoteEnum previous(final boolean alterations) {
        final NoteEnum[] values = values();
        for (byte i = (byte)(values.length - 1); i >= 0; i--) {
            if (values[i].equals(this)) {
                for (byte j = (byte)(i - 1); j >= 0; j--) {
                    if (alterations) {
                        return values[j];
                    }

                    if (!values[j].isAlteration()) {
                        return values[j];
                    }
                }
            }
        }

        return SI;
    }

    /*
     * (non-javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return getItalianName();
    }
}
