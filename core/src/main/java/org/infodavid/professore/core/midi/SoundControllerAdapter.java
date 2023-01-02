package org.infodavid.professore.core.midi;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;

import org.infodavid.professore.core.Note;
import org.infodavid.professore.core.NotePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gs.collections.api.map.primitive.MutableByteObjectMap;
import com.gs.collections.impl.map.mutable.primitive.ByteObjectHashMap;

/**
 * The Class SoundControllerAdapter.
 */
public class SoundControllerAdapter implements SoundController {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SoundControllerAdapter.class);

    /** The filter. */
    private NoteFilter filter = null;

    /** The pressed. */
    private final MutableByteObjectMap<ShortMessage> pressed = new ByteObjectHashMap<>();

    /*
     * (non-javadoc)
     * @see org.infodavid.professore.core.SoundController#controlChange(org.infodavid.professore.core.Note)
     */
    @Override
    public void controlChange(final Note note) {
        LOGGER.debug("controlChange invoked with note: {}", note);
    }

    /*
     * (non-javadoc)
     * @see javax.sound.midi.ControllerEventListener#controlChange(javax.sound.midi.ShortMessage)
     */
    @SuppressWarnings("resource")
    @Override
    public void controlChange(final ShortMessage message) {
        LOGGER.debug("controlChange invoked with message: {}", message);
        final Note note = NotePool.getInstance().borrowObject(message);

        if (filter != null && !filter.accept(note)) {
            LOGGER.info("Filtered note: {}", note);
            return;
        }

        if (note == null) {
            if (message.getCommand() == ShortMessage.CONTROL_CHANGE && message.getData1() >= 120 && message.getData1() <= 127) {
                for (final ShortMessage msg : pressed.values()) {
                    controlChange(NotePool.getInstance().borrowObject(msg).setPressed(false));
                }

                pressed.clear();
            }
        } else {
            if (note.isPressed()) {
                pressed.put(note.getKey(), message);
            } else {
                pressed.remove(note.getKey());
            }

            controlChange(note);
        }
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.professore.core.midi.SoundController#getFilter()
     */
    @Override
    public NoteFilter getFilter() {
        return filter;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.professore.core.SoundController#link(javax.sound.midi.Sequencer)
     */
    @Override
    public void link(final Sequencer sequencer) {
        LOGGER.debug("Link invoked: {}", sequencer);
    }

    /*
     * (non-javadoc)
     * @see javax.sound.midi.MetaEventListener#meta(javax.sound.midi.MetaMessage)
     */
    @Override
    public void meta(final MetaMessage meta) {
        LOGGER.debug("Meta invoked: {}", meta);
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.professore.core.midi.SoundController#setFilter(org.infodavid.professore.core.midi.NoteFilter)
     */
    @Override
    public void setFilter(final NoteFilter filter) {
        this.filter = filter;
    }
}
