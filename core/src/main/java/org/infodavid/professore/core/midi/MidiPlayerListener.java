package org.infodavid.professore.core.midi;

import javax.sound.midi.Sequence;

/**
 * The interface MidiPlayerListener.
 */
public interface MidiPlayerListener {

    /**
     * Paused.
     * @param player the player
     */
    void paused(MidiPlayer player);

    /**
     * Playing.
     * @param player the player
     * @param title the title
     * @param sequence the sequence
     */
    void playing(MidiPlayer player, String title, Sequence sequence);

    /**
     * Resumed.
     * @param player the player
     */
    void resumed(MidiPlayer player);

    /**
     * Stopped.
     * @param player the player
     */
    void stopped(MidiPlayer player);

}
