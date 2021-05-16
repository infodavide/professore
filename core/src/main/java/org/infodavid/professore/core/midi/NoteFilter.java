package org.infodavid.professore.core.midi;

import org.infodavid.professore.core.Note;

/**
 * The Interface NoteFilter.
 */
public interface NoteFilter {

    /**
     * Accept.
     * @param note the note
     * @return true, if successful
     */
    boolean accept(Note note);
}
