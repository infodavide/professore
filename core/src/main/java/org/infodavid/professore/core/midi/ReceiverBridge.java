package org.infodavid.professore.core.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

/**
 * The Class ReceiverBridge.
 */
public class ReceiverBridge implements Receiver {

    /** The controller. */
    private final SoundController controller;

    /** The delegate. */
    private final Receiver delegate;

    /**
     * Instantiates a new delegate bridge.
     * @param controller the controller
     * @param delegate   the delegate
     */
    public ReceiverBridge(final SoundController controller, final Receiver delegate) {
        super();
        this.controller = controller;
        this.delegate = delegate;
    }

    /*
     * (non-javadoc)
     * @see javax.sound.midi.Receiver#close()
     */
    @Override
    public void close() {
        if (delegate == null) {
            return;
        }

        delegate.close();
    }

    /*
     * (non-javadoc)
     * @see javax.sound.midi.Receiver#send(javax.sound.midi.MidiMessage, long)
     */
    @Override
    public void send(final MidiMessage message, final long timeStamp) {
        if (delegate != null) {
            // delegate.send(message, timeStamp);
        }

        if (message instanceof ShortMessage) {
            controller.controlChange((ShortMessage) message);
        }
    }
}
