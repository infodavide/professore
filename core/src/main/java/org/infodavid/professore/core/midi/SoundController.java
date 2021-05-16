package org.infodavid.professore.core.midi;

import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.Sequencer;

import org.infodavid.professore.core.Note;

/**
 * The Interface SoundController.
 */
public interface SoundController extends ControllerEventListener, MetaEventListener{

    /**
     * Control change.
     * @param note the event
     */
    void controlChange(final Note note);

    /**
     * Gets the filter.
     * @return the filter
     */
    NoteFilter getFilter();

    /**
     * Links the sequencer.
     * @param sequencer the sequencer
     */
    void link(Sequencer sequencer);

    /**
     * Sets the filter.
     * @param filter the new filter
     */
    void setFilter(NoteFilter filter);
}
