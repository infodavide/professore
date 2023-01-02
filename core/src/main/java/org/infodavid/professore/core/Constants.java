package org.infodavid.professore.core;

/**
 * The Class Constants.
 */
public final class Constants {

    /** The Constant MID_EXTENSION. */
    public static final String MID_EXTENSION = ".mid";

    /** The Constant NOTE_NAMES_EN. */
    public static final String[] NOTE_NAMES_EN = { NoteEnum.DO.getEnglishName(), NoteEnum.DO_DIESIS.getEnglishName(), NoteEnum.RE.getEnglishName(), NoteEnum.RE_DIESIS.getEnglishName(), NoteEnum.MI.getEnglishName(), NoteEnum.FA.getEnglishName(), NoteEnum.FA_DIESIS.getEnglishName(), NoteEnum.SOL.getEnglishName(), NoteEnum.SOL_DIESIS.getEnglishName(), NoteEnum.LA.getEnglishName(), NoteEnum.LA_DIESIS.getEnglishName(), NoteEnum.SI.getEnglishName() }; //NOSONAR Keep public

    /** The Constant NOTE_NAMES_IT. */
    public static final String[] NOTE_NAMES_IT = { NoteEnum.DO.getItalianName(), NoteEnum.DO_DIESIS.getItalianName(), NoteEnum.RE.getItalianName(), NoteEnum.RE_DIESIS.getItalianName(), NoteEnum.MI.getItalianName(), NoteEnum.FA.getItalianName(), NoteEnum.FA_DIESIS.getItalianName(), NoteEnum.SOL.getItalianName(), NoteEnum.SOL_DIESIS.getItalianName(), NoteEnum.LA.getItalianName(), NoteEnum.LA_DIESIS.getItalianName(), NoteEnum.SI.getItalianName() }; //NOSONAR Keep public

    /** The Constant PITCH_CONTROL_CHANGE. */
    public static final byte PITCH_CONTROL_CHANGE = 5;

    /** The Constant VELOCITY_F. */
    public static final byte VELOCITY_F = 80;

    /** The Constant VELOCITY_FF. */
    public static final byte VELOCITY_FF = 96;

    /** The Constant VELOCITY_FFF. */
    public static final byte VELOCITY_FFF = 112;

    /** The Constant VELOCITY_FFFF. */
    public static final byte VELOCITY_FFFF = 127;

    /** The Constant VELOCITY_MF. */
    public static final byte VELOCITY_MF = 64;

    /** The Constant VELOCITY_MP. */
    public static final byte VELOCITY_MP = 53;

    /** The Constant VELOCITY_P. */
    public static final byte VELOCITY_P = 42;

    /** The Constant VELOCITY_PP. */
    public static final byte VELOCITY_PP = 31;

    /** The Constant VELOCITY_PPP. */
    public static final byte VELOCITY_PPP = 20;

    /** The Constant VELOCITY_PPPP. */
    public static final byte VELOCITY_PPPP = 8;

    /** The Constant VOLUME_CONTROL_CHANGE. */
    public static final byte VOLUME_CONTROL_CHANGE = 7;

    /**
     * Instantiates a new constants.
     */
    private Constants() {
        super();
    }
}
