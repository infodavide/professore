package org.infodavid.professore.desktop;

import org.infodavid.professore.core.Note;
import org.infodavid.professore.core.midi.SoundControllerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SoundControllerImpl.
 */
public class SoundControllerImpl extends SoundControllerAdapter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SoundControllerImpl.class);

    /** The frame. */
    private final ProfessoreFrame frame;

    /**
     * Instantiates a new sound controller impl.
     * @param frame the frame
     */
    public SoundControllerImpl(final ProfessoreFrame frame) {
        super();

        this.frame = frame;
    }

    /*
     * (non-javadoc)
     * @see org.infodavid.professore.core.SoundControllerAdapter#controlChange(org.infodavid.professore.core.Note)
     */
    @Override
    public void controlChange(final Note note) {
        super.controlChange(note);

        final PianoforteComponent pianoforte = frame.getPianoforte();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} {}", String.valueOf(note.getKey()), note.isPressed() ? "pressed" : "released");
        }

        pianoforte.setKey(note.getKey(), note.isPressed());
    }
}
