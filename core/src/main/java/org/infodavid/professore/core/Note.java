package org.infodavid.professore.core;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The Class Note.
 */
public class Note implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -269802217234528925L;

    /** The channel. */
    private byte channel;

    /** The key. */
    private byte key;

    /** The base note. */
    private NoteEnum baseNote;

    /** The octave. */
    private byte octave;

    /** The pressed. */
    private boolean pressed = true;

    /** The track. */
    private byte track;

    /** The velocity. */
    private short velocity;

    /**
     * Instantiates a new note.
     */
    protected Note() {
        super();
    }

    /**
     * Instantiates a new note.
     * @param source the source
     */
    protected Note(final Note source) {
        super();
        key = source.key;
        baseNote = source.baseNote;
        octave = source.octave;
        pressed = source.pressed;
        velocity = source.velocity;
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Note)) {
            return false;
        }

        final Note other = (Note)obj;

        if (baseNote != other.baseNote) {
            return false;
        }

        if (pressed != other.pressed) {
            return false;
        }

        return octave == other.octave;
    }

    /**
     * Gets the channel.
     * @return the channel
     */
    public byte getChannel() {
        return channel;
    }

    /**
     * Gets the key.
     * @return the key
     */
    public byte getKey() {
        return key;
    }

    /**
     * Gets the base note.
     * @return the base note
     */
    public NoteEnum getBaseNote() {
        return baseNote;
    }

    /**
     * Gets the octave.
     * @return the octave
     */
    public byte getOctave() {
        return octave;
    }

    /**
     * Checks if is pressed.
     * @return the pressed
     */
    public boolean isPressed() {
        return pressed;
    }

    /**
     * Gets the track.
     * @return the track
     */
    public byte getTrack() {
        return track;
    }

    /**
     * Gets the velocity.
     * @return the velocity
     */
    public short getVelocity() {
        return velocity;
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + key;
        result = prime * result + octave;

        return result;
    }

    /**
     * Sets the channel.
     * @param channel the channel to set
     * @return the note
     */
    public Note setChannel(final byte channel) {
        this.channel = channel;

        return this;
    }

    /**
     * Sets the key.
     * @param key the key to set
     * @return the note
     */
    public Note setKey(final byte key) {
        this.key = key;

        return this;
    }

    /**
     * Sets the base note.
     * @param baseNote the base note
     * @return the note
     */
    public Note setBaseNote(final NoteEnum baseNote) {
        this.baseNote = baseNote;

        return this;
    }

    /**
     * Sets the octave.
     * @param octave the octave to set
     * @return the note
     */
    public Note setOctave(final byte octave) {
        this.octave = octave;

        return this;
    }

    /**
     * Sets the pressed.
     * @param pressed the pressed to set
     * @return the note
     */
    public Note setPressed(final boolean pressed) {
        this.pressed = pressed;

        return this;
    }

    /**
     * Sets the track.
     * @param track the track to set
     * @return the note
     */
    public Note setTrack(final byte track) {
        this.track = track;

        return this;
    }

    /**
     * Sets the velocity.
     * @param velocity the velocity to set
     * @return the note
     */
    public Note setVelocity(final short velocity) {
        this.velocity = velocity;

        return this;
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
