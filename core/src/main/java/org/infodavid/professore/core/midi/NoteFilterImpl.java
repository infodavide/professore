package org.infodavid.professore.core.midi;

import java.io.Serializable;

import org.infodavid.professore.core.Note;

/**
 * The Class NoteFilterImpl.
 */
public class NoteFilterImpl implements Serializable, NoteFilter {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3041003153277374855L;

    /** The acute. */
    private boolean acute;

    /** The key. */
    private byte key;

    /*
     * (non-javadoc)
     * @see org.infodavid.professore.core.midi.NoteFilter#accept(org.infodavid.professore.core.Note)
     */
    @Override
    public boolean accept(final Note note) {
        if (note == null) {
            return false;
        }

        if (acute) {
            return note.getKey() <= key;
        }

        return note.getKey() >= key;
    }

    /**
     * Gets the key.
     * @return the key
     */
    public byte getKey() {
        return key;
    }

    /**
     * Checks if is acute.
     * @return the acute
     */
    public boolean isAcute() {
        return acute;
    }

    /**
     * Sets the acute.
     * @param acute the acute to set
     */
    public void setAcute(final boolean acute) {
        this.acute = acute;
    }

    /**
     * Sets the key.
     * @param key the key to set
     */
    public void setKey(final byte key) {
        this.key = key;
    }
}
